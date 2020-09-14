/**
 * Copyright © 2016~2020 CRPC (coderhook@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.leeyazhou.crpc.transport.netty;

import java.util.concurrent.TimeUnit;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.exception.ServiceNotFoundException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.concurrent.NamedThreadFactory;
import com.github.leeyazhou.crpc.transport.AbstractServer;
import com.github.leeyazhou.crpc.transport.ChannelManager;
import com.github.leeyazhou.crpc.transport.Handler;
import com.github.leeyazhou.crpc.transport.RpcContext;
import com.github.leeyazhou.crpc.transport.Server;
import com.github.leeyazhou.crpc.transport.factory.ServerFactory;
import com.github.leeyazhou.crpc.transport.netty.handler.NettyServerHandler;
import com.github.leeyazhou.crpc.transport.netty.handler.NettyServerHeartBeatHandler;
import com.github.leeyazhou.crpc.transport.netty.protocol.NettyProtocolDecoder;
import com.github.leeyazhou.crpc.transport.netty.protocol.NettyProtocolEncoder;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 
 * @author leeyazhou
 */
public class NettyServer extends AbstractServer {
  private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
  private EventLoopGroup bossGroup;
  private EventLoopGroup ioGroup;
  private ServerBootstrap bootStrap;
  private final Handler<?> handler;
  private final ServerFactory serverFactory;
  private Channel channel;
  private ChannelManager channelManager;

  public NettyServer(Configuration configuration, final ServerFactory serverFactory, ChannelManager channelManager) {
    super(configuration);
    this.serverFactory = serverFactory;
    this.channelManager = channelManager;
    this.handler = new Handler<ResponseMessage>() {

      @Override
      public Class<ResponseMessage> getHandlerType() {
        return ResponseMessage.class;
      }

      @Override
      public ResponseMessage handle(RpcContext context) {
        final String targetInstanceName = context.getRequest().getServiceTypeName();
        final Handler<?> processor = serverFactory.getServiceHandler(targetInstanceName);
        if (processor == null) {
          throw new ServiceNotFoundException(targetInstanceName);
        }
        return processor.handle(context);
      }
    };
  }

  @Override
  public void doInit() {
    this.bootStrap = new ServerBootstrap();
    final Class<? extends ServerChannel> socketChannelClass;
    if (Epoll.isAvailable()) {
      this.bossGroup = new EpollEventLoopGroup(1);
      this.ioGroup = new EpollEventLoopGroup(0, new NamedThreadFactory("crpc-io"));
      socketChannelClass = EpollServerSocketChannel.class;
    } else {
      this.bossGroup = new NioEventLoopGroup(1, new NamedThreadFactory("crpc-boss"));
      this.ioGroup = new NioEventLoopGroup(0, new NamedThreadFactory("crpc-io"));
      socketChannelClass = NioServerSocketChannel.class;
    }

    bootStrap.group(bossGroup, ioGroup).channel(socketChannelClass);
    bootStrap.childOption(ChannelOption.TCP_NODELAY, true);
    bootStrap.childOption(ChannelOption.SO_REUSEADDR, true);
    bootStrap.childOption(ChannelOption.SO_KEEPALIVE, true);
    bootStrap.childOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, false);
    WriteBufferWaterMark bufferWaterMark = WriteBufferWaterMark.DEFAULT;
    if (getServerConfig().getLowWaterMarker() > 0
        && getServerConfig().getHighWaterMarker() > getServerConfig().getLowWaterMarker()) {
      bufferWaterMark =
          new WriteBufferWaterMark(getServerConfig().getLowWaterMarker(), getServerConfig().getHighWaterMarker());
    }
    bootStrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, bufferWaterMark);
    final NettyServerHandler nettyServerHandler = new NettyServerHandler(handler, serverFactory, channelManager);
    bootStrap.childHandler(new ChannelInitializer<SocketChannel>() {
      @Override
      public void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast("decoder", new NettyProtocolDecoder());
        channel.pipeline().addLast("encoder", new NettyProtocolEncoder());
        channel.pipeline().addLast("heartbeat",
            new NettyServerHeartBeatHandler(0, 0, Constants.DEFAULT_HEARTBEAT_TIMEOUT, TimeUnit.SECONDS));
        channel.pipeline().addLast("handler", nettyServerHandler);
      }
    });
  }

  @Override
  protected void doStartup() {
    ChannelFuture channelFuture =
        bootStrap.bind(configuration.getProtocolConfig().getHost(), configuration.getProtocolConfig().getPort())
            .syncUninterruptibly();
    this.channel = channelFuture.channel();
    Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
    logger.info(String.format("Server is running at %s://%s:%s, businessThreads : %s",
        configuration.getProtocolConfig().getProtocol(), configuration.getProtocolConfig().getHost(),
        configuration.getProtocolConfig().getPort(), configuration.getServerConfig().getWorker()));

  }

  @Override
  public void doShutdown() {
    logger.warn("Server stopped! configuration : " + configuration);
    channelManager.serverClosed();
    if (channel != null) {
      channel.close();
    }
    if (bossGroup != null) {
      bossGroup.shutdownGracefully();
    }
    if (ioGroup != null) {
      ioGroup.shutdownGracefully();
    }

  }


  private static class ShutdownHook extends Thread {
    private Server server;

    public ShutdownHook(Server server) {
      this.server = server;
    }

    @Override
    public void run() {
      try {
        server.shutdown();
      } catch (Exception e) {
        // to do nothing
      }
    }
  }
}

/**
 * Copyright © 2019 leeyazhou (coderhook@gmail.com)
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

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.leeyazhou.crpc.config.crpc.ServerConfig;
import com.github.leeyazhou.crpc.protocol.Response;
import com.github.leeyazhou.crpc.protocol.SimpleProtocol;
import com.github.leeyazhou.crpc.protocol.codec.Codecs;
import com.github.leeyazhou.crpc.protocol.netty.NettyProtocolDecoder;
import com.github.leeyazhou.crpc.protocol.netty.NettyProtocolEncoder;
import com.github.leeyazhou.crpc.transport.AbstractServer;
import com.github.leeyazhou.crpc.transport.Handler;
import com.github.leeyazhou.crpc.transport.RpcContext;
import com.github.leeyazhou.crpc.transport.Server;
import com.github.leeyazhou.crpc.transport.factory.BeanFactory;
import com.github.leeyazhou.crpc.transport.netty.handler.NettyServerHandler;
import com.github.leeyazhou.crpc.transport.netty.handler.NettyServerHeartBeatHandler;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.concurrent.SimpleNamedThreadFactory;
import com.github.leeyazhou.crpc.core.exception.ServiceNotFoundException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.object.MessageType;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

class NettyServer extends AbstractServer {

  private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
  private AtomicBoolean startFlag = new AtomicBoolean(false);
  private NioEventLoopGroup bossGroup;
  private NioEventLoopGroup ioGroup;
  private EventExecutorGroup bussinessGroup;
  private final Handler<?> handler;
  private final BeanFactory beanFactory;
  private ConcurrentMap<String, Channel> channels;
  private Channel channel;

  public NettyServer(ServerConfig serverConfig, final BeanFactory beanFactory) {
    super(serverConfig);
    this.beanFactory = beanFactory;
    this.handler = new Handler<Response>() {

      @Override
      public Class<Response> getHandlerType() {
        return Response.class;
      }

      @Override
      public Response handle(RpcContext context) {
        final String targetInstanceName = context.getRequest().getTargetClassName();
        final Handler<?> processor = beanFactory.getServiceHandler(targetInstanceName);
        if (processor == null) {
          throw new ServiceNotFoundException(targetInstanceName);
        }
        return processor.handle(context);
      }
    };
  }

  @Override
  public void start() {
    if (!startFlag.compareAndSet(false, true)) {
      return;
    }
    doStart();
    afterStartServer();
    logger.info(String.format("Server is running at %s/%s:%s, businessThreads : %s", serverConfig.getName(),
        serverConfig.getHost(), serverConfig.getPort(), serverConfig.getWorker()));
  }

  protected void doStart() {
    bossGroup = new NioEventLoopGroup(1, new SimpleNamedThreadFactory("crpc-boss"));
    ioGroup = new NioEventLoopGroup(0, new SimpleNamedThreadFactory("crpc-io"));
    bussinessGroup = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() * 2,
        new SimpleNamedThreadFactory("crpc-worker"));

    ServerBootstrap bootStrap = new ServerBootstrap();
    bootStrap.group(bossGroup, ioGroup).channel(NioServerSocketChannel.class);
    bootStrap.childOption(ChannelOption.TCP_NODELAY, true);
    bootStrap.childOption(ChannelOption.SO_REUSEADDR, true);
    bootStrap.childOption(ChannelOption.SO_KEEPALIVE, true);
    bootStrap.childOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, false);
    final NettyServerHandler nettyServerHandler = new NettyServerHandler(serverConfig, handler, beanFactory);
    this.channels = nettyServerHandler.getChannels();
    bootStrap.childHandler(new ChannelInitializer<SocketChannel>() {
      @Override
      public void initChannel(SocketChannel channel) throws Exception {
        // channel.pipeline().addLast("SnappyDecoder", new SnappyFrameDecoder());
        channel.pipeline().addLast("decoder", new NettyProtocolDecoder());
        // channel.pipeline().addLast("SnappyEncoder", new SnappyFrameEncoder());
        channel.pipeline().addLast("encoder", new NettyProtocolEncoder());
        channel.pipeline().addLast("heartbeat",
            new NettyServerHeartBeatHandler(0, 0, Constants.DEFAULT_HEARTBEAT_TIMEOUT, TimeUnit.SECONDS));
        channel.pipeline().addLast(bussinessGroup, "handler", nettyServerHandler);
      }
    });
    ChannelFuture channelFuture = bootStrap.bind(serverConfig.getHost(), serverConfig.getPort()).syncUninterruptibly();
    this.channel = channelFuture.channel();
  }

  @Override
  public void stop() {
    if (startFlag.compareAndSet(true, false)) {
      logger.warn("Server stopped! serverConfig : " + serverConfig);
      if (channels != null) {
        // logger.info("关闭通道：" + channels);
        Response response =
            new Response(0, Codecs.JAVA_CODEC.getId(), SimpleProtocol.PROTOCOL_TYPE, MessageType.MESSAGE_SHUTDOWN);
        for (Map.Entry<String, Channel> entry : this.channels.entrySet()) {
          logger.info("通知关闭通道：" + entry.getKey() + ", channel : " + entry.getValue());
          entry.getValue().writeAndFlush(response);
        }
        channels.clear();
      }
      if (channel != null) {
        channel.close();
      }
      if (bossGroup != null) {
        bossGroup.shutdownGracefully();
        ioGroup.shutdownGracefully();
        bussinessGroup.shutdownGracefully();
      }
    }
  }

  /**
   * 
   */
  private void afterStartServer() {
    Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
  }

  private static class ShutdownHook extends Thread {
    private Server server;

    public ShutdownHook(Server server) {
      this.server = server;
    }

    @Override
    public void run() {
      try {
        server.stop();
      } catch (Exception e) {
        // to do nothing
      }
    }
  }
}

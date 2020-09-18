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
/**
 * 
 */
package com.github.leeyazhou.crpc.transport.netty;

import java.util.concurrent.TimeUnit;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.util.concurrent.NamedThreadFactory;
import com.github.leeyazhou.crpc.transport.AbstractTransportFactory;
import com.github.leeyazhou.crpc.transport.Client;
import com.github.leeyazhou.crpc.transport.Server;
import com.github.leeyazhou.crpc.transport.ServerHandler;
import com.github.leeyazhou.crpc.transport.factory.ServerFactory;
import com.github.leeyazhou.crpc.transport.netty.handler.NettyClientHandler;
import com.github.leeyazhou.crpc.transport.netty.handler.NettyClientHeartBeatHandler;
import com.github.leeyazhou.crpc.transport.netty.protocol.NettyProtocolDecoder;
import com.github.leeyazhou.crpc.transport.netty.protocol.NettyProtocolEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author leeyazhou
 *
 */
public class NettyTransportFactory extends AbstractTransportFactory {
  private static final EventLoopGroup bussinessGroup = new NioEventLoopGroup(0, new NamedThreadFactory("crpc-client"));

  @Override
  public Server createServer(Configuration configuration, ServerFactory serverFactory) {
    ServerHandler serverHandler = new ServerHandler(serverFactory);
    serverHandler.setConnectionManager(getConnectionManager());
    return new NettyServer(configuration, getConnectionManager(), serverHandler);
  }

  @Override
  public Client createClient(final URL url) {
    Bootstrap bootStrap = new Bootstrap();

    final NettyClient client = new NettyClient(bootStrap, url);
    int connectTimeout = url.getParameter(Constants.REQUEST_TIMEOUT, 3000);

    bootStrap.group(bussinessGroup).channel(NioSocketChannel.class);
    bootStrap.option(ChannelOption.TCP_NODELAY, true);
    bootStrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(32 * 1024, 64 * 1024));
    bootStrap.option(ChannelOption.SO_REUSEADDR, true);
    bootStrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout < 1000 ? 1000 : connectTimeout);
    bootStrap.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      public void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast("decoder", new NettyProtocolDecoder());
        channel.pipeline().addLast("encoder", new NettyProtocolEncoder());
        channel.pipeline().addLast("heartbeat", new NettyClientHeartBeatHandler(client, 0, 0, 3, TimeUnit.SECONDS));
        channel.pipeline().addLast("handler", new NettyClientHandler(url, client, getConnectionManager()));
      }
    });


    return client;
  }

}

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
package com.github.leeyazhou.crpc.transport.netty.handler;

import java.io.IOException;

import com.github.leeyazhou.crpc.protocol.Request;
import com.github.leeyazhou.crpc.protocol.Response;
import com.github.leeyazhou.crpc.protocol.SimpleProtocol;
import com.github.leeyazhou.crpc.protocol.codec.Codecs;
import com.github.leeyazhou.crpc.transport.TransportFactory;
import com.github.leeyazhou.crpc.transport.netty.NettyClient;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.object.MessageType;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 
 * @author <a href="mailto:lee_yazhou@163.com">Yazhou Li</a>
 */
@Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<Response> {

  private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);
  private static final boolean isTraceEnabled = logger.isTraceEnabled();
  private static final Request ping =
      new Request(Codecs.KRYO_CODEC.getId(), SimpleProtocol.PROTOCOL_TYPE, MessageType.MESSAGE_HEARTBEAT);

  private URL url;

  private NettyClient client;
  private final TransportFactory transportFactory = ServiceLoader.load(TransportFactory.class).load();

  public NettyClientHandler(URL url, NettyClient client) {
    this.url = url;
    this.client = client;
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (!(cause instanceof IOException)) {
      logger.error("catch some exception not IOException", cause);
    }
    Class<?> beanType = Class.forName(url.getParameter(Constants.SERVICE_INTERFACE, null));
    transportFactory.removeClient(beanType, client);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
    if (isTraceEnabled) {
      logger
          .trace(
              "receive response from server: " + ctx.channel().remoteAddress() + ", request id is:" + response.getId());
    }
    client.putResponse(response);
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    super.userEventTriggered(ctx, evt);
    if (evt instanceof IdleStateEvent) {
      client.getIdleCount().getAndIncrement();
      if (client.getIdleCount().compareAndSet(3, 0)) {
        logger.warn("client did't receive heartbeat message from server for long time, so close it! from :" + ctx);
        ctx.close().sync();
        return;
      }
      client.getChannel().send(ping, 3000);
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    Class<?> beanType = Class.forName(url.getParameter(Constants.SERVICE_INTERFACE, null));
    transportFactory.removeClient(beanType, client);
    client.connect();
  }

}

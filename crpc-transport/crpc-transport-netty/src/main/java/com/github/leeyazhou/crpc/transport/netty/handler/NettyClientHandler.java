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
import com.github.leeyazhou.crpc.codec.CodecType;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.transport.ChannelManager;
import com.github.leeyazhou.crpc.transport.TransportFactory;
import com.github.leeyazhou.crpc.transport.netty.NettyChannel;
import com.github.leeyazhou.crpc.transport.netty.NettyClient;
import com.github.leeyazhou.crpc.transport.protocol.ProtocolType;
import com.github.leeyazhou.crpc.transport.protocol.message.MessageType;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 
 * @author leeyazhou
 */
@Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<ResponseMessage> {

  private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);
  private static final boolean isTraceEnabled = logger.isTraceEnabled();
  private static final RequestMessage ping = (RequestMessage) new RequestMessage().setCodecType(CodecType.KRYO_CODEC)
      .setProtocolType(ProtocolType.CRPC).setMessageType(MessageType.MESSAGE_HEARTBEAT);
  private final TransportFactory transportFactory = ServiceLoader.load(TransportFactory.class).load();

  private final URL url;
  private final ChannelManager channelManager;

  private final NettyClient client;

  public NettyClientHandler(URL url, NettyClient client, ChannelManager channelManager) {
    this.url = url;
    this.client = client;
    this.channelManager = channelManager;
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (!(cause instanceof IOException)) {
      logger.error("catch some exception not IOException", cause);
    }
    transportFactory.getClientManager().removeClient(client);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage response) throws Exception {
    if (isTraceEnabled) {
      logger
          .trace("receive response from server: " + ctx.channel().remoteAddress() + ", request id is:" + response.id());
    }
    client.receiveResponse(response);
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    super.userEventTriggered(ctx, evt);
    if (evt instanceof IdleStateEvent) {
      client.getIdleCount().getAndIncrement();
      if (client.getIdleCount().compareAndSet(3, 0)) {
        logger
            .warn("client did't receive heartbeat message from server for " + 3 + " times, so close it! from :" + ctx);
        ctx.close().sync();
        return;
      }
      client.getConnection().send(ping, 3000);
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    transportFactory.getClientManager().removeClient(client);
    client.connect();
    channelManager.removeClientChannel(url.getAddress());
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
    channelManager.addClientChannel(new NettyChannel(ctx.channel(), url));
  }
}

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
import com.github.leeyazhou.crpc.transport.TransportFactory;
import com.github.leeyazhou.crpc.transport.connection.ConnectionManager;
import com.github.leeyazhou.crpc.transport.netty.NettyClient;
import com.github.leeyazhou.crpc.transport.netty.NettyConnection;
import com.github.leeyazhou.crpc.transport.protocol.ProtocolType;
import com.github.leeyazhou.crpc.transport.protocol.message.MessageCode;
import com.github.leeyazhou.crpc.transport.protocol.message.MessageType;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;
import com.github.leeyazhou.crpc.transport.protocol.payload.Payload;
import com.github.leeyazhou.crpc.transport.protocol.payload.ResponsePayloadBody;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 
 * @author leeyazhou
 */
@Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<Payload> {

  private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);
  private static final RequestMessage ping =
      (RequestMessage) new RequestMessage().setTimeout(3000).setMessageCode(MessageCode.MESSAGE_HEARTBEAT)
          .setCodecType(CodecType.KRYO_CODEC).setProtocolType(ProtocolType.CRPC).setMessageType(MessageType.REQUEST);
  private final TransportFactory transportFactory = ServiceLoader.load(TransportFactory.class).load();

  private final URL url;
  private final ConnectionManager connectionManager;

  private final NettyClient client;

  public NettyClientHandler(URL url, NettyClient client, ConnectionManager channelManager) {
    this.url = url;
    this.client = client;
    this.connectionManager = channelManager;
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (!(cause instanceof IOException)) {
      logger.error("catch some exception not IOException", cause);
    }
    transportFactory.getClientManager().removeClient(client);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Payload payload) throws Exception {
    if (logger.isDebugEnabled()) {
      logger
          .debug("receive response from server: " + ctx.channel().remoteAddress() + ", request id is:" + payload.id());
    }
    ResponsePayloadBody payloadBody = (ResponsePayloadBody) payload.getPayloadBody();

    ResponseMessage response = new ResponseMessage();
    response.setCodecType(payload.getCodecType());
    response.setHeaders(payload.getHeaders());
    response.setId(payload.id());
    response.setMessageCode(payload.getMessageCode());
    response.setMessageType(payload.getMessageType());
    response.setProtocolType(payload.getProtocolType());

    response.setResponse(payloadBody.getResponse());
    response.setResponseClassName(payloadBody.getResponseClassName());
    response.setError(payloadBody.isError());


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
      client.getConnection().sendRequest(ping);
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    transportFactory.getClientManager().removeClient(client);
    client.connect();
    connectionManager.removeClientConnection(url.getAddress());
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
    connectionManager.addClientConnection(new NettyConnection(ctx.channel(), url));
  }
}

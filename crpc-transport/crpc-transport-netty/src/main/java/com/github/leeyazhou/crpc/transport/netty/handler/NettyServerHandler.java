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
import java.util.concurrent.atomic.AtomicInteger;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.rpc.Handler;
import com.github.leeyazhou.crpc.rpc.Invocation;
import com.github.leeyazhou.crpc.transport.connection.ConnectionManager;
import com.github.leeyazhou.crpc.transport.netty.NettyConnection;
import com.github.leeyazhou.crpc.transport.netty.util.ChannelUtil;
import com.github.leeyazhou.crpc.transport.protocol.payload.Payload;
import com.github.leeyazhou.crpc.transport.protocol.payload.RequestPayloadBody;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author zach
 * @author leeyazhou
 *
 */
@Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<Payload> {

  private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

  private AtomicInteger idleCount = new AtomicInteger(0);
  private final int IDLE_MAX_COUNT = 60;

  private static final boolean DEBUG_ENABLED = logger.isDebugEnabled();
  private final Handler<?> serverHandler;
  private final ConnectionManager connectionManager;

  public NettyServerHandler(Handler<?> serverHandler, ConnectionManager channelManager) {
    this.serverHandler = serverHandler;
    this.connectionManager = channelManager;
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (!(cause instanceof IOException)) {
      logger.error("catch some exception not IOException", cause);
    }
    if (DEBUG_ENABLED) {
      logger.debug("connection reset , address : " + ctx.channel().remoteAddress(), cause);
    }
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final Payload payload) throws Exception {
    idleCount.set(0);

    RequestPayloadBody request = (RequestPayloadBody) payload.getPayloadBody();
    
    Invocation invocation = new Invocation();
    invocation.setArgs(request.getArgs());
    invocation.setArgTypes(request.getArgTypes());
    invocation.setServiceTypeName(request.getServiceTypeName());
    invocation.setMethodName(request.getMethodName());
    invocation.setOneWay(request.isOneWay());
    invocation.setTimeout(request.getTimeout());

    URL url = ChannelUtil.toUrl(ctx);
    invocation.addAttachement("url", url);
    invocation.addAttachement("payload", payload);

    serverHandler.handle(invocation);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
    if (logger.isInfoEnabled()) {
      logger.info("channel active from " + ctx.channel().remoteAddress());
    }
    URL url = ChannelUtil.toUrl(ctx);
    connectionManager.addServerConnection(new NettyConnection(ctx.channel(), url));
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent event = (IdleStateEvent) evt;
      if (event.state() == IdleState.ALL_IDLE && idleCount.incrementAndGet() >= IDLE_MAX_COUNT) {
        if (logger.isInfoEnabled()) {
          logger.info("channel:" + ctx + " is idle for " + idleCount.get() * Constants.DEFAULT_HEARTBEAT_TIMEOUT
              + " second, so close it !");
        }
        idleCount.set(0);
        ctx.channel().close();
      }
    }
  }


  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    URL url = ChannelUtil.toUrl(ctx);
    connectionManager.removeServerChannel(url.getAddress());
  }

}

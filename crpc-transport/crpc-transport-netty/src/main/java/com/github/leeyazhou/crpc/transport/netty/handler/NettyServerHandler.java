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
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.AddressUtil;
import com.github.leeyazhou.crpc.transport.ChannelManager;
import com.github.leeyazhou.crpc.transport.Handler;
import com.github.leeyazhou.crpc.transport.RpcContext;
import com.github.leeyazhou.crpc.transport.factory.ServerFactory;
import com.github.leeyazhou.crpc.transport.netty.NettyChannel;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
public class NettyServerHandler extends SimpleChannelInboundHandler<RequestMessage> {

  private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

  private AtomicInteger idleCount = new AtomicInteger(0);
  private final int IDLE_MAX_COUNT = 60;

  private static final boolean DEBUG_ENABLED = logger.isDebugEnabled();
  private final Handler<?> serverHandler;
  private final ServerFactory serverFactory;
  private final ChannelManager channelManager;

  public NettyServerHandler(Handler<?> serverHandler, ServerFactory serverFactory, ChannelManager channelManager) {
    this.serverHandler = serverHandler;
    this.serverFactory = serverFactory;
    this.channelManager = channelManager;
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
  protected void channelRead0(final ChannelHandlerContext ctx, final RequestMessage request) throws Exception {
    idleCount.set(0);
    serverFactory.getExecutorService().execute(new Runnable() {
      private long beginTime = System.currentTimeMillis();

      @Override
      public void run() {
        try {
          returnResponse(ctx, request, beginTime);
        } catch (Exception e) {
          logger.error("", e);
        }
      }
    });
  }

  private void returnResponse(final ChannelHandlerContext ctx, final RequestMessage request, long beginTime) {
    long invokeTime;
    // already timeout,so not return
    if ((invokeTime = System.currentTimeMillis() - beginTime) >= request.getTimeout() && logger.isWarnEnabled()) {
      logger.warn("timeout(" + request.getTimeout() + "ms < invoketime : " + invokeTime
          + "ms), so give up send response to client, requestId is:" + request.id() + ", client address : "
          + ctx.channel().remoteAddress());
      return;
    }
    RpcContext context = RpcContext.providerContext(request);
    ResponseMessage response = serverHandler.handle(context);
    // already timeout,so not return
    if ((invokeTime = System.currentTimeMillis() - beginTime) >= request.getTimeout() && logger.isWarnEnabled()) {
      logger.warn("timeout(" + request.getTimeout() + "ms < invoketime : " + invokeTime
          + "ms), so give up send response to client, requestId is:" + request.id() + ", client address : "
          + ctx.channel().remoteAddress());
      return;
    }
    ctx.channel().writeAndFlush(response).addListener(new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) throws Exception {
        if (!future.isSuccess()) {
          logger.error("server write response error, request id is: " + request.id(), future.cause());
        }
      }
    });

  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
    if (logger.isInfoEnabled()) {
      logger.info("channel active from " + ctx.channel().remoteAddress());
    }
    InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
    channelManager
        .addServerChannel(new NettyChannel(ctx.channel(), new URL("crpc", address.getHostName(), address.getPort())));
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
    String key = AddressUtil.toAddressString((InetSocketAddress) ctx.channel().remoteAddress());
    channelManager.removeServerChannel(key);
  }

}

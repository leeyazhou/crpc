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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.github.leeyazhou.crpc.transport.Handler;
import com.github.leeyazhou.crpc.transport.RpcContext;
import com.github.leeyazhou.crpc.transport.factory.ServerFactory;
import com.github.leeyazhou.crpc.config.ServerConfig;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.AddressUtil;
import com.github.leeyazhou.crpc.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.protocol.message.ResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author zach
 *
 */
@Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<RequestMessage> {

  private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

  private AtomicInteger idleCount = new AtomicInteger(0);
  private final int IDLE_MAX_COUNT = 60;

  private static final boolean DEBUG_ENABLED = logger.isDebugEnabled();
  private final boolean sync;
  private final Handler<?> serverHandler;
  private final ServerFactory beanFactory;
  private ConcurrentMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();

  public NettyServerHandler(ServerConfig serverConfig, Handler<?> serverHandler, ServerFactory beanFactory) {
    this.sync = serverConfig.isSync();
    this.serverHandler = serverHandler;
    this.beanFactory = beanFactory;
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
    if (this.sync) {
      returnResponse(ctx, request, System.currentTimeMillis());
      return;
    }
    this.beanFactory.getExecutorService().execute(new Runnable() {
      private long beginTime = System.currentTimeMillis();

      @Override
      public void run() {
        returnResponse(ctx, request, beginTime);
      }
    });
  }

  private void returnResponse(final ChannelHandlerContext ctx, final RequestMessage request, long beginTime) {
    long invokeTime;
    // already timeout,so not return
    if ((invokeTime = System.currentTimeMillis() - beginTime) >= request.getTimeout() && logger.isWarnEnabled()) {
      logger.warn("timeout(" + request.getTimeout() + "ms < invoketime : " + invokeTime
          + "ms), so give up send response to client, requestId is:" + request.getId() + ", client address : "
          + ctx.channel().remoteAddress());
      return;
    }
    RpcContext context = RpcContext.providerContext(request);
    ResponseMessage response = serverHandler.handle(context);
    // already timeout,so not return
    if ((invokeTime = System.currentTimeMillis() - beginTime) >= request.getTimeout() && logger.isWarnEnabled()) {
      logger.warn("timeout(" + request.getTimeout() + "ms < invoketime : " + invokeTime
          + "ms), so give up send response to client, requestId is:" + request.getId() + ", client address : "
          + ctx.channel().remoteAddress());
      return;
    }
    ChannelFuture wf = ctx.channel().writeAndFlush(response);
    wf.addListener(new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) throws Exception {
        if (!future.isSuccess()) {
          logger.error("server write response error, request id is: " + request.getId(), future.cause());
        }
      }
    });

  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    super.channelRegistered(ctx);
    if (logger.isInfoEnabled()) {
      logger.info("channel register from " + ctx.channel().remoteAddress());
    }
    channels.putIfAbsent(AddressUtil.toAddressString((InetSocketAddress) ctx.channel().remoteAddress()), ctx.channel());
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

  /**
   * @return the channels
   */
  public ConcurrentMap<String, Channel> getChannels() {
    return channels;
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    String key = AddressUtil.toAddressString((InetSocketAddress) ctx.channel().remoteAddress());
    channels.remove(key);
  }

}

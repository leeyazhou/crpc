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

package com.github.leeyazhou.crpc.transport.netty.handler;

import java.util.concurrent.TimeUnit;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.transport.protocol.message.MessageCode;
import com.github.leeyazhou.crpc.transport.protocol.message.MessageType;
import com.github.leeyazhou.crpc.transport.protocol.payload.Payload;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author leeyazhou
 */
public class NettyServerHeartBeatHandler extends IdleStateHandler {
  private static final Logger logger = LoggerFactory.getLogger(NettyServerHeartBeatHandler.class);

  /**
   * 心跳检测 Creates a new instance firing {@link IdleStateEvent}s.
   *
   * @param readerIdleTimeSeconds an {@link IdleStateEvent} whose state is {@link IdleState#READER_IDLE} will be
   *        triggered when no read was performed for the specified period of time. Specify {@code 0} to disable.
   * @param writerIdleTimeSeconds an {@link IdleStateEvent} whose state is {@link IdleState#WRITER_IDLE} will be
   *        triggered when no write was performed for the specified period of time. Specify {@code 0} to disable.
   * @param allIdleTimeSeconds an {@link IdleStateEvent} whose state is {@link IdleState#ALL_IDLE} will be triggered
   *        when neither read nor write was performed for the specified period of time. Specify {@code 0} to disable.
   */
  public NettyServerHeartBeatHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
    super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
  }

  /**
   * Creates a new instance firing {@link IdleStateEvent}s.
   *
   * @param readerIdleTime an {@link IdleStateEvent} whose state is {@link IdleState#READER_IDLE} will be triggered when
   *        no read was performed for the specified period of time. Specify {@code 0} to disable.
   * @param writerIdleTime an {@link IdleStateEvent} whose state is {@link IdleState#WRITER_IDLE} will be triggered when
   *        no write was performed for the specified period of time. Specify {@code 0} to disable.
   * @param allIdleTime an {@link IdleStateEvent} whose state is {@link IdleState#ALL_IDLE} will be triggered when
   *        neither read nor write was performed for the specified period of time. Specify {@code 0} to disable.
   * @param unit the {@link TimeUnit} of {@code readerIdleTime}, {@code writeIdleTime}, and {@code allIdleTime}
   */
  public NettyServerHeartBeatHandler(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
    super(readerIdleTime, writerIdleTime, allIdleTime, unit);
  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
    if (!(msg instanceof Payload)) {
      super.channelRead(ctx, msg);
      return;
    }

    Payload payload = (Payload) msg;
    if (MessageCode.MESSAGE_COMMON.getCode() == payload.getMessageCode()) {
      super.channelRead(ctx, msg);
      return;
    }

    if (payload.getMessageType() == MessageType.REQUEST.getCode()) {
      Payload response = new Payload();
      response.setCodecType(payload.getCodecType());
      response.setProtocolType(payload.getProtocolType());
      response.setMessageType(MessageType.RESPONSE.getCode());
      response.setMessageCode(MessageCode.MESSAGE_HEARTBEAT.getCode());

      ctx.channel().writeAndFlush(response).addListener(new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
          if (future.isSuccess()) {
            if (logger.isDebugEnabled()) {
              logger.debug("reply heartbeat success, to : " + ctx.channel().remoteAddress());
            }
            return;
          }
          logger.error("reply heartbeat fail, to : " + ctx.channel().remoteAddress(), future.cause());
        }
      });
    } else {
      logger.warn("Message not support：" + msg);
    }
  }
}

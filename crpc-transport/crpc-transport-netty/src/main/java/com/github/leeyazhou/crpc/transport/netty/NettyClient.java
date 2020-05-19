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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.exception.CrpcException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ExceptionUtil;
import com.github.leeyazhou.crpc.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.protocol.message.ResponseMessage;
import com.github.leeyazhou.crpc.transport.AbstractClient;
import com.github.leeyazhou.crpc.transport.Channel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class NettyClient extends AbstractClient {

  private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
  private final AtomicInteger idleCount = new AtomicInteger(0);
  private volatile Channel channel;

  private final Bootstrap bootStrap;

  public NettyClient(Bootstrap bootStrap, URL url) {
    super(url);
    this.bootStrap = bootStrap;
  }

  @Override
  public void doRequest(final RequestMessage request, int timeout) throws Exception {
    getConnection().send(request, timeout).whenComplete(new BiConsumer<Boolean, Throwable>() {

      @Override
      public void accept(Boolean t, Throwable ex) {
        if (ex == null) {
          return;
        }
        ResponseMessage response = new ResponseMessage(request.id(), request.getCodecType(), request.getProtocolType());
        response.setError(Boolean.TRUE);
        response.setResponseClassName(CrpcException.class.getName());
        // response.setException(ExceptionUtil.getErrorMessage(ex));
        response.setResponse(ExceptionUtil.getErrorMessage(ex));
        NettyClient.this.receiveResponse(response);
      }
    });
  }

  @Override
  public boolean connect() {
    final String host = getUrl().getHost();
    final int port = getUrl().getPort();
    ChannelFuture channelFuture = bootStrap.connect(host, port).addListener(new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
          transportFactory.getClientManager().addClient(NettyClient.this);
          logger.info(String.format("Client connect server %s:%s success ! ", host, port));
        } else {
          String msg = String.format("Client connect server %s:%s fail", host, port);
          logger.error(msg, future.cause());
          transportFactory.getExecutorService().execute(new Runnable() {

            @Override
            public void run() {
              try {
                Thread.sleep(getUrl().getParameter(Constants.timeout, 3000));
                connect();
              } catch (Exception e) {
                // do nothing
              }
            }
          });
        }
      }
    }).syncUninterruptibly();
    this.channel = new NettyChannel(channelFuture.channel(), getUrl());
    return channelFuture.awaitUninterruptibly().isSuccess();
  }


  @Override
  public long getSendingBytesSize() {
    return 0;
  }

  @Override
  public Channel getConnection() {
    return this.channel;
  }

  /**
   * @return the idleCount
   */
  public AtomicInteger getIdleCount() {
    return idleCount;
  }


}

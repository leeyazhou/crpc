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

import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.exception.CrpcConnectException;
import com.github.leeyazhou.crpc.core.util.concurrent.Future;
import com.github.leeyazhou.crpc.transport.Channel;
import com.github.leeyazhou.crpc.transport.protocol.message.Message;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * @author leeyazhou
 *
 */
public class NettyChannel implements Channel {
  private final io.netty.channel.Channel channel;
  private final String serviceName;
  private final URL url;

  public NettyChannel(io.netty.channel.Channel channel, URL url) {
    this.channel = channel;
    this.serviceName = url.getParameter(Constants.APPLICATION, null);
    this.url = url;
  }

  @Override
  public Future send(final Message request, final int timeout) {
   final Future ret = new Future();
    final long beginTime = System.currentTimeMillis();
    // requestWrapper.getMessageLen();
    ChannelFuture writeFuture = channel.writeAndFlush(request);
    // use listener to avoid wait for write & thread context switch
    writeFuture.addListener(new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
          ret.setSuccess(true);
          return;
        }
        // String errorMsg = "";
        StringBuilder errorMsg = new StringBuilder();
        long invokeTime = System.currentTimeMillis() - beginTime;
        errorMsg.append("fail to send request to ");
        errorMsg.append(serviceName).append("/").append(url.getHost()).append(":")
            .append(url.getPort()).append(", ");
        errorMsg.append(request.toString());

        // write timeout
        if (invokeTime >= timeout) {
          errorMsg.append(" timeout");
          errorMsg.append(", invokeTime : ").append(invokeTime);
          errorMsg.append(", timeout : ").append(timeout);
        } else if (future.isCancelled()) {
          errorMsg.append(" cancelled by user ");
        } else {
          if (channel.isActive()) {
            // maybe some exception,so close the channel
            channel.close();
          }
        }
        Exception ex = new CrpcConnectException(errorMsg.toString(), future.cause());
        ret.setException(ex);
      }
    });
    return ret;
  }

  @Override
  public boolean isConnected() {
    if (channel == null) {
      return false;
    }
    return channel.isActive();
  }

  @Override
  public String getAddress() {
    return url.getAddress();
  }

}

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
import com.github.leeyazhou.crpc.core.exception.CrpcException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ExceptionUtil;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.protocol.message.ResponseMessage;
import com.github.leeyazhou.crpc.transport.Client;
import com.github.leeyazhou.crpc.transport.Connection;
import com.github.leeyazhou.crpc.transport.TransportFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * @author leeyazhou
 *
 */
public class NettyConnection implements Connection {
  private static final Logger logger = LoggerFactory.getLogger(NettyConnection.class);
  private final io.netty.channel.Channel channel;
  private final Client client;
  private final String serviceName;
  private final TransportFactory transportFactory = ServiceLoader.load(TransportFactory.class).load();
  private final URL url;

  public NettyConnection(Channel channel, Client client, URL url) {
    this.channel = channel;
    this.client = client;
    this.serviceName = url.getParameter(Constants.APPLICATION, null);
    this.url = url;
  }

  @Override
  public void send(final RequestMessage request, final int timeout) {

    final long beginTime = System.currentTimeMillis();
    // requestWrapper.getMessageLen();
    ChannelFuture writeFuture = channel.writeAndFlush(request);
    // use listener to avoid wait for write & thread context switch
    writeFuture.addListener(new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
          return;
        }
        // String errorMsg = "";
        StringBuilder errorMsg = new StringBuilder();
        long invokeTime = System.currentTimeMillis() - beginTime;
        errorMsg.append("fail to send request to ");
        errorMsg.append(serviceName).append("/").append(client.getUrl().getHost()).append(":")
            .append(client.getUrl().getPort()).append(", ");
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
          } else {
            transportFactory.getClientManager().removeClient(client);
          }
        }
        Exception ex = new CrpcConnectException(errorMsg.toString(), future.cause());
        logger.error("", ex);
        ResponseMessage response = new ResponseMessage(request.id(), request.getCodecType(), request.getProtocolType());
        response.setError(Boolean.TRUE);
        response.setResponseClassName(CrpcException.class.getName());
        // response.setException(ExceptionUtil.getErrorMessage(ex));
        response.setResponse(ExceptionUtil.getErrorMessage(ex));
        client.receiveResponse(response);
      }
    });

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

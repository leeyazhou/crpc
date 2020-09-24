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
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.function.Consumer;
import com.github.leeyazhou.crpc.transport.connection.Connection;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;
import com.github.leeyazhou.crpc.transport.protocol.payload.Payload;
import com.github.leeyazhou.crpc.transport.protocol.payload.RequestPayloadBody;
import com.github.leeyazhou.crpc.transport.protocol.payload.ResponsePayloadBody;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * @author leeyazhou
 *
 */
public class NettyConnection implements Connection {
  private static final Logger logger = LoggerFactory.getLogger(NettyConnection.class);
  private final io.netty.channel.Channel channel;
  private final String serviceName;
  private final URL url;

  public NettyConnection(io.netty.channel.Channel channel, URL url) {
    this.channel = channel;
    this.serviceName = url.getParameter(Constants.APPLICATION, null);
    this.url = url;
  }

  @Override
  public void sendRequest(RequestMessage request) {
    sendRequest(request, new Consumer<Boolean>() {

      @Override
      public void onError(Throwable throwable) {

      }

      @Override
      public void accept(Boolean t) {

      }
    });
  }

  @Override
  public void sendRequest(final RequestMessage request, final Consumer<Boolean> consumer) {
    final long timeout = request.getTimeout();
    final long beginTime = System.currentTimeMillis();


    Payload payLoad = new Payload();
    payLoad.setId(request.id());
    payLoad.setProtocolType(request.getProtocolType());
    payLoad.setMessageCode(request.getMessageCode());
    payLoad.setMessageType(request.getMessageType());
    payLoad.setHeaders(request.getHeaders());
    payLoad.setCodecType(request.getCodecType());

    RequestPayloadBody body = new RequestPayloadBody();
    payLoad.setPayloadBody(body);
    body.setArgs(request.getArgs());
    body.setArgTypes(request.getArgTypes());
    body.setTimeout(request.getTimeout());
    body.setMethodName(request.getMethodName());
    body.setServiceTypeName(request.getServiceTypeName());
    body.setOneWay(request.isOneWay());



    ChannelFuture writeFuture = channel.writeAndFlush(payLoad);
    // use listener to avoid wait for write & thread context switch
    writeFuture.addListener(new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
          consumer.accept(true);
          return;
        }
        // String errorMsg = "";
        StringBuilder errorMsg = new StringBuilder();
        long invokeTime = System.currentTimeMillis() - beginTime;
        errorMsg.append("fail to send request to ");
        errorMsg.append(serviceName).append("/").append(url.getHost()).append(":").append(url.getPort()).append(", ");
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
        consumer.onError(ex);
      }
    });
  }

  @Override
  public void sendResponse(ResponseMessage request) {
    sendResponse(request, new Consumer<Boolean>() {

      @Override
      public void accept(Boolean t) {

      }

      @Override
      public void onError(Throwable throwable) {

      }
    });
  }

  @Override
  public void sendResponse(final ResponseMessage response, final Consumer<Boolean> consumer) {
    Payload payLoad = new Payload();
    payLoad.setId(response.id());
    payLoad.setProtocolType(response.getProtocolType());
    payLoad.setMessageCode(response.getMessageCode());
    payLoad.setMessageType(response.getMessageType());
    payLoad.setHeaders(response.getHeaders());
    payLoad.setCodecType(response.getCodecType());

    ResponsePayloadBody body = new ResponsePayloadBody();
    payLoad.setPayloadBody(body);
    body.setError(response.isError());
    body.setResponse(response.getResponse());
    body.setResponseClassName(response.getResponseClassName());

    this.channel.writeAndFlush(payLoad).addListener(new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
          consumer.accept(true);
        } else {
          consumer.onError(future.cause());
          logger.error("server write response error, id : " + response.id(), future.cause());
        }
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

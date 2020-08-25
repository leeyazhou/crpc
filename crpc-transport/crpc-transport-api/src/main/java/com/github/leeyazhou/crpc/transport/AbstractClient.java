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
package com.github.leeyazhou.crpc.transport;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.exception.CrpcException;
import com.github.leeyazhou.crpc.core.exception.TimeoutException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

public abstract class AbstractClient implements Client {
  private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);
  private static final boolean isDebugEnabled = logger.isDebugEnabled();
  private static final long PRINT_CONSUME_MINTIME = Long.parseLong(System.getProperty("rpc.print.consumetime", "0"));
  protected static ConcurrentMap<Integer, RpcResult> responses = new ConcurrentHashMap<Integer, RpcResult>();
  protected final TransportFactory transportFactory = ServiceLoader.load(TransportFactory.class).load();
  protected final URL url;

  public AbstractClient(URL url) {
    this.url = url;
  }

  @Override
  public ResponseMessage request(RequestMessage request) {
    validate(request);
    long beginTime = System.currentTimeMillis();
    final RpcResult rpcResult = new RpcResult();
    responses.putIfAbsent(request.id(), rpcResult);
    try {
      if (isDebugEnabled) {
        logger.debug("client ready to send message, request id: " + request.id());
      }
      transportFactory.checkSendLimit();
      doRequest(request, request.getTimeout());
      if (isDebugEnabled) {
        logger.debug("client write message to send buffer,wait for response,request id: " + request.id());
      }
    } catch (Throwable err) {
      responses.remove(request.id());
      final String msg = "send request to os sendbuffer error, " + request;
      logger.error(msg, err);
      throw new CrpcException(msg, err);
    }
    ResponseMessage response = null;
    try {
      response =
          rpcResult.getResponse(request.getTimeout() - (System.currentTimeMillis() - beginTime), TimeUnit.MILLISECONDS);
    } catch (Exception err) {
      // logger.error("Get response error", err);
      throw new CrpcException("Get response error, " + request, err);
    } finally {
      responses.remove(request.id());
    }

    if (PRINT_CONSUME_MINTIME > 0 && isDebugEnabled) {
      long consumeTime = System.currentTimeMillis() - beginTime;
      if (consumeTime > PRINT_CONSUME_MINTIME) {
        if (logger.isInfoEnabled()) {
          logger.info(
              String.format("client invokeSync consume time: %s ms, server is: %s:%s request id is:%s, queue : %s",
                  consumeTime, this.url.getHost(), this.url.getPort(), request.id(), responses.size()));
        }
      }
    }
    if (response == null) {
      String errorMsg = "receive response timeout(" + request.getTimeout() + " ms),server is: " + this.url.getHost()
          + ":" + this.url.getPort() + " request id is:" + request.id() + ", queue : " + responses.size();
      throw new TimeoutException(errorMsg);
    } else if (response.isError()) {
      StringBuilder sb = new StringBuilder("server error, server is: [");
      sb.append(this.url.getHost()).append(":").append(this.url.getPort());
      sb.append("], request id is:").append(request.id());
      if (response.getResponse() != null) {
        sb.append(", ").append(response.getResponse());
      }
      throw new CrpcException(sb.toString());
    }
    return response;
  }

  private void validate(RequestMessage requestMessage) {
    if (requestMessage == null) {
      return;
    }
    if (requestMessage.getTimeout() == 0) {
      requestMessage.setTimeout(3000);
    }
  }

  /**
   * receive response
   */
  @Override
  public void receiveResponse(ResponseMessage response) {
    RpcResult rpcResult = responses.get(response.id());
    if (rpcResult != null) {
      rpcResult.setResponse(response);
    } else {
      logger.warn("give up the response because queue is null. id :" + response.id());
    }
  }

  @Override
  public URL getUrl() {
    return url;
  }

  /**
   * send request to os sendbuffer,must ensure write result
   * 
   * @param request {@link RequestMessage}
   * @param timeout timeout
   * @throws Exception any exception
   */
  public abstract void doRequest(RequestMessage request, int timeout) throws Exception;

  /**
   * result holder
   * 
   * @author leeyazhou
   *
   */
  class RpcResult {
    private final CountDownLatch countDownLatch;
    private ResponseMessage response;

    public RpcResult() {
      countDownLatch = new CountDownLatch(1);
    }

    public ResponseMessage getResponse(long timeout, TimeUnit unit) throws InterruptedException {
      countDownLatch.await(timeout, unit);
      return response;
    }

    public void setResponse(ResponseMessage response) {
      this.response = response;
      countDownLatch.countDown();
    }

  }
}

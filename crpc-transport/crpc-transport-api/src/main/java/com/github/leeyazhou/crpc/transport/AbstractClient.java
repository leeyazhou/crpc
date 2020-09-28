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
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.exception.CrpcException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

public abstract class AbstractClient implements Client {
  private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);
  private static final boolean isDebugEnabled = logger.isDebugEnabled();
  protected static ConcurrentMap<Integer, RpcResult> responses = new ConcurrentHashMap<Integer, RpcResult>();
  protected final TransportFactory transportFactory = ServiceLoader.load(TransportFactory.class).load();
  protected final URL url;

  public AbstractClient(URL url) {
    this.url = url;
  }

  @Override
  public RpcResult request(RequestMessage request) {
    validate(request);
    final RpcResult rpcResult = new RpcResult().setId(request.id()).start();
    responses.putIfAbsent(request.id(), rpcResult);
    try {
      if (isDebugEnabled) {
        logger.debug("client ready to send request message, request id: " + request.id());
      }
      transportFactory.checkSendLimit();
      doRequest(request);
      if (isDebugEnabled) {
        logger.debug("client write message to send buffer, wait for response,request id: " + request.id());
      }
    } catch (Throwable err) {
      responses.remove(request.id());
      final String msg = "send request to os sendbuffer error, " + request;
      logger.error(msg, err);
      throw CrpcException.wrap(msg, err);
    }

    return rpcResult;
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
    RpcResult rpcResult = responses.remove(response.id());
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
   * @throws Exception any exception
   */
  protected abstract void doRequest(RequestMessage request) throws Exception;

}

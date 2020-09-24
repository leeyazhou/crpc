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
package com.github.leeyazhou.crpc.transport;

import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.exception.ServiceNotFoundException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.rpc.Handler;
import com.github.leeyazhou.crpc.rpc.Invocation;
import com.github.leeyazhou.crpc.rpc.Result;
import com.github.leeyazhou.crpc.transport.connection.Connection;
import com.github.leeyazhou.crpc.transport.connection.ConnectionManager;
import com.github.leeyazhou.crpc.transport.factory.ServerFactory;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

/**
 * @author leeyazhou
 *
 */
public class ServerHandler implements Handler<ServerHandler> {
  private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
  private ServerFactory serverFactory;
  private ConnectionManager connectionManager;

  public ServerHandler(ServerFactory serverFactory) {
    this.serverFactory = serverFactory;
  }

  @Override
  public Class<ServerHandler> getHandlerType() {
    return ServerHandler.class;
  }

  @Override
  public Result handle(final Invocation context) {
    serverFactory.getExecutorService().execute(new Runnable() {
      private long beginTime = System.currentTimeMillis();

      @Override
      public void run() {
        try {
          returnResponse(context, beginTime);
        } catch (Exception e) {
          logger.error("", e);
        }
      }
    });
    return null;
  }

  private void returnResponse(Invocation context, long beginTime) {
    final URL url = (URL) context.getAttachement("url");
    final Connection connection = connectionManager.getServerConnection(url.getAddress());

    long invokeTime = System.currentTimeMillis() - beginTime;
    // already timeout,so not return
    if (invokeTime >= context.getTimeout() && logger.isWarnEnabled()) {
      logger.warn("timeout(" + context.getTimeout() + "ms < invoketime : " + invokeTime
          + "ms), so give up send response to client, client address : " + connection.getAddress());
      return;
    }
    Result response = doHandle(context);
    // already timeout,so not return
    invokeTime = System.currentTimeMillis() - beginTime;
    if (invokeTime >= context.getTimeout() && logger.isWarnEnabled()) {
      logger.warn("timeout(" + context.getTimeout() + "ms < invoketime : " + invokeTime
          + "ms), so give up send response to client, client address : " + connection.getAddress());
      return;
    }
    if (!context.isOneWay()) {
      connection.sendResponse((ResponseMessage) response.getValue());
    }
  }

  public Result doHandle(Invocation context) {
    final String targetInstanceName = context.getServiceTypeName();
    final Handler<?> processor = serverFactory.getServiceHandler(targetInstanceName);
    if (processor == null) {
      throw new ServiceNotFoundException(targetInstanceName);
    }
    return processor.handle(context);
  }

  public void setConnectionManager(ConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
  }
}

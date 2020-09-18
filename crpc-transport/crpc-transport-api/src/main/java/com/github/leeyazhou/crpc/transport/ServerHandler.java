/**
 * 
 */
package com.github.leeyazhou.crpc.transport;

import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.exception.ServiceNotFoundException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.transport.factory.ServerFactory;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

/**
 * @author leeyazhou
 *
 */
public class ServerHandler implements Handler<ResponseMessage> {
  private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
  private ServerFactory serverFactory;
  private ConnectionManager connectionManager;

  public ServerHandler(ServerFactory serverFactory) {
    this.serverFactory = serverFactory;
  }

  @Override
  public Class<ResponseMessage> getHandlerType() {
    return ResponseMessage.class;
  }

  @Override
  public ResponseMessage handle(final RpcContext context) {
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

  private void returnResponse(RpcContext context, long beginTime) {
    final RequestMessage request = context.getRequest();
    final URL url = (URL) context.getAttachement("url");
    final Connection connection = connectionManager.getServerConnection(url.getAddress());

    long invokeTime = System.currentTimeMillis() - beginTime;
    // already timeout,so not return
    if (invokeTime >= request.getTimeout() && logger.isWarnEnabled()) {
      logger.warn("timeout(" + request.getTimeout() + "ms < invoketime : " + invokeTime
          + "ms), so give up send response to client, id :" + request.id() + ", client address : "
          + connection.getAddress());
      return;
    }
    ResponseMessage response = doHandle(context);
    // already timeout,so not return
    invokeTime = System.currentTimeMillis() - beginTime;
    if (invokeTime >= request.getTimeout() && logger.isWarnEnabled()) {
      logger.warn("timeout(" + request.getTimeout() + "ms < invoketime : " + invokeTime
          + "ms), so give up send response to client, id :" + request.id() + ", client address : "
          + connection.getAddress());
      return;
    }
    connection.sendResponse(response);

  }

  public ResponseMessage doHandle(RpcContext context) {
    final String targetInstanceName = context.getRequest().getServiceTypeName();
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

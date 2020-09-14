/**
 * 
 */
package com.github.leeyazhou.crpc.transport;

import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

/**
 * @author leeyazhou
 *
 */
public class ServerHandler implements Handler<Object> {

  @Override
  public Class<Object> getHandlerType() {
    return Object.class;
  }

  @Override
  public ResponseMessage handle(RpcContext context) {
    return null;
  }

}

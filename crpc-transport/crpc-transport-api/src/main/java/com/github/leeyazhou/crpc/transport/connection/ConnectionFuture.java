/**
 * 
 */
package com.github.leeyazhou.crpc.transport.connection;

/**
 * @author leeyazhou
 *
 */
public class ConnectionFuture {

  private ConnectionListener listener;

  public ConnectionFuture addListener(ConnectionListener listener) {
    this.listener = listener;
    return this;
  }
}

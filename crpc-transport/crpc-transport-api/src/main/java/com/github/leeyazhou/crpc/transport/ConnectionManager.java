/**
 * 
 */
package com.github.leeyazhou.crpc.transport;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author leeyazhou
 *
 */
public class ConnectionManager {

  private final ConcurrentMap<String, Connection> connectionCache = new ConcurrentHashMap<String, Connection>();

  public Connection getConnection(String address) {
    return connectionCache.get(address);
  }

  public boolean addConnection(Connection connection) {
    if (connection == null) {
      return false;
    }

    Connection temp = connectionCache.putIfAbsent(connection.getAddress(), connection);
    if (temp != null) {
      return false;
    }
    return true;

  }

  public void removeConnection(final String address) {
    connectionCache.remove(address);
  }

}

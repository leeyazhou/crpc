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
package com.github.leeyazhou.crpc.transport.connection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.github.leeyazhou.crpc.codec.CodecType;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.transport.protocol.ProtocolType;
import com.github.leeyazhou.crpc.transport.protocol.message.MessageCode;
import com.github.leeyazhou.crpc.transport.protocol.message.MessageType;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

/**
 * @author leeyazhou
 *
 */
public class ConnectionManager {
  private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
  private final ConcurrentMap<String, Connection> clientChannelCache = new ConcurrentHashMap<String, Connection>();
  private final ConcurrentMap<String, Connection> serverChannelCache = new ConcurrentHashMap<String, Connection>();

  public Connection getClientConnection(String address) {
    return clientChannelCache.get(address);
  }

  public boolean addClientConnection(Connection connection) {
    if (connection == null) {
      return false;
    }

    Connection temp = clientChannelCache.putIfAbsent(connection.getAddress(), connection);
    if (temp != null) {
      return false;
    }
    return true;

  }

  public void removeClientConnection(final String address) {
    clientChannelCache.remove(address);
  }

  public Connection getServerConnection(String address) {
    return serverChannelCache.get(address);
  }

  public boolean addServerConnection(Connection connection) {
    if (connection == null) {
      return false;
    }

    Connection temp = serverChannelCache.putIfAbsent(connection.getAddress(), connection);
    if (temp != null) {
      return false;
    }
    return true;

  }

  /**
   * 
   * @param address address, for example: crpc://127.0.0.1:25001
   */
  public void removeServerChannel(final String address) {
    serverChannelCache.remove(address);
  }

  public void serverClosed() {
    // logger.info("关闭通道：" + channels);
    ResponseMessage message = new ResponseMessage();
    message.setCodecType(CodecType.JDK_CODEC).setProtocolType(ProtocolType.CRPC).setMessageType(MessageType.RESPONSE);
    message.setMessageCode(MessageCode.MESSAGE_SHUTDOWN);
    for (Map.Entry<String, Connection> entry : serverChannelCache.entrySet()) {
      logger.info("通知关闭通道：" + entry.getKey() + ", channel : " + entry.getValue());
      entry.getValue().sendResponse(message);
    }
    serverChannelCache.clear();
  }

}

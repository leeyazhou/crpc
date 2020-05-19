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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.github.leeyazhou.crpc.codec.CodecType;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.transport.protocol.SimpleProtocol;
import com.github.leeyazhou.crpc.transport.protocol.message.MessageType;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

/**
 * @author leeyazhou
 *
 */
public class ChannelManager {
  private static final Logger logger = LoggerFactory.getLogger(ChannelManager.class);
  private final ConcurrentMap<String, Channel> clientChannelCache = new ConcurrentHashMap<String, Channel>();
  private final ConcurrentMap<String, Channel> serverChannelCache = new ConcurrentHashMap<String, Channel>();

  public Channel getClientChannel(String address) {
    return clientChannelCache.get(address);
  }

  public boolean addClientChannel(Channel channel) {
    if (channel == null) {
      return false;
    }

    Channel temp = clientChannelCache.putIfAbsent(channel.getAddress(), channel);
    if (temp != null) {
      return false;
    }
    return true;

  }

  public void removeClientChannel(final String address) {
    clientChannelCache.remove(address);
  }

  public Channel getServerChannel(String address) {
    return serverChannelCache.get(address);
  }

  public boolean addServerChannel(Channel channel) {
    if (channel == null) {
      return false;
    }

    Channel temp = serverChannelCache.putIfAbsent(channel.getAddress(), channel);
    if (temp != null) {
      return false;
    }
    return true;

  }

  public void removeServerChannel(final String address) {
    serverChannelCache.remove(address);
  }

  public void serverClosed() {
    // logger.info("关闭通道：" + channels);
    ResponseMessage response =
        new ResponseMessage(0, CodecType.JDK_CODEC.getId(), SimpleProtocol.PROTOCOL_TYPE, MessageType.MESSAGE_SHUTDOWN);
    for (Map.Entry<String, Channel> entry : serverChannelCache.entrySet()) {
      logger.info("通知关闭通道：" + entry.getKey() + ", channel : " + entry.getValue());
      entry.getValue().send(response, 3000);
    }
    serverChannelCache.clear();
  }

}

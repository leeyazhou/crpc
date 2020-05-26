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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.github.leeyazhou.crpc.config.ReferConfig;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.exception.ServiceNotFoundException;

/**
 * @author leeyazhou
 *
 */
public class ClientManager {
  protected static final ConcurrentMap<String, Client> clientCache = new ConcurrentHashMap<String, Client>();

  private final TransportFactory transportFactory;

  public ClientManager(TransportFactory transportFactory) {
    this.transportFactory = transportFactory;
  }

  /**
   * get client,create clientNums connections to targetIP:targetPort(or your custom key)
   * 
   * @param <T> t
   * @param serviceConfig {@link ServiceGroupConfig}
   * @return list of {@link Client}
   * @throws Exception any exception
   */
  public <T> List<Client> get(final ReferConfig<T> referConfig) throws Exception {
    try {
      List<Client> clients = new ArrayList<Client>();
      for (URL url : referConfig.getUrls()) {
        clients.add(getOrCreateClient(url));
      }
      if (clients == null || clients.isEmpty()) {
        throw new ServiceNotFoundException("serviceName : " + referConfig.getApplicationConfig().getName());
      }
      return clients;
    } catch (Exception err) {
      throw err;
    }
  }

  /**
   * remove error client
   * 
   * @param client {@link Client}
   */
  public void removeClient(Client client) {
    clientCache.remove(toKey(client.getUrl()));
  }

  /**
   * remove client from cache
   * 
   * @param client client
   * @return boolean
   */
  public boolean addClient(Client client) {
    clientCache.putIfAbsent(toKey(client.getUrl()), client);
    return Boolean.TRUE;

  }

  public Client getOrCreateClient(URL url) {
    final String key = toKey(url);
    Client client = clientCache.get(key);
    if (client == null) {
      client = transportFactory.createClient(url);
      Client t = clientCache.putIfAbsent(key, client);
      if (t != null) {
        client = t;
      } else {
        client.connect();
      }
    }
    return client;
  }

  public long getSendingBytesSize() throws Exception {
    long sendingBytesSize = 0;
    for (Client client : clientCache.values()) {
      sendingBytesSize += client.getSendingBytesSize();
    }
    return sendingBytesSize;
  }

  private String toKey(URL url) {
    return url.getHost() + ":" + url.getPort();
  }
}

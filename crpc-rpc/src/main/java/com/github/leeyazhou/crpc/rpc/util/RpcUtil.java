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
package com.github.leeyazhou.crpc.rpc.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.ServiceGroupConfig;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.rpc.ProxyFactory;
import com.github.leeyazhou.crpc.transport.Server;
import com.github.leeyazhou.crpc.transport.TransportFactory;
import com.github.leeyazhou.crpc.transport.factory.ServerFactory;
import com.github.leeyazhou.crpc.transport.factory.ServiceHandler;

/**
 * @author leeyazhou
 *
 */
public class RpcUtil {

  private static final Logger logger = LoggerFactory.getLogger(RpcUtil.class);
  private static final TransportFactory transportFactory = ServiceLoader.load(TransportFactory.class).load();
  private static final ConcurrentMap<String, Server> servers = new ConcurrentHashMap<String, Server>();

  public static <T> void export(Configuration configuration, ServiceHandler<T> servsiceHandler,
      ServerFactory beanFactory) {
    logger.info("export : " + configuration);
    final String serverKey = configuration.getProtocolConfig().getAddress();
    Server server = servers.get(serverKey);
    if (server != null) {
      return;
    }
    synchronized (servers) {
      if ((server = servers.get(serverKey)) == null) {
        server = transportFactory.createServer(configuration, beanFactory);
        Server t = servers.putIfAbsent(serverKey, server);
        if (t == null) {
          server.start();
        }
      }
    }

  }

  public static <T> void unexport(Configuration configuration, ServiceHandler<T> serviceHandler) {
    logger.info("unexport : " + configuration.getProtocolConfig());
    final String serverKey = configuration.getProtocolConfig().getAddress();
    Server server = servers.remove(serverKey);
    if (server != null) {
      server.stop();
    }
  }

  public static <T> T refer(ServiceGroupConfig serviceGroupConfig, Class<T> objectType) {
    T ref = ServiceLoader.load(ProxyFactory.class).load().getProxy(objectType, serviceGroupConfig);
    return ref;
  }

  public static void unrefer() {

  }
}

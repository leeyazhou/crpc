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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import com.github.leeyazhou.crpc.config.crpc.Configuration;
import com.github.leeyazhou.crpc.config.crpc.ServiceConfig;
import com.github.leeyazhou.crpc.registry.NotifyListener;
import com.github.leeyazhou.crpc.registry.Registry;
import com.github.leeyazhou.crpc.registry.RegistryFactory;
import com.github.leeyazhou.crpc.transport.loadbalance.LoadBalanceType;
import com.github.leeyazhou.crpc.transport.loadbalance.RandomLoadBalance;
import com.github.leeyazhou.crpc.transport.loadbalance.RoundRobinLoadBalance;
import com.github.leeyazhou.crpc.transport.object.SendLimitPolicy;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.concurrent.Executors;
import com.github.leeyazhou.crpc.core.concurrent.SimpleNamedThreadFactory;
import com.github.leeyazhou.crpc.core.exception.CrpcException;
import com.github.leeyazhou.crpc.core.exception.ServiceNotFoundException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;

/**
 * @author leeyazhou
 */
public abstract class AbstractTransportFactory implements TransportFactory, NotifyListener {
  private static final Logger logger = LoggerFactory.getLogger(AbstractTransportFactory.class);

  protected static final ConcurrentMap<String, Client> clientsMap = new ConcurrentHashMap<String, Client>();
  private static boolean isSendLimitEnabled = false;
  // Cache client
  private static final ConcurrentMap<Class<?>, List<Client>> cachedClients = new ConcurrentHashMap<Class<?>, List<Client>>();

  static Configuration configuration;
  static final String location = "crpc.xml";

  private static final Map<String, LoadBalance> loadBalances = new HashMap<String, LoadBalance>();
  private static final LoadBalance loadBalanceDefault = new RoundRobinLoadBalance();

  protected static final ExecutorService executorService = Executors
      .newCachedThreadPool(new SimpleNamedThreadFactory("crpc"), 0);

  protected AbstractTransportFactory() {
  }

  @Override
  public void enableSendLimit() {
    isSendLimitEnabled = true;
  }

  /**
   * check if sending bytes size exceed limit threshold
   */
  @Override
  public void checkSendLimit() throws Exception {
    if (!isSendLimitEnabled) {
      return;
    }
    long threshold = javaHeapSize * sendLimitPercent / 100;
    long sendingBytesSize = getSendingBytesSize();
    if (sendingBytesSize >= threshold) {
      if (sendLimitPolicy == SendLimitPolicy.REJECT) {
        throw new CrpcException(
            "sending bytes size exceed threshold,size: " + sendingBytesSize + ", threshold: " + threshold);
      } else {
        Thread.sleep(1000);
        sendingBytesSize = getSendingBytesSize();
        if (sendingBytesSize >= threshold) {
          throw new CrpcException(
              "sending bytes size exceed threshold,size: " + sendingBytesSize + ", threshold: " + threshold);
        }
      }
    }
  }

  /**
   * 
   * @return
   * @throws Exception
   */
  private long getSendingBytesSize() throws Exception {
    long sendingBytesSize = 0;
    for (List<Client> clientList : cachedClients.values()) {
      for (Client client : clientList) {
        sendingBytesSize += client.getSendingBytesSize();
      }
    }
    return sendingBytesSize;
  }

  @Override
  public synchronized <T> void initService(final ServiceConfig<T> serviceConfig) {
    for (URL registryConfig : serviceConfig.getRegistries()) {
      RegistryFactory registryFactory = ServiceLoader.load(RegistryFactory.class)
          .load(registryConfig.getRegistryType());
      registryFactory.createRegistry(registryConfig);
    }

    // 从注册中心获取服务提供者
    Map<String, URL> providers = new HashMap<String, URL>();
    List<RegistryFactory> registryFactories = ServiceLoader.load(RegistryFactory.class).loads();

    for (RegistryFactory registryFactory : registryFactories) {
      List<Registry> registries = registryFactory.getRegistries();
      for (Registry registry : registries) {
        logger.info("client 获取注册中心:" + registry);
        List<URL> urls = registry.getProviders(null);
        if (urls == null) {
          continue;
        }
        for (URL url : urls) {
          String beanType = url.getParameter(Constants.SERVICE_INTERFACE, null);
          if (serviceConfig.getInterfaceClass().getName().equals(beanType)) {
            String key = url.getHost() + ":" + url.getPort();
            providers.put(key, url);
            registry.subscribe(url, this);// 订阅
          }
        }
      }
    }
    for (URL server : serviceConfig.getUrls()) {
      String key = server.getHost() + ":" + server.getPort();
      providers.put(key, server);
    }

    List<Client> clients = new ArrayList<Client>(providers.size());
    for (URL provider : providers.values()) {
      clients.add(getOrCreateClient(provider));
    }
    logger.info("find " + clients.size() + " providers of " + serviceConfig.getName());
    cachedClients.putIfAbsent(serviceConfig.getInterfaceClass(), clients);
  }

  @Override
  public <T> List<Client> get(final ServiceConfig<T> serviceConfig) throws Exception {
    try {
      List<Client> clients = cachedClients.get(serviceConfig.getInterfaceClass());
      if (clients == null || clients.isEmpty()) {
        throw new ServiceNotFoundException("serviceName : " + serviceConfig.getName());
      }
      return clients;
    } catch (Exception err) {
      cachedClients.remove(serviceConfig.getInterfaceClass());
      throw err;
    }
  }

  @Override
  public void removeClient(Class<?> beanType, Client client) {
    try {
      final String key = client.getUrl().getHost() + ":" + client.getUrl().getPort();
      clientsMap.remove(key);
      for (Map.Entry<Class<?>, List<Client>> entry : cachedClients.entrySet()) {
        List<Client> clients = entry.getValue();
        if (clients.remove(client) && clients.isEmpty()) {
          cachedClients.remove(beanType);
        }

      }
    } catch (Exception err) {
      logger.error("removeClient error ", err);
    }
  }

  @Override
  public boolean addClient(Class<?> beanType, final Client client) {
    try {
      List<Client> clients = cachedClients.get(beanType);
      if (clients == null) {
        clients = new ArrayList<Client>();
        cachedClients.putIfAbsent(beanType, clients);
      }
      if (!clients.contains(client)) {
        clients.add(client);
        final String key = client.getUrl().getHost() + ":" + client.getUrl().getPort();
        Client temp = clientsMap.putIfAbsent(key, client);
        if (temp != null) {
          logger.warn("fail to cache client : " + client);
        }
      }
      if (logger.isInfoEnabled()) {
        logger.info("addClient success, serviceName:" + beanType + ", client : " + client);
      }
    } catch (Throwable err) {
      logger.error("addClient fail, serviceName:" + beanType, err);
      return Boolean.FALSE;
    }
    return Boolean.TRUE;
  }

  @Override
  public synchronized Configuration getConfiguration() {
    return configuration;
  }

  public LoadBalance getLoadBalance(String loadBalanceStr) {
    if (loadBalanceStr == null) {
      return loadBalanceDefault;
    }
    LoadBalance balance = loadBalances.get(loadBalanceStr);
    if (balance != null) {
      return balance;
    }
    LoadBalanceType type = LoadBalanceType.valueOf(loadBalanceStr);
    switch (type) {
    case RANDOM:
      balance = new RandomLoadBalance();
      break;
    case ROUND_ROBIN:
      balance = new RoundRobinLoadBalance();
      break;
    default:
      balance = new RoundRobinLoadBalance();
    }
    loadBalances.put(loadBalanceStr, balance);
    if (logger.isInfoEnabled()) {
      logger.info(loadBalanceStr + " : " + balance);
    }
    return balance;
  }

  @Override
  public void notify(List<URL> urls) {
    if (urls == null || urls.isEmpty()) {
      return;
    }
    for (URL url : urls) {
      try {
        String beanTypeStr = url.getParameter(Constants.SERVICE_INTERFACE, null);
        Class<?> beanType = Class.forName(beanTypeStr);
        addClient(beanType, getOrCreateClient(url));
      } catch (Exception e) {
        logger.error("notify fail", e);
      }
    }
  }

  /**
   * ExecutorService
   * 
   * @return {@link ExecutorService}
   */
  public ExecutorService getExecutorService() {
    return executorService;
  }

  private Client getOrCreateClient(URL url) {
    final String key = url.getHost() + ":" + url.getPort();
    Client client = clientsMap.get(key);
    if (client == null) {
      client = createClient(url);
      clientsMap.put(key, client);
    }
    return client;
  }
}

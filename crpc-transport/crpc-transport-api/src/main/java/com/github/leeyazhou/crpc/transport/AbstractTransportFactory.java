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
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.RegistryConfig;
import com.github.leeyazhou.crpc.config.ServiceConfig;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.exception.CrpcException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.registry.NotifyListener;
import com.github.leeyazhou.crpc.registry.Registry;
import com.github.leeyazhou.crpc.registry.RegistryFactory;
import com.github.leeyazhou.crpc.transport.loadbalance.LoadBalanceType;
import com.github.leeyazhou.crpc.transport.loadbalance.RandomLoadBalance;
import com.github.leeyazhou.crpc.transport.loadbalance.RoundRobinLoadBalance;
import com.github.leeyazhou.crpc.transport.object.SendLimitPolicy;

/**
 * @author leeyazhou
 */
public abstract class AbstractTransportFactory implements TransportFactory, NotifyListener {
  private static final Logger logger = LoggerFactory.getLogger(AbstractTransportFactory.class);
  private boolean isSendLimitEnabled = false;
  // Cache client
  private final ChannelManager channelManager = new ChannelManager();
  private final ClientManager clientManager = new ClientManager(this);

  protected Configuration configuration;

  private final Map<String, LoadBalance> loadBalances = new HashMap<String, LoadBalance>();
  private final LoadBalance loadBalanceDefault = new RoundRobinLoadBalance();


  protected AbstractTransportFactory() {}

  @Override
  public void enableSendLimit() {
    this.isSendLimitEnabled = true;
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
    long sendingBytesSize = getClientManager().getSendingBytesSize();
    if (sendingBytesSize >= threshold) {
      if (sendLimitPolicy == SendLimitPolicy.REJECT) {
        throw new CrpcException(
            "sending bytes size exceed threshold,size: " + sendingBytesSize + ", threshold: " + threshold);
      } else {
        Thread.sleep(1000);
        sendingBytesSize = getClientManager().getSendingBytesSize();
        if (sendingBytesSize >= threshold) {
          throw new CrpcException(
              "sending bytes size exceed threshold,size: " + sendingBytesSize + ", threshold: " + threshold);
        }
      }
    }
  }


  @Override
  public ClientManager getClientManager() {
    return clientManager;
  }

  @Override
  public synchronized <T> void initService(final ServiceConfig<T> serviceConfig) {
    for (RegistryConfig registryConfig : serviceConfig.getRegistryConfigs()) {
      RegistryFactory registryFactory =
          ServiceLoader.load(RegistryFactory.class).load(registryConfig.toURL().getProtocol());
      registryFactory.createRegistry(registryConfig.toURL());
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
      getClientManager().getOrCreateClient(provider);
    }
    logger.info("find " + clients.size() + " providers for " + serviceConfig.getName());
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
        clientManager.getOrCreateClient(url);
      } catch (Exception e) {
        logger.error("notify fail", e);
      }
    }
  }

  @Override
  public ChannelManager getChannelManager() {
    return channelManager;
  }
}

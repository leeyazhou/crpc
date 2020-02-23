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

package com.github.leeyazhou.crpc.console.inits;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.leeyazhou.crpc.console.config.ConsoleConfig;
import com.github.leeyazhou.crpc.console.model.ServiceModel;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.leeyazhou.crpc.core.URL;

/**
 * @author lee
 */
public class ZookeeperInit {
  private static final Logger logger = LoggerFactory.getLogger(ZookeeperInit.class);
  private static ZkClient zkClient;
  private static ConcurrentMap<String, ServiceModel> services = new ConcurrentHashMap<String, ServiceModel>();
  private static final String providers = "providers";
  private static final String consumers = "consumers";
  // private static final String configurations = "configurations";
  private static ZookeeperInit instance = new ZookeeperInit();
  private static final AtomicBoolean hasInit = new AtomicBoolean(false);
  private static final Map<String, String> listenersPath = new HashMap<String, String>();

  private final IZkChildListener childListener = new IZkChildListener() {

    @Override
    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
      // parentPath : /crpc/com.github.Userservice
      logger.info("parentPath : " + parentPath);
      logger.info("currentChilds : " + currentChilds);
      if (parentPath.endsWith(ConsoleConfig.getRegistryName())) {
        doInit();
      } else if (parentPath.endsWith(providers)) {
        String parentPathTemp = parentPath.substring(0, parentPath.lastIndexOf("/"));
        String serviceClassName = parentPathTemp.substring(parentPathTemp.lastIndexOf("/") + 1, parentPathTemp.length());
        if (services.containsKey(serviceClassName)) {
          services.get(serviceClassName).getProvider().clear();
        }
        if (currentChilds != null) {
          for (String child : currentChilds) {
            initProvider(serviceClassName, parentPath + "/" + child);
          }
        }
      } else if (parentPath.endsWith(consumers)) {
        // initConsumer(serviceClassName, consumerPath);
        String parentPathTemp = parentPath.substring(0, parentPath.lastIndexOf("/"));
        String serviceClassName = parentPathTemp.substring(parentPathTemp.lastIndexOf("/") + 1, parentPathTemp.length());
        if (services.containsKey(serviceClassName)) {
          services.get(serviceClassName).getConsumer().clear();
        }
        if (currentChilds != null) {
          for (String child : currentChilds) {
            initConsumer(serviceClassName, parentPath + "/" + child);
          }
        }
      }
    }
  };

  public static ZookeeperInit getInstance() {
    return instance;
  }

  public void init() {
    try {
      if (hasInit.compareAndSet(false, true)) {
        doInit();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   */
  private void doInit() throws Exception {
    services.clear();
    if (ConsoleConfig.getRegistryAddress() != null) {
      String[] temp = ConsoleConfig.getRegistryAddress().replaceAll("//", "").split(":");
      String host = temp[1];
      String port = temp[2];
      zkClient = new ZkClient(host + ":" + port, 2000, Integer.MAX_VALUE);
    }
    if (ConsoleConfig.getRegistryName() == null) {
      // do nothing
    }
    String root = "/" + ConsoleConfig.getRegistryName();
    addListener(root);
    List<String> children = getZkClient().getChildren(root);
    for (String serviceClassName : children) {
      initService(root, serviceClassName);
    }

  }

  private void initService(String root, String serviceClassName) {
    ServiceModel serviceModel = services.putIfAbsent(serviceClassName, new ServiceModel(serviceClassName));
    if (serviceModel != null) {
      services.get(serviceClassName).getProvider().clear();
      services.get(serviceClassName).getConsumer().clear();
    }
    logger.info("子节点:" + serviceClassName);
    String providersPath = root + "/" + serviceClassName + "/" + providers;
    initProvider(serviceClassName, providersPath);

    String consumerPath = root + "/" + serviceClassName + "/" + consumers;
    initConsumer(serviceClassName, consumerPath);
    logger.info(services.toString());
  }

  private void initProvider(String serviceClassName, String providersPath) {
    if (zkClient.exists(providersPath)) {
      addListener(providersPath);
      List<String> children = getZkClient().getChildren(providersPath);
      for (String providerChild : children) {
        logger.info("服务提供者path:" + providerChild);
        URL providerUrl = URL.valueOf(providerChild);
        services.get(serviceClassName).getProvider().putIfAbsent(providerUrl.getHost() + ":" + providerUrl.getPort(), providerUrl);
      }
    }
  }

  private void initConsumer(String serviceClassName, String consumerPath) {
    if (zkClient.exists(consumerPath)) {
      addListener(consumerPath);
      List<String> children = getZkClient().getChildren(consumerPath);
      for (String consumerChild : children) {
        logger.info("服务消费者path:" + consumerChild);
        URL consumerUrl = URL.valueOf(consumerChild);
        services.get(serviceClassName).getConsumer().putIfAbsent(consumerUrl.getHost() + ":" + consumerUrl.getPort(), consumerUrl);
      }
    }
  }

  /**
   * 监听path路径及其子节点
   * 
   * @param path
   */
  private void addListener(String path) {
    if (zkClient.exists(path)) {
      List<String> children = zkClient.getChildren(path);
      if (children == null) {
        return;
      }
      zkClient.subscribeChildChanges(path, childListener);
      for (String child : children) {
        String childPath = path + "/" + child;
        logger.info("addListener : " + childPath);
        if (!listenersPath.containsKey(childPath)) {
          listenersPath.put(childPath, childPath);
          zkClient.subscribeChildChanges(childPath, childListener);
        }
        addListener(childPath);
      }
    }
  }

  /**
   * @return the zkClient
   */
  public static ZkClient getZkClient() {
    return zkClient;
  }

  /**
   * @return the services
   */
  public static ConcurrentMap<String, ServiceModel> getServices() {
    return services;
  }
}

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

package com.github.leeyazhou.crpc.registry.zookeeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.leeyazhou.crpc.registry.support.FailbackRegistry;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.URLUtil;

/**
 * @author leeyazhou
 */
public class ZookeeperRegistry extends FailbackRegistry {
  private static final Logger logger = LoggerFactory.getLogger(ZookeeperRegistry.class);

  // ConcurrentMap<服务接口, 服务提供者>
  private static final ConcurrentMap<String, Map<String, URL>> providersMap = new ConcurrentHashMap<String, Map<String, URL>>();
  private static final ConcurrentMap<String, Boolean> hasListener = new ConcurrentHashMap<String, Boolean>();
  private IZkChildListener childListener = null;
  private KeeperState state;
  private ZkClient zkClient;

  /**
   * 
   */
  public ZookeeperRegistry(URL registryURL) {
    super(registryURL);
    connect(registryURL);
    createPath(root, false);
  }

  /**
   * 路径添加监听
   * 
   * @param path 监听路径
   */
  private void addPathListener(String path) {
    int num = this.zkClient.countChildren(path);
    if (num > 0) {
      List<String> servicePaths = this.zkClient.getChildren(path);
      if (servicePaths != null && !servicePaths.isEmpty()) {
        for (String p : servicePaths) {
          String temp = path + Constants.FILE_SEPARATOR + p;
          addPathListener(temp);
        }
      }
    }

    if (hasListener.containsKey(path)) {
      return;
    }
    hasListener.putIfAbsent(path, Boolean.TRUE);
    if (childListener == null) {
      childListener = createChildListener();
    }
    this.zkClient.subscribeChildChanges(path, childListener);
  }

  @Override
  public void doRegister(URL registryURL) {
    // example : /crpc/demoService/providers/
    String providersPath = toCategoryPath(registryURL);
    providersPath += Constants.FILE_SEPARATOR + URLUtil.encode(registryURL.getProviderPath());
    if (logger.isDebugEnabled()) {
      logger.debug("zookeeper register path : " + providersPath);
    }
    createPath(providersPath, true);
  }

  private String toServicePath(URL url) {
    return root + Constants.FILE_SEPARATOR + url.getParameter(Constants.SERVICE_INTERFACE, "");
  }

  /**
   * example : /crpc/demoService/providers/
   * 
   * @param url URL
   * @return Str
   */
  private String toCategoryPath(URL url) {
    return toServicePath(url) + Constants.FILE_SEPARATOR + "providers";
  }

  @Override
  public void close() {
    zkClient.close();
  }

  /**
   * 创建路径
   * 
   * @param path 路径
   * @param ephemeral 临时节点
   */
  private synchronized void createPath(String path, boolean ephemeral) {
    int ii = path.lastIndexOf('/');
    if (ii > 0) {
      createPath(path.substring(0, ii), false);
    }
    if (!zkClient.exists(path)) {
      if (ephemeral) {
        zkClient.createEphemeral(path);
      } else {
        zkClient.createPersistent(path);
      }
    }
    addPathListener(path);
  }

  @Override
  public void unregister(URL registryURL) {
    zkClient.delete(toServicePath(registryURL));
  }

  @Override
  public List<URL> doGetProviders(URL registryURL) {
    List<String> servicePathList = Collections.emptyList();
    String serviceInterface1 = null;
    if (registryURL == null) {
      servicePathList = zkClient.getChildren(root);
    } else {
      serviceInterface1 = registryURL.getParameter(Constants.SERVICE_INTERFACE, null);
      if (serviceInterface1 != null) {
        servicePathList.add(serviceInterface1);
      }
    }

    for (String serviceInterfaceItem : servicePathList) {
      String servicePath = root + Constants.FILE_SEPARATOR + serviceInterfaceItem;
      String path = servicePath + Constants.FILE_SEPARATOR + "providers";
      if (zkClient.exists(path)) {
        List<String> children = zkClient.getChildren(path);
        for (String child : children) {
          child = URLUtil.decode(child);
          URL temp = URL.valueOf(child);
          String serviceInterface = temp.getParameter(Constants.SERVICE_INTERFACE, null);
          Map<String, URL> serviceList = providersMap.get(serviceInterface);
          if (serviceList == null) {
            providersMap.putIfAbsent(serviceInterface, new HashMap<String, URL>());
            serviceList = providersMap.get(serviceInterface);
          }
          serviceList.put(temp.getHost() + ":" + temp.getPort(), temp);
          path = servicePath + Constants.FILE_SEPARATOR + "consumers" + Constants.FILE_SEPARATOR
              + URLUtil.encode(temp.getConsumerPath());
          createPath(path, true);
        }
      }
    }
    return doGetProviders(registryURL, serviceInterface1);
  }

  private synchronized List<URL> doGetProviders(URL registryConfig, String serviceInterface) {
    List<URL> result = new ArrayList<URL>();
    if (registryConfig == null) {
      for (Map.Entry<String, Map<String, URL>> entry : providersMap.entrySet()) {
        Iterator<URL> it = entry.getValue().values().iterator();
        while (it.hasNext()) {
          result.add(it.next());
        }
      }
    } else {
      Map<String, URL> temp = providersMap.get(serviceInterface);
      if (temp != null && !temp.isEmpty()) {
        Iterator<URL> it = temp.values().iterator();
        while (it.hasNext()) {
          result.add(it.next());
        }
      }
    }
    return result;
  }

  @Override
  public boolean isAvailable() {
    if (zkClient != null && state == KeeperState.SyncConnected) {
      return true;
    }
    return false;
  }

  /**
   * 服务列表监听
   * 
   * @return {@link IZkChildListener}
   */
  private IZkChildListener createChildListener() {
    return new IZkChildListener() {

      @Override
      public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
        logger.info("handleChildChange, parentPath : " + parentPath + ", currentChilds :  " + currentChilds);
        if (currentChilds == null || currentChilds.isEmpty()) {
          return;
        }
        if (parentPath.endsWith(Constants.DEFAULT_CATEGORY_PROVIDER)) {
          List<URL> localProviders = null;
          // need notify
          List<URL> notifyUrls = new ArrayList<URL>(currentChilds.size());
          for (String urlStr : currentChilds) {
            URL providerURL = URL.valueOf(urlStr);
            if (localProviders == null) {
              String serviceInterface = providerURL.getParameter(Constants.SERVICE_INTERFACE, null);
              // 获取本地缓存
              localProviders = doGetProviders(providerURL, serviceInterface);
            }
            if (localProviders == null || localProviders.isEmpty() || !localProviders.contains(providerURL)) {
              notifyUrls.add(providerURL);
            }
          }
          notifyListener(notifyUrls);
        }

        getProviders(null);// 更新本地缓存
      }

    };
  }

  @Override
  protected void connect(URL registryConfig) {
    try {
      this.zkClient = new ZkClient(getRegistryURL().getHost() + ":" + getRegistryURL().getPort(), 3000, Integer.MAX_VALUE);
      state = KeeperState.SyncConnected;
    } catch (Exception err) {
      logger.error("ZooKeeper connect fail", err);
    }

    zkClient.subscribeStateChanges(new IZkStateListener() {

      @Override
      public void handleStateChanged(KeeperState state) throws Exception {
        changeState(state);
      }

      @Override
      public void handleSessionEstablishmentError(Throwable error) throws Exception {
        changeState(KeeperState.Disconnected);
      }

      @Override
      public void handleNewSession() throws Exception {
        changeState(state = KeeperState.SyncConnected);
      }
    });
  }

  private void changeState(KeeperState state) {
    logger.info("ZooKeeper state is changed from [" + ZookeeperRegistry.this.state + "] to [" + state + "], registryURL : "
        + getRegistryURL());
    ZookeeperRegistry.this.state = state;
  }
}

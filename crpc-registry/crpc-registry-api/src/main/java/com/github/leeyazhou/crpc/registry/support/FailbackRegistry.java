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

package com.github.leeyazhou.crpc.registry.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.github.leeyazhou.crpc.registry.NotifyListener;
import com.github.leeyazhou.crpc.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;

/**
 * @author lee
 */
public abstract class FailbackRegistry implements Registry {

  private static final Logger logger = LoggerFactory.getLogger(FailbackRegistry.class);
  private final Set<URL> registered = new HashSet<URL>();
  private final Map<String, NotifyListener> notifyListeners = new HashMap<String, NotifyListener>();
  private URL registryCenter;// 注册中心地址，如zookeeper的地址
  private final Lock lock = new ReentrantLock(true);
  protected final String root;
  protected static final String DEFAULT_ROOT = "/crpc";

  public FailbackRegistry(final URL registryCenter) {
    root = registryCenter.getParameter(Constants.GROUP, DEFAULT_ROOT);
    this.registryCenter = registryCenter;
  }

  /**
   * @return the registryConfig
   */
  public URL getRegistryURL() {
    return registryCenter;
  }

  /**
   * @param registryUrl the registryConfig to set
   */
  public void setRegistryConfig(URL registryUrl) {
    this.registryCenter = registryUrl;
  }

  @Override
  public void register(URL registryURL) {
    lock.lock();
    try {
      if (logger.isDebugEnabled()) {
        logger.debug("register service : " + registryURL);
      }
      doRegister(registryURL);
      registered.add(registryURL);
    } catch (Exception err) {
      logger.error("", err);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void subscribe(URL registryConfig, NotifyListener notifyListener) {
    if (registryConfig == null) {
      notifyListeners.put(null, notifyListener);
    } else {
      notifyListeners.put(registryConfig.getServiceName(), notifyListener);
    }
  }

  @Override
  public void unsubscribe(URL registryConfig, NotifyListener notifyListener) {
    notifyListeners.remove(registryConfig.getServiceName());
  }

  protected void notifyListener(List<URL> urls) {
    if (urls == null || urls.isEmpty()) {
      return;
    }
    for (NotifyListener listener : notifyListeners.values()) {
      listener.notify(getProviders(null));
    }
  }

  @Override
  public List<URL> getProviders(URL registryConfig) {
    try {
      lock.tryLock(3, TimeUnit.SECONDS);
      return doGetProviders(registryConfig);
    } catch (Exception err) {
      err.printStackTrace();
    } finally {
      lock.unlock();
    }
    return null;
  }

  /**
   * @return the registered
   */
  public Set<URL> getRegistered() {
    return registered;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Registry [registryCenter=");
    builder.append(registryCenter);
    builder.append("]");
    return builder.toString();
  }

  /**
   * 注册
   * 
   * @param registryURL 注册地址
   */
  public abstract void doRegister(URL registryURL);

  /**
   * 订阅
   * 
   * @param registryURL 地址
   * @return {@link List}
   */
  public abstract List<URL> doGetProviders(URL registryURL);

  protected abstract void connect(URL registryURL);

}

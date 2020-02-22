/**
 * Copyright © 2019 leeyazhou (coderhook@gmail.com)
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
package com.github.leeyazhou.crpc.config.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.leeyazhou.crpc.protocol.util.ObjectUtils;
import com.github.leeyazhou.crpc.registry.RegistryFactory;
import com.github.leeyazhou.crpc.transport.Filter;
import com.github.leeyazhou.crpc.transport.Interceptor;
import com.github.leeyazhou.crpc.transport.factory.AbstractBeanFactory;
import com.github.leeyazhou.crpc.transport.factory.ServiceHandler;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.object.RegistryType;

/**
 * @author lee
 */
public class DefaultBeanFactory extends AbstractBeanFactory {

  private static final Logger logger = LoggerFactory.getLogger(DefaultBeanFactory.class);

  private static final Set<Interceptor> interceptors = new HashSet<Interceptor>();

  private static final Map<RegistryType, RegistryFactory> registryFactories = new HashMap<RegistryType, RegistryFactory>();

  private static Filter filterChain;
  private static final ConcurrentMap<String, ServiceHandler<?>> beanClassFactory = new ConcurrentHashMap<String, ServiceHandler<?>>();

  public void registerInterceptor(Class<Interceptor> interceptor) {
    interceptors.add(ObjectUtils.newInstance(interceptor));
  }

  /**
   * @return the interceptors
   */
  public Set<Interceptor> getInterceptors() {
    return interceptors;
  }

  /**
   * 注册处理器
   * 
   * @param serviceHandler
   *          目标Class类
   */
  @Override
  public <T> void registerProcessor(ServiceHandler<T> serviceHandler) {
    Class<?> lookUp = serviceHandler.getClazz().getInterfaces()[0];
    String instanceName = lookUp.getName();
    beanClassFactory.put(instanceName, serviceHandler);
  }

  /**
   * @param targetInstanceName
   *          类类型
   * @return instance of type
   */
  public <T> ServiceHandler<T> getServiceHandler(String targetInstanceName) {
    @SuppressWarnings("unchecked")
    ServiceHandler<T> bean = (ServiceHandler<T>) beanClassFactory.get(targetInstanceName);
    if (bean != null && bean.getInstance() == null) {
      synchronized (bean) {
        try {
          T t = bean.getClazz().newInstance();
          bean.setInstance(t);
          beanClassFactory.putIfAbsent(targetInstanceName, bean);
        } catch (Exception e) {
          logger.error("", e);
        }
      }
    }
    return bean;
  }

  /**
   * @return the filters
   */
  @Override
  public Filter getFilterChain() {
    return filterChain;
  }

  /**
   * 注册中心工厂
   * 
   * @return the registryfactories
   */
  public Map<RegistryType, RegistryFactory> getRegistryFactories() {
    return registryFactories;
  }

}

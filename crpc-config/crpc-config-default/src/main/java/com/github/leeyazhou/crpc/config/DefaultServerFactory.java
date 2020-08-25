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
package com.github.leeyazhou.crpc.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.function.Supplier;
import com.github.leeyazhou.crpc.core.util.object.RegistryType;
import com.github.leeyazhou.crpc.registry.RegistryFactory;
import com.github.leeyazhou.crpc.transport.Filter;
import com.github.leeyazhou.crpc.transport.factory.AbstractServerFactory;
import com.github.leeyazhou.crpc.transport.factory.ServiceHandler;

/**
 * @author leeyazhou
 */
public class DefaultServerFactory extends AbstractServerFactory {

  private static final Logger logger = LoggerFactory.getLogger(DefaultServerFactory.class);

  private static final Map<RegistryType, RegistryFactory> registryFactories =
      new HashMap<RegistryType, RegistryFactory>();

  private static Filter filterChain;
  private static final ConcurrentMap<String, ServiceHandler<?>> beanClassFactory =
      new ConcurrentHashMap<String, ServiceHandler<?>>();


  /**
   * 注册处理器
   * 
   * @param serviceHandler 目标Class类
   */
  @Override
  public <T> void registerProcessor(ServiceHandler<T> serviceHandler) {
    Class<?> lookUp = serviceHandler.getHandlerType().getInterfaces()[0];
    String instanceName = lookUp.getName();
    beanClassFactory.put(instanceName, serviceHandler);
  }

  /**
   * @param targetInstanceName 类型
   * @return instance of type
   */
  public <T> ServiceHandler<T> getServiceHandler(String targetInstanceName) {
    @SuppressWarnings("unchecked")
    final ServiceHandler<T> bean = (ServiceHandler<T>) beanClassFactory.get(targetInstanceName);
    if (bean != null && bean.getServiceConfig().getInstance() == null) {
      synchronized (bean) {
        try {
          bean.getServiceConfig().setInstanceSupplier(new Supplier<T>() {

            @Override
            public T get() {
              try {
                return bean.getServiceConfig().getServiceType().newInstance();
              } catch (Exception e) {
                logger.error("", e);
              }
              return null;
            }

          });
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

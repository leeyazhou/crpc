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
import com.github.leeyazhou.crpc.core.util.object.RegistryType;
import com.github.leeyazhou.crpc.registry.RegistryFactory;
import com.github.leeyazhou.crpc.transport.Handler;
import com.github.leeyazhou.crpc.transport.factory.AbstractServerFactory;

/**
 * @author leeyazhou
 */
public class DefaultServerFactory extends AbstractServerFactory {

  private static final Logger logger = LoggerFactory.getLogger(DefaultServerFactory.class);

  private static final Map<RegistryType, RegistryFactory> registryFactories =
      new HashMap<RegistryType, RegistryFactory>();

  private static final ConcurrentMap<String, Handler<?>> beanClassFactory = new ConcurrentHashMap<String, Handler<?>>();


  /**
   * 注册处理器
   * 
   * @param serviceHandler 目标Class类
   */
  @Override
  public <T> void registerProcessor(Handler<T> serviceHandler) {
    Class<?> lookUp = serviceHandler.getHandlerType().getInterfaces()[0];
    String instanceName = lookUp.getName();
    logger.info("处理器名称：" + instanceName);
    beanClassFactory.put(instanceName, serviceHandler);
  }

  /**
   * @param targetInstanceName 类型
   * @return instance of type
   */
  @Override
  public <T> Handler<T> getServiceHandler(String targetInstanceName) {
    return (Handler<T>) beanClassFactory.get(targetInstanceName);
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

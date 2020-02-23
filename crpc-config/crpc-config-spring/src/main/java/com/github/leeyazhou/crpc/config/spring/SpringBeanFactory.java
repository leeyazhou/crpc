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
package com.github.leeyazhou.crpc.config.spring;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.leeyazhou.crpc.config.crpc.ModuleConfig;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.github.leeyazhou.crpc.transport.Filter;
import com.github.leeyazhou.crpc.transport.factory.AbstractBeanFactory;
import com.github.leeyazhou.crpc.transport.factory.ServiceHandler;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

/**
 * @author <a href="mailto:lee_yazhou@163.com">Yazhou Li</a>
 */
public class SpringBeanFactory extends AbstractBeanFactory implements ApplicationContextAware {
  private static final Logger logger = LoggerFactory.getLogger(SpringBeanFactory.class);
  private ApplicationContext applicationContext;
  private final ConcurrentMap<String, ServiceHandler<?>> serviceHandlers = new ConcurrentHashMap<String, ServiceHandler<?>>();
  private Filter filterChain;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
    if (logger.isInfoEnabled()) {
      logger.info("初始化CRPC BeanFactory, springApplicationContext : " + this.applicationContext);
    }
    this.setServerConfig(applicationContext.getBean(ModuleConfig.class));
    this.filterChain = applicationContext.getBean(Filter.class);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> ServiceHandler<T> getServiceHandler(String targetInstanceName) {
    return (ServiceHandler<T>) serviceHandlers.get(targetInstanceName);
  }

  @Override
  public Filter getFilterChain() {
    return filterChain;
  }

  @Override
  public <T> void registerProcessor(ServiceHandler<T> serviceHandler) {
    serviceHandlers.putIfAbsent(serviceHandler.getClazz().getName(), serviceHandler);
    logger.info("注册服务:" + serviceHandler.getClazz().getName() + ", " + serviceHandler);
  }

}

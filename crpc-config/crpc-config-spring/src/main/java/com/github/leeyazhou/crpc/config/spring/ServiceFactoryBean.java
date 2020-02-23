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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.leeyazhou.crpc.config.crpc.ModuleConfig;
import com.github.leeyazhou.crpc.config.crpc.ServiceConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.github.leeyazhou.crpc.registry.Registry;
import com.github.leeyazhou.crpc.registry.RegistryFactory;
import com.github.leeyazhou.crpc.rpc.util.RpcUtil;
import com.github.leeyazhou.crpc.transport.factory.ServerFactory;
import com.github.leeyazhou.crpc.transport.factory.ServiceHandler;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;

/**
 * 服务类
 * 
 * @author <a href="mailto:lee_yazhou@163.com">Yazhou Li</a>
 */
public class ServiceFactoryBean<T> extends ServiceConfig<T>
    implements InitializingBean, DisposableBean, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
  static final Logger logger = LoggerFactory.getLogger(ServiceFactoryBean.class);
  private static final long serialVersionUID = 1L;
  private T object;
  private ServiceHandler<T> serviceHandler;
  private ApplicationContext applicationContext;
  private ServerFactory beanFactory;
  private ModuleConfig moduleConfig;
  private final AtomicBoolean isExported = new AtomicBoolean(false);

  @SuppressWarnings({ "unchecked" })
  @Override
  public void afterPropertiesSet() throws Exception {
    if (object != null) {
      setImplClass((Class<T>) this.object.getClass());
      Class<?>[] inerfaces = object.getClass().getInterfaces();
      if (inerfaces != null && inerfaces.length > 0) {
        setInterfaceClass((Class<T>) inerfaces[0]);
      } else {
        setInterfaceClass((Class<T>) object.getClass());
      }
    }

    doExport();
  }

  /**
   * @return the serviceHandler
   */
  public ServiceHandler<T> getServiceHandler() {
    return serviceHandler;
  }

  @Override
  public void destroy() throws Exception {
    if (logger.isInfoEnabled()) {
      logger.info("destory service : " + object);
    }
    RpcUtil.unexport(moduleConfig, serviceHandler);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
    this.beanFactory = this.applicationContext.getBean(ServerFactory.class);
    this.moduleConfig = applicationContext.getBean(ModuleConfig.class);
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
  }

  private void doExport() {
    if (isExported.compareAndSet(false, true)) {
      this.serviceHandler = new ServiceHandler<T>(getInterfaceClass(), null, object);
      serviceHandler.setFilter(beanFactory.getFilterChain());
      this.beanFactory.registerProcessor(serviceHandler);

      RpcUtil.export(moduleConfig, serviceHandler, beanFactory);
      register();
    }
  }

  private void register() {
    List<RegistryFactory> registryFactories = ServiceLoader.load(RegistryFactory.class).loads();
    if (registryFactories == null || registryFactories.isEmpty()) {
      return;
    }

    for (RegistryFactory factory : registryFactories) {
      List<Registry> registries = factory.getRegistries();
      if (registries == null) {
        continue;
      }
      for (Registry registry : registries) {
        URL registryUrl = new URL(moduleConfig.getProtocol(), moduleConfig.getHost(), moduleConfig.getPort());
        registryUrl.addParameter(Constants.APPLICATION, moduleConfig.getName());
        registryUrl.addParameter(Constants.SERVICE_INTERFACE, getInterfaceClass().getName());
        registryUrl.addParameter(Constants.TIMESTAMP_KEY, String.valueOf(System.currentTimeMillis()));
        registry.register(registryUrl);
      }
    }
  }

  /**
   * @param object
   *          the object to set
   */
  public void setObject(T object) {
    this.object = object;
  }

}

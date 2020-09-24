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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.ProtocolConfig;
import com.github.leeyazhou.crpc.config.ServiceConfig;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.registry.Registry;
import com.github.leeyazhou.crpc.registry.RegistryFactory;
import com.github.leeyazhou.crpc.rpc.util.RpcUtil;
import com.github.leeyazhou.crpc.transport.Handler;
import com.github.leeyazhou.crpc.transport.factory.ServerFactory;
import com.github.leeyazhou.crpc.transport.factory.ServiceHandlerFilterWrapper;

/**
 * 服务类
 * 
 * @author leeyazhou
 */
public class ServiceFactoryBean<T> extends ServiceConfig<T>
    implements InitializingBean, DisposableBean, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
  static final Logger logger = LoggerFactory.getLogger(ServiceFactoryBean.class);
  private T object;
  private Handler<T> serviceHandler;
  private ApplicationContext applicationContext;
  private ServerFactory beanFactory;
  private Configuration configuration;
  private ProtocolConfig protocolConfig;
  private final AtomicBoolean isExported = new AtomicBoolean(false);

  @SuppressWarnings({"unchecked"})
  @Override
  public void afterPropertiesSet() throws Exception {
    if (object != null) {
      setImplClass((Class<T>) this.object.getClass());
      Class<?>[] inerfaces = object.getClass().getInterfaces();
      if (inerfaces != null && inerfaces.length > 0) {
        setServiceType((Class<T>) inerfaces[0]);
      } else {
        setServiceType((Class<T>) object.getClass());
      }
    }

    doExport();
  }

  /**
   * @return the serviceHandler
   */
  public Handler<T> getServiceHandler() {
    return serviceHandler;
  }

  @Override
  public void destroy() throws Exception {
    if (logger.isInfoEnabled()) {
      logger.info("destory service : " + object);
    }
    RpcUtil.unexport(configuration, serviceHandler);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
    this.beanFactory = this.applicationContext.getBean(ServerFactory.class);
    this.configuration = applicationContext.getBean(Configuration.class);
    this.protocolConfig = applicationContext.getBean(ProtocolConfig.class);
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {}

  private void doExport() {
    if (isExported.compareAndSet(false, true)) {
      this.serviceHandler = new ServiceHandlerFilterWrapper<T>(this, null);
      this.beanFactory.registerProcessor(serviceHandler);

      RpcUtil.export(configuration, beanFactory);
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
        URL registryUrl = new URL(protocolConfig.getProtocol(), protocolConfig.getHost(), protocolConfig.getPort());
        registryUrl.addParameter(Constants.APPLICATION, configuration.getApplicationConfig().getName());
        registryUrl.addParameter(Constants.SERVICE_INTERFACE, getServiceType().getName());
        registryUrl.addParameter(Constants.TIMESTAMP_KEY, String.valueOf(System.currentTimeMillis()));
        registry.register(registryUrl);
      }
    }
  }

  /**
   * @param object the object to set
   */
  public void setObject(T object) {
    this.object = object;
    setInstance(object);
  }

}

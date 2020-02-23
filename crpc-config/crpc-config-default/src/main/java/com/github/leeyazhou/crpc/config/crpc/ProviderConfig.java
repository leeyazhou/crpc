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
package com.github.leeyazhou.crpc.config.crpc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.github.leeyazhou.crpc.config.IConfig;
import com.github.leeyazhou.crpc.config.server.DefaultBeanFactory;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.annotation.Aspect;
import com.github.leeyazhou.crpc.core.annotation.CRPCFilterType;
import com.github.leeyazhou.crpc.core.annotation.CRPCService;
import com.github.leeyazhou.crpc.core.loader.CrpcClassLoader;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.object.SideType;
import com.github.leeyazhou.crpc.core.scanner.ClassScanner;
import com.github.leeyazhou.crpc.core.scanner.DefaultClassScanner;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.registry.RegistryFactory;
import com.github.leeyazhou.crpc.rpc.util.RpcUtil;
import com.github.leeyazhou.crpc.transport.Filter;
import com.github.leeyazhou.crpc.transport.Interceptor;
import com.github.leeyazhou.crpc.transport.factory.ServiceHandler;

/**
 * @author leeyazhou
 */
public class ProviderConfig implements IConfig {
  private static final Logger logger = LoggerFactory.getLogger(ProviderConfig.class);
  private static final long serialVersionUID = 1L;

  private List<ServerConfig> servers;

  public void addServer(ServerConfig serverConfig) {
    if (servers == null) {
      servers = new ArrayList<ServerConfig>();
    }
    servers.add(serverConfig);
  }

  /**
   * 暴露服务
   */
  public void export() {
    for (ServerConfig serverConfig : servers) {
      DefaultBeanFactory beanFactory = new DefaultBeanFactory();
      beanFactory.setServerConfig(serverConfig);
      try {
        prepareEnvironment(serverConfig, beanFactory);
        initBeans(serverConfig, beanFactory);
      } catch (Exception e) {
        logger.error("", e);
      }
      RpcUtil.export(serverConfig, null, beanFactory);
    }
  }

  private void prepareEnvironment(ServerConfig serverConfig, DefaultBeanFactory beanFactory) throws Exception {
    if (serverConfig.getRegistries().isEmpty()) {
      return;
    }
    for (URL registryUrl : serverConfig.getRegistries()) {
      RegistryFactory registryFactory = ServiceLoader.load(RegistryFactory.class).load(registryUrl.getRegistryType());

      registryFactory.createRegistry(registryUrl);
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void initBeans(ServerConfig serverConfig, DefaultBeanFactory beanFactory) throws Exception {
    logger.info("location : " + serverConfig.getLocation());
    CrpcClassLoader.addSystemClassPathFolder(serverConfig.getLocation() + "/lib");
    ClassScanner classScanner = DefaultClassScanner.getInstance();
    Set<String> basepackages = serverConfig.getBasepackages();
    if (basepackages == null || basepackages.isEmpty()) {
      throw new IllegalAccessException("basepackage is required ! ");
    }
    Filter filter = buildFilterChain(serverConfig);

    for (String basepackage : basepackages) {
      logger.info("scan service at basepackage : " + basepackage);
      Set<Class<?>> classSet = classScanner.getClassListBySuper(basepackage, Interceptor.class);
      for (Class<?> interceptor : classSet) {
        if (null == interceptor.getAnnotation(Aspect.class)) {
          logger.warn("Aspect is required for Class " + interceptor.getName());
          continue;
        }
        beanFactory.registerInterceptor((Class<Interceptor>) interceptor);
      }
      classSet = classScanner.getClassListByAnnotation(basepackage, CRPCService.class);
      for (Class<?> targetClass : classSet) {
        logger.info("export service : " + targetClass);
        ServiceHandler<?> serviceHandler = new ServiceHandler(targetClass, beanFactory.getInterceptors());
        serviceHandler.setFilter(filter);
        beanFactory.registerProcessor(serviceHandler);
      }
    }

  }

  private Filter buildFilterChain(ServerConfig serverConfig) throws Exception {
    // init filters
    Set<String> filtersSet = serverConfig.getFilters();
    logger.info("begin scan filters : " + filtersSet.size());
    Filter head = null;

    List<Filter> filters = new ArrayList<Filter>(filtersSet.size());

    for (String filterClassName : filtersSet) {
      Class<Filter> fClass = ServiceLoader.load(Filter.class).loadType(filterClassName);
      if (!isProviderSideFilter(fClass)) {
        continue;
      }
      Filter filter = ServiceLoader.load(Filter.class).load(filterClassName);
      if (filter != null)
        filters.add(filter);
    }

    Collections.sort(filters, new Comparator<Filter>() {

      @Override
      public int compare(Filter o1, Filter o2) {
        if (o1.getOrder() > o2.getOrder()) {
          return 1;
        }
        return -1;
      }
    });
    for (Filter f : filters) {
      if (head == null) {
        head = f;
        continue;
      }
      head.setNext(f);
    }

    return head;
  }

  private boolean isProviderSideFilter(Class<Filter> clazz) {
    if (clazz == null) {
      return false;
    }
    CRPCFilterType annotation = clazz.getAnnotation(CRPCFilterType.class);
    if (annotation == null || annotation.active() == null) {
      return true;
    }

    for (SideType side : annotation.active()) {
      if (SideType.SIDE_PROVIDER.equals(side)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return the servers
   */
  public List<ServerConfig> getServers() {
    return servers;
  }

  /**
   * @param servers
   *          the servers to set
   */
  public void setServers(List<ServerConfig> servers) {
    this.servers = servers;
  }

}

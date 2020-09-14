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
package com.github.leeyazhou.crpc.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.github.leeyazhou.crpc.core.annotation.CRPCFilterType;
import com.github.leeyazhou.crpc.core.annotation.CRPCService;
import com.github.leeyazhou.crpc.core.exception.CrpcException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.scanner.ClassScanner;
import com.github.leeyazhou.crpc.core.scanner.DefaultClassScanner;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.core.util.object.SideType;
import com.github.leeyazhou.crpc.registry.RegistryFactory;
import com.github.leeyazhou.crpc.rpc.util.RpcUtil;
import com.github.leeyazhou.crpc.transport.Filter;
import com.github.leeyazhou.crpc.transport.factory.ServiceHandler;

/**
 * @author leeyazhou
 */
public class ProviderConfig {
  private static final Logger logger = LoggerFactory.getLogger(ProviderConfig.class);

  private Configuration configuration;

  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }


  /**
   * 暴露服务
   */
  public void export() {
    validate();
    DefaultServerFactory beanFactory = new DefaultServerFactory();
    beanFactory.setConfiguration(configuration);
    try {
      prepareEnvironment(configuration.getServerConfig(), beanFactory);
      scan(configuration.getServerConfig(), beanFactory);
    } catch (Exception e) {
      logger.error("", e);
    }
    RpcUtil.export(configuration, beanFactory);
  }

  private void validate() {
    if (configuration == null) {
      throw new CrpcException("configuration不能为空");
    }
    if (configuration.getApplicationConfig() == null) {
      throw new CrpcException("applicationConfig不能为空");
    }
    if (configuration.getProtocolConfig() == null) {
      throw new CrpcException("protocolConfig不能为空");
    }
  }

  private void prepareEnvironment(ServerConfig serverConfig, DefaultServerFactory beanFactory) throws Exception {
    RegistryConfig registryConfig = configuration.getRegistryConfig();
    if (registryConfig == null) {
      return;
    }
    RegistryFactory registryFactory =
        ServiceLoader.load(RegistryFactory.class).load(registryConfig.toURL().getProtocol());

    registryFactory.createRegistry(registryConfig.toURL());
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void scan(ServerConfig serverConfig, DefaultServerFactory beanFactory) throws Exception {
    Set<String> basepackages = serverConfig.getBasepackages();
    if (basepackages == null || basepackages.isEmpty()) {
      throw new IllegalAccessException("basepackage is required ! ");
    }
    List<Filter> filters = buildFilterChain(serverConfig);

    for (String basepackage : basepackages) {
      logger.info("scan service at basepackage : " + basepackage);
      ClassScanner classScanner = new DefaultClassScanner(basepackage);
      Set<Class<?>> classSet = classScanner.getClassListByAnnotation(CRPCService.class);
      for (Class<?> targetClass : classSet) {
        logger.info("export service : " + targetClass);
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setServiceType(targetClass);
        ServiceHandler<?> serviceHandler = new ServiceHandler(serviceConfig);
        serviceHandler.setFilters(filters);
        beanFactory.registerProcessor(serviceHandler);
      }
    }

  }

  private List<Filter> buildFilterChain(ServerConfig serverConfig) throws Exception {
    // init filters
    List<String> filtersSet = serverConfig.getFilters();
    logger.info("begin scan filters : " + filtersSet.size());

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


    return filters;
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


}

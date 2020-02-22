/**
 * Copyright © 2016~2020 leeyazhou (coderhook@gmail.com)
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
///**
// * 
// */
//
//package com.github.crpc.transport.factory;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//import java.util.concurrent.ExecutorService;
//import Constants;
//import URL;
//import CRPCService;
//import Executors;
//import SimpleNamedThreadFactory;
//import Logger;
//import LoggerFactory;
//import RegistryType;
//import com.github.crpc.config.crpc.ServerConfig;
//import com.github.crpc.protocol.util.ObjectUtils;
//import com.github.crpc.registry.Registry;
//import com.github.crpc.registry.RegistryFactory;
//import com.github.crpc.transport.FilterChain;
//import com.github.crpc.transport.Interceptor;
//import com.github.crpc.transport.Server;
//import com.github.crpc.transport.filter.ApplicationFilterChain;
//
///**
// * @author lee
// */
//public class ServerFactory {
//
//  private static final Logger logger = LoggerFactory.getLogger(ServerFactory.class);
//
//  private static ServerConfig serverConfig;
//
//  private static final Map<String, Interceptor> interceptors = new HashMap<String, Interceptor>();
//
//  private static final Map<RegistryType, RegistryFactory> registryFactories = new HashMap<RegistryType, RegistryFactory>();
//
//  private static FilterChain filterChain = new ApplicationFilterChain(null);
//
//  // private static Injector injector;
//
//  private static Server server;
//
//  private static ExecutorService executorService;
//
//  public static void registerInterceptor(Class<Interceptor> interceptor) {
//    if (!interceptors.containsKey(interceptor.getClass().getName())) {
//      interceptors.put(interceptor.getClass().getName(), ObjectUtils.newInstance(interceptor));
//    }
//  }
//
//  /**
//   * @return the interceptors
//   */
//  public static Map<String, Interceptor> getInterceptors() {
//    return interceptors;
//  }
//
//  /**
//   * @return the executorService
//   */
//  public static ExecutorService getExecutorService() {
//    return executorService;
//  }
//
//  /**
//   * 注册处理器
//   * 
//   * @param targetClass 目标Class类
//   */
//  public static <T> void registerProcessor(ServiceHandler<T> serviceHandler) {
//    Class<?> lookUp = serviceHandler.getClazz().getInterfaces()[0];
//    String instanceName = lookUp.getName();
//    beanClassFactory.put(instanceName, serviceHandler);
//  }
//
//  /**
//   * 启动后注册
//   */
//  public static void registerService() {
//    if (registryFactories != null) {
//      for (Entry<String, ServiceHandler<?>> entry : beanClassFactory.entrySet()) {
//        CRPCService serviceAnnotation = entry.getValue().getClazz().getAnnotation(CRPCService.class);
//        if (serviceAnnotation == null) {
//          continue;
//        }
//        Class<?> lookUp = serviceAnnotation.lookUp();
//        if (lookUp == null || lookUp.equals(Object.class)) {
//          lookUp = entry.getValue().getClazz().getInterfaces()[0];
//        }
//        String instanceName = lookUp.getName();
//        for (RegistryFactory registryFactory : registryFactories.values()) {
//          Iterator<Registry> it = registryFactory.getRegistries().iterator();
//          while (it.hasNext()) {
//            Registry registry = it.next();
//            if (!registry.isAvailable()) {
//              continue;
//            }
//            URL registryURL = new URL(serverConfig.getProtocol(), serverConfig.getHost(), serverConfig.getPort());
//            registryURL.addParameter(Constants.SERVICE_INTERFACE, instanceName);
//            registryURL.addParameter(Constants.APPLICATION, serverConfig.getName());
//            registryURL.addParameter(Constants.SIDE_KEY, Constants.PROVIDER_SIDE);
//            registryURL.addParameter(Constants.TIMESTAMP_KEY, String.valueOf(System.currentTimeMillis()));
//            registryURL.addParameter(Constants.SERVER_WEIGHT, String.valueOf(serverConfig.getWeight()));
//            logger.info("注册服务：" + registryURL);
//            registry.register(registryURL);
//          }
//        }
//      }
//    }
//  }
//
//  private static final ConcurrentMap<String, ServiceHandler<?>> beanClassFactory =
//      new ConcurrentHashMap<String, ServiceHandler<?>>();
//
//  /**
//   * @param targetInstanceName 类类型
//   * @return instance of type
//   */
//  public static <T> ServiceHandler<T> getBean(String targetInstanceName) {
//    ServiceHandler<T> bean = (ServiceHandler<T>) beanClassFactory.get(targetInstanceName);
//    if (bean != null && bean.getInstance() == null) {
//      synchronized (bean) {
//        try {
//          T t = bean.getClazz().newInstance();
//          bean.setInstance(t);
//          beanClassFactory.putIfAbsent(targetInstanceName, bean);
//        } catch (Exception e) {
//          logger.error("", e);
//        }
//      }
//    }
//    return bean;
//  }
//
//  /**
//   * @param serverConfig the serverConfig to set
//   */
//  public static void setServerConfig(ServerConfig serverConfig) {
//    ServerFactory.serverConfig = serverConfig;
//    final int corePoolSize = serverConfig.getWorker();
//    final int maximumPoolSize = corePoolSize + corePoolSize / 2;
//    executorService = Executors.newFixedThreadPool(corePoolSize, maximumPoolSize, new SimpleNamedThreadFactory("crpc-server"));
//  }
//
//  /**
//   * @return the filters
//   */
//  public static FilterChain getFilterChain() {
//    return filterChain;
//  }
//
//  /**
//   * @return the server
//   */
//  public static Server getServer() {
//    return server;
//  }
//
//  /**
//   * @param server the server to set
//   */
//  public static void setServer(Server server) {
//    ServerFactory.server = server;
//  }
//
//  /**
//   * 注册中心工厂
//   * 
//   * @return the registryfactories
//   */
//  public static Map<RegistryType, RegistryFactory> getRegistryFactories() {
//    return registryFactories;
//  }
//
//}

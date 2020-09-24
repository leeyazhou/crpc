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
package com.github.leeyazhou.crpc.transport.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.ServerConfig;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.concurrent.NamedThreadFactory;
import com.github.leeyazhou.crpc.core.util.concurrent.TaskQueue;
import com.github.leeyazhou.crpc.core.util.concurrent.ThreadPoolExecutor;
import com.github.leeyazhou.crpc.core.util.object.RegistryType;
import com.github.leeyazhou.crpc.registry.RegistryFactory;
import com.github.leeyazhou.crpc.transport.Handler;

/**
 * @author leeyazhou
 */
public class CrpcServerFactory implements ServerFactory {
  private static final Logger logger = LoggerFactory.getLogger(CrpcServerFactory.class);
  private final ConcurrentMap<String, Handler<?>> serviceHandlers = new ConcurrentHashMap<String, Handler<?>>();
  private final Map<RegistryType, RegistryFactory> registryFactories = new HashMap<RegistryType, RegistryFactory>();
  private ExecutorService executorService;
  private Configuration configuration;
  private ServerConfig serverConfig;

  /**
   * @return the executorService
   */
  @Override
  public ExecutorService getExecutorService() {
    if (executorService == null) {
      synchronized (CrpcServerFactory.class) {
        if (executorService == null) {
          final int availableProcessors = Runtime.getRuntime().availableProcessors();
          final int worker = serverConfig.getWorker();
          final int taskQueueSize = serverConfig.getTaskQueueSize() <= 0 ? 10000 : serverConfig.getTaskQueueSize();
          this.executorService = new ThreadPoolExecutor(Math.max(availableProcessors, 8), worker, 60L, TimeUnit.SECONDS,
              new TaskQueue(taskQueueSize), new NamedThreadFactory("crpc-worker", true));
        }
      }

    }
    return executorService;
  }

  /**
   * @param configuration configuration
   */
  @Override
  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
    this.serverConfig = this.configuration.getServerConfig();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Handler<T> getServiceHandler(String targetInstanceName) {
    return (Handler<T>) serviceHandlers.get(targetInstanceName);
  }

  @Override
  public <T> void registerProcessor(Handler<T> serviceHandler) {
    serviceHandlers.putIfAbsent(serviceHandler.getHandlerType().getName(), serviceHandler);
    logger.info("注册服务:" + serviceHandler.getHandlerType().getName() + ", " + serviceHandler);
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

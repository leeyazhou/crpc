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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.core.util.concurrent.TaskQueue;
import com.github.leeyazhou.crpc.core.util.concurrent.TaskThreadFactory;
import com.github.leeyazhou.crpc.core.util.concurrent.ThreadPoolExecutor;

/**
 * @author leeyazhou
 */
public abstract class AbstractServerFactory implements ServerFactory {

  private ExecutorService executorService;

  /**
   * @return the executorService
   */
  @Override
  public ExecutorService getExecutorService() {
    if(executorService==null) {
      
    }
    return executorService;
  }

  /**
   * @param serverConfig the serverConfig to set
   */
  @Override
  public void setConfiguration(Configuration configuration) {
    if (executorService == null) {
      executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2,
          configuration.getServerConfig().getWorker(), 60L, TimeUnit.SECONDS, new TaskQueue(10000),
          new TaskThreadFactory("crpc-server-", true, Thread.NORM_PRIORITY));
    }
  }
}

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
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.rpc.Handler;

/**
 * 
 * @author leeyazhou
 *
 */
public interface ServerFactory {

  /**
   * 获取缓存的{@link Handler}
   * 
   * @param <T> t
   * @param targetInstanceName targetInstanceName
   * @return {@link Handler}
   */
  <T> Handler<T> getServiceHandler(String targetInstanceName);

  /**
   * executor service
   * 
   * @return {@link ExecutorService}
   */
  ExecutorService getExecutorService();

  /**
   * 注册处理器
   * 
   * @param <T> t
   * @param serviceHandler serviceHandler
   */
  <T> void registerProcessor(Handler<T> serviceHandler);

  /**
   * 设置服务器配置信息
   * 
   * @param configuration configuration
   */
  void setConfiguration(Configuration configuration);

}

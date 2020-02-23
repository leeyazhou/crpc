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

package com.github.leeyazhou.crpc.registry;

import java.util.List;

import com.github.leeyazhou.crpc.core.URL;

/**
 * @author lee
 */
public interface Registry {

  /**
   * 注册服务
   * 
   * @param registryUrl registryUrl
   */
  void register(URL registryUrl);

  /**
   * 取消注册服务
   * 
   * @param registryUrl 服务地址
   */
  void unregister(URL registryUrl);

  /**
   * 订阅服务
   * 
   * @param registryUrl 服务地址
   * @return {@link URL}
   */
  List<URL> getProviders(URL registryUrl);

  /**
   * 订阅服务
   * 
   * @param registryUrl 服务地址
   * @param notifyListener 服务通知回调
   */
  void subscribe(URL registryUrl, NotifyListener notifyListener);

  /**
   * 取消订阅服务
   * 
   * @param registryUrl 服务地址
   * @param notifyListener 服务通知回调
   */
  void unsubscribe(URL registryUrl, NotifyListener notifyListener);

  /**
   * 服务状态
   * 
   * @return true/false
   */
  boolean isAvailable();

  /**
   * 关闭注册中心服务
   */
  public void close();

}

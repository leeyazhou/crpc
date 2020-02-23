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
import com.github.leeyazhou.crpc.core.annotation.SPI;

/**
 * @author lee
 */
@SPI("zookeeper")
public interface RegistryFactory {

  /**
   * 获取注册中心
   * 
   * @param registryUrl 地址
   * @return {@link Registry}
   */
  public Registry getRegistry(URL registryUrl);

  /**
   * 创建注册中心
   * 
   * @param registryUrl registryUrl
   * @return {@link Registry}
   */
  public Registry createRegistry(URL registryUrl);

  /**
   * 获取所有注册中心
   * 
   * @return {@link Registry}
   */
  public List<Registry> getRegistries();

  /**
   * 所有注册中心
   * 
   * @return {@link URL}
   */
  public List<URL> getUrls();
}

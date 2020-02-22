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
/**
 * 
 */
package com.github.leeyazhou.crpc.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.leeyazhou.crpc.core.URL;

/**
 * @author <a href="mailto:lee_yazhou@163.com">Yazhou Li</a>
 */
public abstract class AbstractRegsitryFactory implements RegistryFactory {

  private static final Map<String, URL> urls = new HashMap<String, URL>();
  /**
   * 注册中心
   */
  private static final Map<String, Registry> registries = new HashMap<String, Registry>();

  @Override
  public Registry getRegistry(URL registryURL) {
    return registries.get(registryURL.getHost() + ":" + registryURL.getPort());
  }

  @Override
  public Registry createRegistry(URL registryURL) {
    final String cacheKey = registryURL.getHost() + ":" + registryURL.getPort();
    Registry registry = registries.get(cacheKey);
    if (registry != null) {
      return registry;
    }

    urls.put(cacheKey, registryURL);
    registry = doCreateRegistry(registryURL);
    registries.put(cacheKey, registry);
    return registry;
  }

  /**
   * create registry
   * 
   * @param registryURL registryURL
   * @return {@link Registry}
   */
  public abstract Registry doCreateRegistry(URL registryURL);

  @Override
  public List<Registry> getRegistries() {
    return new ArrayList<Registry>(registries.values());
  }

  @Override
  public List<URL> getUrls() {
    return new ArrayList<URL>(urls.values());
  }
}

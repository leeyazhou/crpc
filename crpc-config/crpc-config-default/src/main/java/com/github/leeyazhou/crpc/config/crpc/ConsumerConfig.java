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

import java.util.Set;
import com.github.leeyazhou.crpc.config.ApplicationConfig;
import com.github.leeyazhou.crpc.config.ReferConfig;
import com.github.leeyazhou.crpc.config.RegistryConfig;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.rpc.proxy.ProxyFactory;

/**
 * @author leeyazhou
 *
 */
public class ConsumerConfig<T> extends ReferConfig<T> {

  public T refer() {
    ProxyFactory proxyFactory = ServiceLoader.load(ProxyFactory.class).load();
    return proxyFactory.getProxy(this);
  }

  public ConsumerConfig<T> setApplicationConfig(ApplicationConfig applicationConfig) {
    super.setApplicationConfig(applicationConfig);
    return this;
  }

  public ConsumerConfig<T> setRegistryConfig(RegistryConfig registryConfig) {
    super.setRegistryConfig(registryConfig);
    return this;
  }

  public ConsumerConfig<T> setServiceType(Class<T> serviceType) {
    super.setServiceType(serviceType);
    return this;
  }

  public ConsumerConfig<T> setUrls(Set<URL> urls) {
    super.setUrls(urls);
    return this;
  }


  public ConsumerConfig<T> addURL(URL url) {
    super.addUrl(url);
    return this;
  }

}

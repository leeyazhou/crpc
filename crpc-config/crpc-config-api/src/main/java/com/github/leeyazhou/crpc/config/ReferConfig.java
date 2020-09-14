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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.github.leeyazhou.crpc.core.URL;

/**
 * @author leeyazhou
 *
 */
public class ReferConfig<T> {
  private Class<T> serviceType;
  private ApplicationConfig applicationConfig;
  private RegistryConfig registryConfig;
  private Set<URL> urls;
  private int timeout;
  private byte codecType;
  private String loadbalance;
  private List<String> filters;

  public Class<T> getServiceType() {
    return serviceType;
  }

  public ReferConfig<T> setServiceType(Class<T> serviceType) {
    this.serviceType = serviceType;
    return this;
  }

  public ApplicationConfig getApplicationConfig() {
    return applicationConfig;
  }

  public ReferConfig<T> setApplicationConfig(ApplicationConfig applicationConfig) {
    this.applicationConfig = applicationConfig;
    return this;
  }

  public RegistryConfig getRegistryConfig() {
    return registryConfig;
  }

  public ReferConfig<T> setRegistryConfig(RegistryConfig registryConfig) {
    this.registryConfig = registryConfig;
    return this;
  }

  public Set<URL> getUrls() {
    return urls;
  }

  public ReferConfig<T> setUrls(Set<URL> urls) {
    this.urls = urls;
    return this;
  }

  public ReferConfig<T> addUrl(URL url) {
    if (urls == null) {
      this.urls = new HashSet<URL>();
    }
    urls.add(url);
    return this;
  }

  public ReferConfig<T> setTimeout(int timeout) {
    this.timeout = timeout;
    return this;
  }

  public int getTimeout() {
    return timeout;
  }

  public byte getCodecType() {
    return codecType;
  }

  public ReferConfig<T> setCodecType(byte codecType) {
    this.codecType = codecType;
    return this;
  }

  public String getLoadbalance() {
    return loadbalance;
  }


  public void setFilters(List<String> filters) {
    this.filters = filters;
  }

  public List<String> getFilters() {
    return filters;
  }

  public ReferConfig<T> setLoadbalance(String loadbalance) {
    this.loadbalance = loadbalance;
    return this;
  }


}

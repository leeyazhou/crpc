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
import java.util.Set;
import com.github.leeyazhou.crpc.core.URL;

/**
 * @author leeyazhou
 */
public class ServiceConfig<T> {
  private String name;
  private Class<T> interfaceClass;
  private Class<T> implClass;
  private String filter;
  private String codec;
  private String loadbalance;
  private int codecValue;
  /**
   * 超时时间/ms
   */
  private int timeout = 3000;
  private Set<RegistryConfig> registryConfigs = new HashSet<RegistryConfig>();
  private Set<URL> urls = new HashSet<URL>();

  /**
   * @return the codecValue
   */
  public int getCodecValue() {
    return codecValue;
  }

  /**
   * @param codecValue the codecValue to set
   */
  public ServiceConfig<T> setCodecValue(int codecValue) {
    this.codecValue = codecValue;
    return this;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public ServiceConfig<T> setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @return the interfaceClass
   */
  public Class<T> getInterfaceClass() {
    return interfaceClass;
  }

  /**
   * @param interfaceClass the interfaceClass to set
   */
  public ServiceConfig<T> setInterfaceClass(Class<T> interfaceClass) {
    this.interfaceClass = interfaceClass;
    return this;
  }

  /**
   * @return the implClass
   */
  public Class<T> getImplClass() {
    return implClass;
  }

  /**
   * @param implClass the implClass to set
   */
  public ServiceConfig<T> setImplClass(Class<T> implClass) {
    this.implClass = implClass;
    return this;
  }

  /**
   * @return the filter
   */
  public String getFilter() {
    return filter;
  }

  /**
   * @param filter the filter to set
   */
  public ServiceConfig<T> setFilter(String filter) {
    this.filter = filter;
    return this;
  }

  /**
   * @return the codec
   */
  public String getCodec() {
    return codec;
  }

  /**
   * @param codec the codec to set
   */
  public ServiceConfig<T> setCodec(String codec) {
    this.codec = codec;
    return this;
  }

  /**
   * @return the loadbalance
   */
  public String getLoadbalance() {
    return loadbalance;
  }

  /**
   * @param loadbalance the loadbalance to set
   */
  public ServiceConfig<T> setLoadbalance(String loadbalance) {
    this.loadbalance = loadbalance;
    return this;
  }

  /**
   * @return the registryConfigs
   */
  public Set<RegistryConfig> getRegistryConfigs() {
    return registryConfigs;
  }

  /**
   * @param registryConfigs the registryConfigs to set
   */
  public ServiceConfig<T> setRegistryConfigs(Set<RegistryConfig> registryConfigs) {
    this.registryConfigs = registryConfigs;
    return this;
  }

  /**
   * @return 直连服务
   */
  public Set<URL> getUrls() {
    return urls;
  }

  /**
   * 直连
   * 
   * @param urls 直连服务
   */
  public ServiceConfig<T> setUrls(Set<URL> urls) {
    this.urls = urls;
    return this;
  }

  /**
   * @return the timeout
   */
  public int getTimeout() {
    return timeout;
  }

  /**
   * @param timeout the timeout to set
   */
  public ServiceConfig<T> setTimeout(int timeout) {
    this.timeout = timeout;
    return this;
  }



}

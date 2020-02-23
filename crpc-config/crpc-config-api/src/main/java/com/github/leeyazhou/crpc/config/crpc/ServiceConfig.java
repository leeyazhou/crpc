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

import java.util.HashSet;
import java.util.Set;

import com.github.leeyazhou.crpc.config.IConfig;
import com.github.leeyazhou.crpc.core.URL;

/**
 * @author <a href="mailto:lee_yazhou@163.com">Yazhou Li</a>
 */
public class ServiceConfig<T> implements IConfig {
  private static final long serialVersionUID = 1L;
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
  private Set<URL> registries = new HashSet<URL>();
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
  public void setCodecValue(int codecValue) {
    this.codecValue = codecValue;
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
  public void setName(String name) {
    this.name = name;
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
  public void setInterfaceClass(Class<T> interfaceClass) {
    this.interfaceClass = interfaceClass;
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
  public void setImplClass(Class<T> implClass) {
    this.implClass = implClass;
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
  public void setFilter(String filter) {
    this.filter = filter;
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
  public void setCodec(String codec) {
    this.codec = codec;
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
  public void setLoadbalance(String loadbalance) {
    this.loadbalance = loadbalance;
  }

  /**
   * @return the registries
   */
  public Set<URL> getRegistries() {
    return registries;
  }

  /**
   * @param registries the registries to set
   */
  public void setRegistries(Set<URL> registries) {
    this.registries = registries;
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
  public void setUrls(Set<URL> urls) {
    this.urls = urls;
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
  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }



}

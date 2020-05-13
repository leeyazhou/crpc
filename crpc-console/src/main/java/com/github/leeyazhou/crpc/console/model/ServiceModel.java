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

package com.github.leeyazhou.crpc.console.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.leeyazhou.crpc.core.URL;

/**
 * @author leeyazhou
 */
public class ServiceModel {

  private String serviceName;
  private ConcurrentMap<String, URL> provider = new ConcurrentHashMap<String, URL>();
  private ConcurrentMap<String, URL> consumer = new ConcurrentHashMap<String, URL>();

  public ServiceModel(String serviceName) {
    this.serviceName = serviceName;
  }

  /**
   * @return the serviceName
   */
  public String getServiceName() {
    return serviceName;
  }

  /**
   * @param serviceName the serviceName to set
   */
  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  /**
   * @return the provider
   */
  public ConcurrentMap<String, URL> getProvider() {
    return provider;
  }

  /**
   * @param provider the provider to set
   */
  public void setProvider(ConcurrentMap<String, URL> provider) {
    this.provider = provider;
  }

  /**
   * @return the consumer
   */
  public ConcurrentMap<String, URL> getConsumer() {
    return consumer;
  }

  /**
   * @param consumer the consumer to set
   */
  public void setConsumer(ConcurrentMap<String, URL> consumer) {
    this.consumer = consumer;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ServiceModel [serviceName=" + serviceName + ", provider=" + provider + ", consumer=" + consumer + "]";
  }

}

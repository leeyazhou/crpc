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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

/**
 * @author leeyazhou
 *
 */
public class Configuration {
  static final Logger logger = LoggerFactory.getLogger(Configuration.class);
  private ApplicationConfig applicationConfig;
  private ProtocolConfig protocolConfig;

  private Map<String, ServiceGroupConfig> serviceGroupConfigs = new HashMap<String, ServiceGroupConfig>();
  private Set<RegistryConfig> registryConfigs = new HashSet<RegistryConfig>();

  private ServerConfig serverConfig;

  public Configuration() {}


  public Map<String, ServiceGroupConfig> getServiceConfigs() {
    return serviceGroupConfigs;
  }


  public Configuration setProtocolConfig(ProtocolConfig protocolConfig) {
    this.protocolConfig = protocolConfig;
    return this;
  }

  public ProtocolConfig getProtocolConfig() {
    return protocolConfig;
  }

  public Configuration setServerConfig(ServerConfig serverConfig) {
    this.serverConfig = serverConfig;
    return this;
  }

  public ServerConfig getServerConfig() {
    return serverConfig;
  }

  public ServiceGroupConfig getServiceConfig(String serviceName) {
    return serviceGroupConfigs.get(serviceName);
  }

  public Configuration addServiceGroupConfig(ServiceGroupConfig serviceGroupConfig) {
    this.serviceGroupConfigs.put(serviceGroupConfig.getName(), serviceGroupConfig);
    return this;
  }

  public Configuration setApplicationConfig(ApplicationConfig applicationConfig) {
    this.applicationConfig = applicationConfig;
    return this;
  }

  public ApplicationConfig getApplicationConfig() {
    return applicationConfig;
  }


  public Configuration setRegistryConfigs(Set<RegistryConfig> registryConfigs) {
    this.registryConfigs = registryConfigs;
    return this;
  }

  public Set<RegistryConfig> getRegistryConfigs() {
    return registryConfigs;
  }

  public Configuration addRegistryConfig(RegistryConfig registryConfig) {
    registryConfigs.add(registryConfig);
    return this;
  }

  public void validate() {
    Objects.requireNonNull(applicationConfig, "applicationConfig can't be null");
  }

}

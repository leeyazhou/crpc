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

package com.github.leeyazhou.crpc.config.crpc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.github.leeyazhou.crpc.config.IConfig;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

/**
 * @author lee
 *
 */
public class Configuration implements IConfig {
  static final Logger logger = LoggerFactory.getLogger(Configuration.class);
  private static final long serialVersionUID = 3858886608932619894L;
  private String location;

  private Map<String, ServiceGroupConfig> serviceGroupConfigs = new HashMap<String, ServiceGroupConfig>();

  private Set<ServerConfig> serverConfigs = new TreeSet<ServerConfig>();

  public Configuration() {
  }

  public Configuration(String location) {
    this.location = location;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Map<String, ServiceGroupConfig> getServiceConfigs() {
    return serviceGroupConfigs;
  }

  /**
   * @return the moduleConfigs
   */
  public Set<ServerConfig> getServerConfigs() {
    return serverConfigs;
  }

  /**
   * 配置serverConfigs
   * 
   * @param serverConfigs
   *          serverConfigs
   */
  public void setServerConfigs(Set<ServerConfig> serverConfigs) {
    this.serverConfigs = serverConfigs;
  }

  public void addServerConfig(ServerConfig serverConfig) {
    serverConfigs.add(serverConfig);
  }

  public ServiceGroupConfig getServiceConfig(String serviceName) {
    return serviceGroupConfigs.get(serviceName);
  }

  public boolean addServiceGroupConfig(ServiceGroupConfig serviceGroupConfig) {
    return this.serviceGroupConfigs.put(serviceGroupConfig.getName(), serviceGroupConfig) != null;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Configuration [\r\n\tlocation=");
    builder.append(location);
    builder.append(", \r\n\tserviceGroupConfigs=");
    builder.append(serviceGroupConfigs);
    builder.append(", \r\n\tserverConfigs=");
    builder.append(serverConfigs);
    builder.append("\r\n]");
    return builder.toString();
  }

}

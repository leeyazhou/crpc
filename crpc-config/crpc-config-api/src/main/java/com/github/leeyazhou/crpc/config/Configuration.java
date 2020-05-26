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

import java.util.Objects;
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

  private RegistryConfig registryConfig;

  private ServerConfig serverConfig;

  public Configuration() {}

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

  public Configuration setApplicationConfig(ApplicationConfig applicationConfig) {
    this.applicationConfig = applicationConfig;
    return this;
  }

  public ApplicationConfig getApplicationConfig() {
    return applicationConfig;
  }


  public Configuration setRegistryConfig(RegistryConfig registryConfig) {
    this.registryConfig = registryConfig;
    return this;
  }

  public RegistryConfig getRegistryConfig() {
    return registryConfig;
  }


  public void validate() {
    Objects.requireNonNull(applicationConfig, "applicationConfig can't be null");
  }

}

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
import java.util.Map;
import java.util.Set;
import com.github.leeyazhou.crpc.core.util.StringUtil;

/**
 * 服务组定义
 * 
 * @author leeyazhou
 *
 */
public class ServiceGroupConfig {

  private String name;
  private String codec = "KRYO_CODEC";
  private int codecValue;
  private int worker = Math.max(Runtime.getRuntime().availableProcessors() * 2 + 1, 32);
  private String protocol;
  private String loadbalance = "RANDOM";
  private Set<ServerConfig> servers = new HashSet<ServerConfig>();
  private Set<RegistryConfig> registryConfigs = new HashSet<RegistryConfig>();
  /**
   * 超时时间/ms
   */
  private int timeout = 3000;

  private List<Map<String, Object>> filters;

  public String getName() {
    return name;
  }

  public ServiceGroupConfig setName(String name) {
    this.name = name;
    return this;
  }

  public String getProtocol() {
    return protocol;
  }

  public ServiceGroupConfig setProtocol(String protocol) {
    this.protocol = protocol;
    return this;
  }

  public String getCodec() {
    return codec;
  }

  public ServiceGroupConfig setCodec(String codec) {
    this.codec = codec;
    return this;
  }

  public int getTimeout() {
    return timeout;
  }

  public ServiceGroupConfig setTimeout(int timeout) {
    this.timeout = timeout;
    return this;
  }

  public List<Map<String, Object>> getFilters() {
    return filters;
  }

  public ServiceGroupConfig setFilters(List<Map<String, Object>> filters) {
    this.filters = filters;
    return this;
  }

  public Set<ServerConfig> getServers() {
    return servers;
  }

  public ServiceGroupConfig setServers(Set<ServerConfig> servers) {
    this.servers = servers;
    return this;
  }

  public ServiceGroupConfig addServerConfig(ServerConfig server) {
    this.servers.add(server);
    this.worker += server.getWorker();
    return this;
  }

  public int getWorker() {
    return worker;
  }

  public String getLoadbalance() {
    return loadbalance;
  }

  public ServiceGroupConfig setLoadbalance(String loadbalance) {
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
  public void setRegistryConfigs(Set<RegistryConfig> registryConfigs) {
    this.registryConfigs = registryConfigs;
  }

  /**
   * @param worker the worker to set
   */
  public void setWorker(int worker) {
    this.worker = worker;
  }

  public ServiceGroupConfig addRegistryConfig(RegistryConfig registryConfig) {
    this.registryConfigs.add(registryConfig);
    return this;
  }

  public int getCodecValue() {
    return codecValue;
  }

  public void setCodecValue(int codecValue) {
    this.codecValue = codecValue;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ServiceGroupConfig [name=");
    builder.append(name);
    builder.append(", codec=");
    builder.append(codec);
    builder.append(", codecValue=");
    builder.append(codecValue);
    builder.append(", worker=");
    builder.append(worker);
    builder.append(", protocol=");
    builder.append(protocol);
    builder.append(", loadbalance=");
    builder.append(loadbalance);
    builder.append(", servers=");
    builder.append(servers);
    builder.append(", timeout=");
    builder.append(timeout);
    builder.append(", filters=");
    builder.append(filters);
    builder.append("]");
    return builder.toString();
  }

  /**
   * 复制非空数据
   * 
   * @param serviceGroupConfig serviceGroupConfig
   * @return {@link ServiceGroupConfig}
   */
  public ServiceGroupConfig copyNotNull(ServiceGroupConfig serviceGroupConfig) {
    if (serviceGroupConfig == null) {
      return this;
    }
    if (codecValue != 0) {
      this.codecValue = serviceGroupConfig.getCodecValue();
    }
    if (StringUtil.isNotBlank(serviceGroupConfig.getName())) {
      this.name = serviceGroupConfig.getName();
    }
    if (this.codec == null && StringUtil.isNotBlank(serviceGroupConfig.getCodec())) {
      this.codec = serviceGroupConfig.getCodec();
    }
    if (StringUtil.isNotBlank(serviceGroupConfig.getProtocol())) {
      this.protocol = serviceGroupConfig.getProtocol();
    }
    if (!"RANDOM".equals(serviceGroupConfig.getLoadbalance())) {
      this.protocol = serviceGroupConfig.getLoadbalance();
    }

    if (worker != 0) {
      this.worker = serviceGroupConfig.getWorker();
    }
    if (!serviceGroupConfig.getServers().isEmpty()) {
      this.servers = serviceGroupConfig.getServers();
    }
    return this;
  }


}
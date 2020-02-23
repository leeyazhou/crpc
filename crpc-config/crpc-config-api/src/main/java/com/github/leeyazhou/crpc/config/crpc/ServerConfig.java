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
import java.util.TreeSet;

import com.github.leeyazhou.crpc.config.IConfig;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;

/**
 * @author lee
 */
public class ServerConfig implements IConfig, Comparable<ServerConfig> {

  private static final long serialVersionUID = 1L;
  private String name;
  private String host;
  private String address;
  private int port;
  private String protocol;
  // 权重
  private int weight = 1;
  private int worker = Runtime.getRuntime().availableProcessors() * 8;
  private Set<String> basepackages = new TreeSet<String>();
  private Set<String> filters = new HashSet<String>();
  private String location;
  private Set<URL> registries = new HashSet<URL>();

  /**
   * 处理器的同步/异步
   */
  private boolean sync;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public int getWorker() {
    return worker;
  }

  public void setWorker(int worker) {
    this.worker = worker;
  }

  public String getAddress() {
    return address;
  }

  public ServerConfig setAddress(String address) {
    this.address = address;
    if (address != null && address.length() != 0) {
      String[] add = address.replaceAll("/", "").split(":");
      this.protocol = add[0];
      setProtocol(add[0]);
      setHost(add[1]);
      setPort(Integer.parseInt(add[2]));
    }
    return this;
  }

  public Set<String> getFilters() {
    return filters;
  }

  public ServerConfig addFilter(String filtersStr) {
    String[] temp = filtersStr.split("[;|,]");
    for (String f : temp) {
      filters.add(f.trim());
    }
    return this;

  }

  public Set<String> getBasepackages() {
    return basepackages;
  }

  public void setBasepackages(Set<String> basepackages) {
    this.basepackages = basepackages;
  }

  public ServerConfig addBasepackage(String basepackage) {
    String[] tempPackages = basepackage.split("[;|,]");
    for (String pack : tempPackages) {
      if (!this.basepackages.contains(pack.trim())) {
        basepackages.add(pack.trim());
      }
    }
    return this;
  }

  /**
   * @return the location
   */
  public String getLocation() {
    return location;
  }

  /**
   * @param location
   *          the location to set
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * @return the registries
   */
  public Set<URL> getRegistries() {
    return registries;
  }

  /**
   * @param registries
   *          the registries to set
   */
  public void setRegistries(Set<URL> registries) {
    this.registries = registries;
  }

  public void addRegistry(String registryStr) {
    if (registryStr == null) {
      return;
    }
    String[] data = registryStr.replaceAll("/", "").split(":");
    if (data.length != 3) {
      return;
    }
    String protocol = data[0];
    String host = data[1];
    URL registryUrl = new URL(protocol, host, Integer.parseInt(data[2]));
    registryUrl.setRegistryType(protocol);
    this.registries.add(registryUrl);
  }

  /**
   * @param filters
   *          the filters to set
   */
  public void setFilters(Set<String> filters) {
    this.filters = filters;
  }

  /**
   * @return the weight
   */
  public int getWeight() {
    return weight;
  }

  /**
   * @param weight
   *          the weight to set
   */
  public void setWeight(int weight) {
    this.weight = weight;
  }

  /**
   * @return the sync
   */
  public boolean isSync() {
    return sync;
  }

  /**
   * @param sync
   *          the sync to set
   */
  public void setSync(boolean sync) {
    this.sync = sync;
  }

  public URL toURL() {
    URL url = new URL(protocol, host, port);
    url.addParameter(Constants.SERVER_WEIGHT, String.valueOf(weight));
    return url;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ServerConfig [name=");
    builder.append(name);
    builder.append(", host=");
    builder.append(host);
    builder.append(", address=");
    builder.append(address);
    builder.append(", port=");
    builder.append(port);
    builder.append(", protocol=");
    builder.append(protocol);
    builder.append(", weight=");
    builder.append(weight);
    builder.append(", worker=");
    builder.append(worker);
    builder.append(", basepackages=");
    builder.append(basepackages);
    builder.append(", filters=");
    builder.append(filters);
    builder.append(", location=");
    builder.append(location);
    builder.append(", registries=");
    builder.append(registries);
    builder.append(", sync=");
    builder.append(sync);
    builder.append("]");
    return builder.toString();
  }

  @Override
  public int compareTo(ServerConfig o) {
    if (this.host.equals(o.getHost()) && this.port == o.getPort()) {
      return 0;
    }
    return 1;
  }
}

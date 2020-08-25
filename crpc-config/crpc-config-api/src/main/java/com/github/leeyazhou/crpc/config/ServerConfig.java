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
import java.util.TreeSet;

/**
 * @author leeyazhou
 */
public class ServerConfig {
  private int worker = 200;
  private Set<String> basepackages = new TreeSet<String>();
  private Set<String> filters = new HashSet<String>();
  private int taskQueueSize;
  private int lowWaterMarker;
  private int highWaterMarker;

  public int getWorker() {
    return worker;
  }

  public ServerConfig setWorker(int worker) {
    this.worker = worker;
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

  public ServerConfig setBasepackages(Set<String> basepackages) {
    this.basepackages = basepackages;
    return this;
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


  public ServerConfig setTaskQueueSize(int taskQueueSize) {
    this.taskQueueSize = taskQueueSize;
    return this;
  }

  public int getTaskQueueSize() {
    return taskQueueSize;
  }

  public ServerConfig setFilters(Set<String> filters) {
    this.filters = filters;
    return this;
  }

  public int getLowWaterMarker() {
    return lowWaterMarker;
  }

  /**
   * low water marker
   * 
   * @param lowWaterMarker default: 32*1024
   * @return {@link ServerConfig}
   */
  public ServerConfig setLowWaterMarker(int lowWaterMarker) {
    this.lowWaterMarker = lowWaterMarker;
    return this;
  }

  public int getHighWaterMarker() {
    return highWaterMarker;
  }

  /**
   * high water marker
   * 
   * @param highWaterMarker default: 64*1024
   * @return {@link ServerConfig}
   */
  public ServerConfig setHighWaterMarker(int highWaterMarker) {
    this.highWaterMarker = highWaterMarker;
    return this;
  }



}

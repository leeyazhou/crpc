/**
 * Copyright © 2019 leeyazhou (coderhook@gmail.com)
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

package com.github.leeyazhou.crpc.core.object;

/**
 * @author lee
 */
public enum RegistryType {

  /**
   * ZooKeeper
   */
  ZOOKEEPER("com.github.crpc.registry.zookeeper.ZookeeperRegistryFactory"),

  /**
   * Redis
   */
  REDIS("com.github.crpc.registry.redis.RedisRegistryFactory");

  private String clazzName;

  /**
   * 
   */
  private RegistryType(String clazzName) {
    this.clazzName = clazzName;
  }

  /**
   * 实现类
   * 
   * @return the clazzName
   */
  public String getClazzName() {
    return clazzName;
  }

  public static RegistryType of(String protocol) {
    RegistryType[] values = values();
    for (RegistryType registryType : values) {
      if (registryType.toString().equalsIgnoreCase(protocol)) {
        return registryType;
      }
    }
    return null;
  }

}

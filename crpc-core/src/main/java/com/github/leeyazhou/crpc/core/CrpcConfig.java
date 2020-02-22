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

package com.github.leeyazhou.crpc.core;

/**
 * @author lee
 */
public class CrpcConfig {

  private static String configLocation;
  private static String deployLocation;

  /**
   * 配置文件地址, 如: /opt/crpc/services/userservice/conf/crpc.xml
   * 
   * @return 配置文件地址
   */
  public static String getConfigLocation() {
    return configLocation;
  }

  /**
   * 配置文件地址, 如: /opt/crpc/services/userservice/conf/crpc.xml
   * 
   * @param configLocation 配置文件地址
   */
  public static void setConfigLocation(String configLocation) {
    CrpcConfig.configLocation = configLocation;
  }

  /**
   * 当前服务部署地址, 如:/opt/crpc/services/userservice
   * 
   * @return 当前服务部署地址
   */
  public static String getDeployLocation() {
    return deployLocation;
  }

  /**
   * 当前服务部署地址, 如:/opt/crpc/services/userservice
   * 
   * @param deployLocation 当前服务部署地址
   */
  public static void setDeployLocation(String deployLocation) {
    CrpcConfig.deployLocation = deployLocation;
  }

}

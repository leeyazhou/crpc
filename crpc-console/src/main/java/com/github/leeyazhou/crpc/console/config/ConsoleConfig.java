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

package com.github.leeyazhou.crpc.console.config;

/**
 * @author lee
 */
public class ConsoleConfig {

  // packagespace=com.github.crpc
  // #WF.clustername=console.github.cn
  private static String packagespace;
  private static String clustername;
  private static String registryAddress;
  private static String registryName;
  private static String passwd;

  private static long start = System.currentTimeMillis();

  /**
   * @return the packagespace
   */
  public static String getPackagespace() {
    return packagespace;
  }

  /**
   * @param packagespace the packagespace to set
   */
  public static void setPackagespace(String packagespace) {
    ConsoleConfig.packagespace = packagespace;
  }

  /**
   * @return the clustername
   */
  public static String getClustername() {
    return clustername;
  }

  /**
   * @param clustername the clustername to set
   */
  public static void setClustername(String clustername) {
    ConsoleConfig.clustername = clustername;
  }

  /**
   * @return the registryAddress
   */
  public static String getRegistryAddress() {
    return registryAddress;
  }

  /**
   * @param registryAddress the registryAddress to set
   */
  public static void setRegistryAddress(String registryAddress) {
    ConsoleConfig.registryAddress = registryAddress;
  }

  /**
   * @return the registryName
   */
  public static String getRegistryName() {
    return registryName;
  }

  /**
   * @param registryName the registryName to set
   */
  public static void setRegistryName(String registryName) {
    ConsoleConfig.registryName = registryName;
  }

  /**
   * @return the passwd
   */
  public static String getPasswd() {
    return passwd;
  }

  /**
   * @param passwd the passwd to set
   */
  public static void setPasswd(String passwd) {
    ConsoleConfig.passwd = passwd;
  }

  public static long getSystemStartTime() {
    return start;
  }

  @Override
  public String toString() {
    return "ConsoleConfig [packagespace=" + packagespace + ", clustername=" + clustername + ", registryAddress=" + registryAddress + "]";
  }

}

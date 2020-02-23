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
package com.github.leeyazhou.crpc.core.util;

import java.io.InputStream;
import java.util.Properties;

public class ServerInfo {

  /**
   * The server information String with which we identify ourselves.
   */
  private static String serverInfo = null;

  static {

    try {
      InputStream is = ServerInfo.class.getResourceAsStream("/META-INF/crpc/internal/common.properties");
      Properties props = new Properties();
      props.load(is);
      is.close();
      serverInfo = props.getProperty("crpc-version");
    } catch (Throwable t) {
      System.out.println("error " + t.getMessage());
    }

  }

  /**
   * 
   * @return Return the server identification for this version of CRPC.
   */
  public static String getServerInfo() {
    return (serverInfo);
  }

  public static void main(String args[]) {
    System.out.println("Server version \t: " + getServerInfo());
    System.out.println("OS Name \t: " + System.getProperty("os.name"));
    System.out.println("OS Version \t: " + System.getProperty("os.version"));
    System.out.println("Architecture \t: " + System.getProperty("os.arch"));
    System.out.println("JVM Version \t: " + System.getProperty("java.runtime.version"));
    System.out.println("JVM Vendor \t: " + System.getProperty("java.vm.vendor"));
  }

}

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

package com.github.leeyazhou.crpc.console.inits;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import javax.annotation.Resource;
import com.github.leeyazhou.crpc.console.config.ConsoleConfig;

/**
 * @author lee
 */
@Resource
public class ConsoleInit {

  /**
   * 
   */
  public ConsoleInit() {
    init();
  }

  public void init() {
    try {
      doInit();
      ZookeeperInit.getInstance().init();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private void doInit() throws Exception {

    URL url = Thread.currentThread().getContextClassLoader().getResource("META-INF/space.properties");
    System.out.println("初始化url : " + url);
    if (url == null) {
      return;
    }

    Properties properties = new Properties();
    properties.load(new FileInputStream(url.getPath()));
    String packagespace = properties.getProperty("packagespace");
    String clustername = properties.getProperty("WF.clustername");
    String registryAddress = properties.getProperty("registry.address");
    String registryName = properties.getProperty("registry.name");
    String adminPasswd = properties.getProperty("root.passwd");
    ConsoleConfig.setPackagespace(packagespace);
    ConsoleConfig.setClustername(clustername);
    ConsoleConfig.setRegistryAddress(registryAddress);
    ConsoleConfig.setRegistryName(registryName);
    ConsoleConfig.setPasswd(adminPasswd);
  }

}

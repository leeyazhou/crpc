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

package com.github.leeyazhou.crpc.registry.zookeeper;

import java.util.List;

import org.junit.Test;

import com.github.leeyazhou.crpc.registry.Registry;
import com.github.leeyazhou.crpc.registry.RegistryFactory;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;

/**
 * @author lee
 */
public class ZookeeperRegistryFactoryTest {

  private String host = "127.0.0.1";

  @Test
  public void testRegister() throws InterruptedException {
    URL zkurl = new URL("zookeeper", host, 2181);

    RegistryFactory factory = new ZookeeperRegistryFactory();
    Registry registry = factory.createRegistry(zkurl);

    URL serviceUrl = new URL("tcp", "127.0.0.1", 12200);
    serviceUrl.addParameter(Constants.SERVICE_INTERFACE, "com.xyz.service.UserService");
    serviceUrl.addParameter(Constants.APPLICATION, "userservice");
    registry.register(serviceUrl);
    Thread.sleep(200000);
    registry.close();
  }

  @Test
  public void testGetProviders() throws Exception {
    URL url = new URL("zookeeper", host, 2181);
    url.addParameter("serviceInterface", "com.xyz.service.UserService");
    url.addParameter(Constants.APPLICATION, "reportservice");
    RegistryFactory factory = new ZookeeperRegistryFactory();
    Registry registry = factory.createRegistry(url);
    int ii = 0;
    while (ii++ < 100) {
      System.out.println("测试 " + ii);
      List<URL> providers = registry.getProviders(null);
      for (URL item : providers) {
        System.out.println(item);
      }
      Thread.sleep(3000);
    }
    registry.close();
  }
}

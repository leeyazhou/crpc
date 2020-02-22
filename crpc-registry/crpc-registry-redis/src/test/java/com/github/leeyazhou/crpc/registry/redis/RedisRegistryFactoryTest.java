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

package com.github.leeyazhou.crpc.registry.redis;

import java.util.List;

import com.github.leeyazhou.crpc.registry.RegistryFactory;
import org.junit.Test;

import com.github.leeyazhou.crpc.registry.Registry;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.util.InetAddressUtil;

import redis.clients.jedis.Jedis;

/**
 * @author lee
 */
public class RedisRegistryFactoryTest {

  private String serviceInterface = "com.github.crpc.commonservice.service.UserService";

  @Test
  public void testRegister() throws Exception {
    URL redisUrl = new URL("redis", "127.0.0.1", 6399);
    RegistryFactory factory = new RedisRegistryFactory();
    Registry registry = factory.createRegistry(redisUrl);
    String host = InetAddressUtil.getLocalHost();
    URL serviceUrl = new URL("tcp", host, 12200);
    serviceUrl.addParameter(Constants.SERVICE_INTERFACE, serviceInterface);
    serviceUrl.addParameter(Constants.APPLICATION, "userservice");
    serviceUrl.addParameter(Constants.TIMESTAMP_KEY, String.valueOf(System.currentTimeMillis()));
    registry.register(serviceUrl);
    // Thread.sleep(60000);
    registry.close();
  }

  @Test
  public void testGetProviders() throws Exception {
    URL redisUrl = new URL("redis", "127.0.0.1", 6399);
    RegistryFactory factory = new RedisRegistryFactory();
    Registry registry = factory.createRegistry(redisUrl);
    String host = InetAddressUtil.getLocalHost();
    URL serviceUrl = new URL("tcp", host, 12200);
    serviceUrl.addParameter(Constants.SERVICE_INTERFACE, serviceInterface);

    List<URL> result = registry.getProviders(null);
    System.out.println(result);
    Thread.sleep(60000);
  }

  @Test
  public void testRedis() {
    Jedis jedis = new Jedis("127.0.0.1", 6399);
    int i = 0;
    while (i++ < 100000) {
      jedis.hset("crpc:127.0.0.1-6399" + i, "name" + i, "liyazhou" + i);
    }
    jedis.close();
  }

}

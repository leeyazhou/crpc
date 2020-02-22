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

package com.github.leeyazhou.crpc.demo.consumer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.leeyazhou.crpc.demo.model.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.leeyazhou.crpc.config.crpc.ServerConfig;
import com.github.leeyazhou.crpc.config.crpc.ServiceGroupConfig;
import com.github.leeyazhou.crpc.demo.service.UserService;
import com.github.leeyazhou.crpc.rpc.ProxyFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;

/**
 * @author lee
 */
public class Consumer {

  private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

  private UserService userService;

  private UserService getUserService() {
    if (userService != null) {
      return userService;
    }
    ServiceGroupConfig serviceGroupConfig = new ServiceGroupConfig().setName("userservice");
    serviceGroupConfig.setTimeout(3000);
    serviceGroupConfig.addServerConfig(new ServerConfig().setAddress("tcp://127.0.0.1:12200"));
    userService = ServiceLoader.load(ProxyFactory.class).load().getProxy(UserService.class, serviceGroupConfig);
    return userService;
  }

  @Test
  public void testConsumer() throws Exception {
    long start = System.currentTimeMillis();
    int num = 200;
    for (int i = 0; i < num; i++) {
      try {
        User user = new User();
        user.setId(i);
        user.setDeleted(i % 2 == 0);
        user.setSex(1);
        user.setUsername("CRPC - " + i);
        boolean flag = false;
        flag = getUserService().say(user);
        getUserService().say(user, null);
        getUserService().doNothing(user);
        getUserService().say(i);
        getUserService().getUser(i);
        logger.info(user + ", result ： " + flag);
      } catch (Exception e) {
        e.printStackTrace();
      }
      Thread.sleep(4000);
    }

    long end = System.currentTimeMillis() - start;
    end = end >= 1000 ? end / 1000 : 1;
    System.out.println("cost time : " + end + " ms, TPS : " + (num / (end)));
  }

  @Test
  public void testConsumer2() {
    boolean flag = getUserService().sayWord("David");
    System.out.println("发送成功,返回结果sayWord" + flag);
    flag = getUserService().say(new User(205, 1, "CRPC"));
    System.out.println("发送成功,返回结果say" + flag);
  }

  @Test
  public void testComplexObject() {
    Map<String, List<User>> complexObj = new HashMap<String, List<User>>();
    List<User> users = new ArrayList<User>();
    int i = 0;
    while (i++ < 40000) {
      User user = new User(26, 1, "CRPC");
      users.add(user);
    }
    complexObj.put("users", users);
    System.out.println("编码:" + complexObj.size());
    Map<String, List<User>> result = getUserService().complexObject(complexObj);
    int j = 0;
    while (j++ < 10) {
      long start = System.currentTimeMillis();
      result = userService.complexObject(complexObj);
      System.out.println("耗时:" + (System.currentTimeMillis() - start) + "ms");
    }
    System.out.println("解码:" + result.size() + ",  " + result.get("users").size());
  }

  @Test
  public void testBigData() throws FileNotFoundException, IOException {
    int byteSize = 1024; // 1kb
    byteSize *= 1024; // 1m
    byteSize *= 100; // 100m

    byte[] data = new byte[byteSize];
    data = getUserService().bigData(data);
    System.out.println("收到回复" + data.length);
  }

  @Test
  public void testProxyUserService2() {
    getUserService().proxyUserService2();
  }
}

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
package com.github.leeyazhou.crpc.example.quickstart;

import com.github.leeyazhou.crpc.config.ServiceGroupConfig;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.rpc.proxy.ProxyFactory;
import com.github.leeyazhou.crpc.service.UserService;

/**
 * @author leeyazhou
 *
 */
public class ConsumerMain {

  public static void main(String[] args) {
    ProxyFactory proxyFactory = ServiceLoader.load(ProxyFactory.class).load();
    ServiceGroupConfig serviceGroupConfig = new ServiceGroupConfig().addProvider(URL.valueOf("crpc://127.0.0.1:25001"));
    UserService userService = proxyFactory.getProxy(UserService.class, serviceGroupConfig);
    userService.sayWord("CRPC");
  }
}

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
package com.github.leeyazhou.crpc.config.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.github.leeyazhou.crpc.config.ApplicationConfig;
import com.github.leeyazhou.crpc.config.spring.demo.service.UserService;
import com.github.leeyazhou.crpc.rpc.Handler;

/**
 * 
 * @author leeyazhou
 */
public class Test {

  public static void main(String[] args) {
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-*.xml");
    ApplicationConfig moduleConfig = applicationContext.getBean(ApplicationConfig.class);
    System.out.println(moduleConfig);
    Handler<UserService> h = applicationContext.getBean(SpringBeanFactory.class).getServiceHandler(UserService.class.getName());
    System.out.println(h);
    UserService userService = (UserService) applicationContext.getBean("userService");
    System.out.println(userService);
    userService.foo();
    userService.proxy();

    applicationContext.close();
  }
}

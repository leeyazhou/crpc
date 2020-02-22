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

package com.github.leeyazhou.crpc.bootstrap.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.leeyazhou.crpc.bootstrap.Bootstrap;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

/**
 * @author lee
 *
 */
public class SpringBootstrap extends Bootstrap {
  private static final Logger logger = LoggerFactory.getLogger(SpringBootstrap.class);

  @Override
  public void doStartup() {
    final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-*.xml");
    applicationContext.start();
    while (true) {
      try {
        Thread.sleep(Integer.MAX_VALUE);
      } catch (InterruptedException e) {
        logger.error("", e);
        applicationContext.close();
      }
    }

  }

}
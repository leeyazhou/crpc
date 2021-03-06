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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.transport.factory.CrpcServerFactory;

/**
 * @author leeyazhou
 */
public class SpringBeanFactory extends CrpcServerFactory implements ApplicationContextAware {
  private static final Logger logger = LoggerFactory.getLogger(SpringBeanFactory.class);
  private ApplicationContext applicationContext;
  

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
    if (logger.isInfoEnabled()) {
      logger.info("初始化CRPC BeanFactory, springApplicationContext : " + this.applicationContext);
    }
    this.setConfiguration(applicationContext.getBean(com.github.leeyazhou.crpc.config.Configuration.class));
  }



}

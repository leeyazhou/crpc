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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.ReferConfig;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.rpc.util.RpcUtil;

/**
 * 依赖类
 * 
 * @author leeyazhou
 */
public class ReferFactoryBean<T> extends ReferConfig<T>
    implements FactoryBean<T>, InitializingBean, DisposableBean, ApplicationContextAware {
  protected static final Logger logger = LoggerFactory.getLogger(ReferFactoryBean.class);
  protected T ref;
  protected Class<T> objectType;
  protected ApplicationContext applicationContext;
  protected Configuration configuration;

  @Override
  public T getObject() throws Exception {
    return getReference();
  }

  @Override
  public Class<?> getObjectType() {
    return objectType;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    getReference();
  }

  private T getReference() {
    this.ref = RpcUtil.refer(this);
    return ref;
  }

  @SuppressWarnings("unchecked")
  public void setInterface(String interfaceStr) throws ClassNotFoundException {
    this.objectType = (Class<T>) Class.forName(interfaceStr);
  }

  /**
   * @param objectType the objectType to set
   */
  public void setObjectType(Class<T> objectType) {
    this.objectType = objectType;
  }

  @Override
  public void destroy() throws Exception {
    RpcUtil.unrefer();
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
    this.configuration = applicationContext.getBean(Configuration.class);
  }

  public void setAddress(String address) {
    String[] addresses =
        StringUtils.tokenizeToStringArray(address, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
    for (String a : addresses) {
      if (StringUtils.isEmpty(a)) {
        continue;
      }
      addUrl(URL.valueOf(a));
    }
  }
}

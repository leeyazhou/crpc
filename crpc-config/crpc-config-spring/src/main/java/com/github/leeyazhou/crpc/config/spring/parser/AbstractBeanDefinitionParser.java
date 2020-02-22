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
package com.github.leeyazhou.crpc.config.spring.parser;

import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.w3c.dom.Element;

import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

/**
 * @author lee
 *
 */
public abstract class AbstractBeanDefinitionParser implements BeanDefinitionParser {

  private static final Logger logger = LoggerFactory.getLogger(AbstractBeanDefinitionParser.class);

  void parsePropertyValue(Element element, BeanDefinition beanDefinition) {
    Set<String> keys = getPropertyNames();
    if (keys == null) {
      return;
    }
    for (String key : keys) {
      String value = element.getAttribute(key);
      if (value == null || value.length() == 0) {
        continue;
      }
      if (logger.isDebugEnabled()) {
        logger.debug("property " + key + " \t: " + value);
      }
      beanDefinition.getPropertyValues().addPropertyValue(key, value);
    }
  }

  /**
   * 获取标签属性名称
   * 
   * @return
   */
  abstract Set<String> getPropertyNames();
}

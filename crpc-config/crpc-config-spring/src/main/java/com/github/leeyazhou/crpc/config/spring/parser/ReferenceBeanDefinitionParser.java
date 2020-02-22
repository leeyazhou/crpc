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
package com.github.leeyazhou.crpc.config.spring.parser;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.github.leeyazhou.crpc.config.spring.ReferenceFactoryBean;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.StringUtil;

/**
 * 
 * @author <a href="mailto:lee_yazhou@163.com">Yazhou Li</a>
 */
public class ReferenceBeanDefinitionParser extends AbstractBeanDefinitionParser {
  static final Logger logger = LoggerFactory.getLogger(ReferenceBeanDefinitionParser.class);
  static final String NAME = "name";
  static final String INTERFACE = "interface";
  static final String ADDRESS = "address";
  static final String SERVICE_GROUP = "serviceGroup";

  @Override
  public BeanDefinition parse(Element element, ParserContext parserContext) {
    logger.info("解析crpc reference");
    String beanName = element.getAttribute(NAME);
    String serviceGroup = element.getAttribute(SERVICE_GROUP);
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ReferenceFactoryBean.class);
    parsePropertyValue(element, beanDefinition);
    if (StringUtil.isNotBlank(serviceGroup)) {
      beanDefinition.getPropertyValues().addPropertyValue("name", serviceGroup);
      beanDefinition.getPropertyValues().addPropertyValue(SERVICE_GROUP, new RuntimeBeanReference(serviceGroup));
    }
    parserContext.getRegistry().registerBeanDefinition(beanName, beanDefinition);
    return beanDefinition;
  }

  @Override
  Set<String> getPropertyNames() {
    Set<String> ret = new HashSet<String>();
    ret.add(INTERFACE);
    ret.add(ADDRESS);
    return ret;
  }

}

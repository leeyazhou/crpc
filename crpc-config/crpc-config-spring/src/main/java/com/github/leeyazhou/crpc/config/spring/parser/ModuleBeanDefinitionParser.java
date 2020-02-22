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
package com.github.leeyazhou.crpc.config.spring.parser;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.github.leeyazhou.crpc.config.crpc.ModuleConfig;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

/**
 * 
 * @author <a href="mailto:lee_yazhou@163.com">Yazhou Li</a>
 */
public class ModuleBeanDefinitionParser extends AbstractBeanDefinitionParser {
  private static final Logger logger = LoggerFactory.getLogger(ModuleBeanDefinitionParser.class);
  private static final String NAME = "name";
  private static final String VERSION = "version";
  private static final String ADDRESS = "address";
  private static final String WORKER = "worker";

  @Override
  public BeanDefinition parse(Element element, ParserContext parserContext) {
    logger.info("解析crpc module");

    BeanDefinition beanDefinition = new RootBeanDefinition(ModuleConfig.class);
    beanDefinition.setLazyInit(false);

    parsePropertyValue(element, beanDefinition);

    parserContext.getRegistry().registerBeanDefinition(ModuleConfig.class.getSimpleName(), beanDefinition);
    return beanDefinition;
  }

  @Override
  Set<String> getPropertyNames() {
    Set<String> ret = new HashSet<String>();
    ret.add(NAME);
    ret.add(VERSION);
    ret.add(ADDRESS);
    ret.add(WORKER);
    return ret;
  }

}

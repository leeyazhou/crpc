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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.github.leeyazhou.crpc.registry.RegistryFactory;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;

/**
 * 
 * @author <a href="mailto:lee_yazhou@163.com">Yazhou Li</a>
 */
public class RegistryBeanDefinitionParser implements BeanDefinitionParser {
  static final Logger logger = LoggerFactory.getLogger(RegistryBeanDefinitionParser.class);
  static final String NAME = "name";
  static final String ADDRESS = "address";

  @Override
  public BeanDefinition parse(Element element, ParserContext parserContext) {
    logger.info("解析crpc registry");
    String address = element.getAttribute(ADDRESS);
    String addressStr[] = address.replaceAll("\\/", "").split(":");
    String protocol = addressStr[0];
    String host = addressStr[1];
    Integer port = Integer.parseInt(addressStr[2]);

    RegistryFactory registryFactory = ServiceLoader.load(RegistryFactory.class).load(protocol);

    URL registryUrl = new URL(protocol, host, port);
    registryFactory.createRegistry(registryUrl);
    return null;
  }

}

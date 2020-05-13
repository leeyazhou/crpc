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
package com.github.leeyazhou.crpc.config.spring.handler;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import com.github.leeyazhou.crpc.config.spring.parser.ComponentScanBeanDefinitionParser;
import com.github.leeyazhou.crpc.config.spring.parser.ApplicationBeanDefinitionParser;
import com.github.leeyazhou.crpc.config.spring.parser.ProtocolBeanDefinitionParser;
import com.github.leeyazhou.crpc.config.spring.parser.ReferenceBeanDefinitionParser;
import com.github.leeyazhou.crpc.config.spring.parser.RegistryBeanDefinitionParser;

/**
 * 
 * @author leeyazhou
 */
public class CRPCNamespaceHandler extends NamespaceHandlerSupport {

  @Override
  public void init() {
    registerBeanDefinitionParser("module", new ApplicationBeanDefinitionParser());
    registerBeanDefinitionParser("protocol", new ProtocolBeanDefinitionParser());
    registerBeanDefinitionParser("registry", new RegistryBeanDefinitionParser());
    registerBeanDefinitionParser("ref", new ReferenceBeanDefinitionParser());
    registerBeanDefinitionParser("component-scan", new ComponentScanBeanDefinitionParser());
  }

}

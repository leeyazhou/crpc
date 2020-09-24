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
package com.github.leeyazhou.crpc.config.parser;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.FieldUtil;

/**
 * @author leeyazhou
 *
 */
public abstract class AbstractParser<T> implements IParser<T> {
  static final Logger logger = LoggerFactory.getLogger(AbstractParser.class);
  protected Configuration configuration;

  public AbstractParser(Configuration configuration) {
    this.configuration = configuration;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  protected void parseProperties(Element element, Object target) {
    NamedNodeMap nodeMap = element.getAttributes();
    for (int i = 0; i < nodeMap.getLength(); i++) {
      Node node = nodeMap.item(i);
      String name = node.getNodeName();
      String value = node.getNodeValue();
      FieldUtil.convertValue(name, value, target);
    }
  }

}

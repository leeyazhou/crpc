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
import org.w3c.dom.Node;
import com.github.leeyazhou.crpc.config.AbstractParser;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.ProtocolConfig;

/**
 * @author leeyazhou
 *
 */
public class ProtocolConfigParser extends AbstractParser<ProtocolConfig> {

  /**
   * @param configuration {@link Configuration}
   */
  public ProtocolConfigParser(Configuration configuration) {
    super(configuration);
  }

  @Override
  public ProtocolConfig parse(Node node) {
    ProtocolConfig protocolConfig = new ProtocolConfig();
    parseProperties((Element) node, protocolConfig);
    return protocolConfig;
  }

}

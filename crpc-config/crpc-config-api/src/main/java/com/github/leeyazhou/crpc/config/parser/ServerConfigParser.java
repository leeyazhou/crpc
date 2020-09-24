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
import org.w3c.dom.NodeList;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.ServerConfig;

/**
 * @author leeyazhou
 */
public class ServerConfigParser extends AbstractParser<ServerConfig> {

  public ServerConfigParser(Configuration configuration) {
    super(configuration);
  }

  @Override
  public ServerConfig parse(Node node) {
    ServerConfig serverConfig = new ServerConfig();
    Element rootElement = (Element) node;

    parseProperties(rootElement, serverConfig);


    parseScan(rootElement, serverConfig);

    return serverConfig;
  }


  /**
   * parse scan base package
   * 
   * @param rootElement root element : crpc
   * @param serverConfig serverConfig
   */
  private void parseScan(Element rootElement, ServerConfig serverConfig) {
    NodeList nodeList = rootElement.getElementsByTagName("scan");
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node scanNode = nodeList.item(i);
      Node basepackage = scanNode.getAttributes().getNamedItem("basepackage");
      Node filter = scanNode.getAttributes().getNamedItem("filter");
      if (basepackage != null && basepackage.getNodeValue() != null) {
        serverConfig.addBasepackage(basepackage.getNodeValue());
      }
      if (filter != null && filter.getNodeValue() != null) {
        serverConfig.addFilter(filter.getNodeValue());
      }
    }
  }

}

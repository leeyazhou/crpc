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

package com.github.leeyazhou.crpc.config.parser;

import com.github.leeyazhou.crpc.config.crpc.ServerConfig;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.leeyazhou.crpc.config.IParser;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.FieldUtil;

/**
 * @author lee
 */
public class ServerParser implements IParser<ServerConfig> {

  private static final long serialVersionUID = -5024440315331487971L;
  private static final Logger logger = LoggerFactory.getLogger(ServerParser.class);

  @Override
  public ServerConfig parse(Node node) {
    ServerConfig serverConfig = new ServerConfig();
    Element rootElement = (Element) node;
    NamedNodeMap nodeMap = rootElement.getAttributes();

    for (int i = 0; i < nodeMap.getLength(); i++) {
      String nodeName = nodeMap.item(i).getNodeName();
      Object nodeValue = nodeMap.item(i).getNodeValue();
      if (logger.isDebugEnabled()) {
        logger.debug("property " + nodeName + " : " + nodeValue);
      }
      FieldUtil.convertValue(nodeName, nodeValue, serverConfig);
    }

    parseRegistry(rootElement, serverConfig);

    parseScan(rootElement, serverConfig);

    return serverConfig;
  }

  /**
   * parse registry center
   * 
   * @param rootElement
   *          root element : crpc
   * @param serverConfig
   *          serverConfig
   */
  private void parseRegistry(Element rootElement, ServerConfig serverConfig) {
    NodeList registryNodeList = rootElement.getElementsByTagName("registry");
    for (int i = 0; i < registryNodeList.getLength(); i++) {
      Node registryNode = registryNodeList.item(i);
      Node addressNode = registryNode.getAttributes().getNamedItem("address");
      if (addressNode == null) {
        continue;
      }
      String address = addressNode.getNodeValue();
      if (logger.isDebugEnabled()) {
        logger.debug("server register center : " + address);
      }
      serverConfig.addRegistry(address);
    }
  }

  /**
   * parse scan base package
   * 
   * @param rootElement
   *          root element : crpc
   * @param serverConfig
   *          serverConfig
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

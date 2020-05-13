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
import org.w3c.dom.NodeList;
import com.github.leeyazhou.crpc.config.AbstractParser;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.ServiceGroupConfig;
import com.github.leeyazhou.crpc.core.util.FieldUtil;

/**
 * @author leeyazhou
 *
 */
public class ServiceGroupParser extends AbstractParser<ServiceGroupConfig> {


  public ServiceGroupParser(Configuration configuration) {
    super(configuration);
  }

  @Override
  public ServiceGroupConfig parse(Node node) {
    ServiceGroupConfig serviceConfig = new ServiceGroupConfig();
    NamedNodeMap nodeMap = node.getAttributes();

    Element rootElement = (Element) node;
    RegistryConfigParser registryConfigParser = new RegistryConfigParser(getConfiguration());
    NodeList registryNodeList = rootElement.getElementsByTagName("registry");
    for (int i = 0; i < registryNodeList.getLength(); i++) {
      Node registryNode = registryNodeList.item(i);
      serviceConfig.addRegistryConfig(registryConfigParser.parse(registryNode));
    }

    NodeList moduleList = rootElement.getElementsByTagName("server");
    ServerConfigParser serverParser = new ServerConfigParser(getConfiguration());
    for (int i = 0; i < moduleList.getLength(); i++) {
      serviceConfig.addServerConfig(serverParser.parse(moduleList.item(i)));
    }

    for (int i = 0; i < nodeMap.getLength(); i++) {
      String nodeName = nodeMap.item(i).getNodeName();
      Object nodeValue = nodeMap.item(i).getNodeValue();
      FieldUtil.convertValue(nodeName, nodeValue, serviceConfig);
    }

    return serviceConfig;
  }

}

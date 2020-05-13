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
import com.github.leeyazhou.crpc.config.AbstractParser;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.ServiceGroupConfig;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.util.StringUtil;

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

    Element rootElement = (Element) node;
    parseProperties(rootElement, serviceConfig);

    NodeList moduleList = rootElement.getElementsByTagName("server");
    for (int i = 0; i < moduleList.getLength(); i++) {
      Element providerNode = (Element) moduleList.item(i);
      String address = providerNode.getAttribute("address");
      URL provider = URL.valueOf(address);
      String weight = providerNode.getAttribute("weight");
      if (StringUtil.isNotBlank(weight)) {
        provider.addParameter("weight", weight);
      }

      serviceConfig.addProvider(provider);
    }


    return serviceConfig;
  }

}

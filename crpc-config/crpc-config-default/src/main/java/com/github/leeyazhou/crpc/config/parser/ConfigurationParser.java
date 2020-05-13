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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.IParser;
import com.github.leeyazhou.crpc.core.exception.CrpcException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

/**
 * @author leeyazhou
 */
public class ConfigurationParser implements IParser<Configuration> {

  private static final Logger logger = LoggerFactory.getLogger(ConfigurationParser.class);
  private String location;

  public ConfigurationParser() {}

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public ConfigurationParser(String location) {
    this.location = location;
  }

  /**
   * 根据location解析配置文件，解析步骤如下：<br>
   * 1. location是绝对地址，则直接解析<br>
   * 2. location是相对classpath的地址，则获取配置文件的绝对地址后解析<br>
   * 3. location是classpath的conf路径下的相对地址，则获取配置文件的绝对地址后解析<br>
   * 4. location是部署目录配置文件目录conf的文件，则获取配置文件绝对地址后解析
   * 
   * @return {@link Configuration}
   * @throws Exception any exception
   */
  private Element doParse() throws Exception {
    if (null == location || location.length() == 0) {
      throw new CrpcException("configuration location can not be null, location : " + location);
    }
    if (!new File(location).exists()) {
      URL locationUrl = Thread.currentThread().getContextClassLoader().getResource(location);
      if (locationUrl == null) {
        locationUrl = Thread.currentThread().getContextClassLoader().getResource("conf/" + location);
      }
      if (locationUrl != null && new File(locationUrl.getPath()).exists()) {
        this.location = locationUrl.getPath();
      } else {
        String rootPath = Configuration.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        int index = rootPath.lastIndexOf(File.separatorChar);
        if (index > 0) {
          rootPath = rootPath.substring(0, index);
          this.location = rootPath + "/../conf/" + location;
        }
      }
    }
    logger.info("crpc configration location : " + location);

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    InputStream inputStream = new FileInputStream(location);
    Document document = db.parse(inputStream);
    Element rootElement = document.getDocumentElement();
    return rootElement;
  }

  @Override
  public Configuration parse(Node rootNode) {
    Element rootElement = null;
    try {
      rootElement = doParse();
    } catch (Exception err) {
      logger.error("Configuration parse exception, location : " + location, err);
      System.exit(0);
    }
    Configuration configuration = new Configuration();

    NodeList nodeList = rootElement.getChildNodes();
    ServiceGroupParser serviceGroupConfigParser = new ServiceGroupParser(configuration);
    ServerConfigParser serverParser = new ServerConfigParser(configuration);
    ApplicationConfigParser applicationConfigParser = new ApplicationConfigParser(configuration);
    RegistryConfigParser registryConfigParser = new RegistryConfigParser(configuration);
    ProtocolConfigParser protocolConfigParser = new ProtocolConfigParser(configuration);
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node item = nodeList.item(i);
      if ("service".equals(item.getNodeName())) {
        configuration.addServiceGroupConfig(serviceGroupConfigParser.parse(item));
      } else if ("server".equals(item.getNodeName())) {
        configuration.setServerConfig(serverParser.parse(item));
      } else if ("application".equals(item.getNodeName())) {
        configuration.setApplicationConfig(applicationConfigParser.parse(item));
      } else if ("registry".equals(item.getNodeName())) {
        configuration.addRegistryConfig(registryConfigParser.parse(item));
      } else if ("protocol".equals(item.getNodeName())) {
        configuration.setProtocolConfig(protocolConfigParser.parse(item));
      }

    }

    return configuration;
  }

}

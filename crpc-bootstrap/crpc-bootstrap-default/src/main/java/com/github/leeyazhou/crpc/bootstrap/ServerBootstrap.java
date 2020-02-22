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

package com.github.leeyazhou.crpc.bootstrap;

import com.github.leeyazhou.crpc.config.crpc.Configuration;
import com.github.leeyazhou.crpc.config.crpc.ProviderConfig;
import com.github.leeyazhou.crpc.config.crpc.ServerConfig;
import com.github.leeyazhou.crpc.config.parser.ConfigurationParser;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.CrpcConfig;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.StringUtil;

/**
 * @author lee
 *
 */
public class ServerBootstrap extends Bootstrap {
  private static final Logger logger = LoggerFactory.getLogger(ServerBootstrap.class);
  private static String location = "crpc.xml";
  private static String deployPath = null;

  @Override
  public void doStartup() {
    String crpcHome = System.getProperty("crpc.home");
    String serverName = System.getProperty("crpc.servername");

    if (StringUtil.isNotBlank(crpcHome) && StringUtil.isNotBlank(serverName)) {
      deployPath = crpcHome + Constants.FILE_SEPARATOR + "services" + Constants.FILE_SEPARATOR + serverName;
      location = deployPath + "/conf/" + location;
    } else {
      deployPath = ServerBootstrap.class.getResource("/").getPath();
    }
    CrpcConfig.setDeployLocation(deployPath);

    Configuration configuration = parseConfiguration();

    for (ServerConfig serverConfig : configuration.getServerConfigs()) {
      serverConfig.setLocation(deployPath);
      ProviderConfig providerConfig = new ProviderConfig();
      providerConfig.addServer(serverConfig);
      providerConfig.export();
    }

  }

  private Configuration parseConfiguration() {
    ConfigurationParser configurationParser = new ConfigurationParser(location);
    Configuration configuration = configurationParser.parse(null);
    return configuration;
  }

}

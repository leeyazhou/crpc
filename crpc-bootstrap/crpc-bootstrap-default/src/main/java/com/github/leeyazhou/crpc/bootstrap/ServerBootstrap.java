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

package com.github.leeyazhou.crpc.bootstrap;

import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.ProviderConfig;
import com.github.leeyazhou.crpc.config.parser.ConfigurationParser;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.CrpcConfig;
import com.github.leeyazhou.crpc.core.util.StringUtil;

/**
 * @author leeyazhou
 *
 */
public class ServerBootstrap extends Bootstrap {
  private static String location = "crpc.xml";
  private static String deployPath = null;

  @Override
  public void doStartup() {
    String crpcHome = System.getProperty("crpc.home");
    String serverName = System.getProperty("crpc.servername");

    if (StringUtil.isNotBlank(crpcHome) && StringUtil.isNotBlank(serverName)) {
      deployPath = crpcHome + Constants.FILE_SEPARATOR + "service" + Constants.FILE_SEPARATOR + serverName;
      location = deployPath + "/conf/" + location;
    } else {
      deployPath = ServerBootstrap.class.getResource("/").getPath();
    }
    CrpcConfig.setDeployLocation(deployPath);

    Configuration configuration = parseConfiguration();

    ProviderConfig providerConfig = new ProviderConfig();
    providerConfig.setConfiguration(configuration);
    providerConfig.export();

  }

  private Configuration parseConfiguration() {
    ConfigurationParser configurationParser = new ConfigurationParser(location);
    Configuration configuration = configurationParser.parse(null);
    return configuration;
  }

}

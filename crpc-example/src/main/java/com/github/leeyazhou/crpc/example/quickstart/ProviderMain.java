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
package com.github.leeyazhou.crpc.example.quickstart;

import com.github.leeyazhou.crpc.config.ApplicationConfig;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.ProtocolConfig;
import com.github.leeyazhou.crpc.config.ServerConfig;
import com.github.leeyazhou.crpc.config.crpc.ProviderConfig;

/**
 * @author leeyazhou
 *
 */
public class ProviderMain {

  public static void main(String[] args) {
    ApplicationConfig applicationConfig = new ApplicationConfig().setName("quick-start-provider").setVersion("1.0.1");
    ProtocolConfig protocolConfig = new ProtocolConfig().setAddress("crpc://127.0.0.1:25001");

    Configuration configuration = new Configuration()
                .setApplicationConfig(applicationConfig)
                .setProtocolConfig(protocolConfig)
                .setServerConfig(new ServerConfig().addBasepackage("com.github.leeyazhou.crpc.service"));

    ProviderConfig providerConfig = new ProviderConfig();
    providerConfig.setConfiguration(configuration);
    providerConfig.export();
  }
}

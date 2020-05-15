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

/**
 * 
 */
package com.github.leeyazhou.crpc.transport.netty;

import org.junit.Assert;
import org.junit.Test;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.ProtocolConfig;
import com.github.leeyazhou.crpc.config.ServerConfig;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.transport.Client;
import com.github.leeyazhou.crpc.transport.Server;
import com.github.leeyazhou.crpc.transport.TransportFactory;

/**
 * @author leeyazhou
 *
 */
public class NettyTransportFactoryTest {
  private TransportFactory transportFactory = new NettyTransportFactory();

  @Test
  public void testCreateServer() {
    Configuration configuration =
        new Configuration().setProtocolConfig(new ProtocolConfig().setAddress("crpc://127.0.0.1:25001"))
            .setServerConfig(new ServerConfig().addBasepackage("com.github.leeyazhou.crpc"));
    Server server = transportFactory.createServer(configuration, null);
    server.start();
    server.stop();
  }

  @Test
  public void testCreateClient() {
    URL url = URL.valueOf("crpc://127.0.0.1:25001");
    Client client = transportFactory.createClient(url);
    Assert.assertNotNull(client);
  }

}

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
    server.startup();
    server.shutdown();
  }

  @Test
  public void testCreateClient() {
    URL url = URL.valueOf("crpc://127.0.0.1:25001");
    Client client = transportFactory.createClient(url);
    Assert.assertNotNull(client);
  }

}

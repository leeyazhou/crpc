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
package com.github.leeyazhou.crpc.rpc;

import java.util.concurrent.ExecutorService;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import com.github.leeyazhou.crpc.config.ApplicationConfig;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.ProtocolConfig;
import com.github.leeyazhou.crpc.config.ReferConfig;
import com.github.leeyazhou.crpc.config.ServerConfig;
import com.github.leeyazhou.crpc.config.ServiceConfig;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.core.util.concurrent.Executors;
import com.github.leeyazhou.crpc.rpc.proxy.ProxyFactory;
import com.github.leeyazhou.crpc.transport.Server;
import com.github.leeyazhou.crpc.transport.TransportFactory;
import com.github.leeyazhou.crpc.transport.factory.ServerFactory;
import com.github.leeyazhou.crpc.transport.factory.ServiceHandler;
import com.github.leeyazhou.crpc.transport.service.InternalEchoService;
import com.github.leeyazhou.crpc.transport.service.impl.InternalEchoServiceImpl;

/**
 * @author leeyazhou
 *
 */
public class DefaultInvokerTest {

  private static Server server;

  @BeforeClass
  public static void beforeClass() {
    Configuration configuration =
        new Configuration().setProtocolConfig(new ProtocolConfig().setAddress("crpc://127.0.0.1:25001"))
            .setServerConfig(new ServerConfig().addBasepackage("com.github.leeyazhou.crpc"));
    ServerFactory serverFactory = Mockito.mock(ServerFactory.class);
    Mockito.when(serverFactory.getExecutorService()).thenAnswer(new Answer<ExecutorService>() {

      @Override
      public ExecutorService answer(InvocationOnMock invocation) throws Throwable {
        return Executors.newFixedThreadPool(8);
      }

    });
    Mockito.when(serverFactory.getServiceHandler(InternalEchoService.class.getName()))
        .then(new Answer<ServiceHandler<InternalEchoServiceImpl>>() {

          @Override
          public ServiceHandler<InternalEchoServiceImpl> answer(InvocationOnMock invocation) throws Throwable {
            ServiceConfig<InternalEchoServiceImpl> serviceConfig = new ServiceConfig<InternalEchoServiceImpl>();
            serviceConfig.setServiceType(InternalEchoServiceImpl.class);
            return new ServiceHandler<InternalEchoServiceImpl>(serviceConfig);
          }
        });
    server = ServiceLoader.load(TransportFactory.class).load().createServer(configuration, serverFactory);
    server.startup();
  }



  @Test
  public void testInvoker() {
    ProxyFactory proxyFactory = ServiceLoader.load(ProxyFactory.class).load();
    ReferConfig<InternalEchoService> referConfig =
        new ReferConfig<InternalEchoService>().setApplicationConfig(new ApplicationConfig().setName("showcase"))
            .addUrl(URL.valueOf("crpc://127.0.0.1:25001"));
    InternalEchoService echoService = proxyFactory.getProxy(referConfig);
    String response = echoService.echo("CRPC");
    Assert.assertEquals("CRPC", response);
  }

  @AfterClass
  public static void afterClass() throws Exception {
    // Thread.sleep(3000);
    server.shutdown();
  }
}

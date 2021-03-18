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
package com.github.leeyazhou.crpc.transport.factory;

import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;
import com.github.leeyazhou.crpc.config.ApplicationConfig;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.registry.meta.ApplicationMeta;
import com.github.leeyazhou.crpc.rpc.Handler;
import com.github.leeyazhou.crpc.rpc.Invocation;
import com.github.leeyazhou.crpc.rpc.Result;

/**
 * @author leeyazhou
 *
 */
public class CrpcServerFactoryTest {
  private static ServerFactory serverFactory;

  @BeforeClass
  public static void beforeClass() {
    Configuration configuration =new Configuration();
    ApplicationConfig applicationConfig = new ApplicationConfig().setName("crpc-example-server").setVersion("1.0.2");
    configuration.setApplicationConfig(applicationConfig);
    serverFactory = new CrpcServerFactory().setConfiguration(configuration);
    
    serverFactory.registerProcessor(new Handler<UserServiceImpl>() {

      @Override
      public Class<UserServiceImpl> getHandlerType() {
        return UserServiceImpl.class;
      }

      @Override
      public Result handle(Invocation context) {
        return null;
      }
    });
  }

  @Test
  public void testGetExecutorService() {
    fail("Not yet implemented");
  }

  /**
   * Test method for
   * {@link com.github.leeyazhou.crpc.transport.factory.CrpcServerFactory#setConfiguration(com.github.leeyazhou.crpc.config.Configuration)}.
   */
  @Test
  public void testSetConfiguration() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link com.github.leeyazhou.crpc.transport.factory.CrpcServerFactory#getConfiguration()}.
   */
  @Test
  public void testGetConfiguration() {
    fail("Not yet implemented");
  }

  /**
   * Test method for
   * {@link com.github.leeyazhou.crpc.transport.factory.CrpcServerFactory#getServiceHandler(java.lang.String)}.
   */
  @Test
  public void testGetServiceHandler() {
    fail("Not yet implemented");
  }

  /**
   * Test method for
   * {@link com.github.leeyazhou.crpc.transport.factory.CrpcServerFactory#registerProcessor(com.github.leeyazhou.crpc.rpc.Handler)}.
   */
  @Test
  public void testRegisterProcessor() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link com.github.leeyazhou.crpc.transport.factory.CrpcServerFactory#getRegistryFactories()}.
   */
  @Test
  public void testGetRegistryFactories() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetApplicationMeta() {
    ApplicationMeta applicationMeta = serverFactory.getApplicationMeta();
    System.out.println(applicationMeta);
  }

}

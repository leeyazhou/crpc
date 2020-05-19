/**
 * 
 */
package com.github.leeyazhou.crpc.transport.netty;

import java.util.concurrent.ExecutorService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.ProtocolConfig;
import com.github.leeyazhou.crpc.config.ServerConfig;
import com.github.leeyazhou.crpc.core.concurrent.Executors;
import com.github.leeyazhou.crpc.transport.ChannelManager;
import com.github.leeyazhou.crpc.transport.Server;
import com.github.leeyazhou.crpc.transport.factory.ServerFactory;
import com.github.leeyazhou.crpc.transport.factory.ServiceHandler;
import com.github.leeyazhou.crpc.transport.service.impl.InternalEchoServiceImpl;

/**
 * @author leeyazhou
 *
 */
public class NettyServerTest {

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
    Mockito.when(serverFactory.getServiceHandler(InternalEchoServiceImpl.class.getName()))
        .then(new Answer<ServiceHandler<InternalEchoServiceImpl>>() {

          @Override
          public ServiceHandler<InternalEchoServiceImpl> answer(InvocationOnMock invocation) throws Throwable {
            return new ServiceHandler<InternalEchoServiceImpl>(InternalEchoServiceImpl.class, null,
                new InternalEchoServiceImpl());
          }
        });
    server = new NettyServer(configuration, serverFactory, new ChannelManager());
    server.init();
    server.start();
  }

  @AfterClass
  public static void afterClass() throws Exception {
    server.stop();
  }

  @Test
  public void test() {

  }

}

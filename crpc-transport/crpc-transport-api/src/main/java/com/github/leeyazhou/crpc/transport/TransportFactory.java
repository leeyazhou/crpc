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
package com.github.leeyazhou.crpc.transport;

import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.ReferConfig;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.annotation.SPI;
import com.github.leeyazhou.crpc.transport.factory.ServerFactory;
import com.github.leeyazhou.crpc.transport.object.SendLimitPolicy;

/**
 * @author leeyazhou
 *
 */
@SPI(value = "netty")
public interface TransportFactory {

  public static final long javaHeapSize = Runtime.getRuntime().maxMemory();

  /**
   * when the size of sending bytes in queue reach percent -Xmx, then do sth based on sendLimitPolicy to avoid oom
   * default is 50<br>
   * 
   * for example: ClientFactory.sendLimitPercent = 50 -Xmx1g, if sending bytes size reaches 500m,when u call
   * client.invokeSync then it'll throw CrpcRPCRejectException
   */
  public static int sendLimitPercent = 50;

  public static SendLimitPolicy sendLimitPolicy = SendLimitPolicy.REJECT;

  /**
   * check if exceed the send limit,if exceed then do sth based on SendLimitPolicy
   * 
   * @throws Exception any exception
   */
  void checkSendLimit() throws Exception;

  /**
   * Enable Send limit,default is false;
   */
  void enableSendLimit();



  /**
   * get crpc configuration
   * 
   * @return {@link Configuration}
   */
  Configuration getConfiguration();

  /**
   * init service
   * 
   * @param <T> t
   * @param referConfig referConfig
   */
  <T> void initService(ReferConfig<T> referConfig);

  /**
   * @param loadbalance loadbalance
   * @return {@link LoadBalance}
   */
  LoadBalance getLoadBalance(String loadbalance);


  Server createServer(Configuration configuration, ServerFactory serverFactory);

  Client createClient(URL url);

  ConnectionManager getConnectionManager();

  ClientManager getClientManager();
}

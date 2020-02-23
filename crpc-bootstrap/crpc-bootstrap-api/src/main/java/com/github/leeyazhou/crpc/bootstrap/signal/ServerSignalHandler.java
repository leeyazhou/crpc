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

package com.github.leeyazhou.crpc.bootstrap.signal;

import java.util.List;

import com.github.leeyazhou.crpc.transport.Server;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * @author lee
 */
@SuppressWarnings("restriction")
public class ServerSignalHandler implements SignalHandler {

  private static final Logger logger = LoggerFactory.getLogger(ServerSignalHandler.class);
  private List<Server> servers;

  public ServerSignalHandler(List<Server> servers) {
    this.servers = servers;
  }


  public void handle(Signal signal) {
    if (SignalType.INT.name().equals(signal.getName()) || SignalType.TERM.name().equals(signal.getName())) {
      logger.info("receive a signal named " + signal + ", so close the server now !");
      if (servers != null) {
        for (Server server : servers) {
          server.stop();
        }
      } else {
        logger.warn("the server is already closed ! So discard this signal !");
      }
      System.exit(0);
    }
  }

}

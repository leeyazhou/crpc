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
import com.github.leeyazhou.crpc.core.util.OSInfoUtil;
import com.github.leeyazhou.crpc.transport.Server;
import sun.misc.Signal;

/**
 * 
 * @author leeyazhou
 */
@SuppressWarnings("restriction")
public class SignalManager {

  /**
   * 注册信号处理器
   * 
   * @param servers servers
   */
  public static synchronized void registerSignalHandlers(List<Server> servers) {
    ServerSignalHandler handler = new ServerSignalHandler(servers);
    Signal.handle(new Signal(SignalType.TERM.name()), handler);
    if (OSInfoUtil.isWindows()) {
      Signal.handle(new Signal(SignalType.INT.name()), handler);
    } else {
      Signal.handle(new Signal(SignalType.USR2.name()), handler);
    }
  }

}

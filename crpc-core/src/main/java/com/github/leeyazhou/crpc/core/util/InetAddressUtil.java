/**
 * Copyright © 2019 leeyazhou (coderhook@gmail.com)
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

package com.github.leeyazhou.crpc.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author lee
 */
public class InetAddressUtil {

  /**
   * get host
   * 
   * @return host
   */
  public static String getLocalHost() {
    try {
      return InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    return null;
  }
}

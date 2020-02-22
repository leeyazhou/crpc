/**
 * Copyright © 2016~2020 leeyazhou (coderhook@gmail.com)
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

package com.github.leeyazhou.crpc.transport.loadbalance;

import java.util.List;
import com.github.leeyazhou.crpc.protocol.Request;
import com.github.leeyazhou.crpc.transport.Client;

/**
 * @author lee
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {

  public static final String name = "RoundRobin";

  protected Client doChooseOne(List<Client> clients, Request request) {
    Client best = null;
    int total = 0;
    for (Client client : clients) {
      setCurrentWeight(client, getCurrentWeight(client) + getEffectiveWeight(client));
      total += getEffectiveWeight(client);

      if (getEffectiveWeight(client) < getWeight(client)) {
        setEffectiveWeight(client, getEffectiveWeight(client) + 1);
      }
      if (best == null || getCurrentWeight(client) > getCurrentWeight(best)) {
        best = client;
      }
    }

    if (best == null) {
      return null;
    }

    setCurrentWeight(best, getCurrentWeight(best) - total);
    return best;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RoundRobinLoadBalance [name=");
    builder.append(name);
    builder.append("]");
    return builder.toString();
  }
}

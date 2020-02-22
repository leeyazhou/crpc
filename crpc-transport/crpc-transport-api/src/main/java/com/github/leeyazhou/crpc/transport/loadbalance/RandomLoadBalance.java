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
import java.util.Random;
import com.github.leeyazhou.crpc.protocol.Request;
import com.github.leeyazhou.crpc.transport.Client;

/**
 * @author lee
 */
public class RandomLoadBalance extends AbstractLoadBalance {

  public static final String name = "random";

  private static final ThreadLocal<Random> random = new ThreadLocal<Random>() {
    protected Random initialValue() {
      return new Random();
    }
  };

  @Override
  protected Client doChooseOne(List<Client> clients, Request request) {
    int clientNums = clients.size();

    int length = clients.size();
    int totalWeight = 0;
    boolean sameWeight = true;
    for (int i = 0; i < length; i++) {
      int weight = getWeight(clients.get(i));
      totalWeight += weight;
      if (sameWeight && i > 0 && weight != getWeight(clients.get(i - 1))) {
        sameWeight = false;
      }
    }
    if (totalWeight > 0 && !sameWeight) {
      // 如果权重不相同且权重大于0则按总权重数随机
      int offset = random.get().nextInt(totalWeight);
      // 并确定随机值落在哪个片断上
      for (int i = 0; i < length; i++) {
        offset -= getWeight(clients.get(i));
        if (offset < 0) {
          return clients.get(i);
        }
      }
    }
    // 如果权重相同或权重为0则均等随机
    return clients.get(random.get().nextInt(clientNums));
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RandomLoadBalance [name=");
    builder.append(name);
    builder.append("]");
    return builder.toString();
  }

}

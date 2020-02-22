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

package com.github.leeyazhou.crpc.transport.loadbalance;

import java.util.List;

import com.github.leeyazhou.crpc.protocol.Request;
import com.github.leeyazhou.crpc.transport.Client;
import com.github.leeyazhou.crpc.transport.LoadBalance;
import com.github.leeyazhou.crpc.core.Constants;

/**
 * @author lee
 */
public abstract class AbstractLoadBalance implements LoadBalance {

  @Override
  public Client chooseOne(List<Client> clients, Request request) {
    if (clients == null || clients.isEmpty()) {
      return null;
    }
    if (clients.size() == 1) {
      return clients.get(0);
    }
    return doChooseOne(clients, request);
  }

  protected int getWeight(Client client) {
    return client.getUrl().getParameter(Constants.SERVER_WEIGHT, 1);
  }

  protected int getEffectiveWeight(Client client) {
    return client.getUrl().getParameter(Constants.SERVER_EFFECTIVE_WEIGHT, 1);
  }

  protected int getCurrentWeight(Client client) {
    return client.getUrl().getParameter(Constants.SERVER_CURRENT_WEIGHT, 0);
  }

  protected void setCurrentWeight(Client client, int currentWeight) {
    client.getUrl().addParameter(Constants.SERVER_CURRENT_WEIGHT, String.valueOf(currentWeight));
  }

  protected void setEffectiveWeight(Client client, int effectiveWeight) {
    client.getUrl().addParameter(Constants.SERVER_EFFECTIVE_WEIGHT, String.valueOf(effectiveWeight));
  }

  /**
   * choose one client to do job
   * 
   * @param clients List
   * @param request request
   * @return {@link Client}
   */
  protected abstract Client doChooseOne(List<Client> clients, Request request);

}

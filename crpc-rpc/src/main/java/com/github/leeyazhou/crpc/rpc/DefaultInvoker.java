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
package com.github.leeyazhou.crpc.rpc;

import java.util.List;
import com.github.leeyazhou.crpc.config.ReferConfig;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.transport.Client;
import com.github.leeyazhou.crpc.transport.Filter;
import com.github.leeyazhou.crpc.transport.LoadBalance;
import com.github.leeyazhou.crpc.transport.RpcContext;
import com.github.leeyazhou.crpc.transport.filter.ApplicationFilterChain;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

public class DefaultInvoker<T> extends AbstractRpcHandler<T> {

  public DefaultInvoker(ReferConfig<T> referConfig) {
    super(referConfig);
  }

  @Override
  protected ResponseMessage doInvoke(RpcContext context) throws Exception {
    final RequestMessage request = context.getRequest();
    List<Client> clients = transportFactory.getClientManager().get(referConfig);
    LoadBalance loadBalance = transportFactory.getLoadBalance(referConfig.getLoadbalance());
    Client client = loadBalance.chooseOne(clients, request);
    ApplicationFilterChain filterChain = buildFilterChain();
    ResponseMessage message = filterChain.doFilter(context);
    if (message != null) {
      return message;
    }
    return client.request(request);
  }

  private ApplicationFilterChain buildFilterChain() {
    ApplicationFilterChain filterChain = new ApplicationFilterChain();
    if (referConfig.getFilters() != null) {
      for (String filterStr : referConfig.getFilters()) {
        Filter filter = ServiceLoader.load(Filter.class).load(filterStr);
        filterChain.addFilter(filter);
      }
    }
    return filterChain;
  }

}

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
import com.github.leeyazhou.crpc.config.ServiceGroupConfig;
import com.github.leeyazhou.crpc.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.protocol.message.ResponseMessage;
import com.github.leeyazhou.crpc.transport.Client;
import com.github.leeyazhou.crpc.transport.LoadBalance;
import com.github.leeyazhou.crpc.transport.RpcContext;

public class DefaultInvoker<T> extends AbstractRpcHandler<T> {

  public DefaultInvoker(Class<T> beanType, ServiceGroupConfig serviceGroupConfig) {
    super(beanType, serviceGroupConfig);
  }

  @Override
  protected ResponseMessage doInvoke(RpcContext context) throws Exception {
    final RequestMessage request = context.getRequest();
    final List<Client> clients = context.getClients();
    final LoadBalance loadBalance = context.getLoadBalance();
    Client client = loadBalance.chooseOne(clients, request);
    context.setChoosedClient(client);
    ResponseMessage response = filter.handle(context);
    if (response != null) {
      return response;
    }
    return client.request(request);
  }


}

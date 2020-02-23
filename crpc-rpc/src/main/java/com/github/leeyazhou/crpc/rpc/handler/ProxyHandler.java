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
package com.github.leeyazhou.crpc.rpc.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import com.github.leeyazhou.crpc.config.crpc.ServiceGroupConfig;
import com.github.leeyazhou.crpc.protocol.Request;
import com.github.leeyazhou.crpc.protocol.Response;
import com.github.leeyazhou.crpc.transport.Client;
import com.github.leeyazhou.crpc.transport.LoadBalance;
import com.github.leeyazhou.crpc.transport.RpcContext;
import com.github.leeyazhou.crpc.core.exception.ServiceNotFoundException;

public class ProxyHandler<T> extends AbstractRpcHandler<T> implements InvocationHandler {

  public ProxyHandler(Class<T> beanType, ServiceGroupConfig serviceGroupConfig) {
    super(beanType, serviceGroupConfig);
  }

  @Override
  protected Response doInvoke(RpcContext context) throws Exception {
    final Request request = context.getRequest();
    final List<Client> clients = context.getClients();
    final LoadBalance loadBalance = context.getLoadBalance();
    Client client = loadBalance.chooseOne(clients, request);
    context.setChoosedClient(client);
    Response response = filter.handle(context);
    if (response != null) {
      return response;
    }
    return client.sendRequest(request);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (serviceConfig == null) {
      throw new ServiceNotFoundException("server : [" + getUrl() + "] is not found ! ");
    }
    String[] argsTypes = createParamSignature(method.getParameterTypes());

    if ("toString".equals(method.getName()) && argsTypes.length == 0) {
      return this.toString();
    }
    if ("hashCode".equals(method.getName()) && argsTypes.length == 0) {
      return this.hashCode();
    }
    if ("equals".equals(method.getName()) && argsTypes.length == 1) {
      return this.equals(args[0]);
    }

    Request request = new Request(getHandlerType().getName(), method.getName(), argsTypes, args,
        serviceConfig.getTimeout(), serviceConfig.getCodecValue(), getProtocolType());
    List<Client> clients = transportFactory.get(serviceConfig);
    LoadBalance loadBalance = transportFactory.getLoadBalance(serviceConfig.getLoadbalance());
    RpcContext context = RpcContext.consumerContext(request, clients, loadBalance);
    return handle(context).getResponse();
  }

  private String[] createParamSignature(Class<?>[] argTypes) {
    if (argTypes == null || argTypes.length == 0) {
      return new String[] {};
    }
    String[] paramSig = new String[argTypes.length];
    for (int x = 0; x < argTypes.length; x++) {
      paramSig[x] = argTypes[x].getName();
    }
    return paramSig;
  }

}

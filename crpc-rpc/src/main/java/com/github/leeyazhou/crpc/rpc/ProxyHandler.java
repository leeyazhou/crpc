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
package com.github.leeyazhou.crpc.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import com.github.leeyazhou.crpc.transport.Handler;
import com.github.leeyazhou.crpc.transport.Result;
import com.github.leeyazhou.crpc.transport.RpcContext;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;

/**
 * @author leeyazhou
 *
 */
public class ProxyHandler<T> implements InvocationHandler {

  private Handler<T> handler;

  public ProxyHandler(Handler<T> handler) {
    this.handler = handler;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if ("toString".equals(method.getName()) && args.length == 0) {
      return this.toString();
    }
    if ("hashCode".equals(method.getName()) && args.length == 0) {
      return this.hashCode();
    }
    if ("equals".equals(method.getName()) && args.length == 1) {
      return this.equals(args[0]);
    }
    return doInvoke(proxy, method, args);
  }

  public Object doInvoke(Object proxy, Method method, Object[] args) {
    String[] argTypes = createParamSignature(method.getParameterTypes());
    RequestMessage request = new RequestMessage();
    request.setMethodName(method.getName());
    request.setArgTypes(argTypes);
    request.setArgs(args);
    request.fillId();

    RpcContext context = RpcContext.consumerContext(request);
    Result result = handler.handle(context);
    return result.getValue();
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

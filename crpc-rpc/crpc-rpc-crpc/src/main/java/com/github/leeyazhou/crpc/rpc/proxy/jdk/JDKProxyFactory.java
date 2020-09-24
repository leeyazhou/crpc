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

package com.github.leeyazhou.crpc.rpc.proxy.jdk;

import java.lang.reflect.Proxy;
import com.github.leeyazhou.crpc.config.ReferConfig;
import com.github.leeyazhou.crpc.rpc.Handler;
import com.github.leeyazhou.crpc.rpc.ProxyHandler;
import com.github.leeyazhou.crpc.rpc.RpcHandlerFilterWrapper;
import com.github.leeyazhou.crpc.rpc.proxy.ProxyFactory;

/**
 * @author leeyazhou
 */
public final class JDKProxyFactory implements ProxyFactory {

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getProxy(ReferConfig<T> referConfig) {
    Handler<T> handler = new RpcHandlerFilterWrapper<T>(referConfig);
    ProxyHandler<T> proxyHandler = new ProxyHandler<T>(handler);
    return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
        new Class<?>[] {referConfig.getServiceType()}, proxyHandler);
  }

}

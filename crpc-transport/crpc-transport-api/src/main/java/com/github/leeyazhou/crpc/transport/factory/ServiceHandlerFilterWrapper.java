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
package com.github.leeyazhou.crpc.transport.factory;

import java.util.List;
import com.github.leeyazhou.crpc.config.ServiceConfig;
import com.github.leeyazhou.crpc.transport.Filter;
import com.github.leeyazhou.crpc.transport.Handler;
import com.github.leeyazhou.crpc.transport.Result;
import com.github.leeyazhou.crpc.transport.RpcContext;

/**
 * @author leeyazhou
 *
 */
public class ServiceHandlerFilterWrapper<T> implements Handler<T> {
  private ServiceConfig<T> serviceConfig;
  private ServiceHandler<T> serviceHandler;
  private Handler<T> handler;
  private List<Filter> filters;

  public ServiceHandlerFilterWrapper(ServiceConfig<T> serviceConfig, List<Filter> filters) {
    this.serviceConfig = serviceConfig;
    this.filters = filters;
    this.serviceHandler = new ServiceHandler<T>(this.serviceConfig);
    this.handler = buildHandlerChain(serviceHandler, this.filters);
  }

  @Override
  public Class<T> getHandlerType() {
    return serviceConfig.getServiceType();
  }

  @Override
  public Result handle(RpcContext context) {
    return this.handler.handle(context);
  }

  private Handler<T> buildHandlerChain(final ServiceHandler<T> serviceHandler, List<Filter> filters) {
    Handler<T> last = serviceHandler;
    if (filters != null) {
      for (int i = filters.size() - 1; i >= 0; i--) {
        final Filter filter = filters.get(i);
        final Handler<?> next = last;
        last = new Handler<T>() {

          @Override
          public Class<T> getHandlerType() {
            return serviceHandler.getHandlerType();
          }

          @Override
          public Result handle(RpcContext context) {
            return filter.filter(next, context);
          }
        };

      }
    }
    return last;
  }

}

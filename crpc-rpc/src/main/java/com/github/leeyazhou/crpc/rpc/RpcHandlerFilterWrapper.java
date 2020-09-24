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

import java.util.ArrayList;
import java.util.List;
import com.github.leeyazhou.crpc.config.ReferConfig;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.transport.Filter;
import com.github.leeyazhou.crpc.transport.Handler;
import com.github.leeyazhou.crpc.transport.Result;
import com.github.leeyazhou.crpc.transport.RpcContext;

/**
 * @author leeyazhou
 *
 */
public class RpcHandlerFilterWrapper<T> implements Handler<T> {
  private ReferConfig<T> referConfig;
  private Handler<T> handler;
  private List<Filter> filters;

  public RpcHandlerFilterWrapper(ReferConfig<T> referConfig) {
    this.referConfig = referConfig;
    this.filters = buildFilters();
    this.handler = buildHandlerChain();
  }



  private List<Filter> buildFilters() {
    this.filters = new ArrayList<Filter>();
    if (referConfig.getFilters() != null) {
      for (String filterStr : referConfig.getFilters()) {
        filters.add(ServiceLoader.load(Filter.class).load(filterStr));
      }
    }
    return filters;
  }



  private Handler<T> buildHandlerChain() {
    Handler<T> last = new RpcHandler<T>(referConfig);
    if (filters != null) {
      for (int i = filters.size() - 1; i >= 0; i--) {
        final Filter filter = filters.get(i);
        final Handler<T> next = last;
        last = new Handler<T>() {

          @Override
          public Class<T> getHandlerType() {
            return next.getHandlerType();
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



  @Override
  public Result handle(RpcContext context) {
    return handler.handle(context);
  }



  @Override
  public Class<T> getHandlerType() {
    return referConfig.getServiceType();
  }
}

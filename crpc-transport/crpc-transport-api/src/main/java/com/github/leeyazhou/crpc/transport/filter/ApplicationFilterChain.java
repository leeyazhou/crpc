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

package com.github.leeyazhou.crpc.transport.filter;

import java.util.ArrayList;
import java.util.List;
import com.github.leeyazhou.crpc.transport.Filter;
import com.github.leeyazhou.crpc.transport.FilterChain;
import com.github.leeyazhou.crpc.transport.RpcContext;
import com.github.leeyazhou.crpc.transport.factory.ServiceHandler;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

/**
 * 
 * @author leeyazhou
 *
 */
public class ApplicationFilterChain implements FilterChain {

  private List<Filter> filters;
  private int currentIndex;
  private ServiceHandler<?> serviceHandler;

  @Override
  public ResponseMessage doFilter(RpcContext context) {
    if (filters != null && currentIndex < filters.size()) {
      ResponseMessage res = filters.get(currentIndex++).filter(context, this);
      if (res != null) {
        return res;
      }
    }
    if (serviceHandler == null) {
      return null;
    }
    return serviceHandler.doHandle(context);
  }


  public ApplicationFilterChain addFilter(Filter filter) {
    if (this.filters == null) {
      this.filters = new ArrayList<Filter>();
    }
    this.filters.add(filter);
    return this;
  }

  public ApplicationFilterChain setFilters(List<Filter> filters) {
    this.filters = filters;
    return this;
  }

  public void setServiceHandler(ServiceHandler<?> serviceHandler) {
    this.serviceHandler = serviceHandler;
  }
}

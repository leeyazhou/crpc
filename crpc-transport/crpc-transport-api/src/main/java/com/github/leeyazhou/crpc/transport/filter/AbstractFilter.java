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
package com.github.leeyazhou.crpc.transport.filter;

import com.github.leeyazhou.crpc.protocol.Response;
import com.github.leeyazhou.crpc.transport.Filter;
import com.github.leeyazhou.crpc.transport.RpcContext;
import com.github.leeyazhou.crpc.core.Ordered;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

/**
 * @author lee_y
 *
 */
public abstract class AbstractFilter implements Filter {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  public Filter next;

  /**
   * 
   * @param next
   *          next filter
   */
  public void setNext(Filter next) {
    if (this.next != null) {
      this.next.setNext(next);
    } else {
      this.next = next;
    }
  }

  /**
   * 执行下一个过滤器
   * 
   * @param context
   *          {@link RpcContext}
   * @return {@link Response}
   */
  protected Response nextFilter(RpcContext context) {
    if (next != null) {
      return next.handle(context);
    }
    return null;
  }

  @Override
  public Class<Filter> getHandlerType() {
    return Filter.class;
  }

  @Override
  public Filter clone() {
    try {
      return (Filter) super.clone();
    } catch (CloneNotSupportedException e) {
      logger.error("", e);
    }
    return null;
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}

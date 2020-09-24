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

package com.github.leeyazhou.crpc.filter;

import com.github.leeyazhou.crpc.transport.Handler;
import com.github.leeyazhou.crpc.transport.Result;
import com.github.leeyazhou.crpc.transport.RpcContext;

/**
 * @author leeyazhou
 */
public class IPFilter extends AbstractFilter {

  @Override
  protected Result doFilter(Handler<?> handler, RpcContext context) {
    if (context.isConsumerSide())
      logger.info("IP过滤器:" + context.getRequest());
    else {
      logger.info("IP过滤器服务器端 : " + context.getRequest());
    }
    return handler.handle(context);
  }

}
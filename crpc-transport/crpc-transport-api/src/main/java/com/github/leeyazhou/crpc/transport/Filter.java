/**
 * Copyright © 2019 leeyazhou (coderhook@gmail.com)
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

package com.github.leeyazhou.crpc.transport;

import com.github.leeyazhou.crpc.protocol.Response;
import com.github.leeyazhou.crpc.core.Ordered;

/**
 * @author lee
 */
public interface Filter extends Handler<Filter>, Ordered {

  /**
   * 拦截处理时返回结果，如果不拦截的话返回Null
   */
  Response handle(RpcContext context);

  void setNext(Filter filter);
}
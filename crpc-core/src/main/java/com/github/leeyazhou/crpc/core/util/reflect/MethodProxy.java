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

package com.github.leeyazhou.crpc.core.util.reflect;

import java.lang.reflect.Method;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

/**
 * @author leeyazhou
 */
public final class MethodProxy {

  static final Logger logger = LoggerFactory.getLogger(MethodProxy.class);

  private Method method;

  public MethodProxy(Method method) {
    this.method = method;
  }

  public Object invoke(Object target, Object... args) throws Exception {
    Object result = method.invoke(target, args);
    return result;
  }


  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

}

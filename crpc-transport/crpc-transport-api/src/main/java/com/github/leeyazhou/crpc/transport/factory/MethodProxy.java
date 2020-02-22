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

package com.github.leeyazhou.crpc.transport.factory;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.github.leeyazhou.crpc.transport.Interceptor;
import com.github.leeyazhou.crpc.core.annotation.PointCut;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.AntPathMatcher;

/**
 * @author lee
 */
public final class MethodProxy {

  private static final Logger logger = LoggerFactory.getLogger(MethodProxy.class);

  private Method method;

  private Set<Interceptor> beforeInterceptors;
  private Set<Interceptor> afterInterceptors;
  private final Set<Interceptor> interceptors;

  public MethodProxy(Method method, Set<Interceptor> interceptors) {
    this.method = method;
    this.interceptors = interceptors;
    init();
  }

  public Object invoke(Object target, Object... args) throws Exception {
    if (beforeInterceptors != null && !beforeInterceptors.isEmpty()) {
      for (Interceptor interceptor : beforeInterceptors) {
        interceptor.intercept(method);
      }
    }
    Object result = method.invoke(target, args);
    if (afterInterceptors != null && !afterInterceptors.isEmpty()) {
      for (Interceptor interceptor : afterInterceptors) {
        interceptor.intercept(method);
      }
    }
    return result;
  }

  private void init() {
    try {
      if (interceptors == null) {
        return;
      }
      for (Interceptor interceptor : this.interceptors) {
        Method interceptMethod = interceptor.getClass().getDeclaredMethod("intercept", Method.class);
        PointCut pointCut;
        if (null == interceptMethod || (pointCut = interceptMethod.getAnnotation(PointCut.class)) == null) {
          continue;
        }
        String methodPath = this.method.getDeclaringClass().getName() + "." + this.method.getName();
        if (AntPathMatcher.getInstance().match(pointCut.value(), methodPath)) {
          switch (pointCut.type()) {
            case BEFORE:
              addBeforeInterceptor(interceptor);
              break;
            case AFTER:
              addAfterInterceptor(interceptor);
              break;
            case AROUND:
              addBeforeInterceptor(interceptor);
              addAfterInterceptor(interceptor);
              break;
            default:
              break;
          }

        }
      }
    } catch (Exception err) {
      logger.error("", err);
    }
  }

  public void addBeforeInterceptor(Interceptor interceptor) {
    if (beforeInterceptors == null) {
      beforeInterceptors = new HashSet<Interceptor>();
    }
    beforeInterceptors.add(interceptor);
  }

  public void addAfterInterceptor(Interceptor interceptor) {
    if (null == afterInterceptors) {
      afterInterceptors = new HashSet<Interceptor>();
    }
    afterInterceptors.add(interceptor);
  }

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

}

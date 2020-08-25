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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

/**
 * @author leeyazhou
 *
 */
public class ClassInfo<T> {
  private static final Logger logger = LoggerFactory.getLogger(ClassInfo.class);
  public static final String METHOD_SEPARATOR = "$";
  public static final String ARG_SEPARATOR = "_";
  private final ConcurrentMap<String, MethodProxy> methodCache = new ConcurrentHashMap<String, MethodProxy>();
  private Class<T> serviceType;

  public ClassInfo(Class<T> serviceType) {
    this.serviceType = serviceType;
  }

  public void init() {
    Method[] methods = serviceType.getDeclaredMethods();
    for (Method method : methods) {
      Class<?>[] argTypes = method.getParameterTypes();
      StringBuilder methodKeyBuilder = new StringBuilder();
      methodKeyBuilder.append(method.getName()).append(METHOD_SEPARATOR);
      if (argTypes != null) {
        for (Class<?> argClass : argTypes) {
          methodKeyBuilder.append(argClass.getName()).append(ARG_SEPARATOR);
        }
      }
      MethodProxy temp = this.methodCache.put(methodKeyBuilder.toString(), new MethodProxy(method));
      if (temp != null) {
        logger.warn("method is already exists! targetClass : " + serviceType + ", MethodProxy : " + temp);
      }
    }

  }

  public MethodProxy getMethod(String methodKey) {
    return methodCache.get(methodKey);
  }

  public String toMethodKey(String methodName, String[] args) {
    StringBuilder methodKeyBuilder = new StringBuilder();
    methodKeyBuilder.append(methodName).append(METHOD_SEPARATOR);

    if (args != null) {
      for (Object arg : args) {
        methodKeyBuilder.append(arg).append(ARG_SEPARATOR);
      }
    }
    return methodKeyBuilder.toString();
  }

}

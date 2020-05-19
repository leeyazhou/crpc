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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import com.github.leeyazhou.crpc.core.exception.ServiceMethodNotFoundException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ExceptionUtil;
import com.github.leeyazhou.crpc.core.util.SerializerUtil;
import com.github.leeyazhou.crpc.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.protocol.message.ResponseMessage;
import com.github.leeyazhou.crpc.transport.Filter;
import com.github.leeyazhou.crpc.transport.Handler;
import com.github.leeyazhou.crpc.transport.RpcContext;

/**
 * @author leeyazhou
 */
public class ServiceHandler<T> implements Handler<T> {
  private static final Logger logger = LoggerFactory.getLogger(ServiceHandler.class);
  private final Class<T> clazz;
  private T instance;
  // Cached Server Methods key: methodname$argtype_argtype
  private final Map<String, MethodProxy> cachedMethods = new HashMap<String, MethodProxy>();
  private Filter filter;

  public ServiceHandler(Class<T> clazz) {
    this(clazz, null);
  }


  public ServiceHandler(Class<T> clazz, T instance) {
    this.clazz = clazz;
    this.instance = instance;
    init();
  }

  @Override
  public Class<T> getHandlerType() {
    return this.clazz;
  }

  @Override
  public ResponseMessage handle(final RpcContext context) {
    if (filter != null) {
      ResponseMessage filterResponse = filter.handle(context);
      if (filterResponse != null) {
        return filterResponse;
      }
    }

    final RequestMessage request = context.getRequest();

    ResponseMessage response = new ResponseMessage(request.id(), request.getCodecType(), request.getProtocolType());
    String targetInstanceName = request.getTargetClassName();
    String methodName = new String(request.getMethodName());
    String[] argTypes = request.getArgTypes();
    Object[] requestObjects = null;
    MethodProxy method = null;
    try {
      if (argTypes != null && argTypes.length > 0) {
        StringBuilder methodKeyBuilder = new StringBuilder();
        methodKeyBuilder.append(methodName).append("$");
        Class<?>[] argTypeClasses = new Class<?>[argTypes.length];
        for (int i = 0; i < argTypes.length; i++) {
          methodKeyBuilder.append(argTypes[i]).append("_");
          argTypeClasses[i] = SerializerUtil.getInstance().getClazz(argTypes[i]);
          if (argTypeClasses[i] == null) {
            argTypeClasses[i] = Class.forName(argTypes[i]);
          }
        }
        requestObjects = request.getArgs();
        method = this.getCachedMethods().get(methodKeyBuilder.toString());
        if (method == null) {
          throw new ServiceMethodNotFoundException(
              "no method: " + methodKeyBuilder.toString() + " find in " + targetInstanceName + " on the server");
        }
      } else {
        Method temp = instance.getClass().getMethod(methodName, new Class<?>[] {});
        if (temp == null) {
          throw new ServiceMethodNotFoundException(
              "no method: " + methodName + " found in " + targetInstanceName + " on the server");
        }
        method = new MethodProxy(temp);
        requestObjects = new Object[] {};
      }
      response.setResponseClassName(method.getMethod().getReturnType().getName());
      response.setResponse(method.invoke(instance, requestObjects));
    } catch (Exception err) {
      logger.error("server handle request error", err);
      response.setError(true);
      response.setException(ExceptionUtil.getErrorMessage(err));
    }
    return response;
  }

  /**
   * 
   */
  private void init() {
    Method[] methods = clazz.getDeclaredMethods();
    for (Method method : methods) {
      Class<?>[] argTypes = method.getParameterTypes();
      StringBuilder methodKeyBuilder = new StringBuilder();
      methodKeyBuilder.append(method.getName()).append("$");
      for (Class<?> argClass : argTypes) {
        methodKeyBuilder.append(argClass.getName()).append("_");
      }
      MethodProxy temp = this.cachedMethods.put(methodKeyBuilder.toString(), new MethodProxy(method));
      if (temp != null) {
        logger.warn("method is already exists! targetClass : " + clazz + ", MethodProxy : " + temp);
      }
    }

  }

  /**
   * @return the clazz
   */
  public Class<T> getClazz() {
    return clazz;
  }

  /**
   * @return the instance
   */
  public T getInstance() {
    return instance;
  }

  /**
   * @param instance the instance to set
   */
  public void setInstance(T instance) {
    this.instance = instance;
  }

  /**
   * @return the cachedMethods
   */
  public Map<String, MethodProxy> getCachedMethods() {
    return cachedMethods;
  }

  public void setFilter(Filter filter) {
    this.filter = filter;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ServiceHandler [clazz=");
    builder.append(clazz);
    builder.append(", instance=");
    builder.append(instance);
    builder.append(", cachedMethods=");
    builder.append(cachedMethods);
    builder.append("]");
    return builder.toString();
  }

}

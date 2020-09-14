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

import java.util.ArrayList;
import java.util.List;
import com.github.leeyazhou.crpc.config.ServiceConfig;
import com.github.leeyazhou.crpc.core.exception.ServiceMethodNotFoundException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ExceptionUtil;
import com.github.leeyazhou.crpc.core.util.reflect.MethodProxy;
import com.github.leeyazhou.crpc.transport.Filter;
import com.github.leeyazhou.crpc.transport.FilterChain;
import com.github.leeyazhou.crpc.transport.Handler;
import com.github.leeyazhou.crpc.transport.RpcContext;
import com.github.leeyazhou.crpc.transport.filter.ApplicationFilterChain;
import com.github.leeyazhou.crpc.transport.protocol.message.MessageType;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

/**
 * @author leeyazhou
 */
public class ServiceHandler<T> implements Handler<T> {
  private static final Logger logger = LoggerFactory.getLogger(ServiceHandler.class);
  private List<Filter> filters;
  private ServiceConfig<T> serviceConfig;

  public ServiceHandler(ServiceConfig<T> serviceConfig) {
    this.serviceConfig = serviceConfig;
    this.serviceConfig.init();
  }


  @Override
  public Class<T> getHandlerType() {
    return serviceConfig.getServiceType();
  }

  public ResponseMessage doHandle(RpcContext context) {
    final RequestMessage request = context.getRequest();

    ResponseMessage response = new ResponseMessage();
    response.setMessageType(MessageType.RESPONSE);
    response.setId(request.id());
    response.setCodecType(request.getCodecType()).setProtocolType(request.getProtocolType());

    final String methodKey = serviceConfig.getClassInfo().toMethodKey(request.getMethodName(), request.getArgTypes());
    MethodProxy method = serviceConfig.getClassInfo().getMethod(methodKey);
    if (method == null) {
      throw new ServiceMethodNotFoundException(
          "no method [" + methodKey + "] find in " + request.getServiceTypeName() + " on the server");
    }

    Object[] requestObjects = null;
    try {

      String[] argTypes = request.getArgTypes();
      if (argTypes != null && argTypes.length > 0) {
        requestObjects = request.getArgs();
      } else {
        requestObjects = new Object[] {};
      }

      response.setResponseClassName(method.getMethod().getReturnType().getName());
      response.setResponse(method.invoke(serviceConfig.getInstance(), requestObjects));

    } catch (Exception err) {
      logger.error("server handle request error", err);
      response.setError(true);
      response.setResponse(ExceptionUtil.getErrorMessage(err));
    }
    return response;
  }

  private FilterChain buildFilterChain() {
    ApplicationFilterChain filterChain = new ApplicationFilterChain();
    filterChain.setFilters(filters);
    filterChain.setServiceHandler(this);
    return filterChain;
  }

  @Override
  public ResponseMessage handle(final RpcContext context) {
    FilterChain filterChain = buildFilterChain();
    return filterChain.doFilter(context);
  }


  public ServiceHandler<T> addFilter(Filter filter) {
    if (filters == null) {
      this.filters = new ArrayList<Filter>();
    }
    this.filters.add(filter);
    return this;
  }

  public ServiceHandler<T> setFilters(List<Filter> filters) {
    this.filters = filters;
    return this;
  }

  public ServiceConfig<T> getServiceConfig() {
    return serviceConfig;
  }



}

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
package com.github.leeyazhou.crpc.rpc.handler;

import java.util.HashSet;
import java.util.Set;

import com.github.leeyazhou.crpc.config.crpc.ServerConfig;
import com.github.leeyazhou.crpc.config.crpc.ServiceConfig;
import com.github.leeyazhou.crpc.config.crpc.ServiceGroupConfig;
import com.github.leeyazhou.crpc.protocol.SimpleProtocol;
import com.github.leeyazhou.crpc.protocol.message.ResponseMessage;
import com.github.leeyazhou.crpc.serializer.CodecType;
import com.github.leeyazhou.crpc.transport.Filter;
import com.github.leeyazhou.crpc.transport.Handler;
import com.github.leeyazhou.crpc.transport.RpcContext;
import com.github.leeyazhou.crpc.transport.TransportFactory;
import com.github.leeyazhou.crpc.transport.filter.CounterFilter;
import com.github.leeyazhou.crpc.transport.filter.IPFilter;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;

/**
 * @author leeyazhou
 *
 */
public abstract class AbstractRpcHandler<T> implements Handler<T> {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  private URL url;

  private Class<T> handlerType;

  private int protocolType = SimpleProtocol.PROTOCOL_TYPE;

  protected ServiceConfig<T> serviceConfig;
  protected final TransportFactory transportFactory = ServiceLoader.load(TransportFactory.class).load();

  protected static Filter filter = null;

  public AbstractRpcHandler(Class<T> beanType, ServiceGroupConfig serviceGroupConfig) {
    this(beanType, serviceGroupConfig, SimpleProtocol.PROTOCOL_TYPE);
  }

  public AbstractRpcHandler(Class<T> beanType, ServiceGroupConfig serviceGroupConfig, int protocolType) {
    this.handlerType = beanType;
    this.protocolType = protocolType;
    buildFilterChain();
    initService(serviceGroupConfig);
  }

  private void initService(ServiceGroupConfig serviceGroupConfig) {
    ServiceConfig<T> serviceConfig = new ServiceConfig<T>();
    serviceConfig.setInterfaceClass(handlerType);
    serviceConfig.setName(serviceGroupConfig.getName());
    serviceConfig.setCodec(serviceGroupConfig.getCodec());
    serviceConfig.setCodecValue(CodecType.valueOf(serviceConfig.getCodec()).getId());
    serviceConfig.setLoadbalance(serviceGroupConfig.getLoadbalance());
    if (serviceGroupConfig.getServers() != null && !serviceGroupConfig.getServers().isEmpty()) {
      Set<URL> urls = new HashSet<URL>();
      for (ServerConfig serverConfig : serviceGroupConfig.getServers()) {
        URL u = serverConfig.toURL();
        u.addParameter(Constants.APPLICATION, serviceGroupConfig.getName());
        u.addParameter(Constants.VERSION, Constants.CRPC_VERSION);
        u.addParameter(Constants.SERVICE_INTERFACE, handlerType.getName());
        urls.add(u);
      }
      serviceConfig.setUrls(urls);
    }
    serviceConfig.setRegistries(serviceGroupConfig.getRegistries());
    this.transportFactory.initService(serviceConfig);
    this.serviceConfig = serviceConfig;
  }

  private void buildFilterChain() {
    if (filter == null) {
      synchronized (AbstractRpcHandler.class) {
        if (filter == null) {
          filter = new IPFilter();
          filter.setNext(new CounterFilter());
          logger.warn("测试消费端拦截器:" + filter);
        }
      }
    }
  }

  @Override
  public ResponseMessage handle(RpcContext context) {
    try {
      return doInvoke(context);
    } catch (Exception err) {
      throw new RuntimeException(err);
    }
  }

  /**
   * @return the url
   */
  public URL getUrl() {
    return url;
  }

  @Override
  public Class<T> getHandlerType() {
    return handlerType;
  }

  /**
   * @return the protocolType
   */
  public int getProtocolType() {
    return protocolType;
  }

  /**
   * do invoce thing
   * 
   * @param context
   *          {@link RpcContext}
   * @return result
   * @throws Exception
   *           any exception
   */
  protected abstract ResponseMessage doInvoke(RpcContext context) throws Exception;
}

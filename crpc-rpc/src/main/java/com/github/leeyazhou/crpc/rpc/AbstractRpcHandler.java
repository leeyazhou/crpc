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
package com.github.leeyazhou.crpc.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import com.github.leeyazhou.crpc.codec.CodecType;
import com.github.leeyazhou.crpc.config.ReferConfig;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.exception.CrpcException;
import com.github.leeyazhou.crpc.core.exception.ServiceNotFoundException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.transport.Handler;
import com.github.leeyazhou.crpc.transport.RpcContext;
import com.github.leeyazhou.crpc.transport.TransportFactory;
import com.github.leeyazhou.crpc.transport.protocol.ProtocolType;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

/**
 * @author leeyazhou
 *
 */
public abstract class AbstractRpcHandler<T> implements Handler<T>, InvocationHandler {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  private URL url;

  private Class<T> handlerType;

  private ProtocolType protocolType = ProtocolType.CRPC;

  protected ReferConfig<T> referConfig;
  protected final TransportFactory transportFactory = ServiceLoader.load(TransportFactory.class).load();

  public AbstractRpcHandler(ReferConfig<T> referConfig) {
    this(referConfig, ProtocolType.CRPC);
  }

  public AbstractRpcHandler(ReferConfig<T> referConfig, ProtocolType protocolType) {
    this.referConfig = referConfig;
    this.handlerType = referConfig.getServiceType();
    this.protocolType = protocolType;
    initService();
  }

  private void initService() {
    Set<URL> providers = referConfig.getUrls();
    if (providers != null && !providers.isEmpty()) {
      Set<URL> urls = new HashSet<URL>();
      for (URL provider : providers) {
        provider.addParameter(Constants.APPLICATION, referConfig.getApplicationConfig().getName());
        provider.addParameter(Constants.VERSION, Constants.CRPC_VERSION);
        provider.addParameter(Constants.SERVICE_INTERFACE, handlerType.getName());
        urls.add(provider);
      }
      referConfig.setUrls(urls);
    }
    this.transportFactory.initService(referConfig);
  }


  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (referConfig == null) {
      throw new ServiceNotFoundException("server : [" + getUrl() + "] is not found ! ");
    }
    String[] argTypes = createParamSignature(method.getParameterTypes());

    if ("toString".equals(method.getName()) && argTypes.length == 0) {
      return this.toString();
    }
    if ("hashCode".equals(method.getName()) && argTypes.length == 0) {
      return this.hashCode();
    }
    if ("equals".equals(method.getName()) && argTypes.length == 1) {
      return this.equals(args[0]);
    }
    CodecType codecType = CodecType.valueOf(referConfig.getCodecType());
    RequestMessage request = new RequestMessage(getHandlerType().getName(), method.getName()).setArgTypes(argTypes)
        .setArgs(args).setTimeout(referConfig.getTimeout()).fillId();
    request.setCodecType(codecType).setProtocolType(protocolType);
    
    RpcContext context = RpcContext.consumerContext(request);
    return handle(context).getResponse();
  }

  private String[] createParamSignature(Class<?>[] argTypes) {
    if (argTypes == null || argTypes.length == 0) {
      return new String[] {};
    }
    String[] paramSig = new String[argTypes.length];
    for (int x = 0; x < argTypes.length; x++) {
      paramSig[x] = argTypes[x].getName();
    }
    return paramSig;
  }

  @Override
  public ResponseMessage handle(RpcContext context) {
    try {
      return doInvoke(context);
    } catch (Exception err) {
      throw new CrpcException(err);
    }
  }

  public URL getUrl() {
    return url;
  }

  @Override
  public Class<T> getHandlerType() {
    return handlerType;
  }

  public ProtocolType getProtocolType() {
    return protocolType;
  }

  /**
   * do invoce thing
   * 
   * @param context {@link RpcContext}
   * @return result
   * @throws Exception any exception
   */
  protected abstract ResponseMessage doInvoke(RpcContext context) throws Exception;
}

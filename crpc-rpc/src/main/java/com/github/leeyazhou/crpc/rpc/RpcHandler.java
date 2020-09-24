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
package com.github.leeyazhou.crpc.rpc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.github.leeyazhou.crpc.codec.CodecType;
import com.github.leeyazhou.crpc.config.ReferConfig;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.exception.CrpcException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.transport.Client;
import com.github.leeyazhou.crpc.transport.Handler;
import com.github.leeyazhou.crpc.transport.LoadBalance;
import com.github.leeyazhou.crpc.transport.Result;
import com.github.leeyazhou.crpc.transport.RpcContext;
import com.github.leeyazhou.crpc.transport.TransportFactory;
import com.github.leeyazhou.crpc.transport.protocol.ProtocolType;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

public class RpcHandler<T> implements Handler<T> {
  protected final Logger logger = LoggerFactory.getLogger(RpcHandler.class);
  private URL url;

  private Class<T> handlerType;

  private ProtocolType protocolType = ProtocolType.CRPC;

  protected ReferConfig<T> referConfig;
  protected final TransportFactory transportFactory = ServiceLoader.load(TransportFactory.class).load();

  public RpcHandler(ReferConfig<T> referConfig) {
    this(referConfig, ProtocolType.CRPC);
  }

  public RpcHandler(ReferConfig<T> referConfig, ProtocolType protocolType) {
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
  public Result handle(RpcContext context) {
    try {
      CodecType codecType = CodecType.valueOf(referConfig.getCodecType());
      RequestMessage request = context.getRequest();
      request.setServiceTypeName(getHandlerType().getName());
      request.setTimeout(referConfig.getTimeout());
      request.setCodecType(codecType);
      request.setProtocolType(protocolType);

      return doHandle(context);
    } catch (Exception err) {
      throw new CrpcException(err);
    }
  }



  protected Result doHandle(RpcContext context) throws Exception {
    final RequestMessage request = context.getRequest();
    List<Client> clients = transportFactory.getClientManager().get(referConfig);
    LoadBalance loadBalance = transportFactory.getLoadBalance(referConfig.getLoadbalance());
    Client client = loadBalance.chooseOne(clients, request);
    ResponseMessage message = client.request(request);
    Result result = new Result();
    result.setValue(message.getResponse());
    return result;
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



}

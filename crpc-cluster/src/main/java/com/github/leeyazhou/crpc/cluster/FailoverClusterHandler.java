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
package com.github.leeyazhou.crpc.cluster;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.github.leeyazhou.crpc.codec.CodecType;
import com.github.leeyazhou.crpc.config.ReferConfig;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.exception.CrpcException;
import com.github.leeyazhou.crpc.core.exception.TimeoutException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.rpc.Handler;
import com.github.leeyazhou.crpc.rpc.Invocation;
import com.github.leeyazhou.crpc.rpc.Result;
import com.github.leeyazhou.crpc.transport.Client;
import com.github.leeyazhou.crpc.transport.LoadBalance;
import com.github.leeyazhou.crpc.transport.RpcResult;
import com.github.leeyazhou.crpc.transport.TransportFactory;
import com.github.leeyazhou.crpc.transport.protocol.ProtocolType;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

/**
 * @author leeyazhou
 *
 */
public class FailoverClusterHandler<T> implements Handler<T> {
  private static final Logger logger = LoggerFactory.getLogger(FailoverClusterHandler.class);
  private ReferConfig<T> referConfig;
  private ProtocolType protocolType = ProtocolType.CRPC;
  private static final long PRINT_CONSUME_MINTIME = Long.parseLong(System.getProperty("rpc.print.consumetime", "0"));
  protected final TransportFactory transportFactory = ServiceLoader.load(TransportFactory.class).load();
  private Class<T> handlerType;
  private LoadBalance loadBalance;

  public FailoverClusterHandler(ReferConfig<T> referConfig) {
    this.referConfig = referConfig;
    this.handlerType = referConfig.getServiceType();
    this.loadBalance = transportFactory.getLoadBalance(referConfig.getLoadbalance());
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
  public Class<T> getHandlerType() {
    return referConfig.getServiceType();
  }

  @Override
  public Result handle(Invocation context) {
    CodecType codecType = CodecType.valueOf(referConfig.getCodecType());
    final RequestMessage request = new RequestMessage(context.getServiceTypeName(), context.getMethodName());
    request.setCodecType(codecType);
    request.setTimeout(referConfig.getTimeout());
    request.setProtocolType(protocolType);
    request.setArgs(context.getArgs());
    request.setArgTypes(context.getArgTypes());


    return doHandle(request);
  }


  protected Result doHandle(RequestMessage request) {
    List<Client> clients = transportFactory.getClientManager().get(referConfig);

    Client client = loadBalance.chooseOne(clients, request);

    RpcResult rpcResult = client.request(request);
    ResponseMessage message = getResponse(rpcResult, request, client.getUrl());

    Result result = new Result();
    result.setValue(message.getResponse());
    return result;
  }

  public ResponseMessage getResponse(RpcResult rpcResult, RequestMessage request, URL url) {
    ResponseMessage response =
        rpcResult.getResponse(request.getTimeout() - (System.currentTimeMillis() - rpcResult.getStartTime()));

    if (PRINT_CONSUME_MINTIME > 0 && logger.isDebugEnabled()) {
      long consumeTime = System.currentTimeMillis() - rpcResult.getStartTime();
      if (consumeTime > PRINT_CONSUME_MINTIME) {
        if (logger.isInfoEnabled()) {
          logger.info(String.format("client invokeSync consume time: %s ms, server is: %s:%s request id is:%s",
              consumeTime, url.getHost(), url.getPort(), request.id()));
        }
      }
    }
    if (response == null) {
      String errorMsg = "receive response timeout(" + request.getTimeout() + " ms), server is: " + url.getHost() + ":"
          + url.getPort() + ", request id is:" + request.id();
      throw new TimeoutException(errorMsg);
    } else if (response.isError()) {
      StringBuilder sb = new StringBuilder("server error, server is: [");
      sb.append(url.getHost()).append(":").append(url.getPort());
      sb.append("], request id is:").append(request.id());
      if (response.getResponse() != null) {
        sb.append(", ").append(response.getResponse());
      }
      throw new CrpcException(sb.toString());
    }
    return response;
  }
}

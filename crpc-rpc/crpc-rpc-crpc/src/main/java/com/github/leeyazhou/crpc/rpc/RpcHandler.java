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

import com.github.leeyazhou.crpc.cluster.FailoverCluster;
import com.github.leeyazhou.crpc.config.ReferConfig;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

public class RpcHandler<T> implements Handler<T> {
  protected final Logger logger = LoggerFactory.getLogger(RpcHandler.class);
  private URL url;

  protected ReferConfig<T> referConfig;
  private Handler<T> handler;


  public RpcHandler(ReferConfig<T> referConfig) {
    this.referConfig = referConfig;
    handler = new FailoverCluster().join(referConfig);
  }


  @Override
  public Result handle(Invocation context) {
    return handler.handle(context);
  }

  public URL getUrl() {
    return url;
  }

  @Override
  public Class<T> getHandlerType() {
    return referConfig.getServiceType();
  }



}

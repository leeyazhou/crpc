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

package com.github.leeyazhou.crpc.transport;

import java.util.HashMap;
import java.util.Map;
import com.github.leeyazhou.crpc.core.util.object.SideType;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;

/**
 * CRPC 上下文信息
 * 
 * @author leeyazhou
 *
 */
public final class RpcContext {
  private final RequestMessage request;
  private SideType sideType;
  private Map<String, Object> attachements;

  private RpcContext(RequestMessage request) {
    this.request = request;
  }

  /**
   * 消费者上下文
   * 
   * @param request 请求信息
   * @return {@link RpcContext}
   */
  public static RpcContext consumerContext(RequestMessage request) {
    RpcContext ret = new RpcContext(request);
    ret.setSideType(SideType.SIDE_CONSUMER);
    return ret;
  }

  /**
   * 生产者上下文
   * 
   * @param request 请求信息
   * @return {@link RpcContext}
   */
  public static RpcContext providerContext(RequestMessage request) {
    RpcContext ret = new RpcContext(request);
    ret.setSideType(SideType.SIDE_PROVIDER);
    return ret;
  }

  /**
   * @return the request
   */
  public RequestMessage getRequest() {
    return request;
  }

  /**
   * @return the sideType
   */
  public SideType getSideType() {
    return sideType;
  }

  public boolean isServerSide() {
    return this.sideType.equals(SideType.SIDE_PROVIDER);
  }

  public boolean isConsumerSide() {
    return this.sideType.equals(SideType.SIDE_CONSUMER);
  }

  public RpcContext setSideType(SideType sideType) {
    this.sideType = sideType;
    return this;
  }



  public RpcContext addAttachement(String key, Object value) {
    if (this.attachements == null) {
      this.attachements = new HashMap<String, Object>();
    }
    this.attachements.put(key, value);
    return this;
  }

  /**
   * 附属信息
   * 
   * @return 附属信息
   */
  public Map<String, Object> getAttachements() {
    return attachements;
  }

  public Object getAttachement(String key) {
    if (attachements == null) {
      return null;
    }
    return attachements.get(key);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RpcContext [sideType=");
    builder.append(sideType);
    builder.append(", request=");
    builder.append(request);
    builder.append("]");
    return builder.toString();
  }

}

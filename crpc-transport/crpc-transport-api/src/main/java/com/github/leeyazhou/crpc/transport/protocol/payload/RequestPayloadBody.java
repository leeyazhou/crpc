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
package com.github.leeyazhou.crpc.transport.protocol.payload;

import java.io.Serializable;

/**
 * @author leeyazhou
 *
 */
public class RequestPayloadBody implements PayloadBody, Serializable {

  private static final long serialVersionUID = 1L;

  private String serviceTypeName;

  private String methodName;

  private String[] argTypes;

  private Object[] args;

  private int timeout;
  private boolean oneWay;

  public String getServiceTypeName() {
    return serviceTypeName;
  }

  public RequestPayloadBody setServiceTypeName(String serviceTypeName) {
    this.serviceTypeName = serviceTypeName;
    return this;
  }

  public String getMethodName() {
    return methodName;
  }

  public RequestPayloadBody setMethodName(String methodName) {
    this.methodName = methodName;
    return this;
  }

  public String[] getArgTypes() {
    return argTypes;
  }

  public RequestPayloadBody setArgTypes(String[] argTypes) {
    this.argTypes = argTypes;
    return this;
  }

  public Object[] getArgs() {
    return args;
  }

  public RequestPayloadBody setArgs(Object[] args) {
    this.args = args;
    return this;
  }

  public int getTimeout() {
    return timeout;
  }

  public RequestPayloadBody setTimeout(int timeout) {
    this.timeout = timeout;
    return this;
  }

  public void setOneWay(boolean oneWay) {
    this.oneWay = oneWay;
  }

  public boolean isOneWay() {
    return oneWay;
  }
}

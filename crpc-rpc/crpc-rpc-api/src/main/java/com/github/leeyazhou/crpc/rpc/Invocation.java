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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leeyazhou
 *
 */
public class Invocation implements Serializable {
  private static final long serialVersionUID = 1L;
  private Map<String, Object> attachements;
  private String methodName;
  private String serviceTypeName;
  private String[] argTypes;
  private Object[] args;
  private int timeout;
  private boolean oneWay;

  public Map<String, Object> getAttachements() {
    return attachements;
  }

  public Invocation setAttachements(Map<String, Object> attachements) {
    this.attachements = attachements;
    return this;
  }

  public String getMethodName() {
    return methodName;
  }

  public Invocation setMethodName(String methodName) {
    this.methodName = methodName;
    return this;
  }

  public String getServiceTypeName() {
    return serviceTypeName;
  }

  public Invocation setServiceTypeName(String serviceTypeName) {
    this.serviceTypeName = serviceTypeName;
    return this;
  }

  public String[] getArgTypes() {
    return argTypes;
  }

  public Invocation setArgTypes(String[] argTypes) {
    this.argTypes = argTypes;
    return this;
  }

  public Object[] getArgs() {
    return args;
  }

  public Invocation setArgs(Object[] args) {
    this.args = args;
    return this;
  }

  public Invocation addAttachement(String name, Object value) {
    if (this.attachements == null) {
      this.attachements = new HashMap<String, Object>();
    }
    attachements.put(name, value);
    return this;
  }

  public int getTimeout() {
    return timeout;
  }

  public Invocation setTimeout(int timeout) {
    this.timeout = timeout;
    return this;
  }

  public boolean isOneWay() {
    return oneWay;
  }

  public Invocation setOneWay(boolean oneWay) {
    this.oneWay = oneWay;
    return this;
  }

  public Object getAttachement(String key) {
    if (this.attachements == null) {
      return null;
    }
    return attachements.get(key);
  }


}

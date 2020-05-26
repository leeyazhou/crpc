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
package com.github.leeyazhou.crpc.transport.protocol.message;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestMessage extends Message {
  private static final long serialVersionUID = 1L;

  private static final AtomicInteger incId = new AtomicInteger(0);

  private String serviceTypeName;

  private String methodName;

  private String[] argTypes;

  private Object[] args;

  private int timeout;

  private int messageLen;

  public RequestMessage() {}

  public RequestMessage(String serviceTypeName, String methodName, String[] argTypes, Object[] args, int timeout) {
    this.args = args;
    setId(incId.incrementAndGet());
    incId.compareAndSet(Integer.MAX_VALUE, 0);
    this.timeout = timeout;
    this.serviceTypeName = serviceTypeName;
    this.methodName = methodName;
    this.argTypes = argTypes;
  }

  public int getMessageLen() {
    return messageLen;
  }

  public RequestMessage setMessageLen(int messageLen) {
    this.messageLen = messageLen;
    return this;
  }

  public RequestMessage setArgTypes(String[] argTypes) {
    this.argTypes = argTypes;
    return this;
  }

  public String getServiceTypeName() {
    return serviceTypeName;
  }

  public String getMethodName() {
    return methodName;
  }

  public int getTimeout() {
    return timeout;
  }

  public Object[] getArgs() {
    return args;
  }

  public String[] getArgTypes() {
    return argTypes;
  }

  public RequestMessage setServiceTypeName(String serviceTypeName) {
    this.serviceTypeName = serviceTypeName;
    return this;
  }

  public RequestMessage setMethodName(String methodName) {
    this.methodName = methodName;
    return this;
  }

  public RequestMessage setArgs(Object[] args) {
    this.args = args;
    return this;
  }


  public RequestMessage setTimeout(int timeout) {
    this.timeout = timeout;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Request [targetClassName=");
    builder.append(serviceTypeName);
    builder.append(", methodName=");
    builder.append(methodName);
    builder.append(", argTypes=");
    builder.append(Arrays.toString(argTypes));
    builder.append(", args=");
    builder.append(Arrays.toString(args));
    builder.append(", timeout=");
    builder.append(timeout);
    builder.append(", messageLen=");
    builder.append(messageLen);
    builder.append("]");
    return builder.toString();

  }

}

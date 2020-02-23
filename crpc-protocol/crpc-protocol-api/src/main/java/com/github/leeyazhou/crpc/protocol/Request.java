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
package com.github.leeyazhou.crpc.protocol;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.leeyazhou.crpc.core.object.MessageType;

public class Request extends Header {

  private static final long serialVersionUID = 3622220033230097935L;

  private static final AtomicInteger incId = new AtomicInteger(0);

  private String targetClassName;

  private String methodName;

  private String[] argTypes;

  private Object[] requestObjects;

  private int timeout;

  private int messageLen;

  public Request() {}

  /**
   * 
   * @param codecType codecType
   * @param protocolType protocolType
   * @param messageType {@link MessageType}
   */
  public Request(int codecType, int protocolType, MessageType messageType) {
    super(codecType, protocolType, messageType);
  }

  public Request(String targetClassName, String methodName, String[] argTypes, Object[] requestObjects, int timeout,
      int codecType, int protocolType) {
    this(targetClassName, methodName, argTypes, requestObjects, timeout, incId.incrementAndGet(), codecType, protocolType);
    incId.compareAndSet(Integer.MAX_VALUE, 0);
  }

  public Request(String targetClassName, String methodName, String[] argTypes, Object[] requestObjects, int timeout, int id,
      int codecType, int protocolType) {
    super(codecType, protocolType);
    this.requestObjects = requestObjects;
    setId(id);
    this.timeout = timeout;
    this.targetClassName = targetClassName;
    this.methodName = methodName;
    this.argTypes = argTypes;
  }

  public int getMessageLen() {
    return messageLen;
  }

  public void setMessageLen(int messageLen) {
    this.messageLen = messageLen;
  }

  public void setArgTypes(String[] argTypes) {
    this.argTypes = argTypes;
  }

  public String getTargetClassName() {
    return targetClassName;
  }

  public String getMethodName() {
    return methodName;
  }

  public int getTimeout() {
    return timeout;
  }

  public Object[] getRequestObjects() {
    return requestObjects;
  }

  public String[] getArgTypes() {
    return argTypes;
  }

  /**
   * @param targetClassName the targetClassName to set
   */
  public void setTargetClassName(String targetClassName) {
    this.targetClassName = targetClassName;
  }

  /**
   * @param methodName the methodName to set
   */
  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  /**
   * @param requestObjects the requestObjects to set
   */
  public void setRequestObjects(Object[] requestObjects) {
    this.requestObjects = requestObjects;
  }

  /**
   * @param timeout the timeout to set
   */
  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Request [targetClassName=");
    builder.append(targetClassName);
    builder.append(", methodName=");
    builder.append(methodName);
    builder.append(", argTypes=");
    builder.append(Arrays.toString(argTypes));
    builder.append(", requestObjects=");
    builder.append(Arrays.toString(requestObjects));
    builder.append(", timeout=");
    builder.append(timeout);
    builder.append(", messageLen=");
    builder.append(messageLen);
    builder.append("]");
    return builder.toString();

  }

}

/**
 * Copyright © 2016~2020 leeyazhou (coderhook@gmail.com)
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

import com.github.leeyazhou.crpc.core.object.MessageType;

public class Response extends Header {

  private static final long serialVersionUID = 3172161907603172383L;


  private Object response = null;

  private boolean isError = false;

  private String exception = null;

  private int messageLen;

  private String responseClassName;

  public Response() {}

  public Response(int id, int codecType, int protocolType) {
    this(id, codecType, protocolType, MessageType.MESSAGE_COMMON);
  }

  public Response(int id, int codecType, int protocolType, MessageType messageType) {
    this(id, codecType, protocolType, null, messageType);
  }

  public Response(int id, int codecType, int protocolType, String responseClassName) {
    this(id, codecType, protocolType, responseClassName, MessageType.MESSAGE_COMMON);
  }

  public Response(int id, int codecType, int protocolType, String responseClassName, MessageType messageType) {
    super(codecType, protocolType, messageType);
    setId(id);
    this.responseClassName = responseClassName;
  }

  public int getMessageLen() {
    return messageLen;
  }

  public void setMessageLen(int messageLen) {
    this.messageLen = messageLen;
  }


  public Object getResponse() {
    return response;
  }

  public void setResponse(Object response) {
    this.response = response;
  }

  public boolean isError() {
    return isError;
  }

  public String getException() {
    return exception;
  }

  public void setException(String exception) {
    this.exception = exception;
    isError = true;
  }

  public String getResponseClassName() {
    return responseClassName;
  }

  public void setResponseClassName(String responseClassName) {
    this.responseClassName = responseClassName;
  }

  /**
   * @param isError the isError to set
   */
  public void setError(boolean isError) {
    this.isError = isError;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Response [response=");
    builder.append(response);
    builder.append(", isError=");
    builder.append(isError);
    builder.append(", exception=");
    builder.append(exception);
    builder.append(", messageLen=");
    builder.append(messageLen);
    builder.append(", responseClassName=");
    builder.append(responseClassName);
    builder.append("]");
    return builder.toString();
  }

}

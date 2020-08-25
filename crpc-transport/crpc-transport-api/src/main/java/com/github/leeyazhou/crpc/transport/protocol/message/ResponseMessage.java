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

public class ResponseMessage extends Message {
  private static final long serialVersionUID = 1L;

  private Object response;

  private boolean error;

  private String responseClassName;

  public ResponseMessage() {}


  public ResponseMessage(String responseClassName) {
    this.responseClassName = responseClassName;
  }


  public Object getResponse() {
    return response;
  }

  public ResponseMessage setResponse(Object response) {
    this.response = response;
    return this;
  }

  public boolean isError() {
    return error;
  }

  public String getResponseClassName() {
    return responseClassName;
  }

  public ResponseMessage setResponseClassName(String responseClassName) {
    this.responseClassName = responseClassName;
    return this;
  }

  /**
   * @param isError the isError to set
   */
  public ResponseMessage setError(boolean isError) {
    this.error = isError;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Response [response=");
    builder.append(response);
    builder.append(", error=");
    builder.append(error);
    builder.append(", responseClassName=");
    builder.append(responseClassName);
    builder.append("]");
    return builder.toString();
  }

}

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
public class ResponseBody implements PayloadBody, Serializable {
  private static final long serialVersionUID = 1L;

  private Object response;

  private boolean error;

  private String responseClassName;

  public Object getResponse() {
    return response;
  }

  public ResponseBody setResponse(Object response) {
    this.response = response;
    return this;
  }

  public boolean isError() {
    return error;
  }

  public ResponseBody setError(boolean error) {
    this.error = error;
    return this;
  }

  public String getResponseClassName() {
    return responseClassName;
  }

  public ResponseBody setResponseClassName(String responseClassName) {
    this.responseClassName = responseClassName;
    return this;
  }
}

/**
 * Copyright © 2019 leeyazhou (coderhook@gmail.com)
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
package com.github.leeyazhou.crpc.protocol;

/**
 * @author <a href="mailto:lee_yazhou@163.com">Yazhou Li</a>
 */
public class RequestHeader {

  private String key;
  private Object value;
  
  public RequestHeader() {
  }

  public RequestHeader(String key, Object value) {
    this.key = key;
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RequestHeader [key=");
    builder.append(key);
    builder.append(", value=");
    builder.append(value);
    builder.append("]");
    return builder.toString();
  }


}

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
package com.github.leeyazhou.crpc.transport.protocol.message;

import java.io.Serializable;

/**
 * @author leeyazhou
 */
public class Header implements Serializable {

  private static final long serialVersionUID = 1L;
  private String key;
  private Object value;

  public Header() {}

  public Header(String key, Object value) {
    this.key = key;
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  public Header setValue(Object value) {
    this.value = value;
    return this;
  }

  public String getKey() {
    return key;
  }

  public Header setKey(String key) {
    this.key = key;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Header [key=");
    builder.append(key);
    builder.append(", value=");
    builder.append(value);
    builder.append("]");
    return builder.toString();
  }


}

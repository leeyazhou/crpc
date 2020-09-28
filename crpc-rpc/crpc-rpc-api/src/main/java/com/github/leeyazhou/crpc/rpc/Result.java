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

/**
 * @author leeyazhou
 *
 */
public class Result {

  private Object value;
  private Exception exception;

  public Result() {}

  public Result(Object value) {
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(Object value) {
    this.value = value;
  }

  /**
   * @return the exception
   */
  public Exception getException() {
    return exception;
  }

  /**
   * @param exception the exception to set
   */
  public void setException(Exception exception) {
    this.exception = exception;
  }


}

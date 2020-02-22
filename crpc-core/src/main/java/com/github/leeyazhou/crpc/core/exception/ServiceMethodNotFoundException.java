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
/**
 * 
 */

package com.github.leeyazhou.crpc.core.exception;

import com.github.leeyazhou.crpc.core.annotation.CRPCSerializable;

/**
 * @author lee
 */
@CRPCSerializable
public class ServiceMethodNotFoundException extends CrpcException {

  private static final long serialVersionUID = 5447454107632101731L;

  public ServiceMethodNotFoundException() {
    super();
  }

  public ServiceMethodNotFoundException(String message) {
    super(message);
  }

  public ServiceMethodNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ServiceMethodNotFoundException(Throwable cause) {
    super(cause);
  }
}

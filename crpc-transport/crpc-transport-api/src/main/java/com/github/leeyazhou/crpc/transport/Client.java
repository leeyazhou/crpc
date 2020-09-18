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
package com.github.leeyazhou.crpc.transport;

import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.transport.protocol.message.Message;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

public interface Client {

  /**
   * invoke sync via rpc
   * 
   * @param request {@link RequestMessage}
   * @return Object return response
   */
  ResponseMessage request(RequestMessage request);

  /**
   * receive response from server
   * 
   * @param response {@link Message}
   */
  void receiveResponse(ResponseMessage response);


  /**
   * get sending bytes size
   * 
   * @return long
   */
  long getSendingBytesSize();

  /**
   * connect channel
   * 
   * @return {@link Connection}
   */
  Connection getConnection();

  /**
   * get server config url
   * 
   * @return {@link URL}
   */
  URL getUrl();

  /**
   * do create connection with server
   * 
   * @return boolean
   */
  boolean connect();
}

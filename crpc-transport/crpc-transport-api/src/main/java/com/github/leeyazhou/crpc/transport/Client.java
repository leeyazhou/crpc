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
package com.github.leeyazhou.crpc.transport;

import com.github.leeyazhou.crpc.protocol.Request;
import com.github.leeyazhou.crpc.protocol.Response;
import com.github.leeyazhou.crpc.core.URL;

public interface Client {

  /**
   * invoke sync via rpc
   * 
   * @param request {@link Request}
   * @return Object return response
   */
  public Response sendRequest(Request request);

  /**
   * receive response from server
   * 
   * @param response {@link Response}
   */
  public void putResponse(Response response);


  /**
   * get sending bytes size
   * 
   * @return long
   */
  public long getSendingBytesSize();

  /**
   * connect channel
   * 
   * @return {@link Channel}
   */
  Channel getChannel();

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

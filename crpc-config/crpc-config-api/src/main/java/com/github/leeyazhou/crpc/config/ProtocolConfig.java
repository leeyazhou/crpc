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
package com.github.leeyazhou.crpc.config;

import com.github.leeyazhou.crpc.core.URL;

/**
 * 
 * @author leeyazhou
 */
public class ProtocolConfig {
  private String address;

  private String protocol;
  private String host;
  private int port;

  /**
   * @return the address
   */
  public String getAddress() {
    return address;
  }

  /**
   * @param address the address to set
   */
  public ProtocolConfig setAddress(String address) {
    this.address = address;
    URL url = URL.valueOf(address);
    this.protocol = url.getProtocol();
    this.host = url.getHost();
    this.port = url.getPort();
    return this;
  }

  /**
   * @return the protocol
   */
  public String getProtocol() {
    return protocol;
  }

  /**
   * @param protocol the protocol to set
   */
  public ProtocolConfig setProtocol(String protocol) {
    this.protocol = protocol;
    return this;
  }

  /**
   * @return the host
   */
  public String getHost() {
    return host;
  }

  /**
   * @param host the host to set
   */
  public ProtocolConfig setHost(String host) {
    this.host = host;
    return this;
  }

  /**
   * @return the port
   */
  public int getPort() {
    return port;
  }

  /**
   * @param port the port to set
   */
  public ProtocolConfig setPort(int port) {
    this.port = port;
    return this;
  }

}

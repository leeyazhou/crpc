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
package com.github.leeyazhou.crpc.config.crpc;

import com.github.leeyazhou.crpc.config.IConfig;

/**
 * 
 * @author <a href="mailto:lee_yazhou@163.com">Yazhou Li</a>
 */
public class ProtocolConfig implements IConfig {

  private static final long serialVersionUID = 901941877669282991L;

  private String name;

  private Integer port;

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the port
   */
  public Integer getPort() {
    return port;
  }

  /**
   * @param port the port to set
   */
  public void setPort(Integer port) {
    this.port = port;
  }

  /**
   * @return the serialversionuid
   */
  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ProtocolConfig [name=");
    builder.append(name);
    builder.append(", port=");
    builder.append(port);
    builder.append("]");
    return builder.toString();
  }



}

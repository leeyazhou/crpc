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
package com.github.leeyazhou.crpc.registry.meta;

import java.io.Serializable;
import java.util.Map;

/**
 * @author leeyazhou
 *
 */
public class ApplicationMeta implements Serializable {

  private static final long serialVersionUID = 1L;
  private String name;
  private String version;
  private Map<String, ServiceMeta> serviceInfos;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Map<String, ServiceMeta> getServiceInfos() {
    return serviceInfos;
  }

  public void setServiceInfos(Map<String, ServiceMeta> serviceInfos) {
    this.serviceInfos = serviceInfos;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ApplicationMeta [name=");
    builder.append(name);
    builder.append(", version=");
    builder.append(version);
    builder.append(", serviceInfos=");
    builder.append(serviceInfos);
    builder.append("]");
    return builder.toString();
  }


}

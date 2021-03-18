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
import java.util.ArrayList;
import java.util.List;

/**
 * @author leeyazhou
 *
 */
public class ServiceMeta implements Serializable {

  private static final long serialVersionUID = 1L;
  private String name;
  private String group;
  private String version;
  private String protocol;
  private List<MethodMeta> methodMetas = new ArrayList<MethodMeta>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public List<MethodMeta> getMethodMetas() {
    return methodMetas;
  }

  public void setMethodMetas(List<MethodMeta> methodMetas) {
    this.methodMetas = methodMetas;
  }


  public void addMethodMeta(MethodMeta methodMeta) {
    this.methodMetas.add(methodMeta);
  }


}

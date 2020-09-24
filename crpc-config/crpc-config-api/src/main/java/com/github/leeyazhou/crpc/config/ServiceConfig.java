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

import com.github.leeyazhou.crpc.core.lifecyle.AbstractLifecycle;
import com.github.leeyazhou.crpc.core.util.reflect.ClassInfo;

/**
 * @author leeyazhou
 */
public class ServiceConfig<T> extends AbstractLifecycle {
  private String name;
  private Class<T> serviceType;
  private Class<T> interfaceClass;
  private T instance;
  private ClassInfo<T> classInfo;

  @Override
  public void doInit() {
    this.classInfo = new ClassInfo<T>(serviceType);
    this.classInfo.init();
  }

  @Override
  protected void doStartup() {
    init();
  }

  @Override
  protected void doShutdown() {}

  public String getName() {
    return name;
  }

  public ServiceConfig<T> setName(String name) {
    this.name = name;
    return this;
  }

  public Class<T> getInterfaceClass() {
    return interfaceClass;
  }

  public ServiceConfig<T> setServiceType(Class<T> serviceType) {
    this.serviceType = serviceType;
    return this;
  }

  public Class<T> getServiceType() {
    return serviceType;
  }

  public ServiceConfig<T> setInterfaceClass(Class<T> interfaceClass) {
    this.interfaceClass = interfaceClass;
    return this;
  }


  public ServiceConfig<T> setInstance(T instance) {
    this.instance = instance;
    return this;
  }

  public Object get() {
    init();
    if (instance == null) {
      synchronized (logger) {
        if (instance == null) {
          try {
            this.instance = getServiceType().newInstance();
          } catch (Exception e) {
            logger.error("", e);
          }
        }
      }
    }
    return instance;
  }

  /**
   * @return the classInfo
   */
  public ClassInfo<T> getClassInfo() {
    return classInfo;
  }

}

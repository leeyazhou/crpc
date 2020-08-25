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
import com.github.leeyazhou.crpc.core.util.function.Supplier;
import com.github.leeyazhou.crpc.core.util.reflect.ClassInfo;

/**
 * @author leeyazhou
 */
public class ServiceConfig<T> extends AbstractLifecycle {
  private String name;
  private Class<T> serviceType;
  private Class<T> implClass;
  private T instance;
  private Supplier<T> instanceSupplier;
  private ClassInfo<T> classInfo;

  @Override
  public void doInit() {
    this.classInfo = new ClassInfo<T>(serviceType);
    this.classInfo.init();
  }

  @Override
  protected void doStart() {}

  @Override
  protected void doStop() {}

  public String getName() {
    return name;
  }

  public ServiceConfig<T> setName(String name) {
    this.name = name;
    return this;
  }

  public Class<T> getImplClass() {
    return implClass;
  }

  public ServiceConfig<T> setServiceType(Class<T> serviceType) {
    this.serviceType = serviceType;
    return this;
  }

  public Class<T> getServiceType() {
    return serviceType;
  }

  public ServiceConfig<T> setImplClass(Class<T> implClass) {
    this.implClass = implClass;
    return this;
  }


  public ServiceConfig<T> setInstance(T instance) {
    this.instance = instance;
    return this;
  }

  public Object getInstance() {
    init();
    if (instanceSupplier != null) {
      return instanceSupplier.get();
    }
    return instance;
  }

  /**
   * @param instanceSupplier the instanceSupplier to set
   */
  public void setInstanceSupplier(Supplier<T> instanceSupplier) {
    this.instanceSupplier = instanceSupplier;
  }


  public Supplier<T> getInstanceSupplier() {
    return instanceSupplier;
  }

  /**
   * @return the classInfo
   */
  public ClassInfo<T> getClassInfo() {
    return classInfo;
  }

}

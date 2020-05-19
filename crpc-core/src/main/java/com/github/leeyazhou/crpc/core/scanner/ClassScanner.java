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
package com.github.leeyazhou.crpc.core.scanner;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * simple introduction
 *
 * <p>
 * 类扫描接口
 * </p>
 *
 * @author Alex.j 2014-5-23
 * @since 1.0
 */
public interface ClassScanner {

  /**
   * 获取指定包名中的所有类
   *
   * @param packagePattern packagePattern
   * @return Set
   */
  Set<Class<?>> getClassList(String packagePattern);

  /**
   * 自定义ClassLoader中获取指定包名中的所有类
   *
   * @param packagePattern packagePattern
   * @param classLoader classLoader
   * @return Set
   */
  Set<Class<?>> getClassList(String packagePattern, ClassLoader classLoader);

  /**
   * 获取指定包名中指定注解的相关类
   *
   * @param annotationClass annotationClass
   * @return Set
   */
  Set<Class<?>> getClassListByAnnotation(Class<? extends Annotation> annotationClass);

  /**
   * 自定义ClassLoader中获取指定包名中指定注解的相关类
   *
   * @param annotationClass annotationClass
   * @param classLoader classLoader
   * @return Set
   */
  Set<Class<?>> getClassListByAnnotation(Class<? extends Annotation> annotationClass, ClassLoader classLoader);

  /**
   * 获取指定包名中指定父类或接口的相关类
   *
   * @param superClass superClass
   * @return Set
   */
  Set<Class<?>> getClassListBySuper(Class<?> superClass);

  /**
   * 自定义ClassLoader中获取指定包名中指定父类或接口的相关类
   *
   * @param superClass superClass
   * @param classLoader classLoader
   * @return Set
   */
  Set<Class<?>> getClassListBySuper(Class<?> superClass, ClassLoader classLoader);
}

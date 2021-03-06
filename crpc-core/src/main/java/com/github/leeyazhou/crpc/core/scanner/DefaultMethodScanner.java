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

import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.scanner.filter.AbstractAnnotationMethodFilter;
import com.github.leeyazhou.crpc.core.scanner.filter.AbstractPatternNameMethodFilter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class DefaultMethodScanner implements MethodScanner {
  private static final Logger logger = LoggerFactory.getLogger(DefaultMethodScanner.class);
  private final Class<?> clazz;

  public DefaultMethodScanner(Class<?> clazz) {
    this.clazz = clazz;
  }

  @Override
  public List<Method> getMethodList(final String methodPattern) {
    return new AbstractPatternNameMethodFilter(clazz, methodPattern) {

      @Override
      public boolean filterCondition(Method method) {
        return method.getName().matches(methodPattern);
      }
    }.getMethodList();
  }

  @Override
  public List<Method> getMethodListByAnnotation(Class<? extends Annotation> annotationType) {
    return new AbstractAnnotationMethodFilter(clazz, annotationType) {

      @Override
      public boolean filterCondition(Method method) {
        return method.isAnnotationPresent(annotationType);
      }
    }.getMethodList();
  }

  @Override
  public List<Method> getMethodListByAnnotationInterface(Class<? extends Annotation> annotationType) {
    return new AbstractAnnotationMethodFilter(clazz, annotationType) {

      @Override
      public boolean filterCondition(Method method) {
        if (method.isAnnotationPresent(annotationType)) {
          return true;
        }
        Class<?>[] cls = clazz.getInterfaces();
        for (Class<?> c : cls) {
          try {
            Method md = c.getDeclaredMethod(method.getName(), method.getParameterTypes());
            if (md.isAnnotationPresent(annotationType)) {
              return true;
            }
          } catch (Exception err) {
            logger.error("", err);
          }
        }
        return false;
      }
    }.getMethodList();
  }
}

/**
 * Copyright © 2016~2020 CRPC (coderhook@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.leeyazhou.crpc.core.scanner;

import com.github.leeyazhou.crpc.core.scanner.filter.AbstractAnnotationClassFilter;
import com.github.leeyazhou.crpc.core.scanner.filter.AbstractClassFilter;
import com.github.leeyazhou.crpc.core.scanner.filter.AbstractSupperClassFilter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Set;

public class DefaultClassScanner implements ClassScanner {

    private final String basePackage;

    public DefaultClassScanner(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public Set<Class<?>> getClassList(final String pattern) {
        return new AbstractClassFilter(basePackage) {
            @Override
            public boolean filterCondition(Class<?> cls) {
                String className = cls.getName();
                String patternStr = (null == pattern || pattern.isEmpty()) ? ".*" : pattern;
                return className.startsWith(packageName) && className.matches(patternStr);

            }
        }.getClassList();
    }

    @Override
    public Set<Class<?>> getClassList(final String pattern, ClassLoader classLoader) {
        return new AbstractClassFilter(basePackage, classLoader) {
            @Override
            public boolean filterCondition(Class<?> cls) {
                String className = cls.getName();
                String pkgName = className.substring(0, className.lastIndexOf("."));
                String patternStr = (null == pattern || pattern.isEmpty()) ? ".*" : pattern;
                return pkgName.startsWith(packageName) && pkgName.matches(patternStr);
            }
        }.getClassList();
    }

    @Override
    public Set<Class<?>> getClassListByAnnotation(Class<? extends Annotation> annotationClass) {
        return new AbstractAnnotationClassFilter(basePackage, annotationClass) {
            @Override
            public boolean filterCondition(Class<?> cls) {
                return cls.isAnnotationPresent(annotationClass);
            }
        }.getClassList();
    }

    @Override
    public Set<Class<?>> getClassListByAnnotation(Class<? extends Annotation> annotationClass,
                                                  ClassLoader classLoader) {
        return new AbstractAnnotationClassFilter(basePackage, annotationClass, classLoader) {
            @Override
            public boolean filterCondition(Class<?> cls) { // 这里去掉了内部类
                return cls.isAnnotationPresent(annotationClass);
            }
        }.getClassList();
    }

    @Override
    public Set<Class<?>> getClassListBySuper(Class<?> superClass) {
        return new AbstractSupperClassFilter(basePackage, superClass) {
            @Override
            public boolean filterCondition(Class<?> clazz) { // 这里去掉了内部类
                return superClass.isAssignableFrom(clazz) && !superClass.equals(clazz)
                        && !Modifier.isInterface(clazz.getModifiers()) && !Modifier.isAbstract(clazz.getModifiers())
                        && Modifier.isPublic(clazz.getModifiers());
                // !cls.getName().contains("$");
            }

        }.getClassList();
    }

    @Override
    public Set<Class<?>> getClassListBySuper(Class<?> superClass, ClassLoader classLoader) {
        return new AbstractSupperClassFilter(basePackage, superClass, classLoader) {
            @Override
            public boolean filterCondition(Class<?> cls) { // 这里去掉了内部类
                return superClass.isAssignableFrom(cls) && !superClass.equals(cls);// &&
                // !cls.getName().contains("$");
            }

        }.getClassList();
    }

}

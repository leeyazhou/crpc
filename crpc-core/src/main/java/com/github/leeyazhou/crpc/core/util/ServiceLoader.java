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
package com.github.leeyazhou.crpc.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.leeyazhou.crpc.core.annotation.SPI;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

public class ServiceLoader<T> {
  private static final Logger logger = LoggerFactory.getLogger(ServiceLoader.class);
  private static final String PREFIX = "META-INF/service/crpc/";
  private static final ConcurrentMap<Class<?>, Object> serviceLoadersMap = new ConcurrentHashMap<Class<?>, Object>();

  private final Class<T> serviceType;
  private final ClassLoader classLoader;
  private final Map<String, Class<T>> classesMap = new HashMap<String, Class<T>>();
  private final Map<String, T> servicesMap = new HashMap<String, T>();

  /*
   * 默认实现
   */
  private String defaultServiceImpl;

  private ServiceLoader(Class<T> serviceType) {
    this(serviceType, null);
  }

  private ServiceLoader(Class<T> serviceType, ClassLoader classLoader) {
    this.serviceType = serviceType;
    if (classLoader == null) {
      this.classLoader = getClass().getClassLoader();
    } else {
      this.classLoader = classLoader;
    }
    loadClasses();
  }

  /**
   * 获取服务加载器（缓存处理）
   * 
   * @param <T> t
   * @param serviceType serviceType
   * @return {@link ServiceLoader}
   */
  @SuppressWarnings("unchecked")
  public static <T> ServiceLoader<T> load(Class<T> serviceType) {
    ServiceLoader<T> ret = (ServiceLoader<T>) serviceLoadersMap.get(serviceType);
    if (ret == null) {
      ret = new ServiceLoader<T>(serviceType);
      serviceLoadersMap.putIfAbsent(serviceType, ret);
    }
    return ret;
  }

  /**
   * 获取默认实现
   * 
   * @return t
   */
  public T load() {
    return load(defaultServiceImpl);
  }



  /**
   * 加载指定实现
   * 
   * @param name 名称
   * @return t
   */
  public T load(String name) {
    T ret = servicesMap.get(name);
    try {
      if (ret != null) {
        return ret;
      }
      Class<T> serviceClass = classesMap.get(name);
      if (serviceClass == null) {
        throw new ClassNotFoundException(name + " for " + serviceType);
      }

      ret = serviceClass.newInstance();
      logger.info("load extend(" + name + ") : " + serviceClass);
      servicesMap.put(name, ret);
      return ret;
    } catch (Exception e) {
      logger.error("", e);
    }
    return ret;
  }

  /**
   * 所有已经加载的扩展
   * 
   * @return t
   */
  public List<T> loads() {
    return new ArrayList<T>(servicesMap.values());
  }

  /**
   * 获取扩展实现类
   * 
   * @param name 扩展
   * @return class
   */
  public Class<T> loadType(String name) {
    Class<T> serviceClass = classesMap.get(name);
    if (serviceClass == null) {
      logger.error("", new ClassNotFoundException(name + " forType " + serviceType));
    }
    return serviceClass;
  }

  private void loadClasses() {
    SPI spi = serviceType.getAnnotation(SPI.class);
    if (spi != null) {
      defaultServiceImpl = spi.value();
    }

    String className = serviceType.getName();
    String path = PREFIX + className;

    Set<String> loadedUrls = new HashSet<String>();
    try {
      Enumeration<URL> urls = classLoader.getResources(path);
      while (urls.hasMoreElements()) {
        URL url = urls.nextElement();
        if (loadedUrls.contains(url.toString())) {
          continue;
        }
        load(url);
        loadedUrls.add(url.toString());
      }
    } catch (IOException ex) {
      // skip
    }
  }

  @SuppressWarnings("unchecked")
  public void load(URL url) throws IOException {
    InputStream is = null;
    BufferedReader reader = null;
    try {
      is = url.openStream();
      reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
      for (;;) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }

        int ci = line.indexOf('#');
        if (ci >= 0) {
          line = line.substring(0, ci);
        }
        line = line.trim();
        if (line.length() == 0) {
          continue;
        }

        String[] kv = line.split("=");
        if (kv.length <= 1) {
          logger.warn("Config of ServiceLoader error : + " + line);
          continue;
        }
        Class<T> cl = (Class<T>) Class.forName(kv[1]);
        classesMap.put(kv[0], cl);
        // logger.info("load class : " + kv[0] + ", vlaue : " + cl);
      }
    } catch (ClassNotFoundException e) {
      logger.error("", e);
    } finally {
      IOUtils.close(reader);
      IOUtils.close(is);
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ServiceLoader@").append(serviceType);
    builder.append("[");
    for (Map.Entry<String, Class<T>> entry : classesMap.entrySet()) {
      builder.append("\n\t ").append(entry.getKey()).append("=").append(entry.getValue());
    }
    builder.append("\n]");
    return builder.toString();
  }

}

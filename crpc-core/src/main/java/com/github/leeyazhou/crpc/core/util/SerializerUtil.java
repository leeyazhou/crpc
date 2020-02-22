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
package com.github.leeyazhou.crpc.core.util;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.leeyazhou.crpc.core.annotation.CRPCSerializable;
import com.github.leeyazhou.crpc.core.exception.SerializeException;

/**
 * @author lee
 */
public final class SerializerUtil {

  // private Set<Class<?>> classList = new HashSet<Class<?>>();
  private Map<String, Class<?>> classList = new HashMap<String, Class<?>>();

  private static final SerializerUtil intance = new SerializerUtil();

  /**
   * 
   */
  private SerializerUtil() {
    initClassList();
  }

  public static SerializerUtil getInstance() {
    return intance;
  }

  /**
   * 
   */
  private void initClassList() {
    classList.clear();
    classList.put(Object.class.getName(), Object.class);
    classList.put(Boolean.class.getName(), Boolean.class);
    classList.put("boolean", Boolean.class);
    classList.put(Character.class.getName(), Character.class);
    classList.put("char", Character.class);
    classList.put(Byte.class.getName(), Byte.class);
    classList.put("byte", Byte.class);
    classList.put(Short.class.getName(), Short.class);
    classList.put("short", Short.class);
    classList.put(Integer.class.getName(), Integer.class);
    classList.put("int", Integer.class);
    classList.put(Long.class.getName(), Long.class);
    classList.put("long", Long.class);
    classList.put(Float.class.getName(), Float.class);
    classList.put("float", Float.class);
    classList.put(Double.class.getName(), Double.class);
    classList.put("double", Double.class);
    classList.put(BigDecimal.class.getName(), BigDecimal.class);
    classList.put(Date.class.getName(), Date.class);
    classList.put(java.sql.Date.class.getName(), java.sql.Date.class);
    classList.put(java.sql.Time.class.getName(), java.sql.Time.class);
    classList.put(java.sql.Timestamp.class.getName(), java.sql.Timestamp.class);
    classList.put(String.class.getName(), String.class);
    classList.put(List.class.getName(), List.class);
    classList.put(Array.class.getName(), Array.class);
    classList.put(Map.class.getName(), Map.class);
    classList.put(Exception.class.getName(), Exception.class);
    classList.put(Set.class.getName(), Set.class);
  }

  public Boolean validate(String className) {
    if (classList.containsKey(className)) {
      return Boolean.TRUE;
    }
    try {
      Class<?> clazz = Class.forName(className);
      if (Array.class.isAssignableFrom(clazz) || List.class.isAssignableFrom(clazz) || Set.class.isAssignableFrom(clazz)
          || Map.class.isAssignableFrom(clazz)) {
        classList.put(className, clazz);
        return Boolean.TRUE;
      }
      CRPCSerializable ann = clazz.getAnnotation(CRPCSerializable.class);
      if (ann != null) {
        classList.put(clazz.getName(), clazz);
        return Boolean.TRUE;
      }
    } catch (Exception err) {
      err.printStackTrace();
    }
    throw new SerializeException("CRPC Serialize is not allowed for " + className);
  }

  /**
   * 校验是否支持序列化
   * 
   * @param clazz class
   * @return true/false
   */
  public Boolean validate(Class<?> clazz) {
    if (classList.containsValue(clazz)) {
      return Boolean.TRUE;
    }

    Boolean result = Boolean.FALSE;
    if (clazz.isArray()) {
      result = classList.containsValue(Array.class);
    } else if (Map.class.isAssignableFrom(clazz)) {
      result = classList.containsValue(Map.class);
    } else if (List.class.isAssignableFrom(clazz)) {
      result = classList.containsValue(List.class);
    } else if (Exception.class.isAssignableFrom(clazz)) {
      result = classList.containsValue(Exception.class);
    } else if (Set.class.isAssignableFrom(clazz)) {
      result = classList.containsValue(Set.class);
    } else {
      result = classList.containsValue(clazz);
    }
    if (result) {
      return Boolean.TRUE;
    }
    CRPCSerializable ann = clazz.getAnnotation(CRPCSerializable.class);
    if (ann != null) {
      classList.put(clazz.getName(), clazz);
      return Boolean.TRUE;
    }
    throw new SerializeException("CRPC Serialize is not allowed for " + clazz.getName());
  }

  public Class<?> getClazz(String className) {
    return classList.get(className);
  }

  public Class<?> getClazzForName(String className) throws ClassNotFoundException {
    Class<?> result = getClazz(className);
    if (result == null) {
      return Class.forName(className);
    }
    return result;
  }

}

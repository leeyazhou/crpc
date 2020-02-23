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
package com.github.leeyazhou.crpc.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.github.leeyazhou.crpc.core.exception.CrpcException;

/**
 * @author lee_y
 *
 */
public class FieldUtil {

  public static void convertValue(String property, Object propertyValue, Object classObject) throws CrpcException {
    Field field = null;
    try {
      field = classObject.getClass().getDeclaredField(property);
      Method method = classObject.getClass().getMethod(
          "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), field.getType());
      field.setAccessible(true);

      if (field.getType() == Integer.class || field.getType() == int.class) {
        propertyValue = Integer.parseInt(propertyValue.toString());
      } else if (field.getType() == Long.class || field.getType() == long.class) {
        propertyValue = Long.parseLong(propertyValue.toString());
      } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
        propertyValue = Boolean.parseBoolean(propertyValue.toString());
      }
      method.invoke(classObject, propertyValue);
    } catch (Exception err) {
      throw new CrpcException(err);
    }
  }
}

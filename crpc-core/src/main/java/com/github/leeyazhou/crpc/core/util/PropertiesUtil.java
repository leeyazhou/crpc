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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author lee
 */
public final class PropertiesUtil {

  private static final Map<String, Properties> propertiesCache = new HashMap<String, Properties>();

  /**
   * 
   */
  private PropertiesUtil() {}

  public static Properties get(String location) {
    try {
      Properties properties = propertiesCache.get(location);
      if (properties == null) {
        properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(location));
        propertiesCache.put(location, properties);
      }
      return properties;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}

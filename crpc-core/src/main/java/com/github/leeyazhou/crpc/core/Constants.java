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

package com.github.leeyazhou.crpc.core;

import com.github.leeyazhou.crpc.core.util.PropertiesUtil;

/**
 * @author leeyazhou
 */
public final class Constants {

  public static final String COMMON_LOCATION = "META-INF/crpc/internal/common.properties";

  public static final String ENCODING = "UTF-8";

  public static final String GROUP = "group";

  public static final String DEFAULT_CATEGORY_PROVIDER = "providers";

  public static final String DEFAULT_CATEGORY_CONSUMER = "consumers";

  public static final String CRPC_VERSION = PropertiesUtil.get(COMMON_LOCATION).getProperty("crpc-version");

  public static final String FILE_SEPARATOR = "/";

  public static final String SESSION_TIMEOUT_KEY = "session";

  public static final int DEFAULT_SESSION_TIMEOUT = 60 * 1000;

  /**
   * TimeUnit : SECONDS
   */
  public static final int DEFAULT_HEARTBEAT_TIMEOUT = 6;

  public static final String TIMESTAMP_KEY = "timestamp";

  /**
   * 服务名称
   */
  public static final String APPLICATION = "application";
  public static final String VERSION = "version";
  public static final String loadBalance = "loadbalance";
  public static final String codec = "codec";
  public static final String timeout = "timeout";


  public static final String REQUEST_TIMEOUT = "requestTimeout";

  public static final String SERVER_WEIGHT = "weight";
  public static final String SERVER_EFFECTIVE_WEIGHT = "effectiveWeight";
  public static final String SERVER_CURRENT_WEIGHT = "currentWeight";

  /**
   * 服务的接口名称
   */
  public static final String SERVICE_INTERFACE = "serviceInterface";

  public static final String REGISTER = "register";
  public static final String UNREGISTER = "unregister";
  public static final String ANY_VALUE = "*";

  public static final String SIDE_KEY = "side";

  public static final String PROVIDER_SIDE = "provider";

  public static final String CONSUMER_SIDE = "consumer";

}

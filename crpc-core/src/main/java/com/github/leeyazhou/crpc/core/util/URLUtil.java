/**
 * Copyright © 2016~2020 leeyazhou (coderhook@gmail.com)
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

import java.net.URLDecoder;
import java.net.URLEncoder;

import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

/**
 * @author lee
 */
public class URLUtil {

  private static final Logger logger = LoggerFactory.getLogger(URLUtil.class);

  public static String encode(String url) {
    try {
      return URLEncoder.encode(url, Constants.ENCODING);
    } catch (Exception err) {
      logger.error("encode error ", err);
      return url;
    }
  }

  public static String decode(String url) {
    try {
      return URLDecoder.decode(url, Constants.ENCODING);
    } catch (Exception err) {
      logger.error("decode error ", err);
      return url;
    }
  }

}

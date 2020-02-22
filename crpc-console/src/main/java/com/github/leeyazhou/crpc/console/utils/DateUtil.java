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
/**
 * 
 */

package com.github.leeyazhou.crpc.console.utils;

/**
 * @author lee
 */
public final class DateUtil {

  /**
   * 
   * @param time 毫秒
   * @return 2 Hours 32 minutes 55 seconds
   */
  public static String parseTime(long time) {
    time = time / 1000;// second

    long second = 0;
    second = time % 60;
    time = time / 60; // minute

    long minutes = 0;
    minutes = time % 60;
    time = time / 60;// hours

    long hours = 0;
    hours = time % 24;

    long days = 0;
    days = time / 24;// days

    StringBuilder sb = new StringBuilder();
    sb.append(days).append(" days ");
    sb.append(hours).append(" hours ");
    sb.append(minutes).append(" minutes ");
    sb.append(second).append(" second ");

    return sb.toString();
  }

  public static void main(String[] args) {
    long time = 1000;
    time = time * 5;
    System.out.println(parseTime(time));

    time = time * 62;
    System.out.println(parseTime(time));
  }

}

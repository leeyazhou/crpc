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

package com.github.leeyazhou.crpc.core.object;

/**
 * @author lee
 */
public enum SideType {

  /**
   * 消费方
   */
  SIDE_CONSUMER((byte) 0),

  /**
   * 服务方
   */
  SIDE_PROVIDER((byte) 1);

  private byte code;

  /**
   * 
   */
  private SideType(byte code) {
    this.code = code;
  }

  /**
   * @return the code
   */
  public byte getCode() {
    return code;
  }

  public static SideType of(int code) {
    SideType[] values = values();
    for (SideType item : values) {
      if (item.getCode() == code) {
        return item;
      }
    }
    return null;
  }
}

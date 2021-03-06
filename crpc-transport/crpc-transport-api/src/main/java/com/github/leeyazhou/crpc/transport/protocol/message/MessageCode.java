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

package com.github.leeyazhou.crpc.transport.protocol.message;

/**
 * @author leeyazhou
 */
public enum MessageCode {

  /**
   * 普通消息
   */
  MESSAGE_COMMON((byte) 0),

  /**
   * 心跳
   */
  MESSAGE_HEARTBEAT((byte) 1),

  /**
   * 注册
   */
  MESSAGE_REGISTER((byte) 2),

  /**
   * 服务器关闭
   */
  MESSAGE_SHUTDOWN((byte) 3),

  /**
   * 服务器重启
   */
  MESSAGE_RESTART((byte) 4);

  private byte code;

  private MessageCode(byte code) {
    this.code = code;
  }

  /**
   * @return the code
   */
  public byte getCode() {
    return code;
  }

  public static MessageCode valueOf(int code) {
    MessageCode[] values = values();
    for (MessageCode item : values) {
      if (item.getCode() == code) {
        return item;
      }
    }
    return null;
  }
}

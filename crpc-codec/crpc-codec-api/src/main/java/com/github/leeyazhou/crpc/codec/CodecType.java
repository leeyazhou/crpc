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
package com.github.leeyazhou.crpc.codec;

/**
 * 
 * @author leeyazhou
 */
public enum CodecType {

  /**
   * Java 原生序列化
   */
  JDK_CODEC((byte) 0, "jdk"),

  /**
   * Kryo 序列化
   */
  KRYO_CODEC((byte) 1, "kryo"),

  /**
   * ProtoBuff序列化
   */
  PB_CODEC((byte) 2, "protobuf"),

  /**
   * Hessian
   */
  HESSIAN_CODEC((byte) 3, "hessian"),

  /**
   * FST
   */
  FST_CODEC((byte) 4, "fst");

  private CodecType(byte code, String name) {
    this.code = code;
    this.name = name;
  }

  public static CodecType valueOf(byte code) {
    return CodecType.values()[code];
  }

  private byte code;
  private String name;

  /**
   * 编/解码器的id
   * 
   * @return id
   */
  public byte getCode() {
    return code;
  }

  /**
   * @return the serializerName
   */
  public String getName() {
    return name;
  }

}

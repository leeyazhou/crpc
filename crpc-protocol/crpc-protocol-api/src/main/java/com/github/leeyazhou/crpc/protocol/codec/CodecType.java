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
package com.github.leeyazhou.crpc.protocol.codec;

/**
 * 
 * @author leeyazhou
 */
public enum CodecType {

  /**
   * Java 原生序列化
   */
  JAVA_CODEC(0, "com.github.leeyazhou.crpc.protocol.codec.jdk.JavaCodec"),

  /**
   * Kryo 序列化
   */
  KRYO_CODEC(1, "com.github.leeyazhou.crpc.protocol.codec.kryo.KryoCodec"),

  /**
   * ProtoBuff序列化
   */
  PB_CODEC(2, "com.github.leeyazhou.crpc.protocol.codec.protobuf.ProtobufCodec"),

  /**
   * Hessian
   */
  HESSIAN_CODEC(3, "com.github.leeyazhou.crpc.protocol.codec.hessian.HessianCodec"),

  /**
   * FST
   */
  FST_CODEC(4, "com.github.leeyazhou.crpc.protocol.codec.fst.FSTCodec");

  private CodecType(int id, String codec) {
    try {
      this.id = id;
      this.codec = (Codec) Class.forName(codec).newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static CodecType valueOf(int id) {
    return CodecType.values()[id];
  }

  private int id;
  private Codec codec;

  /**
   * 编/解码器的id
   * 
   * @return id
   */
  public int getId() {
    return id;
  }

  /**
   * @return the codec
   */
  public Codec getCodec() {
    return codec;
  }

}

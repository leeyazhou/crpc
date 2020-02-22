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
package com.github.leeyazhou.crpc.protocol.codec;

/**
 * 
 * @author lee
 */
public enum Codecs {

  /**
   * Java 原生序列化
   */
  JAVA_CODEC(0, "com.github.crpc.protocol.codec.jdk.JavaEncoder", "com.github.crpc.protocol.codec.jdk.JavaDecoder"),

  /**
   * Kryo 序列化
   */
  KRYO_CODEC(1, "com.github.crpc.protocol.codec.kryo.KryoEncoder",
      "com.github.crpc.protocol.codec.kryo.KryoDecoder"),

  /**
   * ProtoBuff序列化
   */
  PB_CODEC(2, "com.github.crpc.protocol.codec.protobuf.ProtobufEncoder",
      "com.github.crpc.protocol.codec.protobuf.ProtobufDecoder"),

  /**
   * Hessian
   */
  HESSIAN_CODEC(3, "com.github.crpc.protocol.codec.hessian.HessianEncoder",
      "com.github.crpc.protocol.codec.hessian.HessianDecoder"),

  /**
   * FST
   */
  FST_CODEC(4, "com.github.crpc.protocol.codec.fst.FSTEncoder", "com.github.crpc.protocol.codec.fst.FSTDecoder");

  private Codecs(int id, String encoder, String decoder) {
    try {
      this.id = id;
      this.encoder = (Encoder) Class.forName(encoder).newInstance();
      this.decoder = (Decoder) Class.forName(decoder).newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Codecs valueOf(int id) {
    return Codecs.values()[id];
  }

  private int id;
  private Encoder encoder;
  private Decoder decoder;

  /**
   * 编/解码器的id
   * 
   * @return id
   */
  public int getId() {
    return id;
  }

  /**
   * 编码器
   * 
   * @return Encoder
   */
  public Encoder getEncoder() {
    return encoder;
  }

  /**
   * 解码器
   * 
   * @return Decoder
   */
  public Decoder getDecoder() {
    return decoder;
  }

}

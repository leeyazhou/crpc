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
package com.github.leeyazhou.crpc.protocol.codec.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.leeyazhou.crpc.protocol.codec.Codec;

public class KryoCodec implements Codec {

  @Override
  public Object decode(String className, byte[] bytes) throws Exception {
    Input input = new Input(bytes);
    try {
      return getKryo().readClassAndObject(input);
    } finally {
      input.close();
    }
  }

  @Override
  public byte[] encode(Object object) throws Exception {
    Output output = new Output(256, -1);
    try {
      getKryo().writeClassAndObject(output, object);
      return output.toBytes();
    } finally {
      output.flush();
      output.close();
    }
  }
  
  private static final ThreadLocal<Kryo> kryosLocal = new ThreadLocal<Kryo>() {
    protected Kryo initialValue() {
      Kryo kryo = new Kryo();
      /*
       * for (ClassItem item : classSet) { if (item.getSerializer() == null) { kryo.register(item.getType()); } else {
       * kryo.register(item.getType(), item.getSerializer()); } }
       */
      kryo.setRegistrationRequired(false);
      kryo.setReferences(true);
      return kryo;
    }
  };

  public static Kryo getKryo() {
    return kryosLocal.get();
  }

  /*
   * public static synchronized void registerClass(Class<?> clazz, Serializer<?> serializer) { ClassItem ci = new
   * ClassItem(clazz, serializer); classSet.add(ci); } public static synchronized void registerClass(Class<?> clazz) {
   * ClassItem ci = new ClassItem(clazz); classSet.add(ci); } private static class ClassItem { private Class<?> type;
   * private Serializer<?> serializer; ClassItem(final Class<?> type) { this.type = type; } ClassItem(final Class<?>
   * type, final Serializer<?> serializer) { this.type = type; this.serializer = serializer; } public Class<?> getType()
   * { return type; } public void setType(Class<?> type) { this.type = type; } public Serializer<?> getSerializer() {
   * return serializer; } public void setSerializer(Serializer<?> serializer) { this.serializer = serializer; }
   * @Override public String toString() { return "ClassItem [type=" + type + ", serializer=" + serializer + "]"; } }
   */
}

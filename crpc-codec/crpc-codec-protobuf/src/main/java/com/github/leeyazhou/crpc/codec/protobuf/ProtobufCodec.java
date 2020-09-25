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

package com.github.leeyazhou.crpc.codec.protobuf;

import java.util.concurrent.ConcurrentHashMap;
import com.github.leeyazhou.crpc.codec.AbstractCodec;
import com.github.leeyazhou.crpc.core.util.SerializerUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * @author leeyazhou
 *
 */
public class ProtobufCodec extends AbstractCodec {

  @Override
  public Object doDecode(String className, byte[] bytes) {
    Class<?> clazz = SerializerUtil.getInstance().getClazzForName(className);
    return doDecode(bytes, clazz);
  }


  @Override
  public byte[] doEncode(Object object) {
    return doEncodeInternal(object);
  }

  private ConcurrentHashMap<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();


  private <T> Schema<T> getSchema(Class<T> cls) {
    @SuppressWarnings("unchecked")
    Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
    if (schema == null) {
      schema = RuntimeSchema.createFrom(cls);
      if (schema != null) {
        cachedSchema.putIfAbsent(cls, schema);
      }
    }
    return schema;
  }

  public <T> byte[] doEncodeInternal(T obj) {
    LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    try {
      if (obj == null) {
        return null;
      }
      @SuppressWarnings("unchecked")
      Schema<T> schema = (Schema<T>) getSchema(obj.getClass());
      return ProtobufIOUtil.toByteArray(obj, schema, buffer);
    } catch (Exception err) {
      throw new IllegalStateException(err.getMessage(), err);
    } finally {
      buffer.clear();
    }
  }

  public <T> T doDecode(byte[] data, Class<T> cls) {
    try {
      T message = ObjectUtils.newInstance(cls);
      Schema<T> schema = getSchema(cls);
      ProtobufIOUtil.mergeFrom(data, message, schema);
      return message;
    } catch (Exception err) {
      throw new IllegalStateException(err.getMessage(), err);
    }
  }
}

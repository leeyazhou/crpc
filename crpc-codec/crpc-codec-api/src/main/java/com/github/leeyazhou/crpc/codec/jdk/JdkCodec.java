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
package com.github.leeyazhou.crpc.codec.jdk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.github.leeyazhou.crpc.codec.AbstractCodec;
import com.github.leeyazhou.crpc.codec.CodecException;
import com.github.leeyazhou.crpc.core.util.IOUtils;

public class JdkCodec extends AbstractCodec {

  @Override
  public Object doDecode(String className, byte[] bytes) {
    ObjectInputStream objectIn = null;
    Object resultObject = null;
    try {
      objectIn = new ObjectInputStream(new ByteArrayInputStream(bytes));
      resultObject = objectIn.readObject();
    } catch (Exception e) {
      throw new CodecException(e);
    } finally {
      IOUtils.close(objectIn);
    }

    return resultObject;
  }

  @Override
  public byte[] doEncode(Object object) {
    ByteArrayOutputStream byteArray = null;
    ObjectOutputStream output = null;
    try {
      byteArray = new ByteArrayOutputStream();
      output = new ObjectOutputStream(byteArray);
      output.writeObject(object);
      output.flush();
    } catch (Exception e) {
      throw new CodecException(e);
    } finally {
      IOUtils.close(output);
    }
    return byteArray.toByteArray();
  }
}

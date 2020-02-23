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
package com.github.leeyazhou.crpc.protocol.codec.jdk;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import com.github.leeyazhou.crpc.protocol.codec.Decoder;

public class JavaDecoder implements Decoder {

  @Override
  public Object decode(String className, byte[] bytes) throws Exception {
    ObjectInputStream objectIn = null;
    Object resultObject = null;
    try {
      objectIn = new ObjectInputStream(new ByteArrayInputStream(bytes));
      resultObject = objectIn.readObject();
    } finally {
      if (null != objectIn) {
        objectIn.close();
      }
    }

    return resultObject;
  }

}

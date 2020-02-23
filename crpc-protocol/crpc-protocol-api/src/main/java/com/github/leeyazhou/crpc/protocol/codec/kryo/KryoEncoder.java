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

import com.esotericsoftware.kryo.io.Output;
import com.github.leeyazhou.crpc.protocol.codec.Encoder;
import com.github.leeyazhou.crpc.protocol.codec.kryo.util.KryoUtils;

public class KryoEncoder implements Encoder {

  @Override
  public byte[] encode(Object object) throws Exception {
    Output output = new Output(256, -1);
    try {
      KryoUtils.getKryo().writeClassAndObject(output, object);
      return output.toBytes();
    } finally {
      output.flush();
      output.close();
    }
  }

}

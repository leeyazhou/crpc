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

package com.github.leeyazhou.crpc.codec.hessian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.github.leeyazhou.crpc.codec.AbstractCodec;
import com.github.leeyazhou.crpc.codec.CodecException;

/**
 * @author leeyazhou
 */
public class HessianSerializer extends AbstractCodec {

  @Override
  public Object doDecode(String className, byte[] bytes) {
    Hessian2Input input = null;
    try {
      input = new Hessian2Input(new ByteArrayInputStream(bytes));
      return input.readObject();
    } catch (Exception e) {
      throw new CodecException(e);
    } finally {
      try {
        if (input != null) {
          input.close();
        }
      } catch (Exception e2) {
        //
      }
    }

  }

  @Override
  public byte[] doEncode(Object object) {
    Hessian2Output output = null;
    try {
      ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
      output = new Hessian2Output(byteArray);
      output.writeObject(object);
      return byteArray.toByteArray();
    } catch (Exception e) {
      throw new CodecException(e);
    } finally {
      try {
        if (output != null) {
          output.close();
        }
      } catch (IOException e) {
        //
      }
    }

  }
}

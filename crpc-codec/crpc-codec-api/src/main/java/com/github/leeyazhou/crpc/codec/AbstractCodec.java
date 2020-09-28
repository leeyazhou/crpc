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
package com.github.leeyazhou.crpc.codec;

/**
 * @author leeyazhou
 *
 */
public abstract class AbstractCodec implements Codec {

  @Override
  public byte[] encode(Object object) throws CodecException {
    try {
      return doEncode(object);
    } catch (CodecException e) {
      throw e;
    } catch (Exception e) {
      throw new CodecException(e);
    }
  }

  @Override
  public Object decode(String className, byte[] bytes) throws CodecException {
    try {
      return doDecode(className, bytes);
    } catch (CodecException e) {
      throw e;
    } catch (Exception e) {
      throw new CodecException(e);
    }
  }

  protected abstract byte[] doEncode(Object object);

  protected abstract Object doDecode(String className, byte[] bytes);

}

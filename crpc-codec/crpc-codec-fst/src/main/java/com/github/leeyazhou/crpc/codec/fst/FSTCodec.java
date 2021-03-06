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

package com.github.leeyazhou.crpc.codec.fst;

import org.nustaq.serialization.FSTConfiguration;
import com.github.leeyazhou.crpc.codec.AbstractCodec;

/**
 * @author leeyazhou
 */
public class FSTCodec extends AbstractCodec {
  private final FSTConfiguration configuration = FSTConfiguration.createDefaultConfiguration();

  @Override
  public Object doDecode(String className, byte[] bytes) {
    return getConfiguration().asObject(bytes);
  }

  @Override
  public byte[] doEncode(Object object) {
    return getConfiguration().asByteArray(object);
  }


  /**
   * @return the configuration
   */
  public FSTConfiguration getConfiguration() {
    return configuration;
  }
}

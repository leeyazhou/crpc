/**
 * Copyright © 2019 leeyazhou (coderhook@gmail.com)
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

package com.github.leeyazhou.crpc.protocol.codec.fst;

import com.github.leeyazhou.crpc.protocol.codec.Encoder;
import com.github.leeyazhou.crpc.protocol.codec.fst.util.FSTFactory;

/**
 * @author lee
 */
public class FSTEncoder implements Encoder {

  @Override
  public byte[] encode(Object object) throws Exception {
    return FSTFactory.get().getConfiguration().asByteArray(object);
  }

}
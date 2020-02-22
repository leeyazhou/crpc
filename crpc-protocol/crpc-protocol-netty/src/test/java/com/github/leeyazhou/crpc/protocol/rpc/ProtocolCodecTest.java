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
/**
 * 
 */

package com.github.leeyazhou.crpc.protocol.rpc;

import org.junit.Test;

import com.github.leeyazhou.crpc.protocol.codec.Codecs;
import com.github.leeyazhou.crpc.protocol.model.User;

/**
 * @author lee
 */
public class ProtocolCodecTest {

  @Test
  public void testEncode() throws Exception {
    User user = new User(100, 100, "CRPC技术部", "admin@github.cn");
    byte[] bytes = Codecs.KRYO_CODEC.getEncoder().encode(user);
    Object object = Codecs.KRYO_CODEC.getDecoder().decode(User.class.getName(), bytes);
    System.out.println(object);
  }

  @Test
  public void testDecode() throws Exception {
    User user = new User(100, 100, "CRPC技术部", "admin@github.cn");
    byte[] bytes = Codecs.KRYO_CODEC.getEncoder().encode(user);
    System.out.println(bytes);
  }
}

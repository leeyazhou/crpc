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
package com.github.leeyazhou.crpc.protocol;

/**
 * Protocol Interface,for custom network protocol
 * 
 * @author zach
 *
 */
public interface Protocol {

  /**
   * encode Message to byte and write to network framework
   * 
   * @param byteBufwrapper ByteBufferWrapper
   * @param message Object
   * @return ByteBufferWrapper
   * @throws Exception any exception
   */
  public ByteBufWrapper encode(ByteBufWrapper byteBufwrapper, Header message) throws Exception;

  /**
   * decode stream to object
   * 
   * @param byteBufwrapper ByteBufferWrapper
   * @param originPos originPos
   * @return AbstractWrapper
   * @throws Exception any exception
   */
  public Header decode(ByteBufWrapper byteBufwrapper, int originPos) throws Exception;
}

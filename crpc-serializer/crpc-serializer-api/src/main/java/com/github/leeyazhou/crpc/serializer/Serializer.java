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
package com.github.leeyazhou.crpc.serializer;

/**
 * 
 * codec
 * 
 * @author leeyazhou
 *
 */
public interface Serializer {
  /**
   * Encode Object to byte[]
   * 
   * @param object object
   * @return byte of object
   * @throws Exception any exception
   */
  byte[] encode(Object object) throws Exception;

  /**
   * decode byte[] to Object
   * 
   * @param className className
   * @param bytes byte
   * @return Object
   * @throws Exception exception
   */
  Object decode(String className, byte[] bytes) throws Exception;

}

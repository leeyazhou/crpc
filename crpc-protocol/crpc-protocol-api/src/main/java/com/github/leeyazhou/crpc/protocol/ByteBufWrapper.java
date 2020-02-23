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
package com.github.leeyazhou.crpc.protocol;

/**
 * 
 * @author zach
 *
 */
public interface ByteBufWrapper {

  public ByteBufWrapper get(int capacity);

  public void writeByte(int index, byte data);

  public void writeByte(byte data);

  public byte readByte();

  public void writeInt(int data);

  public void writeBytes(byte[] data);

  public int readableBytes();

  public int readInt();

  public void readBytes(byte[] dst);

  public int readerIndex();

  public void setReaderIndex(int readerIndex);

}

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
package com.github.leeyazhou.crpc.protocol.netty;

import com.github.leeyazhou.crpc.protocol.ByteBufWrapper;

import io.netty.buffer.ByteBuf;

public class NettyByteBufWrapper implements ByteBufWrapper {

  private ByteBuf byteBuf;

  public NettyByteBufWrapper(ByteBuf byteBuf) {
    this.byteBuf = byteBuf;
  }

  @Override
  public ByteBufWrapper get(int capacity) {
    return this;
  }

  @Override
  public byte readByte() {
    return byteBuf.readByte();
  }

  @Override
  public void readBytes(byte[] dst) {
    byteBuf.readBytes(dst);
  }

  @Override
  public int readInt() {
    return byteBuf.readInt();
  }

  @Override
  public int readableBytes() {
    return byteBuf.readableBytes();
  }

  @Override
  public int readerIndex() {
    return byteBuf.readerIndex();
  }

  @Override
  public void setReaderIndex(int index) {
    byteBuf.setIndex(index, byteBuf.writerIndex());
  }

  @Override
  public void writeByte(byte data) {
    byteBuf.writeByte(data);
  }

  @Override
  public void writeByte(int index, byte data) {
    byteBuf.writeByte(data);
  }

  @Override
  public void writeBytes(byte[] data) {
    byteBuf.writeBytes(data);
  }

  @Override
  public void writeInt(int data) {
    byteBuf.writeInt(data);
  }

}
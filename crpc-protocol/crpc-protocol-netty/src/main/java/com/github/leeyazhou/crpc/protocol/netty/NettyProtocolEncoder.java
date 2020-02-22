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
package com.github.leeyazhou.crpc.protocol.netty;

import com.github.leeyazhou.crpc.protocol.Header;
import com.github.leeyazhou.crpc.protocol.ProtocolFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyProtocolEncoder extends MessageToByteEncoder<Header> {

  @Override
  protected void encode(ChannelHandlerContext ctx, Header msg, ByteBuf out) throws Exception {
    NettyByteBufWrapper byteBuffer = new NettyByteBufWrapper(out);
    ProtocolFactory.getProtocol().encode(byteBuffer, msg);
  }

}

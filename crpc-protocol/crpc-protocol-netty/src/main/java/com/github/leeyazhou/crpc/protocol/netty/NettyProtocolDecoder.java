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
package com.github.leeyazhou.crpc.protocol.netty;

import java.util.List;
import com.github.leeyazhou.crpc.protocol.Protocol;
import com.github.leeyazhou.crpc.protocol.ProtocolFactory;
import com.github.leeyazhou.crpc.protocol.SimpleProtocol;
import com.github.leeyazhou.crpc.protocol.message.Message;
import com.github.leeyazhou.crpc.core.exception.UnsupportProtocolException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 
 * @author leeyazhou
 *
 */
public class NettyProtocolDecoder extends ByteToMessageDecoder {

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    NettyByteBufWrapper byteBufWrapper = new NettyByteBufWrapper(in);

    final int originPos = byteBufWrapper.readerIndex();
    if (byteBufWrapper.readableBytes() < SimpleProtocol.HEADER_LEN) {
      byteBufWrapper.setReaderIndex(originPos);
      return;
    }

    int version = byteBufWrapper.readByte();
    if (version != SimpleProtocol.VERSION) {
      throw new UnsupportProtocolException("Unsupport protocol version: " + version);
    }

    int protocolType = byteBufWrapper.readByte();
    Protocol protocol = ProtocolFactory.getProtocol();
    if (protocol == null) {
      throw new UnsupportProtocolException("Unsupport protocol type : " + protocolType);
    }
    Message msg = protocol.decode(byteBufWrapper, originPos);
    if (msg != null) {
      out.add(msg);
    }
  }

}

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
package com.github.leeyazhou.crpc.transport.netty.protocol;

import java.util.List;
import com.github.leeyazhou.crpc.core.exception.UnsupportProtocolException;
import com.github.leeyazhou.crpc.transport.protocol.CrpcProtocol;
import com.github.leeyazhou.crpc.transport.protocol.Protocol;
import com.github.leeyazhou.crpc.transport.protocol.ProtocolFactory;
import com.github.leeyazhou.crpc.transport.protocol.ProtocolType;
import com.github.leeyazhou.crpc.transport.protocol.payload.Payload;
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
    if (byteBufWrapper.readableBytes() < CrpcProtocol.HEADER_LEN) {
      byteBufWrapper.setReaderIndex(originPos);
      return;
    }

    byte magic = byteBufWrapper.readByte();
    if (magic != ProtocolType.CRPC.getCode()) {
      throw new UnsupportProtocolException("Unsupport protocol version: " + magic);
    }

    Protocol protocol = ProtocolFactory.getProtocol(magic);
    if (protocol == null) {
      throw new UnsupportProtocolException("Unsupport protocol type : " + magic);
    }
    byteBufWrapper.setReaderIndex(originPos);
    Payload payload = protocol.decode(byteBufWrapper);
    if (payload != null) {
      out.add(payload);
    }
  }

}

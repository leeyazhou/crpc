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

import com.github.leeyazhou.crpc.core.exception.UnsupportProtocolException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.protocol.codec.CodecType;
import com.github.leeyazhou.crpc.protocol.message.Message;
import com.github.leeyazhou.crpc.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.protocol.message.ResponseMessage;

/**
 * <b>Common RPC Protocol</b><br>
 * <br>
 * <b>Protocol Header </b><br>
 * <table>
 * <CAPTION>Common Usenet Abbreviations</CAPTION>
 * <tr>
 * <td>VERSION(1B)</td>
 * <td>Protocol Version</td>
 * </tr>
 * <tr>
 * <td>TYPE(1B)</td>
 * <td>Protocol Type,so u can custom your protocol</td>
 * </tr>
 * </table>
 * 
 * <b>Request Protocol </b> <br>
 * VERSION(1B): <br>
 * TYPE(1B): request/response <br>
 * CODECTYPE(1B): serialize/deserialize type <br>
 * KEEPED(1B): <br>
 * KEEPED(1B): <br>
 * KEEPED(1B): <br>
 * ID(4B): request id <br>
 * TIMEOUT(4B): request timeout <br>
 * TARGETINSTANCELEN(4B): target service name length <br>
 * METHODNAMELEN(4B): method name length <br>
 * ARGSCOUNT(4B): method args count <br>
 * ARG1TYPELEN(4B): method arg1 type len <br>
 * ARG2TYPELEN(4B): method arg2 type len <br>
 * ... <br>
 * ARG1LEN(4B): method arg1 len <br>
 * ARG2LEN(4B): method arg2 len <br>
 * ... <br>
 * TARGETINSTANCENAME <br>
 * METHODNAME <br>
 * ARG1TYPENAME <br>
 * ARG2TYPENAME <br>
 * ... <br>
 * ARG1 <br>
 * ARG2 <br>
 * ... <br>
 * <b>Protocol Header </b><br>
 * VERSION(1B): Protocol Version <br>
 * TYPE(1B): Protocol Type,so u can custom your protocol <br>
 * Response Protocol <br>
 * VERSION(1B): <br>
 * TYPE(1B): request/response <br>
 * DATATYPE(1B): serialize/deserialize type <br>
 * KEEPED(1B): <br>
 * KEEPED(1B): <br>
 * KEEPED(1B): <br>
 * ID(4B): request id <br>
 * BodyClassNameLen(4B): body className Len <br>
 * LENGTH(4B): body length <br>
 * BodyClassName <br>
 * BODY if need than set <br>
 * 
 */
public class SimpleProtocol implements Protocol {

  static final Logger logger = LoggerFactory.getLogger(SimpleProtocol.class);

  public static final int PROTOCOL_TYPE = 1;

  public static final byte VERSION = (byte) 1;

  public static final int HEADER_LEN = 1 * 2;

  private static final int BODY_HEADER_LEN = 1 * 6 + 2 * 4;

  // private static final int RESPONSE_HEADER_LEN = 1 * 6 + 3 * 4;

  private static final byte REQUEST = (byte) 0;

  private static final byte RESPONSE = (byte) 1;

  @Override
  public ByteBufWrapper encode(ByteBufWrapper bytebufferWrapper, Message message) throws Exception {
    byte type = REQUEST;
    if (message instanceof ResponseMessage) {
      type = RESPONSE;
    }

    int id = message.getId();
    byte[] bodyBytes = CodecType.valueOf(message.getCodecType()).getCodec().encode(message);
    int capacity = HEADER_LEN + BODY_HEADER_LEN + bodyBytes.length;
    ByteBufWrapper byteBuffer = bytebufferWrapper.get(capacity);

    byteBuffer.writeByte(VERSION);
    byteBuffer.writeByte((byte) PROTOCOL_TYPE);

    byteBuffer.writeByte(VERSION);
    byteBuffer.writeByte(type);
    byteBuffer.writeByte((byte) message.getCodecType());
    byteBuffer.writeByte((byte) message.getMessageType().getCode());
    byteBuffer.writeByte((byte) 0);
    byteBuffer.writeByte((byte) 0);

    byteBuffer.writeInt(id);
    byteBuffer.writeInt(bodyBytes.length);

    byteBuffer.writeBytes(bodyBytes);

    return byteBuffer;
  }

  @Override
  public Message decode(ByteBufWrapper byteBufWrapper, int originPos) throws Exception {
    if (byteBufWrapper.readableBytes() < BODY_HEADER_LEN) {
      byteBufWrapper.setReaderIndex(originPos);
      return null;
    }
    byte version = byteBufWrapper.readByte();
    if (version != (byte) VERSION) {
      throw new UnsupportProtocolException("protocol version :" + version + " is not supported!");
    }
    final byte type = byteBufWrapper.readByte();
    final int codecType = byteBufWrapper.readByte();
    byteBufWrapper.readByte();// messageType
    byteBufWrapper.readByte();// keeped
    byteBufWrapper.readByte();// keeped

    byteBufWrapper.readInt();// id
    final int bodyLen = byteBufWrapper.readInt();
    if (byteBufWrapper.readableBytes() < bodyLen) {
      byteBufWrapper.setReaderIndex(originPos);
      return null;
    }
    byte[] bodyBytes = new byte[bodyLen];
    byteBufWrapper.readBytes(bodyBytes);
    Message ret = null;
    if (type == REQUEST) {
      ret = (Message) CodecType.valueOf(codecType).getCodec().decode(RequestMessage.class.getName(), bodyBytes);
    } else {
      ret = (Message) CodecType.valueOf(codecType).getCodec().decode(ResponseMessage.class.getName(), bodyBytes);
    }
    return ret;
  }

}

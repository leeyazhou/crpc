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
package com.github.leeyazhou.crpc.transport.protocol;

import com.github.leeyazhou.crpc.codec.Codec;
import com.github.leeyazhou.crpc.codec.CodecType;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.transport.protocol.message.Header;
import com.github.leeyazhou.crpc.transport.protocol.payload.Payload;
import com.github.leeyazhou.crpc.transport.protocol.payload.PayloadBody;
import com.github.leeyazhou.crpc.transport.protocol.payload.RequestPayloadBody;
import com.github.leeyazhou.crpc.transport.protocol.payload.ResponsePayloadBody;

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
 */
public class CrpcProtocol implements Protocol {
  static final Logger logger = LoggerFactory.getLogger(CrpcProtocol.class);

  public static final byte MAGIC = (byte) 1;
  public static final byte VERSION = (byte) 1;

  public static final int HEADER_LEN = 1 * 7 + 3 * 4;

  private static final byte REQUEST = (byte) 0;


  @Override
  public ByteBufWrapper encode(ByteBufWrapper bytebufferWrapper, Payload payload) throws ProtocolException {
    int id = payload.id();
    Codec codec = ServiceLoader.load(Codec.class).load(CodecType.valueOf(payload.getCodecType()).getName());
    byte[] bodyBytes = codec.encode(payload.getPayloadBody());
    byte[] headerBytes = codec.encode(payload.getHeaders());
    payload.setHeaderLength(headerBytes.length);
    payload.setBodyLength(bodyBytes.length);


    final int capacity = HEADER_LEN + payload.getBodyLength() + payload.getHeaderLength();
    ByteBufWrapper byteBuffer = bytebufferWrapper.get(capacity);

    byteBuffer.writeByte(MAGIC);
    byteBuffer.writeByte(VERSION);
    byteBuffer.writeByte(payload.getMessageType());
    byteBuffer.writeByte((byte) payload.getMessageCode());
    if (payload.getPayloadBody() instanceof RequestPayloadBody) {
      byteBuffer.writeBoolean(((RequestPayloadBody) payload.getPayloadBody()).isOneWay());
    } else {
      byteBuffer.writeBoolean(false);
    }
    byteBuffer.writeByte((byte) payload.getCodecType());
    byteBuffer.writeByte((byte) 0);// keeped

    byteBuffer.writeInt(id);
    byteBuffer.writeInt(payload.getHeaderLength());
    byteBuffer.writeInt(payload.getBodyLength());

    byteBuffer.writeBytes(headerBytes);
    byteBuffer.writeBytes(bodyBytes);

    return byteBuffer;
  }

  @Override
  public Payload decode(ByteBufWrapper byteBufWrapper) throws ProtocolException {
    final int originPos = byteBufWrapper.readerIndex();
    if (byteBufWrapper.readableBytes() < HEADER_LEN) {
      byteBufWrapper.setReaderIndex(originPos);
      return null;
    }
    final byte protocolType = byteBufWrapper.readByte();// magic
    byteBufWrapper.readByte(); // version
    final byte messageType = byteBufWrapper.readByte();
    final byte messageCode = byteBufWrapper.readByte();
    final boolean oneway = byteBufWrapper.readBoolean();
    final byte codecType = byteBufWrapper.readByte();
    byteBufWrapper.readByte();// keeped

    final int id = byteBufWrapper.readInt();// id
    final int headerLen = byteBufWrapper.readInt();
    final int bodyLen = byteBufWrapper.readInt();


    if (byteBufWrapper.readableBytes() < headerLen + bodyLen) {
      byteBufWrapper.setReaderIndex(originPos);
      return null;
    }
    Codec codec = ServiceLoader.load(Codec.class).load(CodecType.valueOf(codecType).getName());
    byte[] headerBytes = new byte[headerLen];
    byteBufWrapper.readBytes(headerBytes);

    Header[] headers = (Header[]) codec.decode(Header.class.getName(), headerBytes);

    byte[] bodyBytes = new byte[bodyLen];
    byteBufWrapper.readBytes(bodyBytes);


    PayloadBody payloadBody = null;
    if (messageType == REQUEST) {
      payloadBody = (RequestPayloadBody) codec.decode(RequestPayloadBody.class.getName(), bodyBytes);
      ((RequestPayloadBody) payloadBody).setOneWay(oneway);
    } else {
      payloadBody = (ResponsePayloadBody) codec.decode(ResponsePayloadBody.class.getName(), bodyBytes);
    }

    Payload payload = new Payload();
    payload.setId(id);
    payload.setProtocolType(protocolType);
    payload.setCodecType(codecType);
    payload.setMessageCode(messageCode);
    payload.setMessageType(messageType);
    payload.setHeaderLength(headerLen);
    payload.setHeaders(headers);
    payload.setBodyLength(bodyLen);
    payload.setPayloadBody(payloadBody);

    return payload;
  }

}

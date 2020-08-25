/**
 * Copyright © 2016~2020 CRPC (coderhook@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import com.github.leeyazhou.crpc.transport.protocol.message.Message;
import com.github.leeyazhou.crpc.transport.protocol.message.MessageType;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

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
  public ByteBufWrapper encode(ByteBufWrapper bytebufferWrapper, Message message) throws Exception {
    int id = message.id();
    Codec codec = ServiceLoader.load(Codec.class).load(CodecType.valueOf(message.getCodecType()).getName());
    byte[] bodyBytes = null;
    if (message.getMessageType() == MessageType.REQUEST.getCode()) {
      RequestMessage request = (RequestMessage) message;
      InvocationRequest invocation = new InvocationRequest();
      invocation.setArgs(request.getArgs());
      invocation.setArgTypes(request.getArgTypes());
      invocation.setMethodName(request.getMethodName());
      invocation.setServiceTypeName(request.getServiceTypeName());
      invocation.setTimeout(request.getTimeout());
      bodyBytes = codec.encode(invocation);
    } else {
      ResponseMessage response = (ResponseMessage) message;
      InvocationResponse invocationResponse = new InvocationResponse();
      invocationResponse.setError(response.isError());
      invocationResponse.setResponse(response.getResponse());
      invocationResponse.setResponseClassName(response.getResponseClassName());
      bodyBytes = codec.encode(invocationResponse);
    }

    byte[] headerBytes = codec.encode(message.getHeaders());


    final int capacity = HEADER_LEN + headerBytes.length + bodyBytes.length;
    ByteBufWrapper byteBuffer = bytebufferWrapper.get(capacity);

    byteBuffer.writeByte(MAGIC);
    byteBuffer.writeByte(VERSION);
    byteBuffer.writeByte(message.getMessageType());
    byteBuffer.writeByte((byte) message.getMessageCode());
    if (message instanceof RequestMessage) {
      byteBuffer.writeBoolean(((RequestMessage) message).isOneWay());
    } else {
      byteBuffer.writeBoolean(false);
    }
    byteBuffer.writeByte((byte) message.getCodecType());
    byteBuffer.writeByte((byte) 0);// keeped

    byteBuffer.writeInt(id);
    byteBuffer.writeInt(headerBytes.length);
    byteBuffer.writeInt(bodyBytes.length);

    byteBuffer.writeBytes(headerBytes);
    byteBuffer.writeBytes(bodyBytes);

    return byteBuffer;
  }

  @Override
  public Message decode(ByteBufWrapper byteBufWrapper, int originPos) throws Exception {
    if (byteBufWrapper.readableBytes() < HEADER_LEN) {
      byteBufWrapper.setReaderIndex(originPos);
      return null;
    }
    byteBufWrapper.readByte();// magic
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
    Message message = null;
    if (messageType == REQUEST) {
      InvocationRequest request = (InvocationRequest) codec.decode(InvocationRequest.class.getName(), bodyBytes);
      message = (Message) new RequestMessage(request.getServiceTypeName(), request.getMethodName()).setOneWay(oneway)
          .setTimeout(request.getTimeout()).setArgs(request.getArgs()).setArgTypes(request.getArgTypes());

    } else {
      InvocationResponse response = (InvocationResponse) codec.decode(InvocationResponse.class.getName(), bodyBytes);
      message = (Message) new ResponseMessage(response.getResponseClassName()).setError(response.isError())
          .setResponse(response.getResponse());
    }

    message.setHeaders(headers).setMessageType(messageType).setMessageCode(messageCode)// .setProtocolType(protocol)
        .setCodecType(codecType).setId(id).setHeaders(headers);

    return message;
  }

}

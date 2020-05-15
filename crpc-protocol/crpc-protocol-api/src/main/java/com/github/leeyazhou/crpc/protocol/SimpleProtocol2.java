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

import java.util.ArrayList;
import java.util.List;
import com.github.leeyazhou.crpc.codec.Codec;
import com.github.leeyazhou.crpc.codec.CodecType;
import com.github.leeyazhou.crpc.core.exception.UnsupportProtocolException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
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
public class SimpleProtocol2 implements Protocol {

  private static final Logger logger = LoggerFactory.getLogger(SimpleProtocol2.class);

  public static final int PROTOCOL_TYPE = 1;

  public static final byte VERSION = (byte) 1;

  public static final int HEADER_LEN = 2;

  private static final int REQUEST_HEADER_LEN = 1 * 6 + 5 * 4;

  private static final int RESPONSE_HEADER_LEN = 1 * 6 + 3 * 4;

  private static final byte REQUEST = (byte) 0;

  private static final byte RESPONSE = (byte) 1;

  @Override
  public ByteBufWrapper encode(ByteBufWrapper bytebufferWrapper, Message message) throws Exception {
    if (message instanceof RequestMessage) {
      return encodeRequest(bytebufferWrapper, message);
    } else if (message instanceof ResponseMessage) {
      return encodeResponse(bytebufferWrapper, message);
    }
    throw new UnsupportProtocolException("CRPC Protocol only supports send RequestWrapper && ResponseWrapper");
  }

  @Override
  public Message decode(ByteBufWrapper wrapper, int originPos) throws Exception {
    if (wrapper.readableBytes() < HEADER_LEN) {
      wrapper.setReaderIndex(originPos);
      return null;
    }
    byte version = wrapper.readByte();
    if (version != (byte) VERSION) {
      throw new UnsupportProtocolException("protocol version :" + version + " is not supported!");
    }
    byte type = wrapper.readByte();
    if (type == REQUEST) {
      return decodeRequest(wrapper, originPos);
    } else if (type == RESPONSE) {
      return decodeResponse(wrapper, originPos);
    }
    throw new UnsupportProtocolException("protocol type : " + type + " is not supported!");
  }

  private ByteBufWrapper encodeRequest(ByteBufWrapper bytebufferWrapper, Message message) throws Exception {
    final byte type = REQUEST;
    try {
      int requestArgTypesLen = 0;
      int requestArgsLen = 0;
      List<byte[]> requestArgTypes = new ArrayList<byte[]>();
      List<byte[]> requestArgs = new ArrayList<byte[]>();
      RequestMessage request = (RequestMessage) message;
      String[] requestArgTypeStrings = request.getArgTypes();
      Object[] requestObjects = request.getArgs();
      if (requestObjects != null) {
        for (String requestArgType : requestArgTypeStrings) {
          requestArgTypes.add(requestArgType.getBytes());
          requestArgTypesLen += requestArgType.getBytes().length;
        }
        Codec serializer =
            ServiceLoader.load(Codec.class).load(CodecType.valueOf(request.getCodecType()).getSerializerName());
        for (Object requestArg : requestObjects) {
          byte[] requestArgByte = serializer.encode(requestArg);
          requestArgs.add(requestArgByte);
          requestArgsLen += requestArgByte.length;
        }
      }
      byte[] targetInstanceNameByte = new byte[0];
      byte[] methodNameByte = new byte[0];
      if (request.getTargetClassName() != null) {
        targetInstanceNameByte = request.getTargetClassName().getBytes();
        methodNameByte = request.getMethodName().getBytes();
      }
      int id = request.id();
      int timeout = request.getTimeout();
      int capacity = HEADER_LEN + REQUEST_HEADER_LEN + requestArgs.size() * 4 * 2 + targetInstanceNameByte.length
          + methodNameByte.length + requestArgTypesLen + requestArgsLen;
      ByteBufWrapper byteBuffer = bytebufferWrapper.get(capacity);

      byteBuffer.writeByte(VERSION);
      byteBuffer.writeByte((byte) PROTOCOL_TYPE);

      byteBuffer.writeByte(VERSION);
      byteBuffer.writeByte(type);
      byteBuffer.writeByte((byte) request.getCodecType());
      byteBuffer.writeByte((byte) message.getMessageType().getCode());
      byteBuffer.writeByte((byte) 0);
      byteBuffer.writeByte((byte) 0);

      byteBuffer.writeInt(id);
      byteBuffer.writeInt(timeout);
      byteBuffer.writeInt(targetInstanceNameByte.length);
      byteBuffer.writeInt(methodNameByte.length);
      byteBuffer.writeInt(requestArgs.size());

      for (byte[] requestArgType : requestArgTypes) {
        byteBuffer.writeInt(requestArgType.length);
      }
      for (byte[] requestArg : requestArgs) {
        byteBuffer.writeInt(requestArg.length);
      }

      byteBuffer.writeBytes(targetInstanceNameByte);
      byteBuffer.writeBytes(methodNameByte);
      for (byte[] requestArgType : requestArgTypes) {
        byteBuffer.writeBytes(requestArgType);
      }
      for (byte[] requestArg : requestArgs) {
        byteBuffer.writeBytes(requestArg);
      }
      return byteBuffer;
    } catch (Exception err) {
      logger.error("encode request object error", err);
      throw err;
    }

  }

  private ByteBufWrapper encodeResponse(ByteBufWrapper bytebufferWrapper, Message message) throws Exception {
    final byte type = RESPONSE;
    ResponseMessage response = (ResponseMessage) message;
    final int id = response.id();
    int error = 0;
    byte[] body = new byte[0];
    byte[] className = new byte[0];
    Codec serializer =
        ServiceLoader.load(Codec.class).load(CodecType.valueOf(response.getCodecType()).getSerializerName());
    try {
      if (response.getResponse() != null) {
        className = response.getResponse().getClass().getName().getBytes();
        body = serializer.encode(response.getResponse());
      }
      if (response.isError()) {
        error = 1;
        className = response.getException().getClass().getName().getBytes();
        body = serializer.encode(response.getException());
      }
    } catch (Exception err) {
      logger.error("encode response object error", err);
      response.setResponse(new Exception("serialize response object error", err));
      className = Exception.class.getName().getBytes();
      body = serializer.encode(response.getResponse());
    }
    int capacity = HEADER_LEN + RESPONSE_HEADER_LEN + body.length;
    capacity += className.length;
    ByteBufWrapper byteBuffer = bytebufferWrapper.get(capacity);

    byteBuffer.writeByte(VERSION);
    byteBuffer.writeByte((byte) PROTOCOL_TYPE);

    byteBuffer.writeByte(VERSION);
    byteBuffer.writeByte(type);
    byteBuffer.writeByte((byte) response.getCodecType());
    byteBuffer.writeByte((byte) message.getMessageType().getCode());
    byteBuffer.writeByte((byte) error);
    byteBuffer.writeByte((byte) 0);

    byteBuffer.writeInt(id);
    byteBuffer.writeInt(className.length);
    byteBuffer.writeInt(body.length);

    byteBuffer.writeBytes(className);
    byteBuffer.writeBytes(body);
    return byteBuffer;

  }

  private Message decodeRequest(ByteBufWrapper byteBufWrapper, final int originPos) throws Exception {
    if (byteBufWrapper.readableBytes() < REQUEST_HEADER_LEN - HEADER_LEN) {
      byteBufWrapper.setReaderIndex(originPos);
      return null;
    }

    final int codecType = byteBufWrapper.readByte();
    final byte messageType = byteBufWrapper.readByte();
    byteBufWrapper.readByte();// keeped
    byteBufWrapper.readByte();// keeped
    final int requestId = byteBufWrapper.readInt();
    final int timeout = byteBufWrapper.readInt();
    int targetInstanceLen = byteBufWrapper.readInt();
    int methodNameLen = byteBufWrapper.readInt();
    int argsCount = byteBufWrapper.readInt();
    int argInfosLen = argsCount * 4 * 2;
    int expectedLenInfoLen = argInfosLen + targetInstanceLen + methodNameLen;
    if (byteBufWrapper.readableBytes() < expectedLenInfoLen) {
      byteBufWrapper.setReaderIndex(originPos);
      return null;
    }
    int expectedLen = 0;
    int[] argsTypeLen = new int[argsCount];
    for (int i = 0; i < argsCount; i++) {
      argsTypeLen[i] = byteBufWrapper.readInt();
      expectedLen += argsTypeLen[i];
    }
    int[] argsLen = new int[argsCount];
    for (int i = 0; i < argsCount; i++) {
      argsLen[i] = byteBufWrapper.readInt();
      expectedLen += argsLen[i];
    }
    byte[] targetInstanceByte = new byte[targetInstanceLen];
    byteBufWrapper.readBytes(targetInstanceByte);
    byte[] methodNameByte = new byte[methodNameLen];
    byteBufWrapper.readBytes(methodNameByte);
    if (byteBufWrapper.readableBytes() < expectedLen) {
      byteBufWrapper.setReaderIndex(originPos);
      return null;
    }
    String[] argTypes = new String[argsCount];
    for (int i = 0; i < argsCount; i++) {
      byte[] argTypeByte = new byte[argsTypeLen[i]];
      byteBufWrapper.readBytes(argTypeByte);
      argTypes[i] = new String(argTypeByte);
    }
    Object[] args = new Object[argsCount];
    Codec serializer = ServiceLoader.load(Codec.class).load(CodecType.valueOf(codecType).getSerializerName());
    for (int i = 0; i < argsCount; i++) {
      byte[] argByte = new byte[argsLen[i]];
      byteBufWrapper.readBytes(argByte);
      args[i] = serializer.decode(argTypes[i], argByte);
    }
    RequestMessage request = new RequestMessage(new String(targetInstanceByte), new String(methodNameByte), argTypes,
        args, timeout, requestId, codecType, PROTOCOL_TYPE);
    request.setMessageType(messageType);
    int messageLen = HEADER_LEN + REQUEST_HEADER_LEN + expectedLenInfoLen + expectedLen;
    request.setMessageLen(messageLen);
    return request;

  }

  private Message decodeResponse(ByteBufWrapper wrapper, final int originPos) throws Exception {

    if (wrapper.readableBytes() < RESPONSE_HEADER_LEN - HEADER_LEN) {
      wrapper.setReaderIndex(originPos);
      return null;
    }
    final int codecType = wrapper.readByte();
    final byte messageType = wrapper.readByte();
    final int error = wrapper.readByte();
    wrapper.readByte();
    final int requestId = wrapper.readInt();
    int classNameLen = wrapper.readInt();
    int bodyLen = wrapper.readInt();
    if (wrapper.readableBytes() < classNameLen + bodyLen) {
      wrapper.setReaderIndex(originPos);
      return null;
    }

    byte[] classNameBytes = new byte[classNameLen];
    wrapper.readBytes(classNameBytes);
    byte[] bodyBytes = new byte[bodyLen];
    wrapper.readBytes(bodyBytes);

    ResponseMessage response = new ResponseMessage(requestId, codecType, PROTOCOL_TYPE);
    response.setResponseClassName(new String(classNameBytes));
    response.setMessageType(messageType);
    if (bodyLen != 0) {
      Codec serializer = ServiceLoader.load(Codec.class).load(CodecType.valueOf(codecType).getSerializerName());
      response.setResponse(serializer.decode(response.getResponseClassName(), bodyBytes));
    }
    if (error == 1) {
      response.setError(Boolean.TRUE);
    }
    int messageLen = HEADER_LEN + RESPONSE_HEADER_LEN + classNameLen + bodyLen;
    response.setMessageLen(messageLen);
    return response;

  }

}

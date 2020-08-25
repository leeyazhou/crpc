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

package com.github.leeyazhou.crpc.transport.protocol.message;

import java.io.Serializable;
import com.github.leeyazhou.crpc.codec.CodecType;
import com.github.leeyazhou.crpc.transport.protocol.ProtocolType;

/**
 * @author leeyazhou
 */
public class Message implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * 消息类型
   */
  private byte messageType;

  private byte messageCode;

  private byte protocolType = ProtocolType.CRPC.getCode();

  private byte codecType;

  private Header[] headers;

  private int id;

  public Message() {}

  public byte getProtocolType() {
    return protocolType;
  }

  public Message setProtocolType(byte protocolType) {
    this.protocolType = protocolType;
    return this;
  }

  public Message setProtocolType(ProtocolType protocolType) {
    this.protocolType = protocolType.getCode();
    return this;
  }

  public byte getCodecType() {
    return codecType;
  }

  public Message setCodecType(byte codecType) {
    this.codecType = codecType;
    return this;
  }

  public Message setCodecType(CodecType codecType) {
    this.codecType = codecType.getCode();
    return this;
  }



  /**
   * 消息类型
   * 
   * @return {@link MessageCode}
   */
  public byte getMessageType() {
    return messageType;
  }

  /**
   * {@link MessageCode} * 消息类型<br>
   * 0:普通消息<br>
   * 1:心跳消息
   * 
   * @param messageType {@link MessageCode}
   * @return {@link Message}
   */
  public Message setMessageType(byte messageType) {
    this.messageType = messageType;
    return this;
  }

  public Message setMessageType(MessageType messageType) {
    this.messageType = messageType.getCode();
    return this;
  }



  /**
   * @return the id
   */
  public int id() {
    return id;
  }

  /**
   * @param id the id to set
   * @return {@link Message}
   */
  public Message setId(int id) {
    this.id = id;
    return this;
  }

  public Message setHeaders(Header[] headers) {
    this.headers = headers;
    return this;
  }

  public Header[] getHeaders() {
    return headers;
  }


  public Message addHeader(Header header) {
    if (this.headers == null) {
      this.headers = new Header[1];
      this.headers[0] = header;
      return this;
    }

    Header[] temp = new Header[this.headers.length + 1];
    System.arraycopy(this.headers, 0, temp, 0, headers.length);
    temp[headers.length + 1] = header;
    this.headers = temp;
    return this;
  }

  public byte getMessageCode() {
    return messageCode;
  }

  public Message setMessageCode(byte messageCode) {
    this.messageCode = messageCode;
    return this;
  }

  public Message setMessageCode(MessageCode messageCode) {
    this.messageCode = messageCode.getCode();
    return this;
  }
}

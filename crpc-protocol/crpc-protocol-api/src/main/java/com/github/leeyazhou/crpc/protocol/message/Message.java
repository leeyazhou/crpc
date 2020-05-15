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

package com.github.leeyazhou.crpc.protocol.message;

import java.io.Serializable;

/**
 * @author leeyazhou
 */
public abstract class Message implements Serializable {
  private static final long serialVersionUID = 1L;

  private int version;

  private int protocolType;

  private int codecType;

  private Header[] headers;

  private int id;

  /**
   * 消息类型
   */
  private MessageType messageType = MessageType.MESSAGE_COMMON;

  public Message() {}

  Message(int codecType, int protocolType) {
    this(codecType, protocolType, MessageType.MESSAGE_COMMON);
  }

  Message(int codecType, int protocolType, MessageType messageType) {
    this.codecType = codecType;
    this.protocolType = protocolType;
    this.messageType = messageType;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public int getProtocolType() {
    return protocolType;
  }

  public void setProtocolType(int protocolType) {
    this.protocolType = protocolType;
  }

  public int getCodecType() {
    return codecType;
  }

  public void setCodecType(int codecType) {
    this.codecType = codecType;
  }

  /**
   * 消息类型
   * 
   * @return {@link MessageType}
   */
  public MessageType getMessageType() {
    return messageType;
  }

  /**
   * {@link MessageType}
   * 
   * @param messageType {@link MessageType}
   */
  public void setMessageType(MessageType messageType) {
    this.messageType = messageType;
  }

  /**
   * 消息类型<br>
   * 0:普通消息<br>
   * 1:心跳消息
   * 
   * @param messageTypeCode {@link MessageType}
   */
  public void setMessageType(byte messageTypeCode) {
    this.messageType = MessageType.of(messageTypeCode);
  }


  /**
   * @return the id
   */
  public int id() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public Message setId(int id) {
    this.id = id;
    return this;
  }

  public void setHeaders(Header[] headers) {
    this.headers = headers;
  }

  public Header[] getHeaders() {
    return headers;
  }

  public void addHeader(Header header) {
    if (this.headers == null) {
      this.headers = new Header[1];
      this.headers[0] = header;
      return;
    }

    Header[] requestHeader2 = new Header[this.headers.length + 1];
    System.arraycopy(this.headers, 0, requestHeader2, 0, headers.length);
    requestHeader2[headers.length + 1] = header;
    this.headers = requestHeader2;

  }
}

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
/**
 * 
 */

package com.github.leeyazhou.crpc.protocol;

import java.io.Serializable;

import com.github.leeyazhou.crpc.core.object.MessageType;

/**
 * @author lee
 */
public abstract class Header implements Serializable {
  private static final long serialVersionUID = 7499489102375486122L;

  private int version;

  private int protocolType;

  private int codecType;

  private RequestHeader[] requestHeaders;

  private int id;

  /**
   * 消息类型
   */
  private MessageType messageType = MessageType.MESSAGE_COMMON;

  public Header() {}

  Header(int codecType, int protocolType) {
    this(codecType, protocolType, MessageType.MESSAGE_COMMON);
  }

  Header(int codecType, int protocolType, MessageType messageType) {
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
   * @param messageType messageType
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
  public int getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(int id) {
    this.id = id;
  }

  public void setRequestHeaders(RequestHeader[] requestHeaders) {
    this.requestHeaders = requestHeaders;
  }

  public RequestHeader[] getRequestHeaders() {
    return requestHeaders;
  }

  public void addRequestHeader(RequestHeader requestHeader) {
    if (this.requestHeaders == null) {
      requestHeaders = new RequestHeader[1];
      requestHeaders[0] = requestHeader;
      return;
    }

    RequestHeader[] requestHeader2 = new RequestHeader[this.requestHeaders.length + 1];
    System.arraycopy(this.requestHeaders, 0, requestHeader2, 0, requestHeaders.length);
    requestHeader2[requestHeaders.length + 1] = requestHeader;
    this.requestHeaders = requestHeader2;

  }
}

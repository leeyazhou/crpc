/**
 * 
 */
package com.github.leeyazhou.crpc.transport.protocol.payload;

import com.github.leeyazhou.crpc.transport.protocol.ProtocolType;
import com.github.leeyazhou.crpc.transport.protocol.message.Header;

/**
 * @author leeyazhou
 *
 */
public class Payload {
  private int id;
  private int headerLength;
  private int bodyLength;
  private byte messageType;

  private byte messageCode;

  private byte protocolType = ProtocolType.CRPC.getCode();

  private byte codecType;

  private Header[] headers;

  private PayloadBody payloadBody;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public byte getMessageType() {
    return messageType;
  }

  public void setMessageType(byte messageType) {
    this.messageType = messageType;
  }

  public byte getMessageCode() {
    return messageCode;
  }

  public void setMessageCode(byte messageCode) {
    this.messageCode = messageCode;
  }

  public byte getProtocolType() {
    return protocolType;
  }

  public void setProtocolType(byte protocolType) {
    this.protocolType = protocolType;
  }

  public byte getCodecType() {
    return codecType;
  }

  public void setCodecType(byte codecType) {
    this.codecType = codecType;
  }

  public Header[] getHeaders() {
    return headers;
  }

  public void setHeaders(Header[] headers) {
    this.headers = headers;
  }

  public PayloadBody getPayloadBody() {
    return payloadBody;
  }

  public void setPayloadBody(PayloadBody payloadBody) {
    this.payloadBody = payloadBody;
  }

  public int getHeaderLength() {
    return headerLength;
  }

  public void setHeaderLength(int headerLength) {
    this.headerLength = headerLength;
  }

  public int getBodyLength() {
    return bodyLength;
  }

  public void setBodyLength(int bodyLength) {
    this.bodyLength = bodyLength;
  }

  /**
   * @return
   */
  public int id() {
    return id;
  }



}

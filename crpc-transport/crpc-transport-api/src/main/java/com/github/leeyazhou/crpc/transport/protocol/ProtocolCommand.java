/**
 * 
 */
package com.github.leeyazhou.crpc.transport.protocol;

import java.io.Serializable;

/**
 * @author leeyazhou
 *
 */
public class ProtocolCommand implements Serializable {
  private static final long serialVersionUID = 1L;
  private byte magic;
  private byte version;
  private byte messageVersion;
  private byte messageType;
  private byte oneWay;
  private byte messageCode;
  private byte codecType;
  private int id;
  private int headerLength;
  private byte[] header;
  private int contentLength;
  private byte[] content;

  public byte getMagic() {
    return magic;
  }

  public ProtocolCommand setMagic(byte magic) {
    this.magic = magic;
    return this;
  }

  public byte getVersion() {
    return version;
  }

  public ProtocolCommand setVersion(byte version) {
    this.version = version;
    return this;
  }

  public byte getMessageVersion() {
    return messageVersion;
  }

  public ProtocolCommand setMessageVersion(byte messageVersion) {
    this.messageVersion = messageVersion;
    return this;
  }

  public byte getMessageType() {
    return messageType;
  }

  public ProtocolCommand setMessageType(byte messageType) {
    this.messageType = messageType;
    return this;
  }

  public byte getOneWay() {
    return oneWay;
  }

  public ProtocolCommand setOneWay(byte oneWay) {
    this.oneWay = oneWay;
    return this;
  }

  public ProtocolCommand setOneWay(boolean oneWay) {
    if (oneWay) {
      this.oneWay = 1;
    } else {
      this.oneWay = 0;
    }
    return this;
  }

  public byte getMessageCode() {
    return messageCode;
  }

  public ProtocolCommand setMessageCode(byte messageCode) {
    this.messageCode = messageCode;
    return this;
  }

  public byte getCodecType() {
    return codecType;
  }

  public ProtocolCommand setCodecType(byte codecType) {
    this.codecType = codecType;
    return this;
  }

  public int getId() {
    return id;
  }

  public ProtocolCommand setId(int id) {
    this.id = id;
    return this;
  }

  public int getHeaderLength() {
    return headerLength;
  }

  public ProtocolCommand setHeaderLength(int headerLength) {
    this.headerLength = headerLength;
    return this;
  }

  public byte[] getHeader() {
    return header;
  }

  public ProtocolCommand setHeader(byte[] header) {
    this.header = header;
    return this;
  }

  public int getContentLength() {
    return contentLength;
  }

  public ProtocolCommand setContentLength(int contentLength) {
    this.contentLength = contentLength;
    return this;
  }

  public byte[] getContent() {
    return content;
  }

  public ProtocolCommand setContent(byte[] content) {
    this.content = content;
    return this;
  }
}

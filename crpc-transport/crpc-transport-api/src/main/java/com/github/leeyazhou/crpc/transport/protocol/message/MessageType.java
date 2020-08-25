/**
 * 
 */
package com.github.leeyazhou.crpc.transport.protocol.message;

/**
 * @author leeyazhou
 *
 */
public enum MessageType {

  REQUEST((byte) 0),

  RESPONSE((byte) 1);

  private byte code;

  private MessageType(byte code) {
    this.code = code;
  }

  public byte getCode() {
    return code;
  }

}

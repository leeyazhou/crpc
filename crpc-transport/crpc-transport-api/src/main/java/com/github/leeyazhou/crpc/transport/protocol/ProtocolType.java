/**
 * 
 */
package com.github.leeyazhou.crpc.transport.protocol;

/**
 * @author leeyazhou
 *
 */
public enum ProtocolType {

  CRPC((byte) 1, "crpc");

  private byte code;
  private String name;

  /**
   * 
   */
  private ProtocolType(byte code, String name) {
    this.code = code;
    this.name = name;
  }

  /**
   * @return the code
   */
  public byte getCode() {
    return code;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
}

/**
 * 
 */
package com.github.leeyazhou.crpc.protocol.codec;

/**
 * 
 * codec
 * 
 * @author leeyazhou
 *
 */
public interface Codec {
  /**
   * Encode Object to byte[]
   * 
   * @param object object
   * @return byte of object
   * @throws Exception any exception
   */
  byte[] encode(Object object) throws Exception;

  /**
   * decode byte[] to Object
   * 
   * @param className className
   * @param bytes byte
   * @return Object
   * @throws Exception exception
   */
  Object decode(String className, byte[] bytes) throws Exception;

}

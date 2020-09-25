/**
 * 
 */
package com.github.leeyazhou.crpc.codec;

/**
 * @author leeyazhou
 *
 */
public abstract class AbstractCodec implements Codec {

  @Override
  public byte[] encode(Object object) throws CodecException {
    try {
      return doEncode(object);
    } catch (CodecException e) {
      throw e;
    } catch (Exception e) {
      throw new CodecException(e);
    }
  }

  @Override
  public Object decode(String className, byte[] bytes) throws CodecException {
    try {
      return doDecode(className, bytes);
    } catch (CodecException e) {
      throw e;
    } catch (Exception e) {
      throw new CodecException(e);
    }
  }

  protected abstract byte[] doEncode(Object object);

  protected abstract Object doDecode(String className, byte[] bytes);

}

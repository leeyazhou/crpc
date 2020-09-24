/**
 * 
 */
package com.github.leeyazhou.crpc.rpc;

/**
 * @author leeyazhou
 *
 */
public class Result {

  private Object value;
  private Exception exception;

  /**
   * @return the value
   */
  public Object getValue() {
    return value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(Object value) {
    this.value = value;
  }

  /**
   * @return the exception
   */
  public Exception getException() {
    return exception;
  }

  /**
   * @param exception the exception to set
   */
  public void setException(Exception exception) {
    this.exception = exception;
  }


}

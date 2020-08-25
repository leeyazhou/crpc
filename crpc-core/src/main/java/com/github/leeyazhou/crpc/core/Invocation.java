/**
 * 
 */
package com.github.leeyazhou.crpc.core;

import java.lang.reflect.Method;

/**
 * @author leeyazhou
 *
 */
public class Invocation {

  private Method method;
  private Object[] args;

  /**
   * @param method the method to set
   */
  public Invocation setMethod(Method method) {
    this.method = method;
    return this;
  }

  /**
   * @param args the args to set
   */
  public Invocation setArgs(Object[] args) {
    this.args = args;
    return this;
  }

  /**
   * @return the method
   */
  public Method getMethod() {
    return method;
  }

  /**
   * @return the args
   */
  public Object[] getArgs() {
    return args;
  }
}

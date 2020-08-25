/**
 * 
 */
package com.github.leeyazhou.crpc.transport.protocol;

import java.io.Serializable;

/**
 * @author leeyazhou
 *
 */
public class InvocationRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  private String serviceTypeName;

  private String methodName;

  private String[] argTypes;

  private Object[] args;

  private int timeout;

  public String getServiceTypeName() {
    return serviceTypeName;
  }

  public InvocationRequest setServiceTypeName(String serviceTypeName) {
    this.serviceTypeName = serviceTypeName;
    return this;
  }

  public String getMethodName() {
    return methodName;
  }

  public InvocationRequest setMethodName(String methodName) {
    this.methodName = methodName;
    return this;
  }

  public String[] getArgTypes() {
    return argTypes;
  }

  public InvocationRequest setArgTypes(String[] argTypes) {
    this.argTypes = argTypes;
    return this;
  }

  public Object[] getArgs() {
    return args;
  }

  public InvocationRequest setArgs(Object[] args) {
    this.args = args;
    return this;
  }

  public int getTimeout() {
    return timeout;
  }

  public InvocationRequest setTimeout(int timeout) {
    this.timeout = timeout;
    return this;
  }
}

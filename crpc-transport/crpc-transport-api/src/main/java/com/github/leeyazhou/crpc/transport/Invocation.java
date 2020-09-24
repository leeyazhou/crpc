/**
 * 
 */
package com.github.leeyazhou.crpc.transport;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leeyazhou
 *
 */
public class Invocation {

  private Map<String, Object> attachements;
  private String methodName;
  private String serviceTypeName;

  private String[] argTypes;

  private Object[] args;

  public Map<String, Object> getAttachements() {
    return attachements;
  }

  public Invocation setAttachements(Map<String, Object> attachements) {
    this.attachements = attachements;
    return this;
  }

  public String getMethodName() {
    return methodName;
  }

  public Invocation setMethodName(String methodName) {
    this.methodName = methodName;
    return this;
  }

  public String getServiceTypeName() {
    return serviceTypeName;
  }

  public Invocation setServiceTypeName(String serviceTypeName) {
    this.serviceTypeName = serviceTypeName;
    return this;
  }

  public String[] getArgTypes() {
    return argTypes;
  }

  public Invocation setArgTypes(String[] argTypes) {
    this.argTypes = argTypes;
    return this;
  }

  public Object[] getArgs() {
    return args;
  }

  public Invocation setArgs(Object[] args) {
    this.args = args;
    return this;
  }

  public Invocation addAttachement(String name, Object value) {
    if (this.attachements != null) {
      this.attachements = new HashMap<String, Object>();
    }
    attachements.put(name, value);
    return this;
  }
}

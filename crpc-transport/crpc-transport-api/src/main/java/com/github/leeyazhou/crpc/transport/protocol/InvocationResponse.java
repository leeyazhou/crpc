/**
 * 
 */
package com.github.leeyazhou.crpc.transport.protocol;

import java.io.Serializable;

/**
 * @author leeyazhou
 *
 */
public class InvocationResponse implements Serializable {
  private static final long serialVersionUID = 1L;

  private Object response;

  private boolean error;

  private String responseClassName;

  public Object getResponse() {
    return response;
  }

  public InvocationResponse setResponse(Object response) {
    this.response = response;
    return this;
  }

  public boolean isError() {
    return error;
  }

  public InvocationResponse setError(boolean error) {
    this.error = error;
    return this;
  }

  public String getResponseClassName() {
    return responseClassName;
  }

  public InvocationResponse setResponseClassName(String responseClassName) {
    this.responseClassName = responseClassName;
    return this;
  }
}

/**
 * 
 */
package com.github.leeyazhou.crpc.core.concurrent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leeyazhou
 *
 */
public class Future {
  private boolean success;
  private Throwable exception;
  private List<FutureListener> listeners;

  public void addListener(FutureListener listener) {
    if (listeners == null) {
      this.listeners = new ArrayList<FutureListener>();
    }
    this.listeners.add(listener);
  }

  public void notifyListener() {
    if (this.listeners == null) {
      return;
    }
    for (FutureListener listener : listeners) {
      listener.onComplete(this);
    }
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }


  public void setException(Throwable exception) {
    this.exception = exception;
    this.success = false;
  }

  public Throwable getException() {
    return exception;
  }


}

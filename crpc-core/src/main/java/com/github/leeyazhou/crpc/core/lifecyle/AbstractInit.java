/**
 * 
 */
package com.github.leeyazhou.crpc.core.lifecyle;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author leeyazhou
 *
 */
public abstract class AbstractInit implements Init {
  private final AtomicBoolean init = new AtomicBoolean();

  @Override
  public void init() {
    if (init.compareAndSet(false, true)) {
      doInit();
    }
  }

  public abstract void doInit();

}

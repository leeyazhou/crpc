/**
 * 
 */
package com.github.leeyazhou.crpc.core.concurrent;

import java.util.EventListener;

/**
 * @author leeyazhou
 *
 */
public interface FutureListener extends EventListener {

  void onComplete(Future future);
}

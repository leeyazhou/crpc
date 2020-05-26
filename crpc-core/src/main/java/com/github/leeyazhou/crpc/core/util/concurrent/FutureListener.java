/**
 * 
 */
package com.github.leeyazhou.crpc.core.util.concurrent;

import java.util.EventListener;

/**
 * @author leeyazhou
 *
 */
public interface FutureListener extends EventListener {

  void onComplete(Future future);
}

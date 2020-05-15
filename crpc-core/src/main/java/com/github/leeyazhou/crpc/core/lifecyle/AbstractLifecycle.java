/**
 * Copyright © 2016~2020 CRPC (coderhook@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.leeyazhou.crpc.core.lifecyle;

import java.util.concurrent.atomic.AtomicBoolean;

import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

/**
 * @author leeyazhou
 * 
 */
public abstract class AbstractLifecycle implements Lifecycle {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  private final AtomicBoolean running = new AtomicBoolean();
  private final AtomicBoolean init = new AtomicBoolean();

  @Override
  public void init() {
    if (init.compareAndSet(false, true)) {
      if (logger.isDebugEnabled()) {
        logger.debug("Init " + this);
      }
      doInit();
    }
  }

  @Override
  public boolean isRunning() {
    return running.get();
  }

  @Override
  public void start() {
    if (init.get() == false) {
      init();
    }
    if (running.compareAndSet(false, true)) {
      if (logger.isDebugEnabled()) {
        logger.debug("Start " + this);
      }
      doStart();
    }
  }

  @Override
  public void stop() {
    if (running.compareAndSet(true, false)) {
      if (logger.isDebugEnabled()) {
        logger.debug("Stop " + this);
      }
      doStop();
    }
  }

  /**
   * do init
   */
  protected abstract void doInit();

  /**
   * do start thing.
   */
  protected abstract void doStart();

  /**
   * do stop thing.
   */
  protected abstract void doStop();
}

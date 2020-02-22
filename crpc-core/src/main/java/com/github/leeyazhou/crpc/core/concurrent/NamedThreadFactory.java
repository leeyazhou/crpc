/**
 * Copyright © 2019 leeyazhou (coderhook@gmail.com)
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
package com.github.leeyazhou.crpc.core.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
  static final AtomicInteger poolNumber = new AtomicInteger(1);

  final AtomicInteger threadNumber = new AtomicInteger(1);
  final ThreadGroup group;
  final String namePrefix;
  final boolean daemon;

  public NamedThreadFactory() {
    this("crpc");
  }

  public NamedThreadFactory(String name) {
    this(name, false);
  }

  public NamedThreadFactory(String preffix, boolean daemon) {
    SecurityManager securityManager = System.getSecurityManager();
    this.group = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
    this.namePrefix = preffix + "-" + poolNumber.getAndIncrement() + "-";
    this.daemon = daemon;
  }

  @Override
  public Thread newThread(Runnable runnable) {
    Thread thread = new Thread(group, runnable, getNewThreadName(), 0);
    thread.setDaemon(daemon);
    if (thread.getPriority() != Thread.NORM_PRIORITY) {
      thread.setPriority(Thread.NORM_PRIORITY);
    }
    return thread;
  }

  public String getNewThreadName() {
    return namePrefix + threadNumber.getAndIncrement();
  }
}

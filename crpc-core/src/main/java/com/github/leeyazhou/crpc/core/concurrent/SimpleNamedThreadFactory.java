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
package com.github.leeyazhou.crpc.core.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

public class SimpleNamedThreadFactory extends NamedThreadFactory {

  final AtomicInteger threadNumber = new AtomicInteger(1);
  final ThreadGroup group;
  final String namePrefix;
  final boolean daemon;

  public SimpleNamedThreadFactory() {
    this("crpc");
  }

  public SimpleNamedThreadFactory(String name) {
    this(name, false);
  }

  public SimpleNamedThreadFactory(String preffix, boolean daemon) {
    SecurityManager securityManager = System.getSecurityManager();
    this.group = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
    this.namePrefix = preffix + "-";
    poolNumber.incrementAndGet();
    this.daemon = daemon;
  }

  @Override
  public String getNewThreadName() {
    return namePrefix + threadNumber.getAndIncrement();
  }
}

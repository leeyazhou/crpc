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
/**
 * 
 */

package com.github.leeyazhou.crpc.filter;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.github.leeyazhou.crpc.core.util.LRUCache;
import com.github.leeyazhou.crpc.rpc.Handler;
import com.github.leeyazhou.crpc.rpc.Invocation;
import com.github.leeyazhou.crpc.rpc.Result;

/**
 * @author leeyazhou
 */
public class CounterFilter extends AbstractFilter {
  private static final LRUCache<String, AtomicInteger> counter = new LRUCache<String, AtomicInteger>(2048);
  private static final BlockingQueue<Invocation> QUEUE = new ArrayBlockingQueue<Invocation>(1024);

  public CounterFilter() {
    new Thread() {
      @Override
      public void run() {
        try {
          handleRequestCounter();
        } catch (Exception err) {
          logger.error("", err);
        }
      }
    }.start();

  }

  private void handleRequestCounter() throws Exception {
    Invocation request = null;
    long lastTime = 0;
    while (true) {

      request = QUEUE.poll(5, TimeUnit.SECONDS);
      if (request != null) {
        logger.info("处理请求:" + request);
        final String mName = request.getServiceTypeName() + "." + request.getMethodName();
        AtomicInteger c = counter.get(mName);
        if (c == null) {
          c = new AtomicInteger(0);
          counter.put(mName, c);
        }
        c.incrementAndGet();
      }

      if (System.currentTimeMillis() - lastTime >= TimeUnit.SECONDS.toMillis(30)) {
        printCounter();
        lastTime = System.currentTimeMillis();
      }
    }
  }

  private void printCounter() {
    for (Map.Entry<String, AtomicInteger> entry : counter.entrySet()) {
      logger.info("统计调用量 --> " + entry.getKey() + " : " + entry.getValue().get());
    }
  }

  @Override
  public Result doFilter(Handler<?> handler, Invocation context) {
    QUEUE.offer(context);
    return handler.handle(context);
  }

}

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

package com.github.leeyazhou.crpc.monitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.rpc.Invocation;

/**
 * @author leeyazhou
 */
public class SimpleMonitor extends AbstractMonitor {
  private static final Logger logger = LoggerFactory.getLogger(SimpleMonitor.class);
  private BlockingQueue<Invocation> queue;
  private Thread thread;
  private volatile AtomicBoolean running = new AtomicBoolean(false);
  // TODO 此处只有PUT，注意内存使用
  public static ConcurrentMap<String, MonitorData> statis = new ConcurrentHashMap<String, MonitorData>();

  public SimpleMonitor() {
    init();
  }

  private void init() {
    if (!running.compareAndSet(false, true)) {
      return;
    }
    queue = new LinkedBlockingDeque<Invocation>(10240);
    thread = new Thread(new Runnable() {
      public void run() {
        try {
          while (true) {
            doCollect();
          }
        } catch (InterruptedException err) {
          logger.error("collect log err", err);
        }
      }
    }, "SimpleMonitor");

    thread.setDaemon(true);
    thread.start();
    Timer timer = new Timer("statis", true);
    timer.scheduleAtFixedRate(new TimerTask() {

      @Override
      public void run() {
        Iterator<String> it = statis.keySet().iterator();
        while (it.hasNext()) {
          String key = it.next();
          MonitorData monitorData = statis.get(key);
          logger.info("监控数据：" + monitorData.toString());
        }
      }
    }, 1000, 6000);

  }

  private final Random random = new Random();

  private void doCollect() throws InterruptedException {
    Invocation request = null;
    do {
      request = queue.poll(random.nextInt(10), TimeUnit.SECONDS);
      if (request == null) {
        Thread.sleep(random.nextInt(1000));
      }
    } while (request == null);
    logger.info("request : " + request);
    MonitorData serviceService = statis.get(request.getServiceTypeName());
    if (serviceService == null) {
      serviceService = new MonitorData(request.getServiceTypeName());
      statis.put(request.getServiceTypeName(), serviceService);
    } else {
      serviceService.collect(request.getMethodName());
    }

  }

  @Override
  public void collect(Invocation context) {
    queue.offer(context);
  }

  private static final class MonitorData {
    private String targetClassName;

    private Map<String, AtomicInteger> methodMap = new HashMap<String, AtomicInteger>();

    public MonitorData(String targetClassName) {
      this.targetClassName = targetClassName;
    }

    void collect(String methodName) {
      AtomicInteger count = methodMap.get(methodName);
      if (count == null) {
        count = new AtomicInteger();
        methodMap.putIfAbsent(methodName, count);
      }
      count.incrementAndGet();
    }

    @Override
    public String toString() {
      return "MonitorData [targetClassName=" + targetClassName + ", methodMap=" + methodMap + "]";
    }

  }
}

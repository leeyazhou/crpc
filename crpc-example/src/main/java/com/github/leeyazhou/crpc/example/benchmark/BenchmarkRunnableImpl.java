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
package com.github.leeyazhou.crpc.example.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.service.UserService;

public class BenchmarkRunnableImpl implements BenchmarkRunnable {
  private static final Logger logger = LoggerFactory.getLogger(BenchmarkRunnableImpl.class);
  private CountDownLatch countDownLatch;
  private UserService userService;
  private boolean running = true;
  private long endTime;
  private long acceptRequest;
  private long errorRequest;

  private long[] responseSpreads = new long[9];

  public BenchmarkRunnableImpl(CountDownLatch countDownLatch, UserService userService, long endTime) {
    this.userService = userService;
    this.countDownLatch = countDownLatch;
    this.endTime = endTime;
  }

  @Override
  public void run() {
    while (running) {
      long start = System.currentTimeMillis();
      if (start > endTime) {
        running = false;
        break;
      }
      try {
        boolean flag = userService.sayWord("--");
        if (logger.isInfoEnabled()) {
          logger.info("sayWord result : " + flag);
        }
      } catch (Exception e) {
        e.printStackTrace();
        errorRequest++;
      }
      long costTime = System.currentTimeMillis() - start;
      sumResponseTimeSpread(costTime);
    }
    countDownLatch.countDown();
  }

  private void sumResponseTimeSpread(long responseTime) {
    acceptRequest++;
    if (responseTime <= 0) {
      responseSpreads[0] = responseSpreads[0] + 1;
    } else if (responseTime > 0 && responseTime <= 1) {
      responseSpreads[1] = responseSpreads[1] + 1;
    } else if (responseTime > 1 && responseTime <= 5) {
      responseSpreads[2] = responseSpreads[2] + 1;
    } else if (responseTime > 5 && responseTime <= 10) {
      responseSpreads[3] = responseSpreads[3] + 1;
    } else if (responseTime > 10 && responseTime <= 50) {
      responseSpreads[4] = responseSpreads[4] + 1;
    } else if (responseTime > 50 && responseTime <= 100) {
      responseSpreads[5] = responseSpreads[5] + 1;
    } else if (responseTime > 100 && responseTime <= 500) {
      responseSpreads[6] = responseSpreads[6] + 1;
    } else if (responseTime > 500 && responseTime <= 1000) {
      responseSpreads[7] = responseSpreads[7] + 1;
    } else if (responseTime > 1000) {
      responseSpreads[8] = responseSpreads[8] + 1;
    }
  }

  @Override
  public List<long[]> getResult() {
    List<long[]> result = new ArrayList<long[]>(6);
    result.add(responseSpreads);
    result.add(new long[] {acceptRequest});
    result.add(new long[] {errorRequest});
    return result;
  }
}

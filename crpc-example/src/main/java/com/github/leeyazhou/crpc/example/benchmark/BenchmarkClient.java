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

package com.github.leeyazhou.crpc.example.benchmark;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import com.github.leeyazhou.crpc.config.ApplicationConfig;
import com.github.leeyazhou.crpc.config.ConsumerConfig;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.example.service.UserService;

/**
 * @author leeyazhou
 */
public class BenchmarkClient extends AbstractBenchmarkClient {

  private static int threadNum = 32;

  public static void main(String[] args) throws InterruptedException {
    runtime = 60;
    if (args != null && args.length > 0) {
      runtime = Long.parseLong(args[0]);
      if (args.length > 1) {
        threadNum = Integer.parseInt(args[1]);
      }
    }

    CountDownLatch countDownlatch = new CountDownLatch(threadNum);

    ApplicationConfig applicationConfig = new ApplicationConfig().setName("showcase");
    ConsumerConfig<UserService> consumerConfig = new ConsumerConfig<UserService>().setApplicationConfig(applicationConfig).addURL(URL.valueOf("tcp://127.0.0.1:25001"))
        .setServiceType(UserService.class);
    UserService userService = consumerConfig.refer();
    check(userService);
    long endTime = System.currentTimeMillis() + runtime * 1000;
    System.out.println("ready to start client benchmark, benchmark will end at:"
        + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(endTime)));
    benchmarkRunnables = new ArrayList<BenchmarkRunnable>(threadNum);

    for (int i = 0; i < threadNum; i++) {
      BenchmarkRunnable benchmarkRunnable = new BenchmarkRunnableImpl(countDownlatch, userService, endTime);
      new Thread(benchmarkRunnable, "benchmark-" + i).start();
      benchmarkRunnables.add(benchmarkRunnable);
    }

    countDownlatch.await();
    show();
    System.exit(0);
  }


  private static void check(UserService userService) {
    int i = 0;
    while (i++ < 100) {
      userService.sayName("CRPC");
    }
  }

}

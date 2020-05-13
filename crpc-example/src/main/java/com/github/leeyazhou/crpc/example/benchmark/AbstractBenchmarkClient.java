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
import java.util.Date;
import java.util.List;

/**
 * @author leeyazhou
 */
public class AbstractBenchmarkClient {
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  protected static long runtime;

  private static long allRequestSum;

  private static long allErrorRequestSum;

  // < 0
  private static long below0sum;

  // (0,1]
  private static long above0sum;

  // (1,5]
  private static long above1sum;

  // (5,10]
  private static long above5sum;

  // (10,50]
  private static long above10sum;

  // (50,100]
  private static long above50sum;

  // (100,500]
  private static long above100sum;

  // (500,1000]
  private static long above500sum;

  // > 1000
  private static long above1000sum;

  protected static List<BenchmarkRunnable> benchmarkRunnables;

  protected static void show() {
    for (BenchmarkRunnable benchmarkRunnable : benchmarkRunnables) {
      long[] responseSpreads = benchmarkRunnable.getResult().get(0);
      below0sum += responseSpreads[0];
      above0sum += responseSpreads[1];
      above1sum += responseSpreads[2];
      above5sum += responseSpreads[3];
      above10sum += responseSpreads[4];
      above50sum += responseSpreads[5];
      above100sum += responseSpreads[6];
      above500sum += responseSpreads[7];
      above1000sum += responseSpreads[8];

      allRequestSum += benchmarkRunnable.getResult().get(1)[0];
      allErrorRequestSum += benchmarkRunnable.getResult().get(2)[0];
    }

    System.out.println(" RT <= 0 : " + below0sum * 100 / allRequestSum + "% " + below0sum + "/" + allRequestSum);
    System.out.println(" RT (0,1] : " + above0sum * 100 / allRequestSum + "% " + above0sum + "/" + allRequestSum);
    System.out.println(" RT (1,5] : " + above1sum * 100 / allRequestSum + "% " + above1sum + "/" + allRequestSum);
    System.out.println(" RT (5,10] : " + above5sum * 100 / allRequestSum + "% " + above5sum + "/" + allRequestSum);
    System.out.println(" RT (10,50] : " + above10sum * 100 / allRequestSum + "% " + above10sum + "/" + allRequestSum);
    System.out.println(" RT (50,100] : " + above50sum * 100 / allRequestSum + "% " + above50sum + "/" + allRequestSum);
    System.out
        .println(" RT (100,500] : " + above100sum * 100 / allRequestSum + "% " + above100sum + "/" + allRequestSum);
    System.out
        .println(" RT (500,1000] : " + above500sum * 100 / allRequestSum + "% " + above500sum + "/" + allRequestSum);
    System.out
        .println(" RT > 1000 : " + above1000sum * 100 / allRequestSum + "% " + above1000sum + "/" + allRequestSum);

    System.out.println("allRequestSum \t: " + allRequestSum);
    System.out.println("allErrorRequestSum : " + allErrorRequestSum);
    System.out.println("runtime(second) : " + runtime);
    System.out.println("Average/sec \t: " + allRequestSum / runtime);
    System.out.println("currentTime : " + dateFormat.format(new Date()));
  }
}

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

package com.github.leeyazhou.crpc.rpc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author lee
 *
 */
public class RpcResult {

  private final CountDownLatch countDownLatch;
  private Object result;

  public RpcResult() {
    countDownLatch = new CountDownLatch(1);
  }

  /**
   * @return the result
   * @throws InterruptedException ex
   */
  public Object getResult() throws InterruptedException {
    countDownLatch.await(3, TimeUnit.MILLISECONDS);
    return result;
  }

  /**
   * @param result the result to set
   */
  public void setResult(Object result) {
    this.result = result;
    countDownLatch.countDown();
  }



}

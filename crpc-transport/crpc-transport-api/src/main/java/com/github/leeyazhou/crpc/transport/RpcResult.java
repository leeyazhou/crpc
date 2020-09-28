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
package com.github.leeyazhou.crpc.transport;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import com.github.leeyazhou.crpc.core.exception.TimeoutException;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

/**
 * 
 * @author leeyazhou
 *
 */
public class RpcResult {
  private final CountDownLatch countDownLatch;
  private ResponseMessage response;
  private long startTime;
  private int id;

  public RpcResult() {
    this.countDownLatch = new CountDownLatch(1);
  }

  public ResponseMessage getResponse(long timeout) {
    return getResponse(timeout, TimeUnit.MILLISECONDS);
  }

  public ResponseMessage getResponse(long timeout, TimeUnit unit) {
    try {
      countDownLatch.await(timeout, unit);
    } catch (InterruptedException e) {
      removeCache();
      throw new TimeoutException(e);
    }
    return response;
  }


  public RpcResult setResponse(ResponseMessage response) {
    this.response = response;
    countDownLatch.countDown();
    return this;
  }


  public RpcResult setStartTime(long startTime) {
    this.startTime = startTime;
    return this;
  }

  public long getStartTime() {
    return startTime;
  }

  /**
   * 开始计时
   * 
   * @return {@link RpcResult}
   */
  public RpcResult start() {
    this.startTime = System.currentTimeMillis();
    return this;
  }

  public RpcResult setId(int id) {
    this.id = id;
    return this;
  }

  public RpcResult removeCache() {
    AbstractClient.responses.remove(id);
    return this;
  }
}

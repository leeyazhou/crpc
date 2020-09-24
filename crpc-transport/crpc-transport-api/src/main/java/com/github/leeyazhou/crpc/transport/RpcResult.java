package com.github.leeyazhou.crpc.transport;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import com.github.leeyazhou.crpc.transport.protocol.message.ResponseMessage;

public class RpcResult {
    private final CountDownLatch countDownLatch;
    private ResponseMessage response;

    public RpcResult() {
      countDownLatch = new CountDownLatch(1);
    }

    public ResponseMessage getResponse(long timeout, TimeUnit unit) throws InterruptedException {
      countDownLatch.await(timeout, unit);
      return response;
    }

    public void setResponse(ResponseMessage response) {
      this.response = response;
      countDownLatch.countDown();
    }

  }
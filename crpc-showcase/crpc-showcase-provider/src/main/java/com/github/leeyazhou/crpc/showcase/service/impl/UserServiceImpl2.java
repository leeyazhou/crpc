/**
 * Copyright © 2016~2020 leeyazhou (coderhook@gmail.com)
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

package com.github.leeyazhou.crpc.showcase.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.leeyazhou.crpc.core.annotation.CRPCService;
import com.github.leeyazhou.crpc.showcase.model.User;
import com.github.leeyazhou.crpc.showcase.service.UserService2;

/**
 * 
 * @author lee
 */
@CRPCService
public class UserServiceImpl2 implements UserService2 {

  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl2.class);

  private boolean isInfoEnabled = logger.isInfoEnabled();
  private static final AtomicInteger count = new AtomicInteger(0);

  private static final Timer timer = new Timer("request");
  static {
    timer.scheduleAtFixedRate(new TimerTask() {

      @Override
      public void run() {
        logger.error("请求数：" + count);
        count.set(0);
      }
    }, 0, 60000);
  }

  @Override
  public boolean say(User user) {
    count.getAndAdd(1);
    if (isInfoEnabled) {
      logger.info(user + ", outer");
    }
    return true;
  }

  @Override
  public boolean say(User user, String mark) {
    if (isInfoEnabled) {
      logger.info(user + ", 这是重载方法");
    }
    return false;
  }

  @Override
  public boolean sayWord(String name) {
    if (logger.isInfoEnabled()) {
      logger.info("my name is {} ", name);
    }
    return true;
  }

  @Override
  public boolean say(int age) {
    return age % 2 > 0;
  }

  @Override
  public List<User> getUser(int id) {
    List<User> result = new ArrayList<User>();
    result.add(new User());
    result.add(new User());
    return result;
  }

  @Override
  public void doNothing(User user) {
    logger.info("doNothing : " + user);
  }

  @Override
  public Map<String, List<User>> complexObject(Map<String, List<User>> users) {
    logger.info("执行方法complexObject");
    return users;
  }

  @Override
  public byte[] bigData(byte[] data) {
    logger.info("收到数据包长度:" + data.length);
    return data;
  }

}

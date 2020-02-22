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
package com.github.leeyazhou.crpc.showcase.consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.leeyazhou.crpc.showcase.service.UserServiceConsumer;

/**
 * @author <a href="mailto:lee_yazhou@163.com">Yazhou Li</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring-*.xml")
public class ConsumerSpring {


  @Autowired
  UserServiceConsumer userServiceConsumer;

  @Test
  public void test() throws Exception {
    long start =System.currentTimeMillis();
    int i = 0;
    while (i++ < 1000) {
      userServiceConsumer.foo();
    }
    System.out.println("耗时:" +(System.currentTimeMillis()-start));
    Thread.sleep(5000);
  }
}

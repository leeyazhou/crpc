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

package com.github.leeyazhou.crpc.showcase.service;

import java.util.List;
import java.util.Map;

import com.github.leeyazhou.crpc.showcase.model.User;

/**
 * @author lee
 *
 */
public interface UserService2 {

  public boolean say(User user);

  public boolean sayWord(String name);

  public boolean say(User user, String mark);

  public boolean say(int age);

  public List<User> getUser(int id);

  public void doNothing(User user);

  /**
   * 测试复杂对象
   * 
   * @param users ss
   * @return ss
   */
  public Map<String, List<User>> complexObject(Map<String, List<User>> users);

  public byte[] bigData(byte[] data);

}

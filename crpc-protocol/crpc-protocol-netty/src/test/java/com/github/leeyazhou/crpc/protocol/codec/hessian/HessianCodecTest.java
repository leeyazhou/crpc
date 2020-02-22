/**
 * Copyright © 2019 leeyazhou (coderhook@gmail.com)
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
package com.github.leeyazhou.crpc.protocol.codec.hessian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.leeyazhou.crpc.protocol.model.User;
import org.junit.Test;

import com.github.leeyazhou.crpc.protocol.codec.Decoder;
import com.github.leeyazhou.crpc.protocol.codec.Encoder;

/**
 * @author lee
 *
 */
public class HessianCodecTest {

	  private Encoder encoder = new HessianEncoder();
	  private Decoder decoder = new HessianDecoder();

	  @Test
	  public void testEncoder() throws Exception {
	    User user = new User(100, 100, "查博士技术部", "admin@github.cn");
	    doEncoderAndDecoder(user, User.class);

	    List<String> args = new ArrayList<String>();
	    args.add("demo1");
	    args.add("demo2");

	    doEncoderAndDecoder(args, ArrayList.class);
	    doEncoderAndDecoder(null, String.class);
	    doEncoderAndDecoder(0, int.class);
	  }

	  public void doEncoderAndDecoder(Object obj, Class<?> clazz) throws Exception {
	    System.out.println("\n需要编码数据:" + obj);
	    byte[] objByte = encoder.encode(obj);
	    System.out.println("编码结果:" + objByte);
	    Object obj2 = decoder.decode(clazz.getName(), objByte);
	    System.out.println("解码结果:" + obj2);
	  }

	  @SuppressWarnings("unchecked")
	  @Test
	  public void testComplexObject() throws Exception {
	    Map<String, List<User>> complexObj = new HashMap<String, List<User>>();
	    User user = new User(26, 27, "查博士", "admin@github.cn");
	    User user2 = new User(25, 2, "查博士", "admin@github.cn");
	    List<User> users = new ArrayList<User>();
	    users.add(user);
	    users.add(user2);
	    complexObj.put("users", users);

	    System.out.println("编码:" + complexObj);
	    byte[] encodeByte = encoder.encode(complexObj);
	    Map<String, List<User>> result = (Map<String, List<User>>) decoder.decode(Map.class.getName(), encodeByte);
	    System.out.println("解码:" + result);
	  }


}

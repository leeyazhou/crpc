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

package com.github.leeyazhou.crpc.core.scanner;

import java.util.Set;

import org.junit.Test;

import com.github.leeyazhou.crpc.core.scanner.ClassScanner;
import com.github.leeyazhou.crpc.core.scanner.DefaultClassScanner;

/**
 * @author lee
 *
 */
public class DefaultClassScannerTest {

  @Test
  public void testGetClassListStringString() {
    ClassScanner classScanner = DefaultClassScanner.getInstance();
    Set<Class<?>> classes = classScanner.getClassList("org.junit", null);
    for (Class<?> item : classes) {
      System.out.println(item);
    }
  }

}
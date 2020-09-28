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
package com.github.leeyazhou.crpc.bootstrap;

import java.lang.reflect.Method;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.core.util.StringUtil;

/**
 * @author leeyazhou
 *
 */
public class Main {

  public static final String BOOTSTRAP_TYPE = "crpc.bootstrap";
  public static final String BOOTSTRAP_VALUE_CRPC = "crpc";
  public static final String BOOTSTRAP_VALUE_SPRING = "spring";

  public static void main(String[] args) {
    new Main().start();
  }

  private void start() {
    try {
      doStartup();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void doStartup() throws Exception {
    BannerPrint.printBanner();
    Class<?> bootstrapType = getBootstrapType();
    Object bootstrap = bootstrapType.newInstance();
    Method startup = bootstrapType.getDeclaredMethod("startup");
    startup.invoke(bootstrap);
  }


  public Class<?> getBootstrapType() {
    String bootstrapStr = System.getProperty(BOOTSTRAP_TYPE, null);
    Class<?> bootstrapType = null;
    if (StringUtil.isNotBlank(bootstrapStr)) {
      bootstrapType = ServiceLoader.load(Bootstrap.class).loadType(bootstrapStr);
    } else {
      bootstrapType = ServiceLoader.load(Bootstrap.class).loadType();
    }
    return bootstrapType;
  }
}

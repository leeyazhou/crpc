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

import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.annotation.SPI;
import com.github.leeyazhou.crpc.core.lifecyle.AbstractLifecycle;
import com.github.leeyazhou.crpc.core.lifecyle.Lifecycle;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

/**
 * @author leeyazhou
 */
@SPI("spring")
public abstract class Bootstrap extends AbstractLifecycle implements Lifecycle {
  private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

  @Override
  protected void doInit() {
    printVersion();
  }

  protected void printVersion() {
    logger.info("os.name : " + System.getProperty("os.name"));
    logger.info("os.version : " + System.getProperty("os.version"));
    logger.info("os.arch : " + System.getProperty("os.arch"));
    logger.info("java.home : " + System.getProperty("java.home"));
    logger.info("java.io.tmpdir : " + System.getProperty("java.io.tmpdir"));
    logger.info("vm.version : " + System.getProperty("java.runtime.version"));
    logger.info("vm.vendor : " + System.getProperty("java.vm.vendor"));
    logger.info("crpc.home : " + System.getProperty("crpc.home"));
    logger.info("crpc.version : " + Constants.CRPC_VERSION);
    logger.info("crpc.encoding : " + Constants.ENCODING);
  }

  @Override
  protected void doShutdown() {}

  @Override
  public void startup() {
    final long start = System.currentTimeMillis();
    try {
      doStartup();
    } catch (Throwable e) {
      logger.error("", e);
    } finally {
      logger.info("server start in " + (System.currentTimeMillis() - start) + " ms");
    }
  }

  /**
   * 启动服务
   */
  public abstract void doStartup();


}

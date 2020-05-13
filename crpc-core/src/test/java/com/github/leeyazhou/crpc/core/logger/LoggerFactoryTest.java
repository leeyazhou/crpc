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
package com.github.leeyazhou.crpc.core.logger;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.github.leeyazhou.crpc.core.logger.commons.JakartaCommonsLoggingImpl;
import com.github.leeyazhou.crpc.core.logger.log4j.Log4jImpl;
import com.github.leeyazhou.crpc.core.logger.log4j2.Log4j2Impl;
import com.github.leeyazhou.crpc.core.logger.slf4j.Slf4jImpl;
import com.github.leeyazhou.crpc.core.logger.stdout.StdOutImpl;

public class LoggerFactoryTest {

  @Test
  public void shouldUseCommonsLogging() {
    LoggerFactory.useCommonsLogging();
    Logger log = LoggerFactory.getLogger(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), JakartaCommonsLoggingImpl.class.getName());
  }

  @Test
  public void shouldUseLog4J() {
    LoggerFactory.useLog4JLogging();
    Logger log = LoggerFactory.getLogger(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), Log4jImpl.class.getName());
  }

  @Test
  public void shouldUseLog4J2() {
    LoggerFactory.useLog4J2Logging();
    Logger log = LoggerFactory.getLogger(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), Log4j2Impl.class.getName());
  }

  @Test
  public void shouldUseSlf4j() {
    LoggerFactory.useSlf4jLogging();
    Logger log = LoggerFactory.getLogger(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), Slf4jImpl.class.getName());
  }

  @Test
  public void shouldUseStdOut() {
    LoggerFactory.useStdOutLogging();
    Logger log = LoggerFactory.getLogger(Object.class);
    logSomething(log);
    assertEquals(log.getClass().getName(), StdOutImpl.class.getName());
  }

  private void logSomething(Logger log) {
    log.warn("Warning message.");
    log.debug("Debug message.");
    log.error("Error message.");
    log.error("Error with Exception.", new Exception("Test exception."));
  }

}

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
package com.github.leeyazhou.crpc.core.logger.slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.helpers.NOPLogger;
import org.slf4j.spi.LocationAwareLogger;

public class Slf4jImpl implements com.github.leeyazhou.crpc.core.logger.Logger {

  private com.github.leeyazhou.crpc.core.logger.Logger logger;

  public Slf4jImpl(String clazz) throws Exception {
    Logger logger = LoggerFactory.getLogger(clazz);
    if (logger instanceof NOPLogger) {
      throw new Exception("SLF4J: Failed to load class org.slf4j.impl.StaticLoggerBinder.");
    }
    if (logger instanceof LocationAwareLogger) {
      try {
        // check for slf4j >= 1.6 method signature
        logger.getClass().getMethod("log", Marker.class, String.class, int.class, String.class, Object[].class,
            Throwable.class);
        this.logger = new Slf4jLocationAwareLoggerImpl((LocationAwareLogger) logger);
        return;
      } catch (SecurityException err) {
        // fail-back to Slf4jLoggerImpl
      } catch (NoSuchMethodException err) {
        // fail-back to Slf4jLoggerImpl
      }
    }

    // Logger is not LocationAwareLogger or slf4j version < 1.6
    this.logger = new Slf4jLoggerImpl(logger);
  }

  /*
   * static { try { Class<?> clazz = Class.forName("org.apache.log4j.PropertyConfigurator"); URL log4jUrl =
   * Thread.currentThread().getContextClassLoader().getResource("log4j.properties"); if (clazz != null && null ==
   * log4jUrl) { log4jUrl = Thread.currentThread().getContextClassLoader().getResource("log4j.xml"); if (null ==
   * log4jUrl) { Properties properties = new Properties(); properties.put("log4j.rootLogger", "debug,crpc");
   * properties.put("log4j.appender.crpc", "org.apache.log4j.ConsoleAppender");
   * properties.put("log4j.appender.crpc.Target", "System.out"); properties.put("log4j.appender.crpc.layout",
   * "org.apache.log4j.PatternLayout"); properties.put("log4j.appender.crpc.layout.ConversionPattern",
   * "[%d{MM-dd HH:mm:ss.SSS} %-5p] [%t]%c{3}[%L] - %m%n"); Method method = clazz.getMethod("configure",
   * Properties.class); method.invoke(clazz, properties); } } } catch (Exception err) { // ignore th err } }
   */
  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  @Override
  public boolean isWarnEnabled() {
    return logger.isWarnEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  @Override
  public void error(String msg, Throwable err) {
    logger.error(msg, err);
  }

  @Override
  public void error(String msg) {
    logger.error(msg);
  }

  @Override
  public void debug(String msg) {
    logger.debug(msg);
  }

  @Override
  public void debug(String msg, Throwable throwable) {
    logger.debug(msg, throwable);
  }

  @Override
  public void warn(String msg) {
    logger.warn(msg);
  }


  @Override
  public void info(String msg) {
    logger.info(msg);
  }


  @Override
  public void warn(String msg, Throwable throwable) {
    logger.warn(msg, throwable);
  }

}

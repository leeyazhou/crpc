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
package com.github.leeyazhou.crpc.core.logger.log4j2;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

public class Log4j2AbstractLoggerImpl implements Logger {

  private static Marker MARKER = MarkerManager.getMarker(LoggerFactory.MARKER);

  private static final String FQCN = Log4j2Impl.class.getName();

  private ExtendedLoggerWrapper logger;

  public Log4j2AbstractLoggerImpl(AbstractLogger abstractLogger) {
    logger = new ExtendedLoggerWrapper(abstractLogger, abstractLogger.getName(), abstractLogger.getMessageFactory());
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public void error(String s, Throwable e) {
    logger.logIfEnabled(FQCN, Level.ERROR, MARKER, s, e);
  }

  @Override
  public void error(String s) {
    logger.logIfEnabled(FQCN, Level.ERROR, MARKER, s);
  }

  @Override
  public void debug(String s) {
    logger.logIfEnabled(FQCN, Level.DEBUG, MARKER, s);
  }

  @Override
  public void debug(String s, Throwable t) {
    logger.logIfEnabled(FQCN, Level.DEBUG, MARKER, s, t);
  }

  @Override
  public void warn(String s) {
    logger.logIfEnabled(FQCN, Level.WARN, MARKER, s);
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
  public void info(String msg) {
    logger.logIfEnabled(FQCN, Level.INFO, MARKER, msg);
  }


  @Override
  public void warn(String msg, Throwable throwable) {
    logger.logIfEnabled(FQCN, Level.WARN, MARKER, msg, throwable);
  }


}

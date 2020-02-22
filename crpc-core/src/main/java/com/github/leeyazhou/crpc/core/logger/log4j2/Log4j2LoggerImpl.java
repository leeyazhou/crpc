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
package com.github.leeyazhou.crpc.core.logger.log4j2;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

public class Log4j2LoggerImpl implements com.github.leeyazhou.crpc.core.logger.Logger {

  private static Marker MARKER = MarkerManager.getMarker(LoggerFactory.MARKER);

  private Logger logger;

  public Log4j2LoggerImpl(Logger logger) {
    this.logger = logger;
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }

  @Override
  public void error(String s, Throwable e) {
    logger.error(MARKER, s, e);
  }

  @Override
  public void error(String s) {
    logger.error(MARKER, s);
  }

  @Override
  public void debug(String s) {
    logger.debug(MARKER, s);
  }

  @Override
  public void debug(String msg, Throwable throwable) {
    logger.debug(MARKER,msg, throwable);
  }

  @Override
  public void trace(String s) {
    logger.trace(MARKER, s);
  }

  @Override
  public void warn(String s) {
    logger.warn(MARKER, s);
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
    logger.info(MARKER, msg);
  }


  @Override
  public void warn(String msg, Throwable throwable) {
    logger.warn(MARKER, msg, throwable);
  }


}

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
package com.github.leeyazhou.crpc.core.logger.slf4j;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.spi.LocationAwareLogger;

import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

class Slf4jLocationAwareLoggerImpl implements Logger {

  private static Marker MARKER = MarkerFactory.getMarker(LoggerFactory.MARKER);

  private static final String FQCN = Slf4jImpl.class.getName();

  private LocationAwareLogger logger;

  Slf4jLocationAwareLoggerImpl(LocationAwareLogger logger) {
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
  public void error(String msg, Throwable throwable) {
    logger.log(MARKER, FQCN, LocationAwareLogger.ERROR_INT, msg, null, throwable);
  }

  @Override
  public void error(String msg) {
    logger.log(MARKER, FQCN, LocationAwareLogger.ERROR_INT, msg, null, null);
  }

  @Override
  public void debug(String msg) {
    logger.log(MARKER, FQCN, LocationAwareLogger.DEBUG_INT, msg, null, null);
  }

  @Override
  public void debug(String msg, Throwable throwable) {
    logger.debug(msg, throwable);
  }

  @Override
  public void trace(String msg) {
    logger.log(MARKER, FQCN, LocationAwareLogger.TRACE_INT, msg, null, null);
  }

  @Override
  public void warn(String msg) {
    logger.log(MARKER, FQCN, LocationAwareLogger.WARN_INT, msg, null, null);
  }


  @Override
  public void info(String msg) {
    logger.log(MARKER, FQCN, LocationAwareLogger.INFO_INT, msg, null, null);
  }


  @Override
  public void warn(String msg, Throwable throwable) {
    logger.log(MARKER, FQCN, LocationAwareLogger.WARN_INT, msg, null, throwable);
  }
}

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
package com.github.leeyazhou.crpc.core.logger.slf4j;

import org.slf4j.Logger;

class Slf4jLoggerImpl implements com.github.leeyazhou.crpc.core.logger.Logger {

  private Logger logger;

  public Slf4jLoggerImpl(Logger logger) {
    this.logger = logger;
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
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }

  @Override
  public void error(String msg, Throwable throwable) {
    logger.error(msg, throwable);
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
  public void trace(String msg) {
    logger.trace(msg);
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

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
package com.github.leeyazhou.crpc.core.logger.commons;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.leeyazhou.crpc.core.logger.Logger;

public class JakartaCommonsLoggingImpl implements Logger {

  private Log logger;

  public JakartaCommonsLoggingImpl(String clazz) {
    logger = LogFactory.getLog(clazz);
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
    logger.error(s, e);
  }

  @Override
  public void error(String s) {
    logger.error(s);
  }

  @Override
  public void debug(String s) {
    logger.debug(s);
  }

  @Override
  public void debug(String msg, Throwable throwable) {
    logger.debug(msg, throwable);
  }

  @Override
  public void trace(String s) {
    logger.trace(s);
  }

  @Override
  public void warn(String s) {
    logger.warn(s);
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
    logger.info(msg);
  }


  @Override
  public void warn(String msg, Throwable throwable) {
    logger.warn(msg, throwable);
  }

}

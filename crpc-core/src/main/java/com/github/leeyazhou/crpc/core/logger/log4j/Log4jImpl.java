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
package com.github.leeyazhou.crpc.core.logger.log4j;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log4jImpl implements com.github.leeyazhou.crpc.core.logger.Logger {

  private static final String FQCN = Log4jImpl.class.getName();

  private Logger logger;

  public Log4jImpl(String clazz) {
    logger = Logger.getLogger(clazz);
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
    logger.log(FQCN, Level.ERROR, s, e);
  }

  @Override
  public void error(String s) {
    logger.log(FQCN, Level.ERROR, s, null);
  }

  @Override
  public void debug(String s) {
    logger.log(FQCN, Level.DEBUG, s, null);
  }

  @Override
  public void debug(String msg, Throwable throwable) {
    logger.log(FQCN, Level.DEBUG, msg, throwable);
  }

  @Override
  public void trace(String s) {
    logger.log(FQCN, Level.TRACE, s, null);
  }

  @Override
  public void warn(String s) {
    logger.log(FQCN, Level.WARN, s, null);
  }

  @Override
  public boolean isErrorEnabled() {
    return true;
  }

  @Override
  public boolean isWarnEnabled() {
    return true;
  }

  @Override
  public boolean isInfoEnabled() {
    return false;
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

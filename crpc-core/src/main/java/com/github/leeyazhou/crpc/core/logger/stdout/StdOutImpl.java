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
package com.github.leeyazhou.crpc.core.logger.stdout;

import com.github.leeyazhou.crpc.core.logger.Logger;

/**
 * @author lee
 */
public class StdOutImpl implements Logger {
  private static final String TRACE = "TRACE ";
  private static final String DEBUG = "DEBUG ";
  private static final String INFO = "INFO ";
  private static final String WARN = "WARN ";
  private static final String ERROR = "ERROR ";

  public StdOutImpl(String clazz) {
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
    return true;
  }

  @Override
  public boolean isDebugEnabled() {
    return true;
  }

  @Override
  public boolean isTraceEnabled() {
    return true;
  }

  @Override
  public void error(String msg, Throwable throwable) {
    System.err.println(ERROR + msg);
    throwable.printStackTrace();
  }

  @Override
  public void error(String msg) {
    System.err.println(ERROR + msg);
  }

  @Override
  public void debug(String msg) {
    System.out.println(DEBUG + msg);
  }

  @Override
  public void debug(String msg, Throwable throwable) {
    System.out.println(DEBUG + msg);
    throwable.printStackTrace();
  }

  @Override
  public void info(String msg) {
    System.out.println(INFO + msg);
  }

  @Override
  public void trace(String msg) {
    System.out.println(TRACE + msg);
  }

  @Override
  public void warn(String msg) {
    System.err.println(WARN + msg);
  }

  @Override
  public void warn(String msg, Throwable throwable) {
    System.err.println(WARN + msg);
    throwable.printStackTrace();
  }

}

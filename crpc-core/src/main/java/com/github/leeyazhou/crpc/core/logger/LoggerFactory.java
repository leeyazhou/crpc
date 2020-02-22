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
package com.github.leeyazhou.crpc.core.logger;

import java.lang.reflect.Constructor;

import com.github.leeyazhou.crpc.core.logger.commons.JakartaCommonsLoggingImpl;
import com.github.leeyazhou.crpc.core.logger.log4j.Log4jImpl;
import com.github.leeyazhou.crpc.core.logger.log4j2.Log4j2Impl;
import com.github.leeyazhou.crpc.core.logger.slf4j.Slf4jImpl;
import com.github.leeyazhou.crpc.core.logger.stdout.StdOutImpl;

public final class LoggerFactory {

  public static final String MARKER = "CRPC";

  private static Constructor<? extends Logger> logConstructor;

  static {
    tryImplementation(new Runnable() {
      @Override
      public void run() {
        useSlf4jLogging();
      }
    });
    tryImplementation(new Runnable() {
      @Override
      public void run() {
        useCommonsLogging();
      }
    });
    tryImplementation(new Runnable() {
      @Override
      public void run() {
        useLog4J2Logging();
      }
    });
    tryImplementation(new Runnable() {
      @Override
      public void run() {
        useLog4JLogging();
      }
    });
    tryImplementation(new Runnable() {
      @Override
      public void run() {
        useStdOutLogging();
      }
    });
  }

  private LoggerFactory() {
  }

  public static Logger getLogger(Class<?> clazz) {
    return getLogger(clazz.getName());
  }

  public static Logger getLogger(String logger) {
    try {
      return logConstructor.newInstance(logger);
    } catch (Throwable throwable) {
      throw new LoggerException("Error creating logger for logger " + logger + ".  Cause: " + throwable, throwable);
    }
  }

  public static synchronized void useCustomLogging(Class<? extends Logger> clazz) {
    setImplementation(clazz);
  }

  public static synchronized void useSlf4jLogging() {
    setImplementation(Slf4jImpl.class);
  }

  public static synchronized void useCommonsLogging() {
    setImplementation(JakartaCommonsLoggingImpl.class);
  }

  public static synchronized void useLog4JLogging() {
    setImplementation(Log4jImpl.class);
  }

  public static synchronized void useLog4J2Logging() {
    setImplementation(Log4j2Impl.class);
  }

  public static synchronized void useStdOutLogging() {
    setImplementation(StdOutImpl.class);
  }

  private static void tryImplementation(Runnable runnable) {
    if (logConstructor == null) {
      try {
        runnable.run();
      } catch (Throwable throwable) {
        // ignore
      }
    }
  }

  private static void setImplementation(Class<? extends Logger> implClass) {
    try {
      Constructor<? extends Logger> candidate = implClass.getConstructor(String.class);
      Logger logger = candidate.newInstance(LoggerFactory.class.getName());
      if (logger.isDebugEnabled()) {
        logger.debug("Logging initialized using '" + implClass + "' adapter.");
      }
      logConstructor = candidate;
    } catch (Throwable throwable) {
      throw new LoggerException("Error setting Log implementation.  Cause: " + throwable, throwable);
    }
  }

}

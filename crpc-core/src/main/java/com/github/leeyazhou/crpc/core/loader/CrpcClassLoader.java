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
package com.github.leeyazhou.crpc.core.loader;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.leeyazhou.crpc.core.util.FileUtil;

/**
 * @author lee
 */
public class CrpcClassLoader {

  private static Logger logger = LoggerFactory.getLogger(CrpcClassLoader.class);
  private static Method addURLMethod;

  static {
    try {
      addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
      addURLMethod.setAccessible(true);
    } catch (Exception err) {
      err.printStackTrace();
    }
  }

  private static URLClassLoader system = (URLClassLoader) getSystemClassLoader();

  private static URLClassLoader ext = (URLClassLoader) getExtClassLoader();

  public static ClassLoader getSystemClassLoader() {
    return ClassLoader.getSystemClassLoader();
  }

  public static ClassLoader getExtClassLoader() {
    return getSystemClassLoader().getParent();
  }

  public static void addURL2SystemClassLoader(URL url) throws Exception {
    try {
      logger.info("append jar to system classpath:" + url.toString());
      addURLMethod.invoke(system, new Object[] {url});
    } catch (Exception err) {
      throw err;
    }
  }

  public static void addURL2ExtClassLoader(URL url) throws Exception {
    try {
      logger.info("append jar to classpath:" + url.toString());
      addURLMethod.invoke(ext, new Object[] {url});
    } catch (Exception err) {
      throw err;
    }
  }

  public static void addSystemClassPath(String path) throws Exception {
    try {
      URL url = new URL("file", "", path);
      addURL2SystemClassLoader(url);
    } catch (MalformedURLException err) {
      throw err;
    }
  }

  public static void addExtClassPath(String path) throws Exception {
    try {
      URL url = new URL("file", "", path);
      addURL2ExtClassLoader(url);
    } catch (MalformedURLException err) {
      throw err;
    }
  }

  public static void addSystemClassPathFolder(String dirs) throws Exception {
    List<String> jarList = FileUtil.getUniqueLibPath(dirs);
    for (String jar : jarList) {
      addSystemClassPath(jar);
    }
  }

  public static void addURL2ExtClassLoaderFolder(String... dirs) throws Exception {
    List<String> jarList = FileUtil.getUniqueLibPath(dirs);
    for (String jar : jarList) {
      addExtClassPath(jar);
    }
  }

  public static URL[] getSystemURLs() {
    return system.getURLs();
  }

  public static URL[] getExtURLs() {
    return ext.getURLs();
  }
}

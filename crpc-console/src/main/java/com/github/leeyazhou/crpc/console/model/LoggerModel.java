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
/**
 * 
 */

package com.github.leeyazhou.crpc.console.model;

import java.io.Serializable;

/**
 * @author leeyazhou
 */
public class LoggerModel implements Serializable {

  private static final long serialVersionUID = 4655290065433879969L;

  private int level;
  private String levelStr;

  private String name;

  /**
   * 
   */
  public LoggerModel() {
  }

  /**
   * @param level level
   * @param levelStr levelStr
   * @param name name
   */
  public LoggerModel(int level, String levelStr, String name) {
    this.level = level;
    this.levelStr = levelStr;
    this.name = name;
  }

  /**
   * @return the level
   */
  public int getLevel() {
    return level;
  }

  /**
   * @param level the level to set
   */
  public void setLevel(int level) {
    this.level = level;
  }

  /**
   * @return the levelStr
   */
  public String getLevelStr() {
    return levelStr;
  }

  /**
   * @param levelStr the levelStr to set
   */
  public void setLevelStr(String levelStr) {
    this.levelStr = levelStr;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "LoggerModel [level=" + level + ", levelStr=" + levelStr + ", name=" + name + "]";
  }

}

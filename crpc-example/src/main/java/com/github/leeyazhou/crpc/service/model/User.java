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

package com.github.leeyazhou.crpc.service.model;

import java.io.Serializable;

import com.github.leeyazhou.crpc.core.annotation.CRPCSerializable;

/**
 * @author leeyazhou
 *
 */
@CRPCSerializable
public class User implements Serializable {

  private static final long serialVersionUID = -7963687477265008656L;
  private Integer id;
  private String username;
  private Integer sex;
  private Boolean deleted;
  private byte data[];

  /**
   * 
   */
  public User() {}

  public User(Integer id, Integer sex, String name) {
    this.id = id;
    this.sex = sex;
    this.username = name;
  }

  /**
   * @return the id
   */
  public Integer getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return the sex
   */
  public Integer getSex() {
    return sex;
  }

  /**
   * @param sex the sex to set
   */
  public void setSex(Integer sex) {
    this.sex = sex;
  }

  /**
   * @return the deleted
   */
  public Boolean getDeleted() {
    return deleted;
  }

  /**
   * @param deleted the deleted to set
   */
  public void setDeleted(Boolean deleted) {
    this.deleted = deleted;
  }

  /**
   * @return the data
   */
  public byte[] getData() {
    return data;
  }

  /**
   * @param data the data to set
   */
  public void setData(byte[] data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "User [id=" + id + ", username=" + username + ", sex=" + sex + ", deleted=" + deleted + "]";
  }

}

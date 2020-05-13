/**
 * 
 */
package com.github.leeyazhou.crpc.config;

/**
 * @author leeyazhou
 *
 */
public class ApplicationConfig {

  private String name;
  private String version;
  private String desc;

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public ApplicationConfig setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * @param version the version to set
   */
  public ApplicationConfig setVersion(String version) {
    this.version = version;
    return this;
  }

  /**
   * @return the desc
   */
  public String getDesc() {
    return desc;
  }

  /**
   * @param desc the desc to set
   */
  public ApplicationConfig setDesc(String desc) {
    this.desc = desc;
    return this;
  }


}

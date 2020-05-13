/**
 * 
 */
package com.github.leeyazhou.crpc.config;

import com.github.leeyazhou.crpc.core.URL;

/**
 * @author leeyazhou
 *
 */
public class RegistryConfig {

  private String address;

  /**
   * @return the address
   */
  public String getAddress() {
    return address;
  }

  /**
   * @param address the address to set
   */
  public void setAddress(String address) {
    this.address = address;
  }

  public URL toURL() {
    return URL.valueOf(address);
  }

}

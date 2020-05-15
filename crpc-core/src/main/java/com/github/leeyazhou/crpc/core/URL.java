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

package com.github.leeyazhou.crpc.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import com.github.leeyazhou.crpc.core.util.URLUtil;

/**
 * @author leeyazhou
 */
public class URL implements Serializable {

  private static final long serialVersionUID = -3703995598223622358L;

  private String protocol;

  private final String host;

  private final int port;

  private Map<String, String> parameters = new HashMap<String, String>();

  private String providerPath;
  private String consumerPath;

  private String registryType;

  public URL(String protocol, String host, int port) {
    this.protocol = protocol;
    this.host = host;
    this.port = port;
  }

  public URL(String protocol, String host, int port, Map<String, String> paramters) {
    this(protocol, host, port);
    this.parameters = paramters;
  }

  public static URL valueOf(String url) {
    // crpc://127.0.0.1:12200/com.xyz.service.UserService?application=userservice&version=0.1.1-SNAPSHOT&timestamp=1500011580766
    url = URLUtil.decode(url);
    String protocol = null;
    String host = null;
    int port = 0;

    String[] temp = url.split("\\?");
    String urlStr = temp[0];
    String[] hn = urlStr.split(":");
    if (hn.length == 3) {
      protocol = hn[0];
      host = hn[1].replaceAll("/", "");
      port = Integer.parseInt(hn[2].split("/")[0]);
    }
    Map<String, String> paramMap = new HashMap<String, String>();
    if (temp.length >= 2) {
      String paramsStr = temp[1];
      String[] paramArray = paramsStr.split("\\&");
      for (String param : paramArray) {
        String[] paramPair = param.split("=");
        paramMap.put(paramPair[0], paramPair[1]);
      }
    }

    return new URL(protocol, host, port, paramMap);
  }

  public String getProtocol() {
    return protocol;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  /**
   * @return the parameters
   */
  public Map<String, String> getParameters() {
    return parameters;
  }

  public URL addParameter(String key, String value) {
    this.parameters.put(key, value);
    return this;
  }

  public String getParameter(String key, String defaultValue) {
    String value = this.parameters.get(key);
    if (value == null || value.length() == 0) {
      return defaultValue;
    }
    return value;
  }

  public long getParameter(String key, long defaultValue) {
    String value = this.parameters.get(key);
    if (value == null || value.length() == 0) {
      return defaultValue;
    }
    return Long.parseLong(value);
  }

  public int getParameter(String key, int defaultValue) {
    String value = this.parameters.get(key);
    if (value == null || value.length() == 0) {
      return defaultValue;
    }
    return Integer.parseInt(value);
  }

  public String getProviderPath() {
    if (providerPath != null) {
      return providerPath;
    }
    return getPath("crpc", port);
  }

  public String getConsumerPath() {
    if (consumerPath != null) {
      return consumerPath;
    }
    return getPath("consumer", null);
  }

  private String getPath(String protocol, Integer port) {
    StringBuilder sb = new StringBuilder(protocol);
    sb.append("://");
    sb.append(host);
    if (port != null) {
      sb.append(":").append(port);
    }
    sb.append("/").append(getServiceName());
    this.getParameters().put("version", Constants.CRPC_VERSION);
    boolean first = true;
    for (Map.Entry<String, String> entry : new TreeMap<String, String>(this.getParameters()).entrySet()) {
      if (first) {
        sb.append("?");
        first = false;
      } else {
        sb.append("&");
      }
      sb.append(entry.getKey()).append("=").append(entry.getValue());
    }
    return sb.toString();
  }

  public String getAddress() {
    return new StringBuffer(getProtocol()).append("://").append(getHost()).append(":").append(getPort()).toString();
  }

  public String getServiceName() {
    return parameters.get(Constants.SERVICE_INTERFACE);
  }

  /**
   * @return the registryType
   */
  public String getRegistryType() {
    return registryType;
  }

  /**
   * @param registryType the registryType to set
   */
  public URL setRegistryType(String registryType) {
    this.registryType = registryType;
    return this;
  }

  /**
   * @param protocol the protocol to set
   */
  public URL setProtocol(String protocol) {
    this.protocol = protocol;
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    URL other = (URL) obj;
    if (host == null && other.host != null) {
      return false;
    } else if (!host.equals(other.host)) {
      return false;
    }
    if (parameters == null && other.parameters != null) {
      return false;
    } else if (!parameters.equals(other.parameters)) {
      return false;
    }
    if (port != other.port) {
      return false;
    }
    if (protocol == null && other.protocol != null) {
      return false;
    } else if (!protocol.equals(other.protocol)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((host == null) ? 0 : host.hashCode());
    result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
    result = prime * result + ((registryType == null) ? 0 : registryType.hashCode());
    result = prime * result + port;
    result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "URL [protocol=" + protocol + ", host=" + host + ", port=" + port + ", parameters=" + parameters
        + ", registryType=" + registryType + "]";
  }

}

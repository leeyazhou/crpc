/**
 * 
 */
package com.github.leeyazhou.crpc.config.crpc;

import java.util.Set;
import com.github.leeyazhou.crpc.config.ApplicationConfig;
import com.github.leeyazhou.crpc.config.ReferConfig;
import com.github.leeyazhou.crpc.config.RegistryConfig;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.rpc.proxy.ProxyFactory;

/**
 * @author leeyazhou
 *
 */
public class ConsumerConfig<T> extends ReferConfig<T> {

  public T refer() {
    ProxyFactory proxyFactory = ServiceLoader.load(ProxyFactory.class).load();
    return proxyFactory.getProxy(this);
  }

  public ConsumerConfig<T> setApplicationConfig(ApplicationConfig applicationConfig) {
    super.setApplicationConfig(applicationConfig);
    return this;
  }

  public ConsumerConfig<T> setRegistryConfig(RegistryConfig registryConfig) {
    super.setRegistryConfig(registryConfig);
    return this;
  }



  public ConsumerConfig<T> setServiceType(Class<T> serviceType) {
    super.setServiceType(serviceType);
    return this;
  }


  /**
   * @param urls the urls to set
   */
  public ConsumerConfig<T> setUrls(Set<URL> urls) {
    super.setUrls(urls);
    return this;
  }


  public ConsumerConfig<T> addURL(URL url) {
    super.addUrl(url);
    return this;
  }

}

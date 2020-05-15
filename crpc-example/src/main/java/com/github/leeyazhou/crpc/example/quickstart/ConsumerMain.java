/**
 * 
 */
package com.github.leeyazhou.crpc.example.quickstart;

import com.github.leeyazhou.crpc.config.ServiceGroupConfig;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.rpc.proxy.ProxyFactory;
import com.github.leeyazhou.crpc.service.UserService;

/**
 * @author leeyazhou
 *
 */
public class ConsumerMain {

  public static void main(String[] args) {
    ProxyFactory proxyFactory = ServiceLoader.load(ProxyFactory.class).load();
    ServiceGroupConfig serviceGroupConfig = new ServiceGroupConfig().addProvider(URL.valueOf("crpc://127.0.0.1:25001"));
    UserService userService = proxyFactory.getProxy(UserService.class, serviceGroupConfig);
    userService.sayWord("CRPC");
  }
}

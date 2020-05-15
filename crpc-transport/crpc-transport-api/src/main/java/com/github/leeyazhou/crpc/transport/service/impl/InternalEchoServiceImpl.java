/**
 * 
 */
package com.github.leeyazhou.crpc.transport.service.impl;

import com.github.leeyazhou.crpc.transport.service.InternalEchoService;

/**
 * @author leeyazhou
 *
 */
public class InternalEchoServiceImpl implements InternalEchoService {

  @Override
  public String echo(String echo) {
    return echo;
  }

  @Override
  public String hello(String name) {
    return "Hello " + name;
  }

}

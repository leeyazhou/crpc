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
package com.github.leeyazhou.crpc.transport.protocol;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ProtocolFactory {
  private static ProtocolFactory factory;
  private final ConcurrentMap<Byte, Protocol> protocolCache = new ConcurrentHashMap<Byte, Protocol>();

  private ProtocolFactory() {
    register(CrpcProtocol.VERSION, new CrpcProtocol());
  }


  private static ProtocolFactory getProtocolFactory() {
    if (factory == null) {
      factory = new ProtocolFactory();
    }
    return factory;
  }

  public Protocol get(byte version) {
    return protocolCache.get(version);
  }

  public void register(byte version, Protocol protocol) {
    protocolCache.put(version, protocol);
  }

  public static Protocol getProtocol(byte version) {
    return getProtocolFactory().get(version);
  }

  public static void registerProtocol(byte version, Protocol protocol) {
    getProtocolFactory().register(version, protocol);
  }

}

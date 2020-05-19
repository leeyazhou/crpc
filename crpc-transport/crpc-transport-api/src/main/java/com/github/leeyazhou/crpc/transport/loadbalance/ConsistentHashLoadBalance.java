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
package com.github.leeyazhou.crpc.transport.loadbalance;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.github.leeyazhou.crpc.transport.Client;
import com.github.leeyazhou.crpc.transport.protocol.message.RequestMessage;

/**
 * has load balance
 * 
 * @author leeyazhou
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {
  public static final String name = "ConsistentHash";
  private final ConcurrentMap<String, ConsistentHashSelector<Client>> selectors =
      new ConcurrentHashMap<String, ConsistentHashSelector<Client>>();

  @Override
  protected Client doChooseOne(List<Client> clients, RequestMessage request) {
    String key = request.getTargetClassName();
    int identityHashCode = System.identityHashCode(clients);
    ConsistentHashSelector<Client> selector = selectors.get(key);
    if (selector == null || selector.getIdentityHashCode() != identityHashCode) {
      selectors.put(key, new ConsistentHashSelector<Client>(clients, request.getMethodName(), identityHashCode));
      selector = (ConsistentHashSelector<Client>) selectors.get(key);
    }
    return selector.select(request);
  }

  private static final class ConsistentHashSelector<T> {

    private final TreeMap<Long, Client> virtualClients;

    private final int replicaNumber;

    private final int identityHashCode;

    private final int[] argumentIndex;

    public ConsistentHashSelector(List<Client> clients, String methodName, int identityHashCode) {
      this.virtualClients = new TreeMap<Long, Client>();
      this.identityHashCode = System.identityHashCode(clients);
      this.replicaNumber = methodName.hashCode();
      String[] index = new String[0];
      argumentIndex = new int[index.length];
      for (int i = 0; i < index.length; i++) {
        argumentIndex[i] = Integer.parseInt(index[i]);
      }
      for (Client client : clients) {
        for (int i = 0; i < replicaNumber / 4; i++) {
          byte[] digest = md5(client.toString() + i);
          for (int h = 0; h < 4; h++) {
            long m = hash(digest, h);
            virtualClients.put(m, client);
          }
        }
      }
    }

    public int getIdentityHashCode() {
      return identityHashCode;
    }

    public Client select(RequestMessage request) {
      String key = toKey(request.getArgs());
      byte[] digest = md5(key);
      Client client = sekectForKey(hash(digest, 0));
      return client;
    }

    private String toKey(Object[] args) {
      StringBuilder buf = new StringBuilder();
      for (int i : argumentIndex) {
        if (i >= 0 && i < args.length) {
          buf.append(args[i]);
        }
      }
      return buf.toString();
    }

    private Client sekectForKey(long hash) {
      Client invoker;
      Long key = hash;
      if (!virtualClients.containsKey(key)) {
        SortedMap<Long, Client> tailMap = virtualClients.tailMap(key);
        if (tailMap.isEmpty()) {
          key = virtualClients.firstKey();
        } else {
          key = tailMap.firstKey();
        }
      }
      invoker = virtualClients.get(key);
      return invoker;
    }

    private long hash(byte[] digest, int number) {
      return (((long) (digest[3 + number * 4] & 0xFF) << 24) | ((long) (digest[2 + number * 4] & 0xFF) << 16)
          | ((long) (digest[1 + number * 4] & 0xFF) << 8) | (digest[0 + number * 4] & 0xFF)) & 0xFFFFFFFFL;
    }

    private byte[] md5(String value) {
      MessageDigest md5;
      try {
        md5 = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException e) {
        throw new IllegalStateException(e.getMessage(), e);
      }
      md5.reset();
      byte[] bytes = null;
      try {
        bytes = value.getBytes("UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new IllegalStateException(e.getMessage(), e);
      }
      md5.update(bytes);
      return md5.digest();
    }

  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ConsistentHashLoadBalance [name=");
    builder.append(name);
    builder.append("]");
    return builder.toString();
  }

}

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

package com.github.leeyazhou.crpc.registry.redis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import com.github.leeyazhou.crpc.core.Constants;
import com.github.leeyazhou.crpc.core.URL;
import com.github.leeyazhou.crpc.core.concurrent.NamedThreadFactory;
import com.github.leeyazhou.crpc.core.exception.CrpcException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.registry.support.FailbackRegistry;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

/**
 * @author lee
 */
public class RedisRegistry extends FailbackRegistry {
  private static final Logger logger = LoggerFactory.getLogger(RedisRegistry.class);
  private Map<String, JedisPool> redisPools = new HashMap<String, JedisPool>();
  // private final ConcurrentMap<String, Notifier> notifiers = new
  // ConcurrentHashMap<String, Notifier>();
  private final ScheduledExecutorService expireExecutor =
      Executors.newScheduledThreadPool(1, new NamedThreadFactory("CRPCRegistryExpireTimer", true));
  private long expireTime;
  private ScheduledFuture<?> expireFuture;
  private volatile boolean running = true;

  /**
   * @param registryURL registryConfig
   */
  public RedisRegistry(URL registryURL) {
    super(registryURL);
    connect(registryURL);
  }

  @Override
  public void unregister(URL registryURL) {

    String key = toCategoryPath(registryURL);
    String value = registryURL.getProviderPath();
    CrpcException exception = null;
    boolean success = false;
    for (Map.Entry<String, JedisPool> entry : redisPools.entrySet()) {
      JedisPool jedisPool = entry.getValue();
      Jedis jedis = jedisPool.getResource();
      try {
        jedis.hdel(key, value);
        jedis.publish(key, Constants.UNREGISTER);
        success = true;
      } catch (Throwable t) {
        exception = new CrpcException("Failed to unregister service to redis registry. registry: " + entry.getKey()
            + ", service: " + registryURL + ", cause: " + t.getMessage(), t);
      } finally {
        jedis.close();
      }
    }
    if (exception != null) {
      if (success) {
        logger.warn(exception.getMessage(), exception);
      } else {
        throw exception;
      }
    }

  }

  @Override
  public void close() {
    running = false;
    expireFuture.cancel(true);
  }

  @Override
  public void doRegister(URL registryURL) {
    String key = toCategoryPath(registryURL);
    String value = registryURL.getProviderPath();
    String expire = String.valueOf(System.currentTimeMillis() + expireTime);
    CrpcException exception = null;
    boolean success = false;
    for (Map.Entry<String, JedisPool> jedisPool : redisPools.entrySet()) {
      Jedis client = jedisPool.getValue().getResource();
      try {
        client.hset(key, value, expire);
        client.publish(key, Constants.REGISTER);
        success = true;
      } catch (Throwable e) {
        exception = new CrpcException(e.getMessage(), e);
      } finally {
        client.close();
      }
    }

    if (success) {
      if (exception != null) {
        logger.warn(exception.getMessage(), exception);
      }
    } else {
      throw exception;
    }
  }

  private String toServicePath(URL url) {
    return root + Constants.FILE_SEPARATOR + url.getParameter(Constants.SERVICE_INTERFACE, null);
  }

  private String toCategoryPath(URL url) {
    return toServicePath(url) + Constants.FILE_SEPARATOR
        + url.getParameter(Constants.GROUP, Constants.DEFAULT_CATEGORY_PROVIDER);
  }

  @Override
  public List<URL> doGetProviders(URL registryURL) {
    Set<String> resultStr = new TreeSet<String>();
    for (Map.Entry<String, JedisPool> entry : redisPools.entrySet()) {
      JedisPool jedisPool = entry.getValue();
      if (registryURL == null) {
        Jedis jedis = jedisPool.getResource();
        // patern : /crpc/*/providers
        String key = root + Constants.FILE_SEPARATOR + Constants.ANY_VALUE + Constants.FILE_SEPARATOR
            + Constants.DEFAULT_CATEGORY_PROVIDER;
        Set<String> providers = jedis.keys(key);
        for (String provider : providers) {
          Map<String, String> serviceProviders = jedis.hgetAll(provider);
          resultStr.addAll(serviceProviders.keySet());
        }
        jedis.close();
        continue;
      }
      // String channel = toServicePath(registryConfig);
      // Notifier notifier = notifiers.get(channel);
      // if (notifier == null) {
      // notifier = new Notifier(jedisPool, channel);
      // if (notifiers.putIfAbsent(channel, notifier) != null) {
      // notifier.start();
      // }
      // }
    }
    List<URL> result = new ArrayList<URL>();
    for (String registryConfigStr : resultStr) {
      result.add(URL.valueOf(registryConfigStr));
    }
    return result;
  }

  @Override
  protected void connect(URL registryConfig) {
    String key = getRegistryURL().getHost() + ":" + getRegistryURL().getPort();
    GenericObjectPoolConfig config = new GenericObjectPoolConfig();
    JedisPool jedisPool = new JedisPool(config, getRegistryURL().getHost(), getRegistryURL().getPort(), 2000);
    redisPools.put(key, jedisPool);
    expireTime = registryConfig.getParameter(Constants.SESSION_TIMEOUT_KEY, Constants.DEFAULT_SESSION_TIMEOUT);
    this.expireFuture = expireExecutor.scheduleWithFixedDelay(new Runnable() {
      public void run() {
        try {
          deferExpired(); // 延长过期时间
        } catch (Throwable t) {
          logger.error("Unexpected exception occur at defer expire time, cause: " + t.getMessage(), t);
        }
      }
    }, expireTime / 2, expireTime / 2, TimeUnit.MILLISECONDS);
  }

  private void deferExpired() {
    for (Map.Entry<String, JedisPool> entry : redisPools.entrySet()) {
      JedisPool jedisPool = entry.getValue();
      try {
        Jedis jedis = jedisPool.getResource();
        try {
          for (URL url : new HashSet<URL>(getRegistered())) {
            String side = url.getParameter(Constants.SIDE_KEY, null);
            if (Constants.PROVIDER_SIDE.equals(side)) {
              String key = toCategoryPath(url);
              if (jedis.hset(key, url.getProviderPath(),
                  String.valueOf(System.currentTimeMillis() + expireTime)) == 1) {
                jedis.publish(key, Constants.REGISTER);
              }
            }
          }
          clean(jedis);
        } finally {
          jedis.close();
        }
      } catch (Throwable t) {
        logger.warn("Failed to write provider heartbeat to redis registry. registry: " + entry.getKey() + ", cause: "
            + t.getMessage(), t);
      }
    }

  }

  // 监控中心负责删除过期脏数据
  private void clean(Jedis jedis) {
    Set<String> keys = jedis.keys(root + Constants.ANY_VALUE);
    if (keys != null && keys.size() > 0) {
      for (String key : keys) {
        Map<String, String> values = jedis.hgetAll(key);
        if (values != null && values.size() > 0) {
          boolean delete = false;
          long now = System.currentTimeMillis();
          for (Map.Entry<String, String> entry : values.entrySet()) {
            long expire = Long.parseLong(entry.getValue());
            if (expire < now) {
              jedis.hdel(key, entry.getKey());
              delete = true;
              if (logger.isWarnEnabled()) {
                logger.warn("Delete expired key: " + key + " -> value: " + entry.getKey() + ", expire: "
                    + new Date(expire) + ", now: " + new Date(now));
              }
            }
          }
          if (delete) {
            jedis.publish(key, Constants.UNREGISTER);
          }
        }
      }
    }
  }

  @Override
  public boolean isAvailable() {
    for (Map.Entry<String, JedisPool> item : redisPools.entrySet()) {
      if (!item.getValue().isClosed()) {
        return true;
      }
    }
    return false;
  }

  private void doNotify(Jedis jedis, String channel) {
    logger.info("doNotify channel : " + channel);
  }

  private class NotifySub extends JedisPubSub {
    private final JedisPool jedisPool;

    public NotifySub(JedisPool jedisPool) {
      this.jedisPool = jedisPool;
    }

    @Override
    public void onMessage(String channel, String message) {
      if (logger.isInfoEnabled()) {
        logger.info("msg redis event, channel : " + channel + ", message : " + message);
      }
      if (message.equals(Constants.REGISTER)) {
        Jedis jedis = null;
        try {
          jedis = jedisPool.getResource();
          doNotify(jedis, channel);
        } catch (Throwable t) {
          logger.error(t.getMessage(), t);
        } finally {
          if (jedis != null) {
            jedis.close();
          }
        }
      }

    }

  }

  @SuppressWarnings("unused")
  private class Notifier extends Thread {
    private String channel;

    public Notifier(JedisPool jedisPool, String channel) {
      this.channel = channel;
      super.setDaemon(true);
      super.setName("CRPCRedisSubscribe");
    }

    @Override
    public void run() {
      while (running) {
        try {
          for (Map.Entry<String, JedisPool> jedisPool : redisPools.entrySet()) {
            NotifySub notifySub = new NotifySub(jedisPool.getValue());
            Jedis jedis = jedisPool.getValue().getResource();
            try {
              jedis.subscribe(notifySub, channel);
            } finally {
              jedis.close();
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

    }
  }
}

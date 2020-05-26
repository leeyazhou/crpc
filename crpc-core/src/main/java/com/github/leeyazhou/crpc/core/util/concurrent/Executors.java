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
package com.github.leeyazhou.crpc.core.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author leeyazhou
 *
 */
public final class Executors {

  /**
   * Creates a thread pool that reuses a fixed number of threads operating off a shared unbounded queue. At any point,
   * at most {@code nThreads} threads will be active processing tasks. If additional tasks are submitted when all
   * threads are active, they will wait in the queue until a thread is available. If any thread terminates due to a
   * failure during execution prior to shutdown, a new one will take its place if needed to execute subsequent tasks.
   * The threads in the pool will exist until it is explicitly {@link ExecutorService#shutdown shutdown}.
   *
   * @param nThreads the number of threads in the pool
   * @return the newly created thread pool
   * @throws IllegalArgumentException if {@code nThreads <= 0}
   */
  public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
  }

  /**
   * @param corePoolSize the number of threads to keep in the pool, even if they are idle.
   * @param maximumPoolSize the maximum number of threads to allow in the pool.
   * @return {@link ExecutorService}
   */
  public static ExecutorService newFixedThreadPool(int corePoolSize, int maximumPoolSize) {
    if (corePoolSize == 0) {
      corePoolSize = Runtime.getRuntime().availableProcessors();
    }
    return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>());
  }

  /**
   * Creates a thread pool that reuses a fixed number of threads operating off a shared unbounded queue, using the
   * provided ThreadFactory to create new threads when needed. At any point, at most {@code nThreads} threads will be
   * active processing tasks. If additional tasks are submitted when all threads are active, they will wait in the queue
   * until a thread is available. If any thread terminates due to a failure during execution prior to shutdown, a new
   * one will take its place if needed to execute subsequent tasks. The threads in the pool will exist until it is
   * explicitly {@link ExecutorService#shutdown shutdown}.
   *
   * @param nThreads the number of threads in the pool
   * @param threadFactory the factory to use when creating new threads
   * @return the newly created thread pool
   * @throws NullPointerException if threadFactory is null
   * @throws IllegalArgumentException if {@code nThreads <= 0}
   */
  public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
    return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
        threadFactory);
  }

  /**
   * 
   * @param corePoolSize the number of threads to keep in the pool, even if they are idle.
   * @param maximumPoolSize the maximum number of threads to allow in the pool.
   * @param threadFactory threadFactory
   * @return {@link ExecutorService}
   */
  public static ExecutorService newFixedThreadPool(int corePoolSize, int maximumPoolSize, ThreadFactory threadFactory) {
    return newFixedThreadPool(corePoolSize, maximumPoolSize, Integer.MAX_VALUE, threadFactory);
  }

  /**
   * 
   * @param corePoolSize the number of threads to keep in the pool, even if they are idle.
   * @param maximumPoolSize the maximum number of threads to allow in the pool.
   * @param workQueueSize work queue
   * @param threadFactory threadFactory
   * @return {@link ExecutorService}
   */
  public static ExecutorService newFixedThreadPool(int corePoolSize, int maximumPoolSize, int workQueueSize,
      ThreadFactory threadFactory) {
    if (corePoolSize == 0) {
      corePoolSize = Runtime.getRuntime().availableProcessors();
    }
    return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(workQueueSize), threadFactory);
  }

  /**
   * Creates a thread pool that creates new threads as needed, but will reuse previously constructed threads when they
   * are available, and uses the provided ThreadFactory to create new threads when needed.
   * 
   * @param threadFactory the factory to use when creating new threads
   * @param maximumPoolSize maximumPoolSize
   * @return the newly created thread pool
   * @throws NullPointerException if threadFactory is null
   */
  public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory, int maximumPoolSize) {
    if (maximumPoolSize == 0) {
      maximumPoolSize = Runtime.getRuntime().availableProcessors();
    }
    return new ThreadPoolExecutor(0, maximumPoolSize, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
        threadFactory);
  }
}

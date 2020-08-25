package com.github.leeyazhou.crpc.core.util.function;

public interface Consumer<T> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t the input argument
   */
  void accept(T t);

  void onError(Throwable throwable);

}

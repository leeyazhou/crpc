/**
 * 
 */
package com.github.leeyazhou.crpc.core.util.reflect;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.github.leeyazhou.crpc.core.lifecyle.AbstractInit;
import com.github.leeyazhou.crpc.core.lifecyle.Init;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

/**
 * @author leeyazhou
 *
 */
public class ClassInfo<T> extends AbstractInit implements Init {
  private static final Logger logger = LoggerFactory.getLogger(ClassInfo.class);
  public static final String METHOD_SEPARATOR = "$";
  public static final String ARG_SEPARATOR = "_";
  private final ConcurrentMap<String, MethodProxy> methodCache = new ConcurrentHashMap<String, MethodProxy>();
  private Class<T> serviceType;

  public ClassInfo(Class<T> serviceType) {
    this.serviceType = serviceType;
  }

  @Override
  public void doInit() {
    Method[] methods = serviceType.getDeclaredMethods();
    for (Method method : methods) {
      Class<?>[] argTypes = method.getParameterTypes();
      StringBuilder methodKeyBuilder = new StringBuilder();
      methodKeyBuilder.append(method.getName()).append(METHOD_SEPARATOR);
      if (argTypes != null) {
        for (Class<?> argClass : argTypes) {
          methodKeyBuilder.append(argClass.getName()).append(ARG_SEPARATOR);
        }
      }
      MethodProxy temp = this.methodCache.put(methodKeyBuilder.toString(), new MethodProxy(method));
      if (temp != null) {
        logger.warn("method is already exists! targetClass : " + serviceType + ", MethodProxy : " + temp);
      }
    }

  }

  public MethodProxy getMethod(String methodKey) {
    return methodCache.get(methodKey);
  }

  public String toMethodKey(String methodName, String[] args) {
    StringBuilder methodKeyBuilder = new StringBuilder();
    methodKeyBuilder.append(methodName).append(METHOD_SEPARATOR);

    if (args != null) {
      for (Object arg : args) {
        methodKeyBuilder.append(arg).append(ARG_SEPARATOR);
      }
    }
    return methodKeyBuilder.toString();
  }

}

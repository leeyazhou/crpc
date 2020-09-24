/**
 * 
 */
package com.github.leeyazhou.crpc.bootstrap;

import java.lang.reflect.Method;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.core.util.StringUtil;

/**
 * @author leeyazhou
 *
 */
public class Main {

  public static final String BOOTSTRAP_TYPE = "crpc.bootstrap";
  public static final String BOOTSTRAP_VALUE_CRPC = "crpc";
  public static final String BOOTSTRAP_VALUE_SPRING = "spring";

  public static void main(String[] args) {
    new Main().start();
  }

  private void start() {
    try {
      doStartup();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void doStartup() throws Exception {
    BannerPrint.printBanner();
    Class<?> bootstrapType = getBootstrapType();
    Object bootstrap = bootstrapType.newInstance();
    Method startup = bootstrapType.getDeclaredMethod("startup");
    startup.invoke(bootstrap);
  }


  public Class<?> getBootstrapType() {
    String bootstrapStr = System.getProperty(BOOTSTRAP_TYPE, null);
    Class<?> bootstrapType = null;
    if (StringUtil.isNotBlank(bootstrapStr)) {
      bootstrapType = ServiceLoader.load(Bootstrap.class).loadType(bootstrapStr);
    } else {
      bootstrapType = ServiceLoader.load(Bootstrap.class).loadType();
    }
    return bootstrapType;
  }
}

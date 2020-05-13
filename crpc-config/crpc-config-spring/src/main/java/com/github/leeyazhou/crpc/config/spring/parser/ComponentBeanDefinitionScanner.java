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
package com.github.leeyazhou.crpc.config.spring.parser;

import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

/**
 * 
 * @author leeyazhou
 */
public class ComponentBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {



  /**
   * @param registry registry
   */
  public ComponentBeanDefinitionScanner(BeanDefinitionRegistry registry) {
    super(registry);
  }


  public ComponentBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment,
      ResourceLoader resourceLoader) {
    super(registry, useDefaultFilters, environment, resourceLoader);
  }


  @Override
  protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
    return super.doScan(basePackages);
  }

}

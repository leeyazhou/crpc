/**
 * Copyright © 2019 leeyazhou (coderhook@gmail.com)
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
package com.github.leeyazhou.crpc.config.spring.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.github.leeyazhou.crpc.config.crpc.Configuration;
import com.github.leeyazhou.crpc.config.spring.ServiceFactoryBean;
import com.github.leeyazhou.crpc.config.spring.SpringBeanFactory;
import com.github.leeyazhou.crpc.transport.Filter;
import com.github.leeyazhou.crpc.core.annotation.CRPCService;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.ServiceLoader;
import com.github.leeyazhou.crpc.core.util.StringUtil;

/**
 * 
 * @author <a href="mailto:lee_yazhou@163.com">Yazhou Li</a>
 */
public class ComponentScanBeanDefinitionParser implements BeanDefinitionParser {

  private static final String BASE_PACKAGE_ATTRIBUTE = "base-package";
  private static final String FILTER_ATTRIBUTE = "filter";
  private static final Logger logger = LoggerFactory.getLogger(ComponentScanBeanDefinitionParser.class);

  @Override
  public BeanDefinition parse(Element element, ParserContext parserContext) {
    registerFilterChain(element, parserContext);

    RootBeanDefinition annotationBeanPostProcessorBeanDefinition = new RootBeanDefinition(
        AnnotationBeanPostProcessor.class);
    BeanDefinitionReaderUtils.registerWithGeneratedName(annotationBeanPostProcessorBeanDefinition,
        parserContext.getRegistry());

    RootBeanDefinition springBeanFactoryDefinition = new RootBeanDefinition(SpringBeanFactory.class);
    BeanDefinitionReaderUtils.registerWithGeneratedName(springBeanFactoryDefinition, parserContext.getRegistry());
    RootBeanDefinition configurationBeanDefinition = new RootBeanDefinition(Configuration.class);
    BeanDefinitionReaderUtils.registerWithGeneratedName(configurationBeanDefinition, parserContext.getRegistry());

    // Actually scan for bean definitions and register them.
    String basePackage = element.getAttribute(BASE_PACKAGE_ATTRIBUTE);
    basePackage = parserContext.getReaderContext().getEnvironment().resolvePlaceholders(basePackage);
    String[] basePackages = StringUtils.tokenizeToStringArray(basePackage,
        ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
    ComponentBeanDefinitionScanner scanner = configureScanner(parserContext, element);
    Set<BeanDefinitionHolder> beanDefinitions = scanner.doScan(basePackages);
    logger.info("basePackages : " + basePackage + ", beanDefinitions : " + beanDefinitions.size());
    for (BeanDefinitionHolder ho : beanDefinitions) {
      logger.info("find service : " + ho);
      GenericBeanDefinition beanDefinition = (GenericBeanDefinition) ho.getBeanDefinition();
      final String beanClassName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition,
          parserContext.getRegistry());
      parserContext.getRegistry().registerBeanDefinition(beanClassName, beanDefinition);

      // register service handler bean
      final String beanClassInstanceName = ho.getBeanName() + "@Handler";
      GenericBeanDefinition serviceBeanDefinition = new GenericBeanDefinition();
      serviceBeanDefinition.setBeanClass(ServiceFactoryBean.class);
      serviceBeanDefinition.setLazyInit(true);
      serviceBeanDefinition.getPropertyValues().add("object", new RuntimeBeanReference(beanClassName));
      parserContext.getRegistry().registerBeanDefinition(beanClassInstanceName, serviceBeanDefinition);
    }
    return null;
  }

  /**
   * 创建Filter链
   * 
   * @param element
   * @param parserContext
   */
  private void registerFilterChain(Element element, ParserContext parserContext) {
    String filterStr = element.getAttribute(FILTER_ATTRIBUTE);
    logger.info("build filter chain : " + filterStr);
    RootBeanDefinition last = null;

    // Filter last = null;
    RootBeanDefinition lastFilterBeanDefinition = null;
    if (StringUtil.isNotBlank(filterStr)) {
      String[] filterStrs = StringUtils.tokenizeToStringArray(filterStr,
          ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
      Map<String, RootBeanDefinition> beanDefinitions = new HashMap<String, RootBeanDefinition>();
      for (String item : filterStrs) {
        // Filter filter = ServiceLoader.load(Filter.class).load(item);
        Class<Filter> filterType = ServiceLoader.load(Filter.class).loadType(item);
        if(filterType == null) {
          logger.warn("Filter not found : " + item);
          continue;
        }
        RootBeanDefinition filterBeanDefinition = new RootBeanDefinition(filterType);
        beanDefinitions.put(item, filterBeanDefinition);
        if (lastFilterBeanDefinition == null) {
          lastFilterBeanDefinition = filterBeanDefinition;
          last = filterBeanDefinition;
        } else {
          lastFilterBeanDefinition.getPropertyValues().add("next", new RuntimeBeanReference(item));
        }
        lastFilterBeanDefinition = filterBeanDefinition;
      }

      for (Map.Entry<String, RootBeanDefinition> entry : beanDefinitions.entrySet()) {
        parserContext.getRegistry().registerBeanDefinition(entry.getKey(), entry.getValue());
      }
    }
    BeanDefinitionReaderUtils.registerWithGeneratedName(last, parserContext.getRegistry());
  }

  protected ComponentBeanDefinitionScanner configureScanner(ParserContext parserContext, Element element) {
    boolean useDefaultFilters = false;

    // Delegate bean definition registration to scanner class.
    ComponentBeanDefinitionScanner scanner = createScanner(parserContext.getReaderContext(), useDefaultFilters);
    scanner.setBeanDefinitionDefaults(parserContext.getDelegate().getBeanDefinitionDefaults());
    scanner.setAutowireCandidatePatterns(parserContext.getDelegate().getAutowireCandidatePatterns());
    scanner.addIncludeFilter(new AnnotationTypeFilter(CRPCService.class));
    return scanner;
  }

  protected ComponentBeanDefinitionScanner createScanner(XmlReaderContext readerContext, boolean useDefaultFilters) {
    return new ComponentBeanDefinitionScanner(readerContext.getRegistry(), useDefaultFilters,
        readerContext.getEnvironment(), readerContext.getResourceLoader());
  }

}

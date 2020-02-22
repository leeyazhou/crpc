/**
 * Copyright © 2016~2020 leeyazhou (coderhook@gmail.com)
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

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.LookupOverride;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.github.leeyazhou.crpc.config.spring.ReferenceFactoryBean;
import com.github.leeyazhou.crpc.core.annotation.CRPCReference;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;

/**
 * @author lee
 *
 */
public class AnnotationBeanPostProcessor extends AutowiredAnnotationBeanPostProcessor
    implements MergedBeanDefinitionPostProcessor, PriorityOrdered, ApplicationContextAware, BeanClassLoaderAware, DisposableBean {
  protected static final Logger logger = LoggerFactory.getLogger(AnnotationBeanPostProcessor.class);
  protected ApplicationContext applicationContext;
  protected ClassLoader classLoader;
  private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<Class<? extends Annotation>>(4);

  private String requiredParameterName = "required";

  private boolean requiredParameterValue = true;

  private int order = Ordered.LOWEST_PRECEDENCE - 2;

  private ConfigurableListableBeanFactory beanFactory;

  private final Set<String> lookupMethodsChecked = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(256));

  private final Map<Class<?>, Constructor<?>[]> candidateConstructorsCache =
      new ConcurrentHashMap<Class<?>, Constructor<?>[]>(256);

  private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<String, InjectionMetadata>(256);


  /**
   * Create a new AutowiredAnnotationBeanPostProcessor for Spring's standard {@link Autowired}
   * annotation.
   */
  @SuppressWarnings("unchecked")
  public AnnotationBeanPostProcessor() {
    this.autowiredAnnotationTypes.add(CRPCReference.class);
    try {
      this.autowiredAnnotationTypes.add((Class<? extends Annotation>) ClassUtils.forName("javax.inject.Inject",
          AutowiredAnnotationBeanPostProcessor.class.getClassLoader()));
      logger.info("JSR-330 'javax.inject.Inject' annotation found and supported for autowiring");
    } catch (ClassNotFoundException ex) {
      // JSR-330 API not available - simply skip.
    }
  }


  /**
   * Set the 'autowired' annotation type, to be used on constructors, fields, setter methods and
   * arbitrary config methods.
   * <p>
   * The default autowired annotation type is the Spring-provided {@link Autowired} annotation, as
   * well as {@link Value}.
   * <p>
   * This setter property exists so that developers can provide their own (non-Spring-specific)
   * annotation type to indicate that a member is supposed to be autowired.
   */
  public void setAutowiredAnnotationType(Class<? extends Annotation> autowiredAnnotationType) {
    Assert.notNull(autowiredAnnotationType, "'autowiredAnnotationType' must not be null");
    this.autowiredAnnotationTypes.clear();
    this.autowiredAnnotationTypes.add(autowiredAnnotationType);
  }

  /**
   * Set the 'autowired' annotation types, to be used on constructors, fields, setter methods and
   * arbitrary config methods.
   * <p>
   * The default autowired annotation type is the Spring-provided {@link Autowired} annotation, as
   * well as {@link Value}.
   * <p>
   * This setter property exists so that developers can provide their own (non-Spring-specific)
   * annotation types to indicate that a member is supposed to be autowired.
   */
  public void setAutowiredAnnotationTypes(Set<Class<? extends Annotation>> autowiredAnnotationTypes) {
    Assert.notEmpty(autowiredAnnotationTypes, "'autowiredAnnotationTypes' must not be empty");
    this.autowiredAnnotationTypes.clear();
    this.autowiredAnnotationTypes.addAll(autowiredAnnotationTypes);
  }

  /**
   * Set the name of a parameter of the annotation that specifies whether it is required.
   * 
   * @see #setRequiredParameterValue(boolean)
   */
  public void setRequiredParameterName(String requiredParameterName) {
    this.requiredParameterName = requiredParameterName;
  }

  /**
   * Set the boolean value that marks a dependency as required
   * <p>
   * For example if using 'required=true' (the default), this value should be {@code true}; but if
   * using 'optional=false', this value should be {@code false}.
   * 
   * @see #setRequiredParameterName(String)
   */
  public void setRequiredParameterValue(boolean requiredParameterValue) {
    this.requiredParameterValue = requiredParameterValue;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  @Override
  public int getOrder() {
    return this.order;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) {
    if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
      throw new IllegalArgumentException(
          "AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
    }
    this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
  }


  @Override
  public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    if (beanType != null) {
      InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
      metadata.checkConfigMembers(beanDefinition);
    }
  }

  @Override
  public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, final String beanName) throws BeanCreationException {

    // Let's check for lookup methods here..
    if (!this.lookupMethodsChecked.contains(beanName)) {
      try {
        ReflectionUtils.doWithMethods(beanClass, new ReflectionUtils.MethodCallback() {
          @Override
          public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
            Lookup lookup = method.getAnnotation(Lookup.class);
            if (lookup != null) {
              LookupOverride override = new LookupOverride(method, lookup.value());
              try {
                RootBeanDefinition mbd = (RootBeanDefinition) beanFactory.getMergedBeanDefinition(beanName);
                mbd.getMethodOverrides().addOverride(override);
              } catch (NoSuchBeanDefinitionException ex) {
                throw new BeanCreationException(beanName, "Cannot apply @Lookup to beans without corresponding bean definition");
              }
            }
          }
        });
      } catch (IllegalStateException ex) {
        throw new BeanCreationException(beanName, "Lookup method resolution failed", ex);
      } catch (NoClassDefFoundError err) {
        throw new BeanCreationException(beanName, "Failed to introspect bean class [" + beanClass.getName()
            + "] for lookup method metadata: could not find class that it depends on", err);
      }
      this.lookupMethodsChecked.add(beanName);
    }

    // Quick check on the concurrent map first, with minimal locking.
    Constructor<?>[] candidateConstructors = this.candidateConstructorsCache.get(beanClass);
    if (candidateConstructors == null) {
      // Fully synchronized resolution now...
      synchronized (this.candidateConstructorsCache) {
        candidateConstructors = this.candidateConstructorsCache.get(beanClass);
        if (candidateConstructors == null) {
          Constructor<?>[] rawCandidates;
          try {
            rawCandidates = beanClass.getDeclaredConstructors();
          } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Resolution of declared constructors on bean Class [" + beanClass.getName()
                + "] from ClassLoader [" + beanClass.getClassLoader() + "] failed", ex);
          }
          List<Constructor<?>> candidates = new ArrayList<Constructor<?>>(rawCandidates.length);
          Constructor<?> requiredConstructor = null;
          Constructor<?> defaultConstructor = null;
          for (Constructor<?> candidate : rawCandidates) {
            AnnotationAttributes ann = findAutowiredAnnotation(candidate);
            if (ann == null) {
              Class<?> userClass = ClassUtils.getUserClass(beanClass);
              if (userClass != beanClass) {
                try {
                  Constructor<?> superCtor = userClass.getDeclaredConstructor(candidate.getParameterTypes());
                  ann = findAutowiredAnnotation(superCtor);
                } catch (NoSuchMethodException ex) {
                  // Simply proceed, no equivalent superclass constructor found...
                }
              }
            }
            if (ann != null) {
              if (requiredConstructor != null) {
                throw new BeanCreationException(beanName, "Invalid autowire-marked constructor: " + candidate
                    + ". Found constructor with 'required' Autowired annotation already: " + requiredConstructor);
              }
              boolean required = determineRequiredStatus(ann);
              if (required) {
                if (!candidates.isEmpty()) {
                  throw new BeanCreationException(beanName, "Invalid autowire-marked constructors: " + candidates
                      + ". Found constructor with 'required' Autowired annotation: " + candidate);
                }
                requiredConstructor = candidate;
              }
              candidates.add(candidate);
            } else if (candidate.getParameterTypes().length == 0) {
              defaultConstructor = candidate;
            }
          }
          if (!candidates.isEmpty()) {
            // Add default constructor to list of optional constructors, as fallback.
            if (requiredConstructor == null) {
              if (defaultConstructor != null) {
                candidates.add(defaultConstructor);
              } else if (candidates.size() == 1 && logger.isWarnEnabled()) {
                logger.warn("Inconsistent constructor declaration on bean with name '" + beanName
                    + "': single autowire-marked constructor flagged as optional - "
                    + "this constructor is effectively required since there is no " + "default constructor to fall back to: "
                    + candidates.get(0));
              }
            }
            candidateConstructors = candidates.toArray(new Constructor<?>[candidates.size()]);
          } else if (rawCandidates.length == 1 && rawCandidates[0].getParameterTypes().length > 0) {
            candidateConstructors = new Constructor<?>[] {rawCandidates[0]};
          } else {
            candidateConstructors = new Constructor<?>[0];
          }
          this.candidateConstructorsCache.put(beanClass, candidateConstructors);
        }
      }
    }
    return (candidateConstructors.length > 0 ? candidateConstructors : null);
  }

  @Override
  public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName)
      throws BeanCreationException {

    InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
    try {
      metadata.inject(bean, beanName, pvs);
    } catch (BeanCreationException ex) {
      throw ex;
    } catch (Throwable ex) {
      throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
    }
    return pvs;
  }

  /**
   * 'Native' processing method for direct calls with an arbitrary target instance, resolving all of
   * its fields and methods which are annotated with {@code @Autowired}.
   * 
   * @param bean the target instance to process
   * @throws BeanCreationException if autowiring failed
   */
  public void processInjection(Object bean) throws BeanCreationException {
    Class<?> clazz = bean.getClass();
    InjectionMetadata metadata = findAutowiringMetadata(clazz.getName(), clazz, null);
    try {
      metadata.inject(bean, null, null);
    } catch (BeanCreationException ex) {
      throw ex;
    } catch (Throwable ex) {
      throw new BeanCreationException("Injection of autowired dependencies failed for class [" + clazz + "]", ex);
    }
  }


  private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
    // Fall back to class name as cache key, for backwards compatibility with custom callers.
    String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
    // Quick check on the concurrent map first, with minimal locking.
    InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
    if (InjectionMetadata.needsRefresh(metadata, clazz)) {
      synchronized (this.injectionMetadataCache) {
        metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
          if (metadata != null) {
            metadata.clear(pvs);
          }
          try {
            metadata = buildAutowiringMetadata(clazz);
            this.injectionMetadataCache.put(cacheKey, metadata);
          } catch (NoClassDefFoundError err) {
            throw new IllegalStateException("Failed to introspect bean class [" + clazz.getName()
                + "] for autowiring metadata: could not find class that it depends on", err);
          }
        }
      }
    }
    return metadata;
  }

  private InjectionMetadata buildAutowiringMetadata(final Class<?> clazz) {
    LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();
    Class<?> targetClass = clazz;

    do {
      final LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList<InjectionMetadata.InjectedElement>();

      ReflectionUtils.doWithLocalFields(targetClass, new ReflectionUtils.FieldCallback() {
        @Override
        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
          AnnotationAttributes ann = findAutowiredAnnotation(field);
          if (ann != null) {
            if (Modifier.isStatic(field.getModifiers())) {
              if (logger.isWarnEnabled()) {
                logger.warn("Autowired annotation is not supported on static fields: " + field);
              }
              return;
            }
            boolean required = determineRequiredStatus(ann);
            currElements.add(new AutowiredFieldElement(field, required));
          }
        }
      });

      ReflectionUtils.doWithLocalMethods(targetClass, new ReflectionUtils.MethodCallback() {
        @Override
        public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
          Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
          if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
            return;
          }
          AnnotationAttributes ann = findAutowiredAnnotation(bridgedMethod);
          if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
            if (Modifier.isStatic(method.getModifiers())) {
              if (logger.isWarnEnabled()) {
                logger.warn("Autowired annotation is not supported on static methods: " + method);
              }
              return;
            }
            if (method.getParameterTypes().length == 0) {
              if (logger.isWarnEnabled()) {
                logger.warn("Autowired annotation should only be used on methods with parameters: " + method);
              }
            }
            boolean required = determineRequiredStatus(ann);
            PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
            currElements.add(new AutowiredMethodElement(method, required, pd));
          }
        }
      });

      elements.addAll(0, currElements);
      targetClass = targetClass.getSuperclass();
    } while (targetClass != null && targetClass != Object.class);

    return new InjectionMetadata(clazz, elements);
  }

  private AnnotationAttributes findAutowiredAnnotation(AccessibleObject ao) {
    if (ao.getAnnotations().length > 0) { // autowiring annotations have to be local
      for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
        AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(ao, type);
        if (attributes != null) {
          return attributes;
        }
      }
    }
    return null;
  }

  /**
   * Determine if the annotated field or method requires its dependency.
   * <p>
   * A 'required' dependency means that autowiring should fail when no beans are found. Otherwise, the
   * autowiring process will simply bypass the field or method when no beans are found.
   * 
   * @param ann the Autowired annotation
   * @return whether the annotation indicates that a dependency is required
   */
  protected boolean determineRequiredStatus(AnnotationAttributes ann) {
    return (!ann.containsKey(this.requiredParameterName)
        || this.requiredParameterValue == ann.getBoolean(this.requiredParameterName));
  }

  /**
   * Obtain all beans of the given type as autowire candidates.
   * 
   * @param type the type of the bean
   * @return the target beans, or an empty Collection if no bean of this type is found
   * @throws BeansException if bean retrieval failed
   */
  protected <T> Map<String, T> findAutowireCandidates(Class<T> type) throws BeansException {
    if (this.beanFactory == null) {
      throw new IllegalStateException(
          "No BeanFactory configured - " + "override the getBeanOfType method or specify the 'beanFactory' property");
    }
    return BeanFactoryUtils.beansOfTypeIncludingAncestors(this.beanFactory, type);
  }

  /**
   * Register the specified bean as dependent on the autowired beans.
   */
  private void registerDependentBeans(String beanName, Set<String> autowiredBeanNames) {
    if (beanName != null) {
      for (String autowiredBeanName : autowiredBeanNames) {
        if (this.beanFactory.containsBean(autowiredBeanName)) {
          this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
        }
        if (logger.isDebugEnabled()) {
          logger.debug("Autowiring by type from bean name '" + beanName + "' to bean named '" + autowiredBeanName + "'");
        }
      }
    }
  }

  /**
   * Resolve the specified cached method argument or field value.
   */
  private Object resolvedCachedArgument(String beanName, Object cachedArgument) {
    if (cachedArgument instanceof DependencyDescriptor) {
      DependencyDescriptor descriptor = (DependencyDescriptor) cachedArgument;
      return this.beanFactory.resolveDependency(descriptor, beanName, null, null);
    } else {
      return cachedArgument;
    }
  }

  /**
   * @param beanType
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  private Object buildReferenceFactoryBean(Class<?> beanType) throws Exception {
    ReferenceFactoryBean<Object> factoryBean = new ReferenceFactoryBean<Object>();
    factoryBean.setObjectType((Class<Object>) beanType);
    factoryBean.setApplicationContext(applicationContext);
    return factoryBean.getObject();
  }

  /**
   * Class representing injection information about an annotated field.
   */
  private class AutowiredFieldElement extends InjectionMetadata.InjectedElement {

    private final boolean required;

    private volatile boolean cached = false;

    private volatile Object cachedFieldValue;

    public AutowiredFieldElement(Field field, boolean required) {
      super(field, null);
      this.required = required;
    }

    @Override
    protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
      Field field = (Field) this.member;
      Object value;
      if (this.cached) {
        value = resolvedCachedArgument(beanName, this.cachedFieldValue);
      } else {
        DependencyDescriptor desc = new DependencyDescriptor(field, this.required);
        desc.setContainingClass(bean.getClass());
        Set<String> autowiredBeanNames = new LinkedHashSet<String>(1);
        TypeConverter typeConverter = beanFactory.getTypeConverter();
        try {
          value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
        } catch (BeansException ex) {
          value = buildReferenceFactoryBean(field.getType());
          // throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(field), ex);
        }
        synchronized (this) {
          if (!this.cached) {
            if (value != null || this.required) {
              this.cachedFieldValue = desc;
              registerDependentBeans(beanName, autowiredBeanNames);
              if (autowiredBeanNames.size() == 1) {
                String autowiredBeanName = autowiredBeanNames.iterator().next();
                if (beanFactory.containsBean(autowiredBeanName) && beanFactory.isTypeMatch(autowiredBeanName, field.getType())) {
                  this.cachedFieldValue = new ShortcutDependencyDescriptor(desc, autowiredBeanName, field.getType());
                }
              }
            } else {
              this.cachedFieldValue = null;
            }
            this.cached = true;
          }
        }
      }
      if (value != null) {
        ReflectionUtils.makeAccessible(field);
        field.set(bean, value);
      }
    }

  }


  /**
   * Class representing injection information about an annotated method.
   */
  private class AutowiredMethodElement extends InjectionMetadata.InjectedElement {

    private final boolean required;

    private volatile boolean cached = false;

    private volatile Object[] cachedMethodArguments;

    public AutowiredMethodElement(Method method, boolean required, PropertyDescriptor pd) {
      super(method, pd);
      this.required = required;
    }

    @Override
    protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
      if (checkPropertySkipping(pvs)) {
        return;
      }
      Method method = (Method) this.member;
      Object[] arguments;
      if (this.cached) {
        // Shortcut for avoiding synchronization...
        arguments = resolveCachedArguments(beanName);
      } else {
        Class<?>[] paramTypes = method.getParameterTypes();
        arguments = new Object[paramTypes.length];
        DependencyDescriptor[] descriptors = new DependencyDescriptor[paramTypes.length];
        Set<String> autowiredBeans = new LinkedHashSet<String>(paramTypes.length);
        TypeConverter typeConverter = beanFactory.getTypeConverter();
        for (int i = 0; i < arguments.length; i++) {
          MethodParameter methodParam = new MethodParameter(method, i);
          DependencyDescriptor currDesc = new DependencyDescriptor(methodParam, this.required);
          currDesc.setContainingClass(bean.getClass());
          descriptors[i] = currDesc;
          try {
            Object arg = beanFactory.resolveDependency(currDesc, beanName, autowiredBeans, typeConverter);
            if (arg == null && !this.required) {
              arguments = null;
              break;
            }
            arguments[i] = arg;
          } catch (BeansException ex) {
            throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(methodParam), ex);
          }
        }
        synchronized (this) {
          if (!this.cached) {
            if (arguments != null) {
              this.cachedMethodArguments = new Object[paramTypes.length];
              for (int i = 0; i < arguments.length; i++) {
                this.cachedMethodArguments[i] = descriptors[i];
              }
              registerDependentBeans(beanName, autowiredBeans);
              if (autowiredBeans.size() == paramTypes.length) {
                Iterator<String> it = autowiredBeans.iterator();
                for (int i = 0; i < paramTypes.length; i++) {
                  String autowiredBeanName = it.next();
                  if (beanFactory.containsBean(autowiredBeanName)) {
                    if (beanFactory.isTypeMatch(autowiredBeanName, paramTypes[i])) {
                      this.cachedMethodArguments[i] =
                          new ShortcutDependencyDescriptor(descriptors[i], autowiredBeanName, paramTypes[i]);
                    }
                  }
                }
              }
            } else {
              this.cachedMethodArguments = null;
            }
            this.cached = true;
          }
        }
      }
      if (arguments != null) {
        try {
          ReflectionUtils.makeAccessible(method);
          method.invoke(bean, arguments);
        } catch (InvocationTargetException ex) {
          throw ex.getTargetException();
        }
      }
    }

    private Object[] resolveCachedArguments(String beanName) {
      if (this.cachedMethodArguments == null) {
        return null;
      }
      Object[] arguments = new Object[this.cachedMethodArguments.length];
      for (int i = 0; i < arguments.length; i++) {
        arguments[i] = resolvedCachedArgument(beanName, this.cachedMethodArguments[i]);
      }
      return arguments;
    }
  }


  /**
   * DependencyDescriptor variant with a pre-resolved target bean name.
   */
  @SuppressWarnings("serial")
  private static class ShortcutDependencyDescriptor extends DependencyDescriptor {

    private final String shortcut;

    private final Class<?> requiredType;

    public ShortcutDependencyDescriptor(DependencyDescriptor original, String shortcut, Class<?> requiredType) {
      super(original);
      this.shortcut = shortcut;
      this.requiredType = requiredType;
    }

    @Override
    public Object resolveShortcut(BeanFactory beanFactory) {
      return resolveCandidate(this.shortcut, this.requiredType, beanFactory);
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }


  @Override
  public void destroy() throws Exception {
    // do nothing
  }


  @Override
  public void setBeanClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

}

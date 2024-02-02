/*     */ package cn.hutool.extra.spring;
/*     */ 
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.lang.TypeReference;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import org.springframework.beans.BeansException;
/*     */ import org.springframework.beans.factory.ListableBeanFactory;
/*     */ import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
/*     */ import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
/*     */ import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
/*     */ import org.springframework.context.ApplicationContext;
/*     */ import org.springframework.context.ApplicationContextAware;
/*     */ import org.springframework.context.ApplicationEvent;
/*     */ import org.springframework.context.ConfigurableApplicationContext;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.stereotype.Component;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Component
/*     */ public class SpringUtil
/*     */   implements BeanFactoryPostProcessor, ApplicationContextAware
/*     */ {
/*     */   private static ConfigurableListableBeanFactory beanFactory;
/*     */   private static ApplicationContext applicationContext;
/*     */   
/*     */   public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
/*  49 */     SpringUtil.beanFactory = beanFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setApplicationContext(ApplicationContext applicationContext) {
/*  55 */     SpringUtil.applicationContext = applicationContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ApplicationContext getApplicationContext() {
/*  64 */     return applicationContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ListableBeanFactory getBeanFactory() {
/*  74 */     return (null == beanFactory) ? (ListableBeanFactory)applicationContext : (ListableBeanFactory)beanFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ConfigurableListableBeanFactory getConfigurableBeanFactory() throws UtilException {
/*     */     ConfigurableListableBeanFactory factory;
/*  86 */     if (null != beanFactory) {
/*  87 */       factory = beanFactory;
/*  88 */     } else if (applicationContext instanceof ConfigurableApplicationContext) {
/*  89 */       factory = ((ConfigurableApplicationContext)applicationContext).getBeanFactory();
/*     */     } else {
/*  91 */       throw new UtilException("No ConfigurableListableBeanFactory from context!");
/*     */     } 
/*  93 */     return factory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T getBean(String name) {
/* 107 */     return (T)getBeanFactory().getBean(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T getBean(Class<T> clazz) {
/* 118 */     return (T)getBeanFactory().getBean(clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T getBean(String name, Class<T> clazz) {
/* 130 */     return (T)getBeanFactory().getBean(name, clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T getBean(TypeReference<T> reference) {
/* 143 */     ParameterizedType parameterizedType = (ParameterizedType)reference.getType();
/* 144 */     Class<T> rawType = (Class<T>)parameterizedType.getRawType();
/* 145 */     Class<?>[] genericTypes = (Class[])Arrays.<Type>stream(parameterizedType.getActualTypeArguments()).map(type -> (Class)type).toArray(x$0 -> new Class[x$0]);
/* 146 */     String[] beanNames = getBeanFactory().getBeanNamesForType(ResolvableType.forClassWithGenerics(rawType, genericTypes));
/* 147 */     return getBean(beanNames[0], rawType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Map<String, T> getBeansOfType(Class<T> type) {
/* 159 */     return getBeanFactory().getBeansOfType(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String[] getBeanNamesForType(Class<?> type) {
/* 170 */     return getBeanFactory().getBeanNamesForType(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getProperty(String key) {
/* 181 */     if (null == applicationContext) {
/* 182 */       return null;
/*     */     }
/* 184 */     return applicationContext.getEnvironment().getProperty(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getApplicationName() {
/* 194 */     return getProperty("spring.application.name");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String[] getActiveProfiles() {
/* 204 */     if (null == applicationContext) {
/* 205 */       return null;
/*     */     }
/* 207 */     return applicationContext.getEnvironment().getActiveProfiles();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getActiveProfile() {
/* 217 */     String[] activeProfiles = getActiveProfiles();
/* 218 */     return ArrayUtil.isNotEmpty((Object[])activeProfiles) ? activeProfiles[0] : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> void registerBean(String beanName, T bean) {
/* 235 */     ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
/* 236 */     factory.autowireBean(bean);
/* 237 */     factory.registerSingleton(beanName, bean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void unregisterBean(String beanName) {
/* 250 */     ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
/* 251 */     if (factory instanceof DefaultSingletonBeanRegistry) {
/* 252 */       DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry)factory;
/* 253 */       registry.destroySingleton(beanName);
/*     */     } else {
/* 255 */       throw new UtilException("Can not unregister bean, the factory is not a DefaultSingletonBeanRegistry!");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void publishEvent(ApplicationEvent event) {
/* 266 */     if (null != applicationContext) {
/* 267 */       applicationContext.publishEvent(event);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void publishEvent(Object event) {
/* 279 */     if (null != applicationContext)
/* 280 */       applicationContext.publishEvent(event); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\spring\SpringUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package org.jboss.logging;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.ServiceConfigurationError;
/*     */ import java.util.ServiceLoader;
/*     */ import java.util.logging.LogManager;
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
/*     */ final class LoggerProviders
/*     */ {
/*     */   static final String LOGGING_PROVIDER_KEY = "org.jboss.logging.provider";
/*  29 */   static final LoggerProvider PROVIDER = find();
/*     */   
/*     */   private static LoggerProvider find() {
/*  32 */     return findProvider();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static LoggerProvider findProvider() {
/*  39 */     ClassLoader cl = LoggerProviders.class.getClassLoader();
/*     */     
/*     */     try {
/*  42 */       String loggerProvider = SecurityActions.getSystemProperty("org.jboss.logging.provider");
/*  43 */       if (loggerProvider != null) {
/*  44 */         if ("jboss".equalsIgnoreCase(loggerProvider))
/*  45 */           return tryJBossLogManager(cl, "system property"); 
/*  46 */         if ("jdk".equalsIgnoreCase(loggerProvider))
/*  47 */           return tryJDK("system property"); 
/*  48 */         if ("log4j2".equalsIgnoreCase(loggerProvider))
/*  49 */           return tryLog4j2(cl, "system property"); 
/*  50 */         if ("log4j".equalsIgnoreCase(loggerProvider))
/*  51 */           return tryLog4j(cl, "system property"); 
/*  52 */         if ("slf4j".equalsIgnoreCase(loggerProvider)) {
/*  53 */           return trySlf4j("system property");
/*     */         }
/*     */       } 
/*  56 */     } catch (Throwable throwable) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  62 */       ServiceLoader<LoggerProvider> loader = ServiceLoader.load(LoggerProvider.class, cl);
/*  63 */       Iterator<LoggerProvider> iter = loader.iterator();
/*     */       while (true) {
/*     */         try {
/*  66 */           if (!iter.hasNext())
/*  67 */             break;  LoggerProvider provider = iter.next();
/*     */           
/*  69 */           logProvider(provider, "service loader");
/*  70 */           return provider;
/*  71 */         } catch (ServiceConfigurationError serviceConfigurationError) {}
/*     */       } 
/*  73 */     } catch (Throwable throwable) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  79 */       return tryJBossLogManager(cl, null);
/*  80 */     } catch (Throwable throwable) {
/*     */ 
/*     */       
/*     */       try {
/*     */         
/*  85 */         return tryLog4j2(cl, null);
/*  86 */       } catch (Throwable throwable1) {
/*     */ 
/*     */         
/*     */         try {
/*  90 */           return tryLog4j(cl, null);
/*  91 */         } catch (Throwable throwable2) {
/*     */ 
/*     */           
/*     */           try {
/*     */             
/*  96 */             Class.forName("ch.qos.logback.classic.Logger", false, cl);
/*  97 */             return trySlf4j(null);
/*  98 */           } catch (Throwable throwable3) {
/*     */ 
/*     */             
/* 101 */             return tryJDK(null);
/*     */           } 
/*     */         } 
/*     */       } 
/* 105 */     }  } private static JDKLoggerProvider tryJDK(String via) { JDKLoggerProvider provider = new JDKLoggerProvider();
/* 106 */     logProvider(provider, via);
/* 107 */     return provider; }
/*     */ 
/*     */   
/*     */   private static LoggerProvider trySlf4j(String via) {
/* 111 */     LoggerProvider provider = new Slf4jLoggerProvider();
/* 112 */     logProvider(provider, via);
/* 113 */     return provider;
/*     */   }
/*     */ 
/*     */   
/*     */   private static LoggerProvider tryLog4j2(ClassLoader cl, String via) throws ClassNotFoundException {
/* 118 */     Class.forName("org.apache.logging.log4j.Logger", true, cl);
/* 119 */     Class.forName("org.apache.logging.log4j.LogManager", true, cl);
/* 120 */     Class.forName("org.apache.logging.log4j.spi.AbstractLogger", true, cl);
/* 121 */     LoggerProvider provider = new Log4j2LoggerProvider();
/*     */     
/* 123 */     logProvider(provider, via);
/* 124 */     return provider;
/*     */   }
/*     */   
/*     */   private static LoggerProvider tryLog4j(ClassLoader cl, String via) throws ClassNotFoundException {
/* 128 */     Class.forName("org.apache.log4j.LogManager", true, cl);
/*     */ 
/*     */     
/* 131 */     Class.forName("org.apache.log4j.config.PropertySetter", true, cl);
/* 132 */     LoggerProvider provider = new Log4jLoggerProvider();
/* 133 */     logProvider(provider, via);
/* 134 */     return provider;
/*     */   }
/*     */   
/*     */   private static LoggerProvider tryJBossLogManager(ClassLoader cl, String via) throws ClassNotFoundException {
/* 138 */     Class<? extends LogManager> logManagerClass = (Class)LogManager.getLogManager().getClass();
/* 139 */     if (logManagerClass == Class.forName("org.jboss.logmanager.LogManager", false, cl) && 
/* 140 */       Class.forName("org.jboss.logmanager.Logger$AttachmentKey", true, cl).getClassLoader() == logManagerClass.getClassLoader()) {
/* 141 */       LoggerProvider provider = new JBossLogManagerProvider();
/* 142 */       logProvider(provider, via);
/* 143 */       return provider;
/*     */     } 
/* 145 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   
/*     */   private static void logProvider(LoggerProvider provider, String via) {
/* 150 */     Logger logger = provider.getLogger(LoggerProviders.class.getPackage().getName());
/* 151 */     if (via == null) {
/* 152 */       logger.debugf("Logging Provider: %s", provider.getClass().getName());
/*     */     } else {
/* 154 */       logger.debugf("Logging Provider: %s found via %s", provider.getClass().getName(), via);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\LoggerProviders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package org.jboss.logging;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.logging.LogManager;

final class LoggerProviders {
   static final String LOGGING_PROVIDER_KEY = "org.jboss.logging.provider";
   static final LoggerProvider PROVIDER = find();

   private static LoggerProvider find() {
      return findProvider();
   }

   private static LoggerProvider findProvider() {
      ClassLoader cl = LoggerProviders.class.getClassLoader();

      try {
         String loggerProvider = SecurityActions.getSystemProperty("org.jboss.logging.provider");
         if (loggerProvider != null) {
            if ("jboss".equalsIgnoreCase(loggerProvider)) {
               return tryJBossLogManager(cl, "system property");
            }

            if ("jdk".equalsIgnoreCase(loggerProvider)) {
               return tryJDK("system property");
            }

            if ("log4j2".equalsIgnoreCase(loggerProvider)) {
               return tryLog4j2(cl, "system property");
            }

            if ("log4j".equalsIgnoreCase(loggerProvider)) {
               return tryLog4j(cl, "system property");
            }

            if ("slf4j".equalsIgnoreCase(loggerProvider)) {
               return trySlf4j("system property");
            }
         }
      } catch (Throwable var8) {
      }

      try {
         ServiceLoader<LoggerProvider> loader = ServiceLoader.load(LoggerProvider.class, cl);
         Iterator<LoggerProvider> iter = loader.iterator();

         while(true) {
            try {
               if (iter.hasNext()) {
                  LoggerProvider provider = (LoggerProvider)iter.next();
                  logProvider(provider, "service loader");
                  return provider;
               }
               break;
            } catch (ServiceConfigurationError var9) {
            }
         }
      } catch (Throwable var10) {
      }

      try {
         return tryJBossLogManager(cl, (String)null);
      } catch (Throwable var7) {
         try {
            return tryLog4j2(cl, (String)null);
         } catch (Throwable var6) {
            try {
               return tryLog4j(cl, (String)null);
            } catch (Throwable var5) {
               try {
                  Class.forName("ch.qos.logback.classic.Logger", false, cl);
                  return trySlf4j((String)null);
               } catch (Throwable var4) {
                  return tryJDK((String)null);
               }
            }
         }
      }
   }

   private static JDKLoggerProvider tryJDK(String via) {
      JDKLoggerProvider provider = new JDKLoggerProvider();
      logProvider(provider, via);
      return provider;
   }

   private static LoggerProvider trySlf4j(String via) {
      LoggerProvider provider = new Slf4jLoggerProvider();
      logProvider(provider, via);
      return provider;
   }

   private static LoggerProvider tryLog4j2(ClassLoader cl, String via) throws ClassNotFoundException {
      Class.forName("org.apache.logging.log4j.Logger", true, cl);
      Class.forName("org.apache.logging.log4j.LogManager", true, cl);
      Class.forName("org.apache.logging.log4j.spi.AbstractLogger", true, cl);
      LoggerProvider provider = new Log4j2LoggerProvider();
      logProvider(provider, via);
      return provider;
   }

   private static LoggerProvider tryLog4j(ClassLoader cl, String via) throws ClassNotFoundException {
      Class.forName("org.apache.log4j.LogManager", true, cl);
      Class.forName("org.apache.log4j.config.PropertySetter", true, cl);
      LoggerProvider provider = new Log4jLoggerProvider();
      logProvider(provider, via);
      return provider;
   }

   private static LoggerProvider tryJBossLogManager(ClassLoader cl, String via) throws ClassNotFoundException {
      Class<? extends LogManager> logManagerClass = LogManager.getLogManager().getClass();
      if (logManagerClass == Class.forName("org.jboss.logmanager.LogManager", false, cl) && Class.forName("org.jboss.logmanager.Logger$AttachmentKey", true, cl).getClassLoader() == logManagerClass.getClassLoader()) {
         LoggerProvider provider = new JBossLogManagerProvider();
         logProvider(provider, via);
         return provider;
      } else {
         throw new IllegalStateException();
      }
   }

   private static void logProvider(LoggerProvider provider, String via) {
      Logger logger = provider.getLogger(LoggerProviders.class.getPackage().getName());
      if (via == null) {
         logger.debugf("Logging Provider: %s", (Object)provider.getClass().getName());
      } else {
         logger.debugf((String)"Logging Provider: %s found via %s", (Object)provider.getClass().getName(), (Object)via);
      }

   }

   private LoggerProviders() {
   }
}

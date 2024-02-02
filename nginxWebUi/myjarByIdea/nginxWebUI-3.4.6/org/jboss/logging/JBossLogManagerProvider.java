package org.jboss.logging;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jboss.logmanager.LogContext;

final class JBossLogManagerProvider implements LoggerProvider {
   private static final org.jboss.logmanager.Logger.AttachmentKey<Logger> KEY = new org.jboss.logmanager.Logger.AttachmentKey();
   private static final org.jboss.logmanager.Logger.AttachmentKey<ConcurrentMap<String, Logger>> LEGACY_KEY = new org.jboss.logmanager.Logger.AttachmentKey();

   public Logger getLogger(final String name) {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         return (Logger)AccessController.doPrivileged(new PrivilegedAction<Logger>() {
            public Logger run() {
               try {
                  return JBossLogManagerProvider.doGetLogger(name);
               } catch (NoSuchMethodError var2) {
                  return JBossLogManagerProvider.doLegacyGetLogger(name);
               }
            }
         });
      } else {
         try {
            return doGetLogger(name);
         } catch (NoSuchMethodError var4) {
            return doLegacyGetLogger(name);
         }
      }
   }

   private static Logger doLegacyGetLogger(String name) {
      org.jboss.logmanager.Logger lmLogger = LogContext.getLogContext().getLogger("");
      ConcurrentMap<String, Logger> loggers = (ConcurrentMap)lmLogger.getAttachment(LEGACY_KEY);
      if (loggers == null) {
         loggers = new ConcurrentHashMap();
         ConcurrentMap<String, Logger> appearing = (ConcurrentMap)lmLogger.attachIfAbsent(LEGACY_KEY, loggers);
         if (appearing != null) {
            loggers = appearing;
         }
      }

      Logger l = (Logger)((ConcurrentMap)loggers).get(name);
      if (l != null) {
         return l;
      } else {
         org.jboss.logmanager.Logger logger = org.jboss.logmanager.Logger.getLogger(name);
         Logger l = new JBossLogManagerLogger(name, logger);
         Logger appearing = (Logger)((ConcurrentMap)loggers).putIfAbsent(name, l);
         return (Logger)(appearing == null ? l : appearing);
      }
   }

   private static Logger doGetLogger(String name) {
      Logger l = (Logger)LogContext.getLogContext().getAttachment(name, KEY);
      if (l != null) {
         return l;
      } else {
         org.jboss.logmanager.Logger logger = org.jboss.logmanager.Logger.getLogger(name);
         Logger l = new JBossLogManagerLogger(name, logger);
         Logger a = (Logger)logger.attachIfAbsent(KEY, l);
         return (Logger)(a == null ? l : a);
      }
   }

   public void clearMdc() {
      org.jboss.logmanager.MDC.clear();
   }

   public Object putMdc(String key, Object value) {
      return org.jboss.logmanager.MDC.put(key, String.valueOf(value));
   }

   public Object getMdc(String key) {
      return org.jboss.logmanager.MDC.get(key);
   }

   public void removeMdc(String key) {
      org.jboss.logmanager.MDC.remove(key);
   }

   public Map<String, Object> getMdcMap() {
      return org.jboss.logmanager.MDC.copy();
   }

   public void clearNdc() {
      org.jboss.logmanager.NDC.clear();
   }

   public String getNdc() {
      return org.jboss.logmanager.NDC.get();
   }

   public int getNdcDepth() {
      return org.jboss.logmanager.NDC.getDepth();
   }

   public String popNdc() {
      return org.jboss.logmanager.NDC.pop();
   }

   public String peekNdc() {
      return org.jboss.logmanager.NDC.get();
   }

   public void pushNdc(String message) {
      org.jboss.logmanager.NDC.push(message);
   }

   public void setNdcMaxDepth(int maxDepth) {
      org.jboss.logmanager.NDC.trimTo(maxDepth);
   }
}

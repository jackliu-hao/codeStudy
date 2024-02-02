package org.jboss.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.text.MessageFormat;
import org.slf4j.spi.LocationAwareLogger;

final class Slf4jLocationAwareLogger extends Logger {
   private static final long serialVersionUID = 8685757928087758380L;
   private static final Object[] EMPTY = new Object[0];
   private static final boolean POST_1_6;
   private static final Method LOG_METHOD;
   private final LocationAwareLogger logger;

   Slf4jLocationAwareLogger(String name, LocationAwareLogger logger) {
      super(name);
      this.logger = logger;
   }

   public boolean isEnabled(Logger.Level level) {
      if (level != null) {
         switch (level) {
            case FATAL:
               return this.logger.isErrorEnabled();
            case ERROR:
               return this.logger.isErrorEnabled();
            case WARN:
               return this.logger.isWarnEnabled();
            case INFO:
               return this.logger.isInfoEnabled();
            case DEBUG:
               return this.logger.isDebugEnabled();
            case TRACE:
               return this.logger.isTraceEnabled();
         }
      }

      return true;
   }

   protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
      if (this.isEnabled(level)) {
         String text = parameters != null && parameters.length != 0 ? MessageFormat.format(String.valueOf(message), parameters) : String.valueOf(message);
         doLog(this.logger, loggerClassName, translate(level), text, thrown);
      }

   }

   protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
      if (this.isEnabled(level)) {
         String text = parameters == null ? String.format(format) : String.format(format, parameters);
         doLog(this.logger, loggerClassName, translate(level), text, thrown);
      }

   }

   private static void doLog(LocationAwareLogger logger, String className, int level, String text, Throwable thrown) {
      try {
         if (POST_1_6) {
            LOG_METHOD.invoke(logger, null, className, level, text, EMPTY, thrown);
         } else {
            LOG_METHOD.invoke(logger, null, className, level, text, thrown);
         }

      } catch (InvocationTargetException var10) {
         InvocationTargetException e = var10;

         try {
            throw e.getCause();
         } catch (RuntimeException var7) {
            throw var7;
         } catch (Error var8) {
            throw var8;
         } catch (Throwable var9) {
            throw new UndeclaredThrowableException(var9);
         }
      } catch (IllegalAccessException var11) {
         throw new IllegalAccessError(var11.getMessage());
      }
   }

   private static int translate(Logger.Level level) {
      if (level != null) {
         switch (level) {
            case FATAL:
            case ERROR:
               return 40;
            case WARN:
               return 30;
            case INFO:
               return 20;
            case DEBUG:
               return 10;
            case TRACE:
               return 0;
         }
      }

      return 0;
   }

   static {
      Method[] methods = LocationAwareLogger.class.getDeclaredMethods();
      Method logMethod = null;
      boolean post16 = false;
      Method[] var3 = methods;
      int var4 = methods.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Method method = var3[var5];
         if (method.getName().equals("log")) {
            logMethod = method;
            Class<?>[] parameterTypes = method.getParameterTypes();
            post16 = parameterTypes.length == 6;
         }
      }

      if (logMethod == null) {
         throw new NoSuchMethodError("Cannot find LocationAwareLogger.log() method");
      } else {
         POST_1_6 = post16;
         LOG_METHOD = logMethod;
      }
   }
}

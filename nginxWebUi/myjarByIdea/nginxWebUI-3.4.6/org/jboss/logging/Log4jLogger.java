package org.jboss.logging;

import java.text.MessageFormat;

final class Log4jLogger extends Logger {
   private static final long serialVersionUID = -5446154366955151335L;
   private final org.apache.log4j.Logger logger;

   Log4jLogger(String name) {
      super(name);
      this.logger = org.apache.log4j.Logger.getLogger(name);
   }

   public boolean isEnabled(Logger.Level level) {
      org.apache.log4j.Level l = translate(level);
      return this.logger.isEnabledFor(l) && l.isGreaterOrEqual(this.logger.getEffectiveLevel());
   }

   protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
      org.apache.log4j.Level translatedLevel = translate(level);
      if (this.logger.isEnabledFor(translatedLevel)) {
         try {
            this.logger.log(loggerClassName, translatedLevel, parameters != null && parameters.length != 0 ? MessageFormat.format(String.valueOf(message), parameters) : message, thrown);
         } catch (Throwable var8) {
         }
      }

   }

   protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
      org.apache.log4j.Level translatedLevel = translate(level);
      if (this.logger.isEnabledFor(translatedLevel)) {
         try {
            this.logger.log(loggerClassName, translatedLevel, parameters == null ? String.format(format) : String.format(format, parameters), thrown);
         } catch (Throwable var8) {
         }
      }

   }

   private static org.apache.log4j.Level translate(Logger.Level level) {
      if (level == Logger.Level.TRACE) {
         return org.apache.log4j.Level.TRACE;
      } else {
         return level == Logger.Level.DEBUG ? org.apache.log4j.Level.DEBUG : infoOrHigher(level);
      }
   }

   private static org.apache.log4j.Level infoOrHigher(Logger.Level level) {
      if (level == Logger.Level.INFO) {
         return org.apache.log4j.Level.INFO;
      } else if (level == Logger.Level.WARN) {
         return org.apache.log4j.Level.WARN;
      } else if (level == Logger.Level.ERROR) {
         return org.apache.log4j.Level.ERROR;
      } else {
         return level == Logger.Level.FATAL ? org.apache.log4j.Level.FATAL : org.apache.log4j.Level.ALL;
      }
   }
}

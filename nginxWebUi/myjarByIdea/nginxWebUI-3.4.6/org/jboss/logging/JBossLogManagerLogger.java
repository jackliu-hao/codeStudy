package org.jboss.logging;

import org.jboss.logmanager.ExtLogRecord.FormatStyle;

final class JBossLogManagerLogger extends Logger {
   private static final long serialVersionUID = 7429618317727584742L;
   private final org.jboss.logmanager.Logger logger;

   JBossLogManagerLogger(String name, org.jboss.logmanager.Logger logger) {
      super(name);
      this.logger = logger;
   }

   public boolean isEnabled(Logger.Level level) {
      return this.logger.isLoggable(translate(level));
   }

   protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
      java.util.logging.Level translatedLevel = translate(level);
      if (this.logger.isLoggable(translatedLevel)) {
         if (parameters == null) {
            this.logger.log(loggerClassName, translatedLevel, String.valueOf(message), thrown);
         } else {
            this.logger.log(loggerClassName, translatedLevel, String.valueOf(message), FormatStyle.MESSAGE_FORMAT, parameters, thrown);
         }
      }

   }

   protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
      if (parameters == null) {
         this.logger.log(loggerClassName, translate(level), format, thrown);
      } else {
         this.logger.log(loggerClassName, translate(level), format, FormatStyle.PRINTF, parameters, thrown);
      }

   }

   private static java.util.logging.Level translate(Logger.Level level) {
      if (level == Logger.Level.TRACE) {
         return org.jboss.logmanager.Level.TRACE;
      } else {
         return (java.util.logging.Level)(level == Logger.Level.DEBUG ? org.jboss.logmanager.Level.DEBUG : infoOrHigher(level));
      }
   }

   private static java.util.logging.Level infoOrHigher(Logger.Level level) {
      if (level == Logger.Level.INFO) {
         return org.jboss.logmanager.Level.INFO;
      } else if (level == Logger.Level.WARN) {
         return org.jboss.logmanager.Level.WARN;
      } else if (level == Logger.Level.ERROR) {
         return org.jboss.logmanager.Level.ERROR;
      } else {
         return (java.util.logging.Level)(level == Logger.Level.FATAL ? org.jboss.logmanager.Level.FATAL : org.jboss.logmanager.Level.ALL);
      }
   }
}

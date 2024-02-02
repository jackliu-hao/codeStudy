package org.jboss.logging;

import java.text.MessageFormat;

final class Slf4jLogger extends Logger {
   private static final long serialVersionUID = 8685757928087758380L;
   private final org.slf4j.Logger logger;

   Slf4jLogger(String name, org.slf4j.Logger logger) {
      super(name);
      this.logger = logger;
   }

   public boolean isEnabled(Logger.Level level) {
      if (level == Logger.Level.TRACE) {
         return this.logger.isTraceEnabled();
      } else {
         return level == Logger.Level.DEBUG ? this.logger.isDebugEnabled() : this.infoOrHigherEnabled(level);
      }
   }

   private boolean infoOrHigherEnabled(Logger.Level level) {
      if (level == Logger.Level.INFO) {
         return this.logger.isInfoEnabled();
      } else if (level == Logger.Level.WARN) {
         return this.logger.isWarnEnabled();
      } else {
         return level != Logger.Level.ERROR && level != Logger.Level.FATAL ? true : this.logger.isErrorEnabled();
      }
   }

   protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
      if (this.isEnabled(level)) {
         try {
            String text = parameters != null && parameters.length != 0 ? MessageFormat.format(String.valueOf(message), parameters) : String.valueOf(message);
            if (level == Logger.Level.INFO) {
               this.logger.info(text, thrown);
            } else if (level == Logger.Level.WARN) {
               this.logger.warn(text, thrown);
            } else if (level != Logger.Level.ERROR && level != Logger.Level.FATAL) {
               if (level == Logger.Level.DEBUG) {
                  this.logger.debug(text, thrown);
               } else if (level == Logger.Level.TRACE) {
                  this.logger.debug(text, thrown);
               }
            } else {
               this.logger.error(text, thrown);
            }
         } catch (Throwable var7) {
         }
      }

   }

   protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
      if (this.isEnabled(level)) {
         try {
            String text = parameters == null ? String.format(format) : String.format(format, parameters);
            if (level == Logger.Level.INFO) {
               this.logger.info(text, thrown);
            } else if (level == Logger.Level.WARN) {
               this.logger.warn(text, thrown);
            } else if (level != Logger.Level.ERROR && level != Logger.Level.FATAL) {
               if (level == Logger.Level.DEBUG) {
                  this.logger.debug(text, thrown);
               } else if (level == Logger.Level.TRACE) {
                  this.logger.debug(text, thrown);
               }
            } else {
               this.logger.error(text, thrown);
            }
         } catch (Throwable var7) {
         }
      }

   }
}

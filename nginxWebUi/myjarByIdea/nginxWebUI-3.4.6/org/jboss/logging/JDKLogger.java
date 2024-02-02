package org.jboss.logging;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

final class JDKLogger extends Logger {
   private static final long serialVersionUID = 2563174097983721393L;
   private final transient java.util.logging.Logger logger;

   public JDKLogger(String name) {
      super(name);
      this.logger = java.util.logging.Logger.getLogger(name);
   }

   protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
      if (this.isEnabled(level)) {
         try {
            JBossLogRecord rec = new JBossLogRecord(translate(level), String.valueOf(message), loggerClassName);
            if (thrown != null) {
               rec.setThrown(thrown);
            }

            rec.setLoggerName(this.getName());
            rec.setParameters(parameters);
            rec.setResourceBundleName(this.logger.getResourceBundleName());
            rec.setResourceBundle(this.logger.getResourceBundle());
            this.logger.log(rec);
         } catch (Throwable var7) {
         }
      }

   }

   protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
      if (this.isEnabled(level)) {
         try {
            ResourceBundle resourceBundle = this.logger.getResourceBundle();
            if (resourceBundle != null) {
               try {
                  format = resourceBundle.getString(format);
               } catch (MissingResourceException var9) {
               }
            }

            String msg = parameters == null ? String.format(format) : String.format(format, parameters);
            JBossLogRecord rec = new JBossLogRecord(translate(level), msg, loggerClassName);
            if (thrown != null) {
               rec.setThrown(thrown);
            }

            rec.setLoggerName(this.getName());
            rec.setResourceBundleName(this.logger.getResourceBundleName());
            rec.setResourceBundle((ResourceBundle)null);
            rec.setParameters((Object[])null);
            this.logger.log(rec);
         } catch (Throwable var10) {
         }
      }

   }

   private static java.util.logging.Level translate(Logger.Level level) {
      if (level == Logger.Level.TRACE) {
         return JDKLevel.TRACE;
      } else {
         return (java.util.logging.Level)(level == Logger.Level.DEBUG ? JDKLevel.DEBUG : infoOrHigher(level));
      }
   }

   private static java.util.logging.Level infoOrHigher(Logger.Level level) {
      if (level == Logger.Level.INFO) {
         return JDKLevel.INFO;
      } else if (level == Logger.Level.WARN) {
         return JDKLevel.WARN;
      } else if (level == Logger.Level.ERROR) {
         return JDKLevel.ERROR;
      } else {
         return (java.util.logging.Level)(level == Logger.Level.FATAL ? JDKLevel.FATAL : JDKLevel.ALL);
      }
   }

   public boolean isEnabled(Logger.Level level) {
      return this.logger.isLoggable(translate(level));
   }
}

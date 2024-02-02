package org.jboss.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.MessageFormatMessageFactory;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.apache.logging.log4j.spi.AbstractLogger;

final class Log4j2Logger extends Logger {
   private static final long serialVersionUID = -2507841068232627725L;
   private final AbstractLogger logger;
   private final MessageFormatMessageFactory messageFactory;

   Log4j2Logger(String name) {
      super(name);
      org.apache.logging.log4j.Logger logger = LogManager.getLogger(name);
      if (!(logger instanceof AbstractLogger)) {
         throw new LoggingException("The logger for [" + name + "] does not extend AbstractLogger. Actual logger: " + logger.getClass().getName());
      } else {
         this.logger = (AbstractLogger)logger;
         this.messageFactory = new MessageFormatMessageFactory();
      }
   }

   public boolean isEnabled(Logger.Level level) {
      return this.logger.isEnabled(translate(level));
   }

   protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
      org.apache.logging.log4j.Level translatedLevel = translate(level);
      if (this.logger.isEnabled(translatedLevel)) {
         try {
            this.logger.logMessage(loggerClassName, translatedLevel, (Marker)null, parameters != null && parameters.length != 0 ? this.messageFactory.newMessage(String.valueOf(message), parameters) : this.messageFactory.newMessage(message), thrown);
         } catch (Throwable var8) {
         }
      }

   }

   protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
      org.apache.logging.log4j.Level translatedLevel = translate(level);
      if (this.logger.isEnabled(translatedLevel)) {
         try {
            this.logger.logMessage(loggerClassName, translatedLevel, (Marker)null, new StringFormattedMessage(format, parameters), thrown);
         } catch (Throwable var8) {
         }
      }

   }

   private static org.apache.logging.log4j.Level translate(Logger.Level level) {
      if (level == Logger.Level.TRACE) {
         return org.apache.logging.log4j.Level.TRACE;
      } else {
         return level == Logger.Level.DEBUG ? org.apache.logging.log4j.Level.DEBUG : infoOrHigher(level);
      }
   }

   private static org.apache.logging.log4j.Level infoOrHigher(Logger.Level level) {
      if (level == Logger.Level.INFO) {
         return org.apache.logging.log4j.Level.INFO;
      } else if (level == Logger.Level.WARN) {
         return org.apache.logging.log4j.Level.WARN;
      } else if (level == Logger.Level.ERROR) {
         return org.apache.logging.log4j.Level.ERROR;
      } else {
         return level == Logger.Level.FATAL ? org.apache.logging.log4j.Level.FATAL : org.apache.logging.log4j.Level.ALL;
      }
   }
}

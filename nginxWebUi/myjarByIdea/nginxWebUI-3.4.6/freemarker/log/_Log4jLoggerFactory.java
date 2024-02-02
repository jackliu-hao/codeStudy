package freemarker.log;

import org.apache.log4j.Level;

public class _Log4jLoggerFactory implements LoggerFactory {
   public Logger getLogger(String category) {
      return new Log4jLogger(org.apache.log4j.Logger.getLogger(category));
   }

   private static class Log4jLogger extends Logger {
      private final org.apache.log4j.Logger logger;

      Log4jLogger(org.apache.log4j.Logger logger) {
         this.logger = logger;
      }

      public void debug(String message) {
         this.logger.debug(message);
      }

      public void debug(String message, Throwable t) {
         this.logger.debug(message, t);
      }

      public void error(String message) {
         this.logger.error(message);
      }

      public void error(String message, Throwable t) {
         this.logger.error(message, t);
      }

      public void info(String message) {
         this.logger.info(message);
      }

      public void info(String message, Throwable t) {
         this.logger.info(message, t);
      }

      public void warn(String message) {
         this.logger.warn(message);
      }

      public void warn(String message, Throwable t) {
         this.logger.warn(message, t);
      }

      public boolean isDebugEnabled() {
         return this.logger.isDebugEnabled();
      }

      public boolean isInfoEnabled() {
         return this.logger.isInfoEnabled();
      }

      public boolean isWarnEnabled() {
         return this.logger.isEnabledFor(Level.WARN);
      }

      public boolean isErrorEnabled() {
         return this.logger.isEnabledFor(Level.ERROR);
      }

      public boolean isFatalEnabled() {
         return this.logger.isEnabledFor(Level.FATAL);
      }
   }
}

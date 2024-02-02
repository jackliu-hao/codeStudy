package freemarker.log;

import java.util.logging.Level;

public class _JULLoggerFactory implements LoggerFactory {
   public Logger getLogger(String category) {
      return new JULLogger(java.util.logging.Logger.getLogger(category));
   }

   private static class JULLogger extends Logger {
      private final java.util.logging.Logger logger;

      JULLogger(java.util.logging.Logger logger) {
         this.logger = logger;
      }

      public void debug(String message) {
         this.logger.log(Level.FINE, message);
      }

      public void debug(String message, Throwable t) {
         this.logger.log(Level.FINE, message, t);
      }

      public void error(String message) {
         this.logger.log(Level.SEVERE, message);
      }

      public void error(String message, Throwable t) {
         this.logger.log(Level.SEVERE, message, t);
      }

      public void info(String message) {
         this.logger.log(Level.INFO, message);
      }

      public void info(String message, Throwable t) {
         this.logger.log(Level.INFO, message, t);
      }

      public void warn(String message) {
         this.logger.log(Level.WARNING, message);
      }

      public void warn(String message, Throwable t) {
         this.logger.log(Level.WARNING, message, t);
      }

      public boolean isDebugEnabled() {
         return this.logger.isLoggable(Level.FINE);
      }

      public boolean isInfoEnabled() {
         return this.logger.isLoggable(Level.INFO);
      }

      public boolean isWarnEnabled() {
         return this.logger.isLoggable(Level.WARNING);
      }

      public boolean isErrorEnabled() {
         return this.logger.isLoggable(Level.SEVERE);
      }

      public boolean isFatalEnabled() {
         return this.logger.isLoggable(Level.SEVERE);
      }
   }
}

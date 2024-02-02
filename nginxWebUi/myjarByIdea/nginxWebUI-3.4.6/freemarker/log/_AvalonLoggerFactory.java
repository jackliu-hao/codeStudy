package freemarker.log;

import org.apache.log.Hierarchy;

public class _AvalonLoggerFactory implements LoggerFactory {
   public Logger getLogger(String category) {
      return new AvalonLogger(Hierarchy.getDefaultHierarchy().getLoggerFor(category));
   }

   private static class AvalonLogger extends Logger {
      private final org.apache.log.Logger logger;

      AvalonLogger(org.apache.log.Logger logger) {
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
         return this.logger.isWarnEnabled();
      }

      public boolean isErrorEnabled() {
         return this.logger.isErrorEnabled();
      }

      public boolean isFatalEnabled() {
         return this.logger.isFatalErrorEnabled();
      }
   }
}

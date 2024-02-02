package freemarker.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** @deprecated */
@Deprecated
public class CommonsLoggingLoggerFactory implements LoggerFactory {
   public Logger getLogger(String category) {
      return new CommonsLoggingLogger(LogFactory.getLog(category));
   }

   private static class CommonsLoggingLogger extends Logger {
      private final Log logger;

      CommonsLoggingLogger(Log logger) {
         this.logger = logger;
      }

      public void debug(String message) {
         this.logger.debug(message);
      }

      public void debug(String message, Throwable t) {
         this.logger.debug(message, t);
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

      public void error(String message) {
         this.logger.error(message);
      }

      public void error(String message, Throwable t) {
         this.logger.error(message, t);
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
         return this.logger.isFatalEnabled();
      }
   }
}

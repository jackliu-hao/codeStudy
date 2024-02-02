package freemarker.template;

import freemarker.core.Environment;
import freemarker.log.Logger;

class LoggingAttemptExceptionReporter implements AttemptExceptionReporter {
   private static final Logger LOG = Logger.getLogger("freemarker.runtime");
   private final boolean logAsWarn;

   public LoggingAttemptExceptionReporter(boolean logAsWarn) {
      this.logAsWarn = logAsWarn;
   }

   public void report(TemplateException te, Environment env) {
      String message = "Error executing FreeMarker template part in the #attempt block";
      if (!this.logAsWarn) {
         LOG.error(message, te);
      } else {
         LOG.warn(message, te);
      }

   }
}

package org.wildfly.common.ref;

import java.io.Serializable;
import java.util.Locale;
import org.jboss.logging.Logger;

public class Log_$logger implements Log, Serializable {
   private static final long serialVersionUID = 1L;
   private static final String FQCN = Log_$logger.class.getName();
   protected final Logger log;
   private static final Locale LOCALE;

   public Log_$logger(Logger log) {
      this.log = log;
   }

   protected Locale getLoggingLocale() {
      return LOCALE;
   }

   public final void reapFailed(Throwable cause) {
      this.log.logf(FQCN, Logger.Level.DEBUG, cause, this.reapFailed$str());
   }

   protected String reapFailed$str() {
      return "COM03000: Reaping a reference failed";
   }

   static {
      LOCALE = Locale.ROOT;
   }
}

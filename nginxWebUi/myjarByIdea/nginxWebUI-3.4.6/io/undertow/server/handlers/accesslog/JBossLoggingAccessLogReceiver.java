package io.undertow.server.handlers.accesslog;

import org.jboss.logging.Logger;

public class JBossLoggingAccessLogReceiver implements AccessLogReceiver {
   public static final String DEFAULT_CATEGORY = "io.undertow.accesslog";
   private final Logger logger;

   public JBossLoggingAccessLogReceiver(String category) {
      this.logger = Logger.getLogger(category);
   }

   public JBossLoggingAccessLogReceiver() {
      this.logger = Logger.getLogger("io.undertow.accesslog");
   }

   public void logMessage(String message) {
      this.logger.info(message);
   }
}

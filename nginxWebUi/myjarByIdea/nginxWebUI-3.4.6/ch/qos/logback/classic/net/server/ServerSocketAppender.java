package ch.qos.logback.classic.net.server;

import ch.qos.logback.classic.net.LoggingEventPreSerializationTransformer;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.net.server.AbstractServerSocketAppender;
import ch.qos.logback.core.spi.PreSerializationTransformer;

public class ServerSocketAppender extends AbstractServerSocketAppender<ILoggingEvent> {
   private static final PreSerializationTransformer<ILoggingEvent> pst = new LoggingEventPreSerializationTransformer();
   private boolean includeCallerData;

   protected void postProcessEvent(ILoggingEvent event) {
      if (this.isIncludeCallerData()) {
         event.getCallerData();
      }

   }

   protected PreSerializationTransformer<ILoggingEvent> getPST() {
      return pst;
   }

   public boolean isIncludeCallerData() {
      return this.includeCallerData;
   }

   public void setIncludeCallerData(boolean includeCallerData) {
      this.includeCallerData = includeCallerData;
   }
}

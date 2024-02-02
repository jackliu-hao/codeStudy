package io.undertow.websockets.core;

import io.undertow.websockets.WebSocketExtension;
import java.io.Serializable;
import java.util.Locale;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;

public class WebSocketLogger_$logger extends DelegatingBasicLogger implements WebSocketLogger, BasicLogger, Serializable {
   private static final long serialVersionUID = 1L;
   private static final String FQCN = WebSocketLogger_$logger.class.getName();
   private static final Locale LOCALE;

   public WebSocketLogger_$logger(Logger log) {
      super(log);
   }

   protected Locale getLoggingLocale() {
      return LOCALE;
   }

   public final void decodingFrameWithOpCode(int opCode) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.decodingFrameWithOpCode$str(), (Object)opCode);
   }

   protected String decodingFrameWithOpCode$str() {
      return "UT025003: Decoding WebSocket Frame with opCode %s";
   }

   public final void unhandledErrorInAnnotatedEndpoint(Object instance, Throwable thr) {
      super.log.logf(FQCN, Logger.Level.ERROR, thr, this.unhandledErrorInAnnotatedEndpoint$str(), instance);
   }

   protected String unhandledErrorInAnnotatedEndpoint$str() {
      return "UT025007: Unhandled exception for annotated endpoint %s";
   }

   public final void incorrectExtensionParameter(WebSocketExtension.Parameter param) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)null, (String)this.incorrectExtensionParameter$str(), (Object)param);
   }

   protected String incorrectExtensionParameter$str() {
      return "UT025008: Incorrect parameter %s for extension";
   }

   static {
      LOCALE = Locale.ROOT;
   }
}

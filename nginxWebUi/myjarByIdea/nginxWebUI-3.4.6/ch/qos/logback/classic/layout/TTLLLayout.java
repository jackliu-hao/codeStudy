package ch.qos.logback.classic.layout;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.util.CachingDateFormatter;

public class TTLLLayout extends LayoutBase<ILoggingEvent> {
   CachingDateFormatter cachingDateFormatter = new CachingDateFormatter("HH:mm:ss.SSS");
   ThrowableProxyConverter tpc = new ThrowableProxyConverter();

   public void start() {
      this.tpc.start();
      super.start();
   }

   public String doLayout(ILoggingEvent event) {
      if (!this.isStarted()) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder();
         long timestamp = event.getTimeStamp();
         sb.append(this.cachingDateFormatter.format(timestamp));
         sb.append(" [");
         sb.append(event.getThreadName());
         sb.append("] ");
         sb.append(event.getLevel().toString());
         sb.append(" ");
         sb.append(event.getLoggerName());
         sb.append(" - ");
         sb.append(event.getFormattedMessage());
         sb.append(CoreConstants.LINE_SEPARATOR);
         IThrowableProxy tp = event.getThrowableProxy();
         if (tp != null) {
            String stackTrace = this.tpc.convert(event);
            sb.append(stackTrace);
         }

         return sb.toString();
      }
   }
}

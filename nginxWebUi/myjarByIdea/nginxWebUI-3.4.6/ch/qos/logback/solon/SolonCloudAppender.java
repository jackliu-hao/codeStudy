package ch.qos.logback.solon;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;
import org.noear.solon.logging.AppenderHolder;
import org.noear.solon.logging.AppenderManager;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;

public class SolonCloudAppender extends AppenderBase<ILoggingEvent> {
   AppenderHolder appender;

   protected void append(ILoggingEvent e) {
      if (this.appender == null) {
         this.appender = AppenderManager.getInstance().get("cloud");
         if (this.appender == null) {
            return;
         }
      }

      Level level = Level.INFO;
      switch (e.getLevel().toInt()) {
         case 5000:
            level = Level.TRACE;
            break;
         case 10000:
            level = Level.DEBUG;
            break;
         case 30000:
            level = Level.WARN;
            break;
         case 40000:
            level = Level.ERROR;
      }

      String message = e.getFormattedMessage();
      IThrowableProxy throwableProxy = e.getThrowableProxy();
      if (throwableProxy != null) {
         String errorStr = ThrowableProxyUtil.asString(throwableProxy);
         if (message.contains("{}")) {
            message = message.replace("{}", errorStr);
         } else {
            message = message + "\n" + errorStr;
         }
      }

      LogEvent event = new LogEvent(e.getLoggerName(), level, e.getMDCPropertyMap(), message, e.getTimeStamp(), e.getThreadName(), (Throwable)null);
      this.appender.append(event);
   }
}

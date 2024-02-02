package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class ContextNameConverter extends ClassicConverter {
   public String convert(ILoggingEvent event) {
      return event.getLoggerContextVO().getName();
   }
}

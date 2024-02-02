package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class ThreadConverter extends ClassicConverter {
   public String convert(ILoggingEvent event) {
      return event.getThreadName();
   }
}

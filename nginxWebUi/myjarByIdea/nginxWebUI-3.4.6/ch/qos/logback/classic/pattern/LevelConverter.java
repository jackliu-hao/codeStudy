package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class LevelConverter extends ClassicConverter {
   public String convert(ILoggingEvent le) {
      return le.getLevel().toString();
   }
}

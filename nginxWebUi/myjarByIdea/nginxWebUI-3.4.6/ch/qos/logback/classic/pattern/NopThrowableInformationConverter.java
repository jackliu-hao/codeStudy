package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class NopThrowableInformationConverter extends ThrowableHandlingConverter {
   public String convert(ILoggingEvent event) {
      return "";
   }
}

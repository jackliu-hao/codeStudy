package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class ClassOfCallerConverter extends NamedConverter {
   protected String getFullyQualifiedName(ILoggingEvent event) {
      StackTraceElement[] cda = event.getCallerData();
      return cda != null && cda.length > 0 ? cda[0].getClassName() : "?";
   }
}

package ch.qos.logback.classic.pattern.color;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

public class HighlightingCompositeConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {
   protected String getForegroundColorCode(ILoggingEvent event) {
      Level level = event.getLevel();
      switch (level.toInt()) {
         case 20000:
            return "34";
         case 30000:
            return "31";
         case 40000:
            return "1;31";
         default:
            return "39";
      }
   }
}

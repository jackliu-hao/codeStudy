package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextVO;
import java.util.Map;

public final class PropertyConverter extends ClassicConverter {
   String key;

   public void start() {
      String optStr = this.getFirstOption();
      if (optStr != null) {
         this.key = optStr;
         super.start();
      }

   }

   public String getKey() {
      return this.key;
   }

   public String convert(ILoggingEvent event) {
      if (this.key == null) {
         return "Property_HAS_NO_KEY";
      } else {
         LoggerContextVO lcvo = event.getLoggerContextVO();
         Map<String, String> map = lcvo.getPropertyMap();
         String val = (String)map.get(this.key);
         return val != null ? val : System.getProperty(this.key);
      }
   }
}

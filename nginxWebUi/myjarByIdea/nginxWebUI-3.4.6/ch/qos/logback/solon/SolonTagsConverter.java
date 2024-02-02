package ch.qos.logback.solon;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Map;

public class SolonTagsConverter extends MessageConverter {
   public String convert(ILoggingEvent event) {
      Map<String, String> eData = event.getMDCPropertyMap();
      if (eData != null) {
         StringBuilder buf = new StringBuilder();
         eData.forEach((tag, val) -> {
            if (!"traceId".equals(tag)) {
               buf.append("[@").append(tag).append(":").append(val).append("]");
            }

         });
         return buf.toString();
      } else {
         return "";
      }
   }
}

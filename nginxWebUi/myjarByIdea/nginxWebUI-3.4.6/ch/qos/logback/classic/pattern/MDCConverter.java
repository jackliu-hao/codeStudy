package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Iterator;
import java.util.Map;

public class MDCConverter extends ClassicConverter {
   private String key;
   private String defaultValue = "";

   public void start() {
      String[] keyInfo = OptionHelper.extractDefaultReplacement(this.getFirstOption());
      this.key = keyInfo[0];
      if (keyInfo[1] != null) {
         this.defaultValue = keyInfo[1];
      }

      super.start();
   }

   public void stop() {
      this.key = null;
      super.stop();
   }

   public String convert(ILoggingEvent event) {
      Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
      if (mdcPropertyMap == null) {
         return this.defaultValue;
      } else if (this.key == null) {
         return this.outputMDCForAllKeys(mdcPropertyMap);
      } else {
         String value = (String)mdcPropertyMap.get(this.key);
         return value != null ? value : this.defaultValue;
      }
   }

   private String outputMDCForAllKeys(Map<String, String> mdcPropertyMap) {
      StringBuilder buf = new StringBuilder();
      boolean first = true;

      Map.Entry entry;
      for(Iterator var4 = mdcPropertyMap.entrySet().iterator(); var4.hasNext(); buf.append((String)entry.getKey()).append('=').append((String)entry.getValue())) {
         entry = (Map.Entry)var4.next();
         if (first) {
            first = false;
         } else {
            buf.append(", ");
         }
      }

      return buf.toString();
   }

   public String getKey() {
      return this.key;
   }
}

package ch.qos.logback.classic.sift;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AbstractDiscriminator;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Map;

public class MDCBasedDiscriminator extends AbstractDiscriminator<ILoggingEvent> {
   private String key;
   private String defaultValue;

   public String getDiscriminatingValue(ILoggingEvent event) {
      Map<String, String> mdcMap = event.getMDCPropertyMap();
      if (mdcMap == null) {
         return this.defaultValue;
      } else {
         String mdcValue = (String)mdcMap.get(this.key);
         return mdcValue == null ? this.defaultValue : mdcValue;
      }
   }

   public void start() {
      int errors = 0;
      if (OptionHelper.isEmpty(this.key)) {
         ++errors;
         this.addError("The \"Key\" property must be set");
      }

      if (OptionHelper.isEmpty(this.defaultValue)) {
         ++errors;
         this.addError("The \"DefaultValue\" property must be set");
      }

      if (errors == 0) {
         this.started = true;
      }

   }

   public String getKey() {
      return this.key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public String getDefaultValue() {
      return this.defaultValue;
   }

   public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
   }
}

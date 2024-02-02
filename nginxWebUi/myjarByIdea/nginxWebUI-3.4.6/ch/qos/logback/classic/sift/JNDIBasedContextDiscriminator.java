package ch.qos.logback.classic.sift;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.selector.ContextSelector;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import ch.qos.logback.core.sift.AbstractDiscriminator;

public class JNDIBasedContextDiscriminator extends AbstractDiscriminator<ILoggingEvent> {
   private static final String KEY = "contextName";
   private String defaultValue;

   public String getDiscriminatingValue(ILoggingEvent event) {
      ContextSelector selector = ContextSelectorStaticBinder.getSingleton().getContextSelector();
      if (selector == null) {
         return this.defaultValue;
      } else {
         LoggerContext lc = selector.getLoggerContext();
         return lc == null ? this.defaultValue : lc.getName();
      }
   }

   public String getKey() {
      return "contextName";
   }

   public void setKey(String key) {
      throw new UnsupportedOperationException("Key cannot be set. Using fixed key contextName");
   }

   public String getDefaultValue() {
      return this.defaultValue;
   }

   public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
   }
}

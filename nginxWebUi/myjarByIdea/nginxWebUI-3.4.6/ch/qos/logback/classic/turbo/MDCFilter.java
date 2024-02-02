package ch.qos.logback.classic.turbo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.MDC;
import org.slf4j.Marker;

public class MDCFilter extends MatchingFilter {
   String MDCKey;
   String value;

   public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
      if (this.MDCKey == null) {
         return FilterReply.NEUTRAL;
      } else {
         String value = MDC.get(this.MDCKey);
         return this.value.equals(value) ? this.onMatch : this.onMismatch;
      }
   }

   public void setValue(String value) {
      this.value = value;
   }

   public void setMDCKey(String MDCKey) {
      this.MDCKey = MDCKey;
   }
}

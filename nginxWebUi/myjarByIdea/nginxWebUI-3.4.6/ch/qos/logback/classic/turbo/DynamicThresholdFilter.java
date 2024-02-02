package ch.qos.logback.classic.turbo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.MDC;
import org.slf4j.Marker;

public class DynamicThresholdFilter extends TurboFilter {
   private Map<String, Level> valueLevelMap = new HashMap();
   private Level defaultThreshold;
   private String key;
   private FilterReply onHigherOrEqual;
   private FilterReply onLower;

   public DynamicThresholdFilter() {
      this.defaultThreshold = Level.ERROR;
      this.onHigherOrEqual = FilterReply.NEUTRAL;
      this.onLower = FilterReply.DENY;
   }

   public String getKey() {
      return this.key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public Level getDefaultThreshold() {
      return this.defaultThreshold;
   }

   public void setDefaultThreshold(Level defaultThreshold) {
      this.defaultThreshold = defaultThreshold;
   }

   public FilterReply getOnHigherOrEqual() {
      return this.onHigherOrEqual;
   }

   public void setOnHigherOrEqual(FilterReply onHigherOrEqual) {
      this.onHigherOrEqual = onHigherOrEqual;
   }

   public FilterReply getOnLower() {
      return this.onLower;
   }

   public void setOnLower(FilterReply onLower) {
      this.onLower = onLower;
   }

   public void addMDCValueLevelPair(MDCValueLevelPair mdcValueLevelPair) {
      if (this.valueLevelMap.containsKey(mdcValueLevelPair.getValue())) {
         this.addError(mdcValueLevelPair.getValue() + " has been already set");
      } else {
         this.valueLevelMap.put(mdcValueLevelPair.getValue(), mdcValueLevelPair.getLevel());
      }

   }

   public void start() {
      if (this.key == null) {
         this.addError("No key name was specified");
      }

      super.start();
   }

   public FilterReply decide(Marker marker, Logger logger, Level level, String s, Object[] objects, Throwable throwable) {
      String mdcValue = MDC.get(this.key);
      if (!this.isStarted()) {
         return FilterReply.NEUTRAL;
      } else {
         Level levelAssociatedWithMDCValue = null;
         if (mdcValue != null) {
            levelAssociatedWithMDCValue = (Level)this.valueLevelMap.get(mdcValue);
         }

         if (levelAssociatedWithMDCValue == null) {
            levelAssociatedWithMDCValue = this.defaultThreshold;
         }

         return level.isGreaterOrEqual(levelAssociatedWithMDCValue) ? this.onHigherOrEqual : this.onLower;
      }
   }
}

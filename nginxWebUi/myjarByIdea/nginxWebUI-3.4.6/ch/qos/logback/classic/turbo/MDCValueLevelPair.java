package ch.qos.logback.classic.turbo;

import ch.qos.logback.classic.Level;

public class MDCValueLevelPair {
   private String value;
   private Level level;

   public String getValue() {
      return this.value;
   }

   public void setValue(String name) {
      this.value = name;
   }

   public Level getLevel() {
      return this.level;
   }

   public void setLevel(Level level) {
      this.level = level;
   }
}

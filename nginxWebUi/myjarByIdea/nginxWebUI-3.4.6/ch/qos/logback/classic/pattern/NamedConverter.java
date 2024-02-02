package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

public abstract class NamedConverter extends ClassicConverter {
   Abbreviator abbreviator = null;

   protected abstract String getFullyQualifiedName(ILoggingEvent var1);

   public void start() {
      String optStr = this.getFirstOption();
      if (optStr != null) {
         try {
            int targetLen = Integer.parseInt(optStr);
            if (targetLen == 0) {
               this.abbreviator = new ClassNameOnlyAbbreviator();
            } else if (targetLen > 0) {
               this.abbreviator = new TargetLengthBasedClassNameAbbreviator(targetLen);
            }
         } catch (NumberFormatException var3) {
         }
      }

   }

   public String convert(ILoggingEvent event) {
      String fqn = this.getFullyQualifiedName(event);
      return this.abbreviator == null ? fqn : this.abbreviator.abbreviate(fqn);
   }
}

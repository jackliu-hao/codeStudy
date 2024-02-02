package ch.qos.logback.classic.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LevelToSyslogSeverity {
   public static int convert(ILoggingEvent event) {
      Level level = event.getLevel();
      switch (level.levelInt) {
         case 5000:
         case 10000:
            return 7;
         case 20000:
            return 6;
         case 30000:
            return 4;
         case 40000:
            return 3;
         default:
            throw new IllegalArgumentException("Level " + level + " is not a valid level for a printing method");
      }
   }
}

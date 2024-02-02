package ch.qos.logback.classic.jul;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JULHelper {
   public static final boolean isRegularNonRootLogger(Logger julLogger) {
      if (julLogger == null) {
         return false;
      } else {
         return !julLogger.getName().equals("");
      }
   }

   public static final boolean isRoot(Logger julLogger) {
      return julLogger == null ? false : julLogger.getName().equals("");
   }

   public static Level asJULLevel(ch.qos.logback.classic.Level lbLevel) {
      if (lbLevel == null) {
         throw new IllegalArgumentException("Unexpected level [null]");
      } else {
         switch (lbLevel.levelInt) {
            case Integer.MIN_VALUE:
               return Level.ALL;
            case 5000:
               return Level.FINEST;
            case 10000:
               return Level.FINE;
            case 20000:
               return Level.INFO;
            case 30000:
               return Level.WARNING;
            case 40000:
               return Level.SEVERE;
            case Integer.MAX_VALUE:
               return Level.OFF;
            default:
               throw new IllegalArgumentException("Unexpected level [" + lbLevel + "]");
         }
      }
   }

   public static String asJULLoggerName(String loggerName) {
      return "ROOT".equals(loggerName) ? "" : loggerName;
   }

   public static Logger asJULLogger(String loggerName) {
      String julLoggerName = asJULLoggerName(loggerName);
      return Logger.getLogger(julLoggerName);
   }

   public static Logger asJULLogger(ch.qos.logback.classic.Logger logger) {
      return asJULLogger(logger.getName());
   }
}

package ch.qos.logback.core.pattern.util;

public class RestrictedEscapeUtil implements IEscapeUtil {
   public void escape(String escapeChars, StringBuffer buf, char next, int pointer) {
      if (escapeChars.indexOf(next) >= 0) {
         buf.append(next);
      } else {
         buf.append("\\");
         buf.append(next);
      }

   }
}

package ch.qos.logback.core.pattern.util;

public class AsIsEscapeUtil implements IEscapeUtil {
   public void escape(String escapeChars, StringBuffer buf, char next, int pointer) {
      buf.append("\\");
      buf.append(next);
   }
}

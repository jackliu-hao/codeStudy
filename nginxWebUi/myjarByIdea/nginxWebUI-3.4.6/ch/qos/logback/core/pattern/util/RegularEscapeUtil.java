package ch.qos.logback.core.pattern.util;

public class RegularEscapeUtil implements IEscapeUtil {
   public void escape(String escapeChars, StringBuffer buf, char next, int pointer) {
      if (escapeChars.indexOf(next) >= 0) {
         buf.append(next);
      } else {
         switch (next) {
            case '\\':
               buf.append(next);
            case '_':
               break;
            case 'n':
               buf.append('\n');
               break;
            case 'r':
               buf.append('\r');
               break;
            case 't':
               buf.append('\t');
               break;
            default:
               String commaSeperatedEscapeChars = this.formatEscapeCharsForListing(escapeChars);
               throw new IllegalArgumentException("Illegal char '" + next + " at column " + pointer + ". Only \\\\, \\_" + commaSeperatedEscapeChars + ", \\t, \\n, \\r combinations are allowed as escape characters.");
         }
      }

   }

   String formatEscapeCharsForListing(String escapeChars) {
      StringBuilder commaSeperatedEscapeChars = new StringBuilder();

      for(int i = 0; i < escapeChars.length(); ++i) {
         commaSeperatedEscapeChars.append(", \\").append(escapeChars.charAt(i));
      }

      return commaSeperatedEscapeChars.toString();
   }

   public static String basicEscape(String s) {
      int len = s.length();
      StringBuilder sbuf = new StringBuilder(len);

      char c;
      for(int i = 0; i < len; sbuf.append(c)) {
         c = s.charAt(i++);
         if (c == '\\') {
            c = s.charAt(i++);
            if (c == 'n') {
               c = '\n';
            } else if (c == 'r') {
               c = '\r';
            } else if (c == 't') {
               c = '\t';
            } else if (c == 'f') {
               c = '\f';
            } else if (c == '\b') {
               c = '\b';
            } else if (c == '"') {
               c = '"';
            } else if (c == '\'') {
               c = '\'';
            } else if (c == '\\') {
               c = '\\';
            }
         }
      }

      return sbuf.toString();
   }
}

package io.undertow.util;

import io.undertow.UndertowMessages;
import io.undertow.server.handlers.Cookie;

public final class LegacyCookieSupport {
   static final boolean ALLOW_HTTP_SEPARATORS_IN_V0 = Boolean.getBoolean("io.undertow.legacy.cookie.ALLOW_HTTP_SEPARATORS_IN_V0");
   private static final boolean FWD_SLASH_IS_SEPARATOR = Boolean.getBoolean("io.undertow.legacy.cookie.FWD_SLASH_IS_SEPARATOR");
   static final boolean COMMA_IS_SEPARATOR = Boolean.getBoolean("io.undertow.legacy.cookie.COMMA_IS_SEPARATOR");
   private static final char[] V0_SEPARATORS = new char[]{',', ';', ' ', '\t'};
   private static final boolean[] V0_SEPARATOR_FLAGS = new boolean[128];
   private static final char[] HTTP_SEPARATORS;
   private static final boolean[] HTTP_SEPARATOR_FLAGS = new boolean[128];

   private static boolean isV0Separator(char c) {
      if ((c < ' ' || c >= 127) && c != '\t') {
         throw UndertowMessages.MESSAGES.invalidControlCharacter(Integer.toString(c));
      } else {
         return V0_SEPARATOR_FLAGS[c];
      }
   }

   private static boolean isV0Token(String value) {
      if (value == null) {
         return false;
      } else {
         int i = 0;
         int len = value.length();
         if (alreadyQuoted(value)) {
            ++i;
            --len;
         }

         while(i < len) {
            char c = value.charAt(i);
            if (isV0Separator(c)) {
               return true;
            }

            ++i;
         }

         return false;
      }
   }

   static boolean isHttpSeparator(char c) {
      if ((c < ' ' || c >= 127) && c != '\t') {
         throw UndertowMessages.MESSAGES.invalidControlCharacter(Integer.toString(c));
      } else {
         return HTTP_SEPARATOR_FLAGS[c];
      }
   }

   private static boolean isHttpToken(String value) {
      if (value == null) {
         return false;
      } else {
         int i = 0;
         int len = value.length();
         if (alreadyQuoted(value)) {
            ++i;
            --len;
         }

         while(i < len) {
            char c = value.charAt(i);
            if (isHttpSeparator(c)) {
               return true;
            }

            ++i;
         }

         return false;
      }
   }

   private static boolean alreadyQuoted(String value) {
      if (value != null && value.length() >= 2) {
         return value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"';
      } else {
         return false;
      }
   }

   public static void maybeQuote(StringBuilder buf, String value) {
      if (value != null && value.length() != 0) {
         if (alreadyQuoted(value)) {
            buf.append('"');
            buf.append(escapeDoubleQuotes(value, 1, value.length() - 1));
            buf.append('"');
         } else if ((!isHttpToken(value) || ALLOW_HTTP_SEPARATORS_IN_V0) && (!isV0Token(value) || !ALLOW_HTTP_SEPARATORS_IN_V0)) {
            buf.append(value);
         } else {
            buf.append('"');
            buf.append(escapeDoubleQuotes(value, 0, value.length()));
            buf.append('"');
         }
      } else {
         buf.append("\"\"");
      }

   }

   private static String escapeDoubleQuotes(String s, int beginIndex, int endIndex) {
      if (s != null && s.length() != 0 && s.indexOf(34) != -1) {
         StringBuilder b = new StringBuilder();

         for(int i = beginIndex; i < endIndex; ++i) {
            char c = s.charAt(i);
            if (c == '\\') {
               b.append(c);
               ++i;
               if (i >= endIndex) {
                  throw UndertowMessages.MESSAGES.invalidEscapeCharacter();
               }

               b.append(s.charAt(i));
            } else if (c == '"') {
               b.append('\\').append('"');
            } else {
               b.append(c);
            }
         }

         return b.toString();
      } else {
         return s;
      }
   }

   public static int adjustedCookieVersion(Cookie cookie) {
      int version = cookie.getVersion();
      String value = cookie.getValue();
      String path = cookie.getPath();
      String domain = cookie.getDomain();
      String comment = cookie.getComment();
      if (version == 0 && (!ALLOW_HTTP_SEPARATORS_IN_V0 && isHttpToken(value) || ALLOW_HTTP_SEPARATORS_IN_V0 && isV0Token(value))) {
         version = 1;
      }

      if (version == 0 && comment != null) {
         version = 1;
      }

      if (version == 0 && (!ALLOW_HTTP_SEPARATORS_IN_V0 && isHttpToken(path) || ALLOW_HTTP_SEPARATORS_IN_V0 && isV0Token(path))) {
         version = 1;
      }

      if (version == 0 && (!ALLOW_HTTP_SEPARATORS_IN_V0 && isHttpToken(domain) || ALLOW_HTTP_SEPARATORS_IN_V0 && isV0Token(domain))) {
         version = 1;
      }

      return version;
   }

   private LegacyCookieSupport() {
   }

   static {
      if (FWD_SLASH_IS_SEPARATOR) {
         HTTP_SEPARATORS = new char[]{'\t', ' ', '"', '(', ')', ',', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '{', '}'};
      } else {
         HTTP_SEPARATORS = new char[]{'\t', ' ', '"', '(', ')', ',', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '{', '}'};
      }

      for(int i = 0; i < 128; ++i) {
         V0_SEPARATOR_FLAGS[i] = false;
         HTTP_SEPARATOR_FLAGS[i] = false;
      }

      char[] var4 = V0_SEPARATORS;
      int var1 = var4.length;

      int var2;
      char HTTP_SEPARATOR;
      for(var2 = 0; var2 < var1; ++var2) {
         HTTP_SEPARATOR = var4[var2];
         V0_SEPARATOR_FLAGS[HTTP_SEPARATOR] = true;
      }

      var4 = HTTP_SEPARATORS;
      var1 = var4.length;

      for(var2 = 0; var2 < var1; ++var2) {
         HTTP_SEPARATOR = var4[var2];
         HTTP_SEPARATOR_FLAGS[HTTP_SEPARATOR] = true;
      }

   }
}

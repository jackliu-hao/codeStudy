package io.undertow.util;

import io.undertow.UndertowMessages;
import java.util.BitSet;

public final class Rfc6265CookieSupport {
   private static final BitSet domainValid = new BitSet(128);

   public static void validateCookieValue(String value) {
      int start = 0;
      int end = value.length();
      if (end > 1 && value.charAt(0) == '"' && value.charAt(end - 1) == '"') {
         start = 1;
         --end;
      }

      char[] chars = value.toCharArray();

      for(int i = start; i < end; ++i) {
         char c = chars[i];
         if (c < '!' || c == '"' || c == ',' || c == ';' || c == '\\' || c == 127) {
            throw UndertowMessages.MESSAGES.invalidCookieValue(Integer.toString(c));
         }
      }

   }

   public static void validateDomain(String domain) {
      int i = 0;
      int prev = true;
      int cur = -1;

      for(char[] chars = domain.toCharArray(); i < chars.length; ++i) {
         int prev = cur;
         cur = chars[i];
         if (!domainValid.get(cur)) {
            throw UndertowMessages.MESSAGES.invalidCookieDomain(domain);
         }

         if ((prev == 46 || prev == -1) && (cur == 46 || cur == 45)) {
            throw UndertowMessages.MESSAGES.invalidCookieDomain(domain);
         }

         if (prev == 45 && cur == 46) {
            throw UndertowMessages.MESSAGES.invalidCookieDomain(domain);
         }
      }

      if (cur == 46 || cur == 45) {
         throw UndertowMessages.MESSAGES.invalidCookieDomain(domain);
      }
   }

   public static void validatePath(String path) {
      char[] chars = path.toCharArray();

      for(int i = 0; i < chars.length; ++i) {
         char ch = chars[i];
         if (ch < ' ' || ch > '~' || ch == ';') {
            throw UndertowMessages.MESSAGES.invalidCookiePath(path);
         }
      }

   }

   static {
      char c;
      for(c = '0'; c <= '9'; ++c) {
         domainValid.set(c);
      }

      for(c = 'a'; c <= 'z'; ++c) {
         domainValid.set(c);
      }

      for(c = 'A'; c <= 'Z'; ++c) {
         domainValid.set(c);
      }

      domainValid.set(46);
      domainValid.set(45);
   }
}

package oshi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Util {
   private static final Logger LOG = LoggerFactory.getLogger(Util.class);

   private Util() {
   }

   public static void sleep(long ms) {
      try {
         LOG.trace((String)"Sleeping for {} ms", (Object)ms);
         Thread.sleep(ms);
      } catch (InterruptedException var3) {
         LOG.warn((String)"Interrupted while sleeping for {} ms: {}", (Object)ms, (Object)var3.getMessage());
         Thread.currentThread().interrupt();
      }

   }

   public static boolean wildcardMatch(String text, String pattern) {
      if (pattern.length() > 0 && pattern.charAt(0) == '^') {
         return !wildcardMatch(text, pattern.substring(1));
      } else {
         return text.matches(pattern.replace("?", ".?").replace("*", ".*?"));
      }
   }

   public static boolean isBlank(String s) {
      return s == null || s.isEmpty();
   }
}

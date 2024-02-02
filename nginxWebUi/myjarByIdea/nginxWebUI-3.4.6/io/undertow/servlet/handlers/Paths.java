package io.undertow.servlet.handlers;

import java.util.StringTokenizer;

final class Paths {
   private static final String META_INF = "META-INF";
   private static final String WEB_INF = "WEB-INF";

   private Paths() {
   }

   static boolean isForbidden(String path) {
      StringTokenizer st = new StringTokenizer(path, "/\\", false);

      String subPath;
      do {
         if (!st.hasMoreTokens()) {
            return false;
         }

         subPath = st.nextToken();
      } while(!"META-INF".equalsIgnoreCase(subPath) && !"WEB-INF".equalsIgnoreCase(subPath));

      return true;
   }
}

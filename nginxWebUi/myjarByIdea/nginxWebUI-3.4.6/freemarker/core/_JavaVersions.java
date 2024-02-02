package freemarker.core;

import freemarker.log.Logger;
import freemarker.template.Version;
import freemarker.template.utility.SecurityUtilities;

public final class _JavaVersions {
   private static final boolean IS_AT_LEAST_8;
   public static final _Java8 JAVA_8;

   private _JavaVersions() {
   }

   static {
      boolean result = false;
      String vStr = SecurityUtilities.getSystemProperty("java.version", (String)null);
      if (vStr != null) {
         try {
            Version v = new Version(vStr);
            result = v.getMajor() == 1 && v.getMinor() >= 8 || v.getMajor() > 1;
         } catch (Exception var6) {
         }
      } else {
         try {
            Class.forName("java.time.Instant");
            result = true;
         } catch (Exception var5) {
         }
      }

      IS_AT_LEAST_8 = result;
      _Java8 java8;
      if (IS_AT_LEAST_8) {
         try {
            java8 = (_Java8)Class.forName("freemarker.core._Java8Impl").getField("INSTANCE").get((Object)null);
         } catch (Exception var4) {
            Exception e = var4;

            try {
               Logger.getLogger("freemarker.runtime").error("Failed to access Java 8 functionality", e);
            } catch (Exception var3) {
            }

            java8 = null;
         }
      } else {
         java8 = null;
      }

      JAVA_8 = java8;
   }
}

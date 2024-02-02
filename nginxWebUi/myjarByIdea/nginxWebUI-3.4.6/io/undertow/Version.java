package io.undertow;

import java.io.InputStream;
import java.util.Properties;

public class Version {
   private static final String versionString;
   private static final String SERVER_NAME = "Undertow";
   private static final String fullVersionString;

   public static String getVersionString() {
      return versionString;
   }

   public static String getFullVersionString() {
      return fullVersionString;
   }

   static {
      String version = "Unknown";

      try {
         InputStream versionPropsStream = Version.class.getResourceAsStream("version.properties");
         Throwable var2 = null;

         try {
            Properties props = new Properties();
            props.load(versionPropsStream);
            version = props.getProperty("undertow.version");
         } catch (Throwable var12) {
            var2 = var12;
            throw var12;
         } finally {
            if (versionPropsStream != null) {
               if (var2 != null) {
                  try {
                     versionPropsStream.close();
                  } catch (Throwable var11) {
                     var2.addSuppressed(var11);
                  }
               } else {
                  versionPropsStream.close();
               }
            }

         }
      } catch (Exception var14) {
         var14.printStackTrace();
      }

      versionString = version;
      fullVersionString = "Undertow - " + versionString;
   }
}

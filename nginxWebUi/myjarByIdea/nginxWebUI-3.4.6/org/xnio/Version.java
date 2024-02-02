package org.xnio;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class Version {
   private static final String JAR_NAME;
   public static final String VERSION;

   private Version() {
   }

   public static void main(String[] args) {
      System.out.print(VERSION);
   }

   public static String getJarName() {
      return JAR_NAME;
   }

   public static String getVersionString() {
      return VERSION;
   }

   static {
      Properties versionProps = new Properties();
      String jarName = "(unknown)";
      String versionString = "(unknown)";

      try {
         InputStream stream = Version.class.getResourceAsStream("Version.properties");

         try {
            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

            try {
               versionProps.load(reader);
               jarName = versionProps.getProperty("jarName", jarName);
               versionString = versionProps.getProperty("version", versionString);
            } finally {
               try {
                  reader.close();
               } catch (Throwable var24) {
               }

            }
         } finally {
            try {
               stream.close();
            } catch (Throwable var23) {
            }

         }
      } catch (IOException var27) {
      }

      JAR_NAME = jarName;
      VERSION = versionString;
   }
}

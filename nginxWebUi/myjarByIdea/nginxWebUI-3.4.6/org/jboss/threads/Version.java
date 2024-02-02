package org.jboss.threads;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class Version {
   private static final String JAR_NAME;
   private static final String VERSION_STRING;

   private Version() {
   }

   public static String getJarName() {
      return JAR_NAME;
   }

   public static String getVersionString() {
      return VERSION_STRING;
   }

   public static void main(String[] args) {
      System.out.printf("JBoss Threads version %s\n", VERSION_STRING);
   }

   static {
      Properties versionProps = new Properties();
      String jarName = "(unknown)";
      String versionString = "(unknown)";

      try {
         InputStream stream = Version.class.getResourceAsStream("Version.properties");

         try {
            if (stream != null) {
               InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

               try {
                  versionProps.load(reader);
                  jarName = versionProps.getProperty("jarName", jarName);
                  versionString = versionProps.getProperty("version", versionString);
               } catch (Throwable var10) {
                  try {
                     reader.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }

                  throw var10;
               }

               reader.close();
            }
         } catch (Throwable var11) {
            if (stream != null) {
               try {
                  stream.close();
               } catch (Throwable var8) {
                  var11.addSuppressed(var8);
               }
            }

            throw var11;
         }

         if (stream != null) {
            stream.close();
         }
      } catch (IOException var12) {
      }

      JAR_NAME = jarName;
      VERSION_STRING = versionString;

      try {
         Messages.msg.version(versionString);
      } catch (Throwable var7) {
      }

   }
}

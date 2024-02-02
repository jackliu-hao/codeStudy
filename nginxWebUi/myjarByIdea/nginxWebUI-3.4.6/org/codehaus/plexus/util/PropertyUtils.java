package org.codehaus.plexus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class PropertyUtils {
   public static Properties loadProperties(URL url) {
      try {
         return loadProperties(url.openStream());
      } catch (Exception var2) {
         return null;
      }
   }

   public static Properties loadProperties(File file) {
      try {
         return loadProperties((InputStream)(new FileInputStream(file)));
      } catch (Exception var2) {
         return null;
      }
   }

   public static Properties loadProperties(InputStream is) {
      try {
         Properties properties = new Properties();
         if (is != null) {
            properties.load(is);
         }

         Properties var2 = properties;
         return var2;
      } catch (IOException var12) {
      } finally {
         try {
            if (is != null) {
               is.close();
            }
         } catch (IOException var11) {
         }

      }

      return null;
   }
}

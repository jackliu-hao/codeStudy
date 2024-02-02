package org.apache.commons.compress.utils;

import java.io.File;

public class FileNameUtils {
   public static String getExtension(String filename) {
      if (filename == null) {
         return null;
      } else {
         String name = (new File(filename)).getName();
         int extensionPosition = name.lastIndexOf(46);
         return extensionPosition < 0 ? "" : name.substring(extensionPosition + 1);
      }
   }

   public static String getBaseName(String filename) {
      if (filename == null) {
         return null;
      } else {
         String name = (new File(filename)).getName();
         int extensionPosition = name.lastIndexOf(46);
         return extensionPosition < 0 ? name : name.substring(0, extensionPosition);
      }
   }
}

package com.cym.utils;

public class ToolUtils {
   public static String handleConf(String path) {
      return path.replace("};", "  }");
   }

   public static String handlePath(String path) {
      return path.replace("\\", "/").replace("//", "/");
   }

   public static String endDir(String path) {
      if (!path.endsWith("/")) {
         path = path + "/";
      }

      return path;
   }
}

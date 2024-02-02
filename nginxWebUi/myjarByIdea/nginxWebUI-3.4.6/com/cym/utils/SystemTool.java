package com.cym.utils;

import cn.hutool.system.SystemUtil;

public class SystemTool {
   public static String getSystem() {
      if (SystemUtil.get("os.name").toLowerCase().contains("windows")) {
         return "Windows";
      } else {
         return SystemUtil.get("os.name").toLowerCase().contains("mac os") ? "Mac OS" : "Linux";
      }
   }

   public static Boolean isWindows() {
      return getSystem().equals("Windows");
   }

   public static Boolean isMacOS() {
      return getSystem().equals("Mac OS");
   }

   public static Boolean isLinux() {
      return getSystem().equals("Linux");
   }

   public static boolean hasRoot() {
      if (isLinux()) {
         String user = System.getProperties().getProperty("user.name");
         return "root".equals(user);
      } else {
         return true;
      }
   }
}

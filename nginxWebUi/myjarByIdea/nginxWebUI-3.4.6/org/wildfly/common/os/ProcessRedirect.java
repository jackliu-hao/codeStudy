package org.wildfly.common.os;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;

public final class ProcessRedirect {
   private ProcessRedirect() {
   }

   public static ProcessBuilder.Redirect discard() {
      return Redirect.to(new File(isWindows() ? "NUL" : "/dev/null"));
   }

   private static boolean isWindows() {
      SecurityManager sm = System.getSecurityManager();
      return (sm == null ? getOsName() : getOsNamePrivileged()).toLowerCase(Locale.ROOT).contains("windows");
   }

   private static String getOsNamePrivileged() {
      return (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return ProcessRedirect.getOsName();
         }
      });
   }

   private static String getOsName() {
      return System.getProperty("os.name", "unknown");
   }
}

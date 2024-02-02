package org.jboss.logging;

import java.security.AccessController;
import java.security.PrivilegedAction;

class SecurityActions {
   static String getSystemProperty(final String key) {
      return System.getSecurityManager() == null ? System.getProperty(key) : (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return System.getProperty(key);
         }
      });
   }

   static String getSystemProperty(final String key, final String dft) {
      return System.getSecurityManager() == null ? System.getProperty(key, dft) : (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return System.getProperty(key, dft);
         }
      });
   }
}

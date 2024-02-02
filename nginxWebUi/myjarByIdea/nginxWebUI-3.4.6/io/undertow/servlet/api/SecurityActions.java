package io.undertow.servlet.api;

import java.security.AccessController;
import java.security.PrivilegedAction;

final class SecurityActions {
   private SecurityActions() {
   }

   static String getSystemProperty(final String prop) {
      return System.getSecurityManager() == null ? System.getProperty(prop) : (String)AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            return System.getProperty(prop);
         }
      });
   }
}

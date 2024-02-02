package io.undertow.server.protocol.ajp;

import java.security.AccessController;
import java.security.PrivilegedAction;

final class SecurityActions {
   private SecurityActions() {
   }

   static String getSystemProperty(final String key) {
      return System.getSecurityManager() == null ? System.getProperty(key) : (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return System.getProperty(key);
         }
      });
   }
}

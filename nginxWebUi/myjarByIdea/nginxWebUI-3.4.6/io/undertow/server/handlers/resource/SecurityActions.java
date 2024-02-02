package io.undertow.server.handlers.resource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;

class SecurityActions {
   static Boolean isSymbolicLink(final Path file) {
      return System.getSecurityManager() == null ? Files.isSymbolicLink(file) : (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            return Files.isSymbolicLink(file);
         }
      });
   }
}

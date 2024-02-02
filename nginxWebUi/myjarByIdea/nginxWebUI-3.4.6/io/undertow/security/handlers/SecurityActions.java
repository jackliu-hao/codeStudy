package io.undertow.security.handlers;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;
import java.security.AccessController;
import java.security.PrivilegedAction;

class SecurityActions {
   static void setSecurityContext(final HttpServerExchange exchange, final SecurityContext securityContext) {
      if (System.getSecurityManager() == null) {
         exchange.setSecurityContext(securityContext);
      } else {
         AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               exchange.setSecurityContext(securityContext);
               return null;
            }
         });
      }

   }
}

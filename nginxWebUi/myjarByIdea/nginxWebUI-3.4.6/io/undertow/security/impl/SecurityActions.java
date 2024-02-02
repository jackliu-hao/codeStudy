package io.undertow.security.impl;

import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;
import java.security.AccessController;
import java.security.PrivilegedAction;

class SecurityActions {
   static SecurityContextImpl createSecurityContextImpl(final HttpServerExchange exchange, final AuthenticationMode authenticationMode, final IdentityManager identityManager) {
      return System.getSecurityManager() == null ? new SecurityContextImpl(exchange, authenticationMode, identityManager) : (SecurityContextImpl)AccessController.doPrivileged(new PrivilegedAction<SecurityContextImpl>() {
         public SecurityContextImpl run() {
            return new SecurityContextImpl(exchange, authenticationMode, identityManager);
         }
      });
   }
}

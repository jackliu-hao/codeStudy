package io.undertow.security.impl;

import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityContextFactory;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;

public class SecurityContextFactoryImpl implements SecurityContextFactory {
   public static final SecurityContextFactory INSTANCE = new SecurityContextFactoryImpl();

   private SecurityContextFactoryImpl() {
   }

   public SecurityContext createSecurityContext(HttpServerExchange exchange, AuthenticationMode mode, IdentityManager identityManager, String programmaticMechName) {
      SecurityContextImpl securityContext = SecurityActions.createSecurityContextImpl(exchange, mode, identityManager);
      if (programmaticMechName != null) {
         securityContext.setProgramaticMechName(programmaticMechName);
      }

      return securityContext;
   }
}

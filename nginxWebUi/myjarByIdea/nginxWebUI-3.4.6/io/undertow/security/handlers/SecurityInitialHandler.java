package io.undertow.security.handlers;

import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityContextFactory;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.SecurityContextFactoryImpl;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class SecurityInitialHandler extends AbstractSecurityContextAssociationHandler {
   private final AuthenticationMode authenticationMode;
   private final IdentityManager identityManager;
   private final String programaticMechName;
   private final SecurityContextFactory contextFactory;

   public SecurityInitialHandler(AuthenticationMode authenticationMode, IdentityManager identityManager, String programaticMechName, SecurityContextFactory contextFactory, HttpHandler next) {
      super(next);
      this.authenticationMode = authenticationMode;
      this.identityManager = identityManager;
      this.programaticMechName = programaticMechName;
      this.contextFactory = contextFactory;
   }

   public SecurityInitialHandler(AuthenticationMode authenticationMode, IdentityManager identityManager, String programaticMechName, HttpHandler next) {
      this(authenticationMode, identityManager, programaticMechName, SecurityContextFactoryImpl.INSTANCE, next);
   }

   public SecurityInitialHandler(AuthenticationMode authenticationMode, IdentityManager identityManager, HttpHandler next) {
      this(authenticationMode, identityManager, (String)null, SecurityContextFactoryImpl.INSTANCE, next);
   }

   public SecurityContext createSecurityContext(HttpServerExchange exchange) {
      return this.contextFactory.createSecurityContext(exchange, this.authenticationMode, this.identityManager, this.programaticMechName);
   }
}

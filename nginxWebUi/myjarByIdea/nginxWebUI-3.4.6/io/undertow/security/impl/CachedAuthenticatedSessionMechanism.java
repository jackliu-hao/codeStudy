package io.undertow.security.impl;

import io.undertow.security.api.AuthenticatedSessionManager;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;

public class CachedAuthenticatedSessionMechanism implements AuthenticationMechanism {
   private final IdentityManager identityManager;

   public CachedAuthenticatedSessionMechanism() {
      this((IdentityManager)null);
   }

   public CachedAuthenticatedSessionMechanism(IdentityManager identityManager) {
      this.identityManager = identityManager;
   }

   private IdentityManager getIdentityManager(SecurityContext securityContext) {
      return this.identityManager != null ? this.identityManager : securityContext.getIdentityManager();
   }

   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
      AuthenticatedSessionManager sessionManager = (AuthenticatedSessionManager)exchange.getAttachment(AuthenticatedSessionManager.ATTACHMENT_KEY);
      return sessionManager != null ? this.runCached(exchange, securityContext, sessionManager) : AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
   }

   public AuthenticationMechanism.AuthenticationMechanismOutcome runCached(HttpServerExchange exchange, SecurityContext securityContext, AuthenticatedSessionManager sessionManager) {
      AuthenticatedSessionManager.AuthenticatedSession authSession = sessionManager.lookupSession(exchange);
      if (authSession != null) {
         Account account = this.getIdentityManager(securityContext).verify(authSession.getAccount());
         if (account != null) {
            securityContext.authenticationComplete(account, authSession.getMechanism(), false);
            return AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
         } else {
            sessionManager.clearSession(exchange);
            return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
         }
      } else {
         return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
      }
   }

   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
      return AuthenticationMechanism.ChallengeResult.NOT_SENT;
   }
}

package io.undertow.security.impl;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.ExternalCredential;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.AttachmentKey;
import java.util.Map;

public class ExternalAuthenticationMechanism implements AuthenticationMechanism {
   public static final AuthenticationMechanismFactory FACTORY = new Factory();
   public static final String NAME = "EXTERNAL";
   private final String name;
   private final IdentityManager identityManager;
   public static final AttachmentKey<String> EXTERNAL_PRINCIPAL = AttachmentKey.create(String.class);
   public static final AttachmentKey<String> EXTERNAL_AUTHENTICATION_TYPE = AttachmentKey.create(String.class);

   public ExternalAuthenticationMechanism(String name, IdentityManager identityManager) {
      this.name = name;
      this.identityManager = identityManager;
   }

   public ExternalAuthenticationMechanism(String name) {
      this(name, (IdentityManager)null);
   }

   public ExternalAuthenticationMechanism() {
      this("EXTERNAL");
   }

   private IdentityManager getIdentityManager(SecurityContext securityContext) {
      return this.identityManager != null ? this.identityManager : securityContext.getIdentityManager();
   }

   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
      String principal = (String)exchange.getAttachment(EXTERNAL_PRINCIPAL);
      if (principal == null) {
         return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
      } else {
         Account account = this.getIdentityManager(securityContext).verify(principal, ExternalCredential.INSTANCE);
         if (account == null) {
            return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
         } else {
            String name = (String)exchange.getAttachment(EXTERNAL_AUTHENTICATION_TYPE);
            securityContext.authenticationComplete(account, name != null ? name : this.name, false);
            return AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
         }
      }
   }

   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
      return AuthenticationMechanism.ChallengeResult.NOT_SENT;
   }

   public static final class Factory implements AuthenticationMechanismFactory {
      /** @deprecated */
      @Deprecated
      public Factory(IdentityManager identityManager) {
      }

      public Factory() {
      }

      public AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
         return new ExternalAuthenticationMechanism(mechanismName, identityManager);
      }
   }
}

package io.undertow.security.impl;

import io.undertow.UndertowMessages;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.HttpString;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GenericHeaderAuthenticationMechanism implements AuthenticationMechanism {
   public static final AuthenticationMechanismFactory FACTORY = new Factory();
   public static final String NAME = "GENERIC_HEADER";
   public static final String IDENTITY_HEADER = "identity-header";
   public static final String SESSION_HEADER = "session-header";
   private final String mechanismName;
   private final List<HttpString> identityHeaders;
   private final List<String> sessionCookieNames;
   private final IdentityManager identityManager;

   public GenericHeaderAuthenticationMechanism(String mechanismName, List<HttpString> identityHeaders, List<String> sessionCookieNames, IdentityManager identityManager) {
      this.mechanismName = mechanismName;
      this.identityHeaders = identityHeaders;
      this.sessionCookieNames = sessionCookieNames;
      this.identityManager = identityManager;
   }

   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
      String principal = this.getPrincipal(exchange);
      if (principal == null) {
         return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
      } else {
         String session = this.getSession(exchange);
         if (session == null) {
            return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
         } else {
            Account account = this.identityManager.verify(principal, new PasswordCredential(session.toCharArray()));
            if (account == null) {
               securityContext.authenticationFailed(UndertowMessages.MESSAGES.authenticationFailed(principal), this.mechanismName);
               return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            } else {
               securityContext.authenticationComplete(account, this.mechanismName, false);
               return AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
            }
         }
      }
   }

   private String getSession(HttpServerExchange exchange) {
      Iterator var2 = this.sessionCookieNames.iterator();

      while(var2.hasNext()) {
         String header = (String)var2.next();
         Iterator var4 = exchange.requestCookies().iterator();

         while(var4.hasNext()) {
            Cookie cookie = (Cookie)var4.next();
            if (header.equals(cookie.getName())) {
               return cookie.getValue();
            }
         }
      }

      return null;
   }

   private String getPrincipal(HttpServerExchange exchange) {
      Iterator var2 = this.identityHeaders.iterator();

      String res;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         HttpString header = (HttpString)var2.next();
         res = exchange.getRequestHeaders().getFirst(header);
      } while(res == null);

      return res;
   }

   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
      return AuthenticationMechanism.ChallengeResult.NOT_SENT;
   }

   public static class Factory implements AuthenticationMechanismFactory {
      /** @deprecated */
      @Deprecated
      public Factory(IdentityManager identityManager) {
      }

      public Factory() {
      }

      public AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
         String identity = (String)properties.get("identity-header");
         if (identity == null) {
            throw UndertowMessages.MESSAGES.authenticationPropertyNotSet(mechanismName, "identity-header");
         } else {
            String session = (String)properties.get("session-header");
            if (session == null) {
               throw UndertowMessages.MESSAGES.authenticationPropertyNotSet(mechanismName, "session-header");
            } else {
               List<HttpString> ids = new ArrayList();
               String[] var8 = identity.split(",");
               int var9 = var8.length;

               int var10;
               for(var10 = 0; var10 < var9; ++var10) {
                  String s = var8[var10];
                  ids.add(new HttpString(s));
               }

               List<String> sessions = new ArrayList();
               String[] var15 = session.split(",");
               var10 = var15.length;

               for(int var13 = 0; var13 < var10; ++var13) {
                  String s = var15[var13];
                  sessions.add(s);
               }

               return new GenericHeaderAuthenticationMechanism(mechanismName, ids, sessions, identityManager);
            }
         }
      }
   }
}

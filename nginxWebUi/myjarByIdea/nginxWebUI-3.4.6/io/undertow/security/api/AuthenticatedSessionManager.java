package io.undertow.security.api;

import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import java.io.Serializable;

public interface AuthenticatedSessionManager {
   AttachmentKey<AuthenticatedSessionManager> ATTACHMENT_KEY = AttachmentKey.create(AuthenticatedSessionManager.class);

   AuthenticatedSession lookupSession(HttpServerExchange var1);

   void clearSession(HttpServerExchange var1);

   public static class AuthenticatedSession implements Serializable {
      private final Account account;
      private final String mechanism;

      public AuthenticatedSession(Account account, String mechanism) {
         this.account = account;
         this.mechanism = mechanism;
      }

      public Account getAccount() {
         return this.account;
      }

      public String getMechanism() {
         return this.mechanism;
      }
   }
}

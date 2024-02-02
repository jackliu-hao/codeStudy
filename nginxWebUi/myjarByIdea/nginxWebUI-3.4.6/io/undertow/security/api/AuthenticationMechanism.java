package io.undertow.security.api;

import io.undertow.server.HttpServerExchange;

public interface AuthenticationMechanism {
   AuthenticationMechanismOutcome authenticate(HttpServerExchange var1, SecurityContext var2);

   ChallengeResult sendChallenge(HttpServerExchange var1, SecurityContext var2);

   public static class ChallengeResult {
      public static final ChallengeResult NOT_SENT = new ChallengeResult(false);
      private final boolean challengeSent;
      private final Integer statusCode;

      public ChallengeResult(boolean challengeSent, Integer statusCode) {
         this.statusCode = statusCode;
         this.challengeSent = challengeSent;
      }

      public ChallengeResult(boolean challengeSent) {
         this(challengeSent, (Integer)null);
      }

      public Integer getDesiredResponseCode() {
         return this.statusCode;
      }

      public boolean isChallengeSent() {
         return this.challengeSent;
      }
   }

   public static enum AuthenticationMechanismOutcome {
      AUTHENTICATED,
      NOT_ATTEMPTED,
      NOT_AUTHENTICATED;
   }
}

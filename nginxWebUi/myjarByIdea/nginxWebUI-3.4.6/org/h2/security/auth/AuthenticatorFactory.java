package org.h2.security.auth;

public class AuthenticatorFactory {
   public static Authenticator createAuthenticator() {
      return DefaultAuthenticator.getInstance();
   }
}

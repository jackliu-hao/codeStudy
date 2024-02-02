package org.apache.http.auth;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public final class AuthOption {
   private final AuthScheme authScheme;
   private final Credentials creds;

   public AuthOption(AuthScheme authScheme, Credentials creds) {
      Args.notNull(authScheme, "Auth scheme");
      Args.notNull(creds, "User credentials");
      this.authScheme = authScheme;
      this.creds = creds;
   }

   public AuthScheme getAuthScheme() {
      return this.authScheme;
   }

   public Credentials getCredentials() {
      return this.creds;
   }

   public String toString() {
      return this.authScheme.toString();
   }
}

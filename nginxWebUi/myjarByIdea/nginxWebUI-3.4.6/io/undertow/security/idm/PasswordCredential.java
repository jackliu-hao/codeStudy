package io.undertow.security.idm;

public final class PasswordCredential implements Credential {
   private final char[] password;

   public PasswordCredential(char[] password) {
      this.password = password;
   }

   public char[] getPassword() {
      return this.password;
   }
}

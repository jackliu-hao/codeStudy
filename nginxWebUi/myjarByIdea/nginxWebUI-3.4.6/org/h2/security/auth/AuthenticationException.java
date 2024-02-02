package org.h2.security.auth;

public class AuthenticationException extends Exception {
   private static final long serialVersionUID = 1L;

   public AuthenticationException() {
   }

   public AuthenticationException(String var1) {
      super(var1);
   }

   public AuthenticationException(Throwable var1) {
      super(var1);
   }

   public AuthenticationException(String var1, Throwable var2) {
      super(var1, var2);
   }
}

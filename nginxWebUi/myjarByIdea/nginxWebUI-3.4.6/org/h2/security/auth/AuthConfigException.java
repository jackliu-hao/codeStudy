package org.h2.security.auth;

public class AuthConfigException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public AuthConfigException() {
   }

   public AuthConfigException(String var1) {
      super(var1);
   }

   public AuthConfigException(Throwable var1) {
      super(var1);
   }

   public AuthConfigException(String var1, Throwable var2) {
      super(var1, var2);
   }
}

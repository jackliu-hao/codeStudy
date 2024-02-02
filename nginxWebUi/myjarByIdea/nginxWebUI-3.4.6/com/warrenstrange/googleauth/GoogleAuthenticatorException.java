package com.warrenstrange.googleauth;

public class GoogleAuthenticatorException extends RuntimeException {
   public GoogleAuthenticatorException(String message) {
      super(message);
   }

   public GoogleAuthenticatorException(String message, Throwable cause) {
      super(message, cause);
   }
}

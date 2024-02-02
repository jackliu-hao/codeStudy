package io.undertow.util;

public class BadRequestException extends Exception {
   public BadRequestException() {
   }

   public BadRequestException(String message) {
      super(message);
   }

   public BadRequestException(Throwable cause) {
      super(cause);
   }

   public BadRequestException(String message, Throwable cause) {
      super(message, cause);
   }
}

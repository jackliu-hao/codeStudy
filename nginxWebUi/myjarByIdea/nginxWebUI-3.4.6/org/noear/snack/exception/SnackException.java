package org.noear.snack.exception;

public class SnackException extends RuntimeException {
   public SnackException(String message) {
      super(message);
   }

   public SnackException(Throwable cause) {
      super(cause);
   }

   public SnackException(String message, Throwable cause) {
      super(message, cause);
   }
}

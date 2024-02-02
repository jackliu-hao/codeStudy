package io.undertow.server;

public class RenegotiationRequiredException extends Exception {
   public RenegotiationRequiredException() {
   }

   public RenegotiationRequiredException(String message) {
      super(message);
   }

   public RenegotiationRequiredException(String message, Throwable cause) {
      super(message, cause);
   }

   public RenegotiationRequiredException(Throwable cause) {
      super(cause);
   }

   public RenegotiationRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }
}

package org.xnio.channels;

public class UnsupportedOptionException extends IllegalArgumentException {
   private static final long serialVersionUID = 250195510855241708L;

   public UnsupportedOptionException() {
   }

   public UnsupportedOptionException(String message) {
      super(message);
   }

   public UnsupportedOptionException(String message, Throwable cause) {
      super(message, cause);
   }

   public UnsupportedOptionException(Throwable cause) {
      super(cause);
   }
}

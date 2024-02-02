package freemarker.core;

public class UnregisteredOutputFormatException extends Exception {
   public UnregisteredOutputFormatException(String message) {
      this(message, (Throwable)null);
   }

   public UnregisteredOutputFormatException(String message, Throwable cause) {
      super(message, cause);
   }
}

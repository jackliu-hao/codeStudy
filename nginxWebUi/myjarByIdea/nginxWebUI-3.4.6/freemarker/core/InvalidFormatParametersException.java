package freemarker.core;

public final class InvalidFormatParametersException extends InvalidFormatStringException {
   public InvalidFormatParametersException(String message, Throwable cause) {
      super(message, cause);
   }

   public InvalidFormatParametersException(String message) {
      this(message, (Throwable)null);
   }
}

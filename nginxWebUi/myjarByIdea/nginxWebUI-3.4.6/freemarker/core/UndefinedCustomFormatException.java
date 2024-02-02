package freemarker.core;

public class UndefinedCustomFormatException extends InvalidFormatStringException {
   public UndefinedCustomFormatException(String message, Throwable cause) {
      super(message, cause);
   }

   public UndefinedCustomFormatException(String message) {
      super(message);
   }
}

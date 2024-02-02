package freemarker.core;

public class UnparsableValueException extends TemplateValueFormatException {
   public UnparsableValueException(String message, Throwable cause) {
      super(message, cause);
   }

   public UnparsableValueException(String message) {
      this(message, (Throwable)null);
   }
}

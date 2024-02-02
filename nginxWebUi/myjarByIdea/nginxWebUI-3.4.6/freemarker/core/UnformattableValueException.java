package freemarker.core;

public class UnformattableValueException extends TemplateValueFormatException {
   public UnformattableValueException(String message, Throwable cause) {
      super(message, cause);
   }

   public UnformattableValueException(String message) {
      super(message);
   }
}

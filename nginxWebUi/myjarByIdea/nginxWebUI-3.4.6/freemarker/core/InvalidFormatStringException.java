package freemarker.core;

public abstract class InvalidFormatStringException extends TemplateValueFormatException {
   public InvalidFormatStringException(String message, Throwable cause) {
      super(message, cause);
   }

   public InvalidFormatStringException(String message) {
      this(message, (Throwable)null);
   }
}

package freemarker.core;

public abstract class TemplateValueFormatException extends Exception {
   public TemplateValueFormatException(String message, Throwable cause) {
      super(message, cause);
   }

   public TemplateValueFormatException(String message) {
      super(message);
   }
}

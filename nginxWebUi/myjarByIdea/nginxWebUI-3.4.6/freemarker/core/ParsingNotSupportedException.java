package freemarker.core;

public class ParsingNotSupportedException extends TemplateValueFormatException {
   public ParsingNotSupportedException(String message, Throwable cause) {
      super(message, cause);
   }

   public ParsingNotSupportedException(String message) {
      this(message, (Throwable)null);
   }
}

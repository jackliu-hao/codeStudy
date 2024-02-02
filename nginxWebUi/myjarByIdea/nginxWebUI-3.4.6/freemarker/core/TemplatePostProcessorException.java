package freemarker.core;

class TemplatePostProcessorException extends Exception {
   public TemplatePostProcessorException(String message, Throwable cause) {
      super(message, cause);
   }

   public TemplatePostProcessorException(String message) {
      super(message);
   }
}

package freemarker.template.utility;

public class UndeclaredThrowableException extends RuntimeException {
   public UndeclaredThrowableException(Throwable t) {
      super(t);
   }

   public UndeclaredThrowableException(String message, Throwable t) {
      super(message, t);
   }

   public Throwable getUndeclaredThrowable() {
      return this.getCause();
   }
}

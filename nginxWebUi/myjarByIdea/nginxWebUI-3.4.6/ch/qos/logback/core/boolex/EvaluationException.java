package ch.qos.logback.core.boolex;

public class EvaluationException extends Exception {
   private static final long serialVersionUID = 1L;

   public EvaluationException(String msg) {
      super(msg);
   }

   public EvaluationException(String msg, Throwable cause) {
      super(msg, cause);
   }

   public EvaluationException(Throwable cause) {
      super(cause);
   }
}

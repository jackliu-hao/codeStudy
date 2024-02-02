package ch.qos.logback.core.spi;

public class ScanException extends Exception {
   private static final long serialVersionUID = -3132040414328475658L;
   Throwable cause;

   public ScanException(String msg) {
      super(msg);
   }

   public ScanException(String msg, Throwable rootCause) {
      super(msg);
      this.cause = rootCause;
   }

   public Throwable getCause() {
      return this.cause;
   }
}

package ch.qos.logback.core.status;

public class ErrorStatus extends StatusBase {
   public ErrorStatus(String msg, Object origin) {
      super(2, msg, origin);
   }

   public ErrorStatus(String msg, Object origin, Throwable t) {
      super(2, msg, origin, t);
   }
}

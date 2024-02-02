package ch.qos.logback.core.status;

public class WarnStatus extends StatusBase {
   public WarnStatus(String msg, Object origin) {
      super(1, msg, origin);
   }

   public WarnStatus(String msg, Object origin, Throwable t) {
      super(1, msg, origin, t);
   }
}

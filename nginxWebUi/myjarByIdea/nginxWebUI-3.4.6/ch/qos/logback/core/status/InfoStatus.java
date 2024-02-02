package ch.qos.logback.core.status;

public class InfoStatus extends StatusBase {
   public InfoStatus(String msg, Object origin) {
      super(0, msg, origin);
   }

   public InfoStatus(String msg, Object origin, Throwable t) {
      super(0, msg, origin, t);
   }
}

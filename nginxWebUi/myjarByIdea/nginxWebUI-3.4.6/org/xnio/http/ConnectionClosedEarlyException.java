package org.xnio.http;

public class ConnectionClosedEarlyException extends UpgradeFailedException {
   private static final long serialVersionUID = -2954011903833115915L;

   public ConnectionClosedEarlyException() {
   }

   public ConnectionClosedEarlyException(String msg) {
      super(msg);
   }

   public ConnectionClosedEarlyException(Throwable cause) {
      super(cause);
   }

   public ConnectionClosedEarlyException(String msg, Throwable cause) {
      super(msg, cause);
   }
}

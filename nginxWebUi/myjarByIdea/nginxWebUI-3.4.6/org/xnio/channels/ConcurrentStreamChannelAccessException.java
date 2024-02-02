package org.xnio.channels;

public class ConcurrentStreamChannelAccessException extends IllegalStateException {
   public ConcurrentStreamChannelAccessException() {
   }

   public ConcurrentStreamChannelAccessException(String msg) {
      super(msg);
   }

   public ConcurrentStreamChannelAccessException(Throwable cause) {
      super(cause);
   }

   public ConcurrentStreamChannelAccessException(String msg, Throwable cause) {
      super(msg, cause);
   }
}

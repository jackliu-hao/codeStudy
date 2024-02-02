package org.xnio.channels;

import java.io.InterruptedIOException;

public class WriteTimeoutException extends InterruptedIOException {
   private static final long serialVersionUID = 2058056832934733469L;

   public WriteTimeoutException() {
   }

   public WriteTimeoutException(String msg) {
      super(msg);
   }

   public WriteTimeoutException(Throwable cause) {
      this.initCause(cause);
   }

   public WriteTimeoutException(String msg, Throwable cause) {
      super(msg);
      this.initCause(cause);
   }
}

package org.xnio.channels;

import java.io.InterruptedIOException;

public class ReadTimeoutException extends InterruptedIOException {
   private static final long serialVersionUID = 2058056832934733469L;

   public ReadTimeoutException() {
   }

   public ReadTimeoutException(String msg) {
      super(msg);
   }

   public ReadTimeoutException(Throwable cause) {
      this.initCause(cause);
   }

   public ReadTimeoutException(String msg, Throwable cause) {
      super(msg);
      this.initCause(cause);
   }
}

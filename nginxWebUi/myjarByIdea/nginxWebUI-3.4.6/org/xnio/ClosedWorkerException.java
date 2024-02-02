package org.xnio;

import java.io.IOException;

public class ClosedWorkerException extends IOException {
   public ClosedWorkerException() {
   }

   public ClosedWorkerException(String msg) {
      super(msg);
   }

   public ClosedWorkerException(Throwable cause) {
      super(cause);
   }

   public ClosedWorkerException(String msg, Throwable cause) {
      super(msg, cause);
   }
}

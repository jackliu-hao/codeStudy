package org.xnio;

import java.io.IOException;

public class BrokenPipeException extends IOException {
   public BrokenPipeException() {
   }

   public BrokenPipeException(String msg) {
      super(msg);
   }

   public BrokenPipeException(Throwable cause) {
      super(cause);
   }

   public BrokenPipeException(String msg, Throwable cause) {
      super(msg, cause);
   }
}

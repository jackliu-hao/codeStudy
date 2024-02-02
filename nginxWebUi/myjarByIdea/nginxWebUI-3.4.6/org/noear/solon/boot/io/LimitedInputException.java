package org.noear.solon.boot.io;

import java.io.IOException;

public class LimitedInputException extends IOException {
   public LimitedInputException() {
   }

   public LimitedInputException(String message) {
      super(message);
   }

   public LimitedInputException(String message, Throwable cause) {
      super(message, cause);
   }

   public LimitedInputException(Throwable cause) {
      super(cause);
   }
}

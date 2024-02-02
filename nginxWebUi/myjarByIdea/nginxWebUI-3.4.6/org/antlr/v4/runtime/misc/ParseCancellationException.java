package org.antlr.v4.runtime.misc;

import java.util.concurrent.CancellationException;

public class ParseCancellationException extends CancellationException {
   public ParseCancellationException() {
   }

   public ParseCancellationException(String message) {
      super(message);
   }

   public ParseCancellationException(Throwable cause) {
      this.initCause(cause);
   }

   public ParseCancellationException(String message, Throwable cause) {
      super(message);
      this.initCause(cause);
   }
}

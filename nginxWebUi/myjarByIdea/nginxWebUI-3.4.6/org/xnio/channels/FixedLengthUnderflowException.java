package org.xnio.channels;

import java.io.IOException;

public class FixedLengthUnderflowException extends IOException {
   private static final long serialVersionUID = 7294784996964683484L;

   public FixedLengthUnderflowException() {
   }

   public FixedLengthUnderflowException(String msg) {
      super(msg);
   }

   public FixedLengthUnderflowException(Throwable cause) {
      super(cause);
   }

   public FixedLengthUnderflowException(String msg, Throwable cause) {
      super(msg, cause);
   }
}

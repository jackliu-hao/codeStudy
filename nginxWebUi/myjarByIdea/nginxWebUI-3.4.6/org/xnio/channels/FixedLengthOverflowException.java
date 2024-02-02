package org.xnio.channels;

import java.io.IOException;

public class FixedLengthOverflowException extends IOException {
   private static final long serialVersionUID = 475540863890698430L;

   public FixedLengthOverflowException() {
   }

   public FixedLengthOverflowException(String msg) {
      super(msg);
   }

   public FixedLengthOverflowException(Throwable cause) {
      super(cause);
   }

   public FixedLengthOverflowException(String msg, Throwable cause) {
      super(msg, cause);
   }
}

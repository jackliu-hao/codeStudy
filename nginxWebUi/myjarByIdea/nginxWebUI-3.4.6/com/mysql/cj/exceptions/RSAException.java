package com.mysql.cj.exceptions;

public class RSAException extends CJException {
   private static final long serialVersionUID = -1878681511263159173L;

   public RSAException() {
   }

   public RSAException(String message) {
      super(message);
   }

   public RSAException(String message, Throwable cause) {
      super(message, cause);
   }

   public RSAException(Throwable cause) {
      super(cause);
   }

   protected RSAException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }
}

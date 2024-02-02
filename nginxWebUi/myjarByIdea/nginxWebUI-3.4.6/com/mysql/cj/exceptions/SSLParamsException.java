package com.mysql.cj.exceptions;

public class SSLParamsException extends CJException {
   private static final long serialVersionUID = -6597843374954727858L;

   public SSLParamsException() {
      this.setSQLState("08000");
   }

   public SSLParamsException(String message) {
      super(message);
      this.setSQLState("08000");
   }

   public SSLParamsException(String message, Throwable cause) {
      super(message, cause);
      this.setSQLState("08000");
   }

   public SSLParamsException(Throwable cause) {
      super(cause);
      this.setSQLState("08000");
   }

   public SSLParamsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
      this.setSQLState("08000");
   }
}

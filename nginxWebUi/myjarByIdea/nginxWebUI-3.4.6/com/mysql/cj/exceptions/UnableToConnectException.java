package com.mysql.cj.exceptions;

public class UnableToConnectException extends CJException {
   private static final long serialVersionUID = 6824175447292574109L;

   public UnableToConnectException() {
      this.setSQLState("08001");
   }

   public UnableToConnectException(String message) {
      super(message);
      this.setSQLState("08001");
   }

   public UnableToConnectException(String message, Throwable cause) {
      super(message, cause);
      this.setSQLState("08001");
   }

   public UnableToConnectException(Throwable cause) {
      super(cause);
      this.setSQLState("08001");
   }

   public UnableToConnectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
      this.setSQLState("08001");
   }
}

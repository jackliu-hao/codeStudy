package com.mysql.cj.exceptions;

public class InvalidConnectionAttributeException extends CJException {
   private static final long serialVersionUID = -4814924499233623016L;

   public InvalidConnectionAttributeException() {
      this.setSQLState("01S00");
   }

   public InvalidConnectionAttributeException(String message) {
      super(message);
      this.setSQLState("01S00");
   }

   public InvalidConnectionAttributeException(String message, Throwable cause) {
      super(message, cause);
      this.setSQLState("01S00");
   }

   public InvalidConnectionAttributeException(Throwable cause) {
      super(cause);
      this.setSQLState("01S00");
   }

   public InvalidConnectionAttributeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
      this.setSQLState("01S00");
   }
}

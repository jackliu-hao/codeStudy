package com.mysql.cj.exceptions;

public class StatementIsClosedException extends CJException {
   private static final long serialVersionUID = -4214028635985851906L;

   public StatementIsClosedException() {
      this.setSQLState("S1009");
   }

   public StatementIsClosedException(String message) {
      super(message);
      this.setSQLState("S1009");
   }

   public StatementIsClosedException(String message, Throwable cause) {
      super(message, cause);
      this.setSQLState("S1009");
   }

   public StatementIsClosedException(Throwable cause) {
      super(cause);
      this.setSQLState("S1009");
   }

   protected StatementIsClosedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
      this.setSQLState("S1009");
   }
}

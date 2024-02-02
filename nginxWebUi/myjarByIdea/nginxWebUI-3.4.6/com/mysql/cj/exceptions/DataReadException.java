package com.mysql.cj.exceptions;

public class DataReadException extends CJException {
   private static final long serialVersionUID = 1684265521187171525L;

   public DataReadException(Exception cause) {
      super((Throwable)cause);
      this.setSQLState("S1009");
   }

   public DataReadException(String msg) {
      super(msg);
      this.setSQLState("S1009");
   }
}

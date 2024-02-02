package com.mysql.cj.exceptions;

public class DataConversionException extends DataReadException {
   private static final long serialVersionUID = -863576663404236982L;

   public DataConversionException(String msg) {
      super(msg);
      this.setSQLState("22018");
   }
}

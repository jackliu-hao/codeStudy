package com.mysql.cj.exceptions;

public class NumberOutOfRange extends DataReadException {
   private static final long serialVersionUID = -61091413023651438L;

   public NumberOutOfRange(String msg) {
      super(msg);
      this.setSQLState("22003");
   }
}

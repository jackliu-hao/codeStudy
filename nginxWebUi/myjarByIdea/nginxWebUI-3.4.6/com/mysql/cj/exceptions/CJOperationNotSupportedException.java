package com.mysql.cj.exceptions;

public class CJOperationNotSupportedException extends CJException {
   private static final long serialVersionUID = 2619184100062994443L;

   public CJOperationNotSupportedException() {
   }

   public CJOperationNotSupportedException(String message) {
      super(message);
   }
}

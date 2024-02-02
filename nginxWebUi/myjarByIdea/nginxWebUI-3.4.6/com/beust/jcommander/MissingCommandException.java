package com.beust.jcommander;

public class MissingCommandException extends ParameterException {
   public MissingCommandException(String string) {
      super(string);
   }

   public MissingCommandException(Throwable t) {
      super(t);
   }
}

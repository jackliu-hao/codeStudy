package com.sun.mail.iap;

public class CommandFailedException extends ProtocolException {
   private static final long serialVersionUID = 793932807880443631L;

   public CommandFailedException() {
   }

   public CommandFailedException(String s) {
      super(s);
   }

   public CommandFailedException(Response r) {
      super(r);
   }
}

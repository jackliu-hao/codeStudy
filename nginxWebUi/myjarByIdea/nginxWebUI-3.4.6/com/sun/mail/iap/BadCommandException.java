package com.sun.mail.iap;

public class BadCommandException extends ProtocolException {
   private static final long serialVersionUID = 5769722539397237515L;

   public BadCommandException() {
   }

   public BadCommandException(String s) {
      super(s);
   }

   public BadCommandException(Response r) {
      super(r);
   }
}

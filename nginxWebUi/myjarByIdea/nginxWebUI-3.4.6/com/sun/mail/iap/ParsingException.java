package com.sun.mail.iap;

public class ParsingException extends ProtocolException {
   private static final long serialVersionUID = 7756119840142724839L;

   public ParsingException() {
   }

   public ParsingException(String s) {
      super(s);
   }

   public ParsingException(Response r) {
      super(r);
   }
}

package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;

public class RFC822SIZE implements Item {
   static final char[] name = new char[]{'R', 'F', 'C', '8', '2', '2', '.', 'S', 'I', 'Z', 'E'};
   public int msgno;
   public int size;

   public RFC822SIZE(FetchResponse r) throws ParsingException {
      this.msgno = r.getNumber();
      r.skipSpaces();
      this.size = r.readNumber();
   }
}

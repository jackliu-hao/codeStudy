package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;

public class UID implements Item {
   static final char[] name = new char[]{'U', 'I', 'D'};
   public int seqnum;
   public long uid;

   public UID(FetchResponse r) throws ParsingException {
      this.seqnum = r.getNumber();
      r.skipSpaces();
      this.uid = r.readLong();
   }
}

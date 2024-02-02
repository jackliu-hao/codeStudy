package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ByteArray;
import com.sun.mail.iap.ParsingException;
import java.io.ByteArrayInputStream;

public class RFC822DATA implements Item {
   static final char[] name = new char[]{'R', 'F', 'C', '8', '2', '2'};
   public int msgno;
   public ByteArray data;

   public RFC822DATA(FetchResponse r) throws ParsingException {
      this.msgno = r.getNumber();
      r.skipSpaces();
      this.data = r.readByteArray();
   }

   public ByteArray getByteArray() {
      return this.data;
   }

   public ByteArrayInputStream getByteArrayInputStream() {
      return this.data != null ? this.data.toByteArrayInputStream() : null;
   }
}

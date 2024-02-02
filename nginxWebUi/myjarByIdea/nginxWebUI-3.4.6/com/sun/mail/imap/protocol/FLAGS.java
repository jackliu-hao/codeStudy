package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import javax.mail.Flags;

public class FLAGS extends Flags implements Item {
   static final char[] name = new char[]{'F', 'L', 'A', 'G', 'S'};
   public int msgno;
   private static final long serialVersionUID = 439049847053756670L;

   public FLAGS(IMAPResponse var1) throws ParsingException {
      // $FF: Couldn't be decompiled
   }
}

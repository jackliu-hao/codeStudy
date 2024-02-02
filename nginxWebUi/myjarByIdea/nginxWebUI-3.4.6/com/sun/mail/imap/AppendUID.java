package com.sun.mail.imap;

public class AppendUID {
   public long uidvalidity = -1L;
   public long uid = -1L;

   public AppendUID(long uidvalidity, long uid) {
      this.uidvalidity = uidvalidity;
      this.uid = uid;
   }
}

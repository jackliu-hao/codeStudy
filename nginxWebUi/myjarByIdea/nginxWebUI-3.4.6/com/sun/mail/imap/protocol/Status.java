package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;

public class Status {
   public String mbox = null;
   public int total = -1;
   public int recent = -1;
   public long uidnext = -1L;
   public long uidvalidity = -1L;
   public int unseen = -1;
   static final String[] standardItems = new String[]{"MESSAGES", "RECENT", "UNSEEN", "UIDNEXT", "UIDVALIDITY"};

   public Status(Response r) throws ParsingException {
      this.mbox = r.readAtomString();
      StringBuffer buffer = new StringBuffer();
      boolean onlySpaces = true;

      while(r.peekByte() != 40 && r.peekByte() != 0) {
         char next = (char)r.readByte();
         buffer.append(next);
         if (next != ' ') {
            onlySpaces = false;
         }
      }

      if (!onlySpaces) {
         this.mbox = (this.mbox + buffer).trim();
      }

      if (r.readByte() != 40) {
         throw new ParsingException("parse error in STATUS");
      } else {
         do {
            String attr = r.readAtom();
            if (attr.equalsIgnoreCase("MESSAGES")) {
               this.total = r.readNumber();
            } else if (attr.equalsIgnoreCase("RECENT")) {
               this.recent = r.readNumber();
            } else if (attr.equalsIgnoreCase("UIDNEXT")) {
               this.uidnext = r.readLong();
            } else if (attr.equalsIgnoreCase("UIDVALIDITY")) {
               this.uidvalidity = r.readLong();
            } else if (attr.equalsIgnoreCase("UNSEEN")) {
               this.unseen = r.readNumber();
            }
         } while(r.readByte() != 41);

      }
   }

   public static void add(Status s1, Status s2) {
      if (s2.total != -1) {
         s1.total = s2.total;
      }

      if (s2.recent != -1) {
         s1.recent = s2.recent;
      }

      if (s2.uidnext != -1L) {
         s1.uidnext = s2.uidnext;
      }

      if (s2.uidvalidity != -1L) {
         s1.uidvalidity = s2.uidvalidity;
      }

      if (s2.unseen != -1) {
         s1.unseen = s2.unseen;
      }

   }
}

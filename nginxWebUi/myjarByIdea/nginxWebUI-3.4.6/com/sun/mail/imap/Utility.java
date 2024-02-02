package com.sun.mail.imap;

import com.sun.mail.imap.protocol.MessageSet;
import com.sun.mail.imap.protocol.UIDSet;
import java.util.Vector;
import javax.mail.Message;

public final class Utility {
   private Utility() {
   }

   public static MessageSet[] toMessageSet(Message[] msgs, Condition cond) {
      Vector v = new Vector(1);

      for(int i = 0; i < msgs.length; ++i) {
         IMAPMessage msg = (IMAPMessage)msgs[i];
         if (!msg.isExpunged()) {
            int current = msg.getSequenceNumber();
            if (cond == null || cond.test(msg)) {
               MessageSet set = new MessageSet();
               set.start = current;
               ++i;

               for(; i < msgs.length; ++i) {
                  msg = (IMAPMessage)msgs[i];
                  if (!msg.isExpunged()) {
                     int next = msg.getSequenceNumber();
                     if (cond == null || cond.test(msg)) {
                        if (next != current + 1) {
                           --i;
                           break;
                        }

                        current = next;
                     }
                  }
               }

               set.end = current;
               v.addElement(set);
            }
         }
      }

      if (v.isEmpty()) {
         return null;
      } else {
         MessageSet[] sets = new MessageSet[v.size()];
         v.copyInto(sets);
         return sets;
      }
   }

   public static UIDSet[] toUIDSet(Message[] msgs) {
      Vector v = new Vector(1);

      for(int i = 0; i < msgs.length; ++i) {
         IMAPMessage msg = (IMAPMessage)msgs[i];
         if (!msg.isExpunged()) {
            long current = msg.getUID();
            UIDSet set = new UIDSet();
            set.start = current;
            ++i;

            for(; i < msgs.length; ++i) {
               msg = (IMAPMessage)msgs[i];
               if (!msg.isExpunged()) {
                  long next = msg.getUID();
                  if (next != current + 1L) {
                     --i;
                     break;
                  }

                  current = next;
               }
            }

            set.end = current;
            v.addElement(set);
         }
      }

      if (v.isEmpty()) {
         return null;
      } else {
         UIDSet[] sets = new UIDSet[v.size()];
         v.copyInto(sets);
         return sets;
      }
   }

   public interface Condition {
      boolean test(IMAPMessage var1);
   }
}

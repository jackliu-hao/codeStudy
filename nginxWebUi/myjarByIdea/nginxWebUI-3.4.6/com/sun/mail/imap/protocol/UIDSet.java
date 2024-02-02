package com.sun.mail.imap.protocol;

import java.util.Vector;

public class UIDSet {
   public long start;
   public long end;

   public UIDSet() {
   }

   public UIDSet(long start, long end) {
      this.start = start;
      this.end = end;
   }

   public long size() {
      return this.end - this.start + 1L;
   }

   public static UIDSet[] createUIDSets(long[] msgs) {
      Vector v = new Vector();

      for(int i = 0; i < msgs.length; ++i) {
         UIDSet ms = new UIDSet();
         ms.start = msgs[i];

         int j;
         for(j = i + 1; j < msgs.length && msgs[j] == msgs[j - 1] + 1L; ++j) {
         }

         ms.end = msgs[j - 1];
         v.addElement(ms);
         i = j - 1;
      }

      UIDSet[] msgsets = new UIDSet[v.size()];
      v.copyInto(msgsets);
      return msgsets;
   }

   public static String toString(UIDSet[] msgsets) {
      if (msgsets != null && msgsets.length != 0) {
         int i = 0;
         StringBuffer s = new StringBuffer();
         int size = msgsets.length;

         while(true) {
            long start = msgsets[i].start;
            long end = msgsets[i].end;
            if (end > start) {
               s.append(start).append(':').append(end);
            } else {
               s.append(start);
            }

            ++i;
            if (i >= size) {
               return s.toString();
            }

            s.append(',');
         }
      } else {
         return null;
      }
   }

   public static long size(UIDSet[] msgsets) {
      long count = 0L;
      if (msgsets == null) {
         return 0L;
      } else {
         for(int i = 0; i < msgsets.length; ++i) {
            count += msgsets[i].size();
         }

         return count;
      }
   }
}

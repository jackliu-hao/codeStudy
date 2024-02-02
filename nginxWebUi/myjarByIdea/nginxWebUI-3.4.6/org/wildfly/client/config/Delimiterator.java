package org.wildfly.client.config;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class Delimiterator implements Iterator<String> {
   private final String subject;
   private final char delimiter;
   private int i;
   private static final int[] NO_INTS = new int[0];
   private static final long[] NO_LONGS = new long[0];
   private static final String[] NO_STRINGS = new String[0];

   Delimiterator(String subject, char delimiter) {
      this.subject = subject;
      this.delimiter = delimiter;
      this.i = 0;
   }

   static Delimiterator over(String subject, char delimiter) {
      return new Delimiterator(subject, delimiter);
   }

   public boolean hasNext() {
      return this.i != -1;
   }

   public String next() {
      int i = this.i;
      if (i == -1) {
         throw new NoSuchElementException();
      } else {
         int n = this.subject.indexOf(this.delimiter, i);

         String var3;
         try {
            var3 = n == -1 ? this.subject.substring(i) : this.subject.substring(i, n);
         } finally {
            this.i = n == -1 ? -1 : n + 1;
         }

         return var3;
      }
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }

   public String[] toStringArray() {
      return this.toStringArray(0);
   }

   String[] toStringArray(int count) {
      if (this.hasNext()) {
         String next = this.next();
         String[] strings = this.toStringArray(count + 1);
         strings[count] = next;
         return strings;
      } else {
         return count == 0 ? NO_STRINGS : new String[count];
      }
   }

   public int[] toIntArray() throws NumberFormatException {
      return this.toIntArray(0);
   }

   int[] toIntArray(int count) {
      if (this.hasNext()) {
         String next = this.next();
         int[] ints = this.toIntArray(count + 1);
         ints[count] = Integer.parseInt(next);
         return ints;
      } else {
         return count == 0 ? NO_INTS : new int[count];
      }
   }

   public long[] toLongArray() throws NumberFormatException {
      return this.toLongArray(0);
   }

   long[] toLongArray(int count) {
      if (this.hasNext()) {
         String next = this.next();
         long[] longs = this.toLongArray(count + 1);
         longs[count] = Long.parseLong(next);
         return longs;
      } else {
         return count == 0 ? NO_LONGS : new long[count];
      }
   }
}

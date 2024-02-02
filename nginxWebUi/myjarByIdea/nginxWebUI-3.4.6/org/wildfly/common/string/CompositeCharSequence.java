package org.wildfly.common.string;

import java.io.Serializable;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CompositeCharSequence implements CharSequence, Serializable {
   private static final long serialVersionUID = 4975968165050531721L;
   private final List<CharSequence> sequences;
   private transient int hashCode;

   public CompositeCharSequence(CharSequence... sequences) {
      this(Arrays.asList(sequences));
   }

   public CompositeCharSequence(List<CharSequence> sequences) {
      this.hashCode = 0;
      this.sequences = sequences;
   }

   public int length() {
      int length = 0;

      CharSequence sequence;
      for(Iterator var2 = this.sequences.iterator(); var2.hasNext(); length += sequence.length()) {
         sequence = (CharSequence)var2.next();
      }

      return length;
   }

   public char charAt(int index) {
      int relativeIndex = index;

      CharSequence sequence;
      for(Iterator var3 = this.sequences.iterator(); var3.hasNext(); relativeIndex -= sequence.length()) {
         sequence = (CharSequence)var3.next();
         if (relativeIndex < sequence.length()) {
            return sequence.charAt(relativeIndex);
         }
      }

      throw new IndexOutOfBoundsException();
   }

   public CharSequence subSequence(int start, int end) {
      if (start >= 0 && start <= end && end <= this.length()) {
         if (start == end) {
            return "";
         } else {
            List<CharSequence> result = null;
            int relativeStart = start;
            int relativeEnd = end;

            CharSequence sequence;
            for(Iterator var6 = this.sequences.iterator(); var6.hasNext(); relativeEnd -= sequence.length()) {
               sequence = (CharSequence)var6.next();
               if (relativeStart < sequence.length() && relativeEnd > 0) {
                  CharSequence subSequence = sequence.subSequence(Math.max(relativeStart, 0), Math.min(relativeEnd, sequence.length()));
                  if (result == null) {
                     if (relativeStart >= 0 && relativeEnd <= sequence.length()) {
                        return subSequence;
                     }

                     result = new LinkedList();
                  }

                  result.add(subSequence);
               }

               relativeStart -= sequence.length();
            }

            return new CompositeCharSequence(result);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int hashCode() {
      int result = this.hashCode;
      if (result == 0) {
         for(int i = 0; i < this.length(); ++i) {
            result = 31 * result + this.charAt(i);
         }

         this.hashCode = result;
      }

      return result;
   }

   public boolean equals(Object object) {
      if (!(object instanceof CharSequence)) {
         return false;
      } else {
         CharSequence sequence = (CharSequence)object;
         int length = sequence.length();
         if (this.length() != length) {
            return false;
         } else {
            for(int i = 0; i < length; ++i) {
               if (this.charAt(i) != sequence.charAt(i)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public String toString() {
      CharBuffer buffer = CharBuffer.allocate(this.length());
      Iterator var2 = this.sequences.iterator();

      while(var2.hasNext()) {
         CharSequence sequence = (CharSequence)var2.next();
         buffer.put(CharBuffer.wrap(sequence));
      }

      return String.valueOf(buffer.array());
   }
}

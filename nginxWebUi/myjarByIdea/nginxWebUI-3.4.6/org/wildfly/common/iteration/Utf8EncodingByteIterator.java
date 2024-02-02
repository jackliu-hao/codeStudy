package org.wildfly.common.iteration;

import java.util.NoSuchElementException;
import org.wildfly.common.Assert;
import org.wildfly.common.bytes.ByteStringBuilder;

final class Utf8EncodingByteIterator extends ByteIterator {
   private final CodePointIterator iter;
   private final boolean escapeNul;
   private int st;
   private int cp;
   private long offset;

   Utf8EncodingByteIterator(CodePointIterator iter, boolean escapeNul) {
      this.iter = iter;
      this.escapeNul = escapeNul;
      this.cp = -1;
   }

   public boolean hasNext() {
      return this.st != 0 || this.iter.hasNext();
   }

   public boolean hasPrevious() {
      return this.st != 0 || this.iter.hasPrevious();
   }

   public int next() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         ++this.offset;
         switch (this.st) {
            case 0:
               int cp = this.iter.next();
               if ((cp != 0 || this.escapeNul) && cp >= 128) {
                  if (cp < 2048) {
                     this.cp = cp;
                     this.st = 1;
                     return 192 | cp >> 6;
                  } else if (cp < 65536) {
                     this.cp = cp;
                     this.st = 2;
                     return 224 | cp >> 12;
                  } else if (cp < 1114112) {
                     this.cp = cp;
                     this.st = 4;
                     return 240 | cp >> 18;
                  } else {
                     this.cp = 65533;
                     this.st = 2;
                     return 239;
                  }
               } else {
                  return cp;
               }
            case 1:
            case 3:
            case 6:
               this.st = 0;
               return 128 | this.cp & 63;
            case 2:
               this.st = 3;
               return 128 | this.cp >> 6 & 63;
            case 4:
               this.st = 5;
               return 128 | this.cp >> 12 & 63;
            case 5:
               this.st = 6;
               return 128 | this.cp >> 6 & 63;
            default:
               throw Assert.impossibleSwitchCase(this.st);
         }
      }
   }

   public int peekNext() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         switch (this.st) {
            case 0:
               int cp = this.iter.peekNext();
               if (cp < 128) {
                  return cp;
               } else if (cp < 2048) {
                  return 192 | cp >> 6;
               } else if (cp < 65536) {
                  return 224 | cp >> 12;
               } else {
                  if (cp < 1114112) {
                     return 240 | cp >> 18;
                  }

                  return 239;
               }
            case 1:
            case 3:
            case 6:
               return 128 | this.cp & 63;
            case 2:
            case 5:
               return 128 | this.cp >> 6 & 63;
            case 4:
               return 128 | this.cp >> 12 & 63;
            default:
               throw Assert.impossibleSwitchCase(this.st);
         }
      }
   }

   public int previous() throws NoSuchElementException {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         --this.offset;
         switch (this.st) {
            case 0:
               int cp = this.iter.previous();
               if ((cp != 0 || this.escapeNul) && cp >= 128) {
                  if (cp < 2048) {
                     this.cp = cp;
                     this.st = 1;
                     return 128 | cp & 63;
                  } else if (cp < 65536) {
                     this.cp = cp;
                     this.st = 3;
                     return 128 | cp & 63;
                  } else if (cp < 1114112) {
                     this.cp = cp;
                     this.st = 6;
                     return 128 | cp & 63;
                  } else {
                     this.cp = 65533;
                     this.st = 3;
                     return 189;
                  }
               } else {
                  return cp;
               }
            case 1:
               this.st = 0;
               return 192 | this.cp >> 6;
            case 2:
               this.st = 0;
               return 224 | this.cp >> 12;
            case 3:
               this.st = 2;
               return 128 | this.cp >> 6 & 63;
            case 4:
               this.st = 0;
               return 240 | this.cp >> 18;
            case 5:
               this.st = 4;
               return 128 | this.cp >> 12 & 63;
            case 6:
               this.st = 5;
               return 128 | this.cp >> 6 & 63;
            default:
               throw Assert.impossibleSwitchCase(this.st);
         }
      }
   }

   public int peekPrevious() throws NoSuchElementException {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         switch (this.st) {
            case 0:
               int cp = this.iter.peekPrevious();
               if ((cp != 0 || this.escapeNul) && cp >= 128) {
                  if (cp < 2048) {
                     return 128 | cp & 63;
                  } else if (cp < 65536) {
                     return 128 | cp & 63;
                  } else {
                     return cp < 1114112 ? 128 | cp & 63 : 189;
                  }
               } else {
                  return cp;
               }
            case 1:
               return 192 | this.cp >> 6;
            case 2:
               return 224 | this.cp >> 12;
            case 3:
            case 6:
               return 128 | this.cp >> 6 & 63;
            case 4:
               return 240 | this.cp >> 18;
            case 5:
               return 128 | this.cp >> 12 & 63;
            default:
               throw Assert.impossibleSwitchCase(this.st);
         }
      }
   }

   public ByteStringBuilder appendTo(ByteStringBuilder builder) {
      if (this.st == 0) {
         int oldLen = builder.length();
         builder.appendUtf8(this.iter);
         this.offset += (long)(builder.length() - oldLen);
      } else {
         super.appendTo(builder);
      }

      return builder;
   }

   public long getIndex() {
      return this.offset;
   }
}

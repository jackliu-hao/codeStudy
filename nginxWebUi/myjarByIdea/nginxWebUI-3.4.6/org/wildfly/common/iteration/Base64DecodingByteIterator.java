package org.wildfly.common.iteration;

import java.util.NoSuchElementException;
import org.wildfly.common._private.CommonMessages;

abstract class Base64DecodingByteIterator extends ByteIterator {
   private final CodePointIterator iter;
   private final boolean requirePadding;
   private int state = 0;
   private int o0;
   private int o1;
   private int o2;
   private int offset;

   Base64DecodingByteIterator(CodePointIterator iter, boolean requirePadding) {
      this.iter = iter;
      this.requirePadding = requirePadding;
   }

   public boolean hasNext() {
      if (this.state == 0) {
         if (!this.iter.hasNext()) {
            return false;
         } else {
            int b0 = this.iter.next();
            if (b0 == 61) {
               throw CommonMessages.msg.unexpectedPadding();
            } else if (!this.iter.hasNext()) {
               if (this.requirePadding) {
                  throw CommonMessages.msg.expectedPadding();
               } else {
                  throw CommonMessages.msg.incompleteDecode();
               }
            } else {
               int b1 = this.iter.next();
               if (b1 == 61) {
                  throw CommonMessages.msg.unexpectedPadding();
               } else {
                  this.o0 = this.calc0(b0, b1);
                  if (!this.iter.hasNext()) {
                     if (this.requirePadding) {
                        throw CommonMessages.msg.expectedPadding();
                     } else {
                        this.state = 9;
                        return true;
                     }
                  } else {
                     int b2 = this.iter.next();
                     if (b2 == 61) {
                        if (!this.iter.hasNext()) {
                           throw CommonMessages.msg.expectedTwoPaddingCharacters();
                        } else if (this.iter.next() != 61) {
                           throw CommonMessages.msg.expectedTwoPaddingCharacters();
                        } else {
                           this.state = 6;
                           return true;
                        }
                     } else {
                        this.o1 = this.calc1(b1, b2);
                        if (!this.iter.hasNext()) {
                           if (this.requirePadding) {
                              throw CommonMessages.msg.expectedPadding();
                           } else {
                              this.state = 7;
                              return true;
                           }
                        } else {
                           int b3 = this.iter.next();
                           if (b3 == 61) {
                              this.state = 4;
                              return true;
                           } else {
                              this.o2 = this.calc2(b2, b3);
                              this.state = 1;
                              return true;
                           }
                        }
                     }
                  }
               }
            }
         }
      } else {
         return this.state < 10;
      }
   }

   public boolean hasPrevious() {
      return this.state != 0 || this.offset > 0;
   }

   abstract int calc0(int var1, int var2);

   abstract int calc1(int var1, int var2);

   abstract int calc2(int var1, int var2);

   public int next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         switch (this.state) {
            case 1:
               this.state = 2;
               ++this.offset;
               return this.o0;
            case 2:
               this.state = 3;
               ++this.offset;
               return this.o1;
            case 3:
               this.state = 0;
               ++this.offset;
               return this.o2;
            case 4:
               this.state = 5;
               ++this.offset;
               return this.o0;
            case 5:
               this.state = 11;
               ++this.offset;
               return this.o1;
            case 6:
               this.state = 10;
               ++this.offset;
               return this.o0;
            case 7:
               this.state = 8;
               ++this.offset;
               return this.o0;
            case 8:
               this.state = 13;
               ++this.offset;
               return this.o1;
            case 9:
               this.state = 12;
               ++this.offset;
               return this.o0;
            default:
               throw new NoSuchElementException();
         }
      }
   }

   public int peekNext() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         switch (this.state) {
            case 1:
            case 4:
            case 6:
            case 7:
            case 9:
               return this.o0;
            case 2:
            case 5:
            case 8:
               return this.o1;
            case 3:
               return this.o2;
            default:
               throw new NoSuchElementException();
         }
      }
   }

   public int previous() {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         switch (this.state) {
            case 2:
               this.state = 1;
               --this.offset;
               return this.o0;
            case 3:
               this.state = 2;
               --this.offset;
               return this.o1;
            case 5:
               this.state = 4;
               --this.offset;
               return this.o0;
            case 6:
               this.iter.previous();
            case 4:
               this.iter.previous();
            case 0:
            case 1:
            case 7:
            case 9:
               int b3 = this.iter.previous();
               int b2 = this.iter.previous();
               int b1 = this.iter.previous();
               int b0 = this.iter.previous();
               this.o0 = this.calc0(b0, b1);
               this.o1 = this.calc1(b1, b2);
               this.state = 3;
               --this.offset;
               return this.o2 = this.calc2(b2, b3);
            case 8:
               this.state = 7;
               --this.offset;
               return this.o0;
            case 10:
               this.state = 6;
               --this.offset;
               return this.o0;
            case 11:
               this.state = 5;
               --this.offset;
               return this.o1;
            case 12:
               this.state = 9;
               --this.offset;
               return this.o0;
            case 13:
               this.state = 8;
               --this.offset;
               return this.o1;
            default:
               throw new NoSuchElementException();
         }
      }
   }

   public int peekPrevious() throws NoSuchElementException {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         switch (this.state) {
            case 2:
               return this.o0;
            case 3:
               return this.o1;
            case 5:
               return this.o0;
            case 6:
               this.iter.previous();
            case 4:
               this.iter.previous();
            case 0:
            case 1:
            case 7:
            case 9:
               int b3 = this.iter.previous();
               int b2 = this.iter.peekPrevious();
               this.iter.next();
               if (this.state == 4) {
                  this.iter.next();
               } else if (this.state == 6) {
                  this.iter.next();
                  this.iter.next();
               }

               return this.calc2(b2, b3);
            case 8:
               return this.o0;
            case 10:
               return this.o0;
            case 11:
               return this.o1;
            case 12:
               return this.o0;
            case 13:
               return this.o1;
            default:
               throw new NoSuchElementException();
         }
      }
   }

   public long getIndex() {
      return (long)this.offset;
   }
}

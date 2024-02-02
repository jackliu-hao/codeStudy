package org.wildfly.common.iteration;

import java.util.NoSuchElementException;
import org.wildfly.common.Assert;

abstract class Base32EncodingCodePointIterator extends CodePointIterator {
   private final ByteIterator iter;
   private final boolean addPadding;
   private int c0;
   private int c1;
   private int c2;
   private int c3;
   private int c4;
   private int c5;
   private int c6;
   private int c7;
   private int state;
   private int offset;

   Base32EncodingCodePointIterator(ByteIterator iter, boolean addPadding) {
      this.iter = iter;
      this.addPadding = addPadding;
   }

   public boolean hasNext() {
      return this.state == 0 && this.iter.hasNext() || this.state > 0 && this.state < 41;
   }

   public boolean hasPrevious() {
      return this.offset > 0;
   }

   abstract int calc0(int var1);

   abstract int calc1(int var1, int var2);

   abstract int calc2(int var1);

   abstract int calc3(int var1, int var2);

   abstract int calc4(int var1, int var2);

   abstract int calc5(int var1);

   abstract int calc6(int var1, int var2);

   abstract int calc7(int var1);

   public int next() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         ++this.offset;
         switch (this.state) {
            case 0:
               assert this.iter.hasNext();

               int b0 = this.iter.next();
               this.c0 = this.calc0(b0);
               if (!this.iter.hasNext()) {
                  this.c1 = this.calc1(b0, 0);
                  this.state = 10;
                  return this.c0;
               } else {
                  int b1 = this.iter.next();
                  this.c1 = this.calc1(b0, b1);
                  this.c2 = this.calc2(b1);
                  if (!this.iter.hasNext()) {
                     this.c3 = this.calc3(b1, 0);
                     this.state = 18;
                     return this.c0;
                  } else {
                     int b2 = this.iter.next();
                     this.c3 = this.calc3(b1, b2);
                     if (!this.iter.hasNext()) {
                        this.c4 = this.calc4(b2, 0);
                        this.state = 26;
                        return this.c0;
                     } else {
                        int b3 = this.iter.next();
                        this.c4 = this.calc4(b2, b3);
                        this.c5 = this.calc5(b3);
                        if (!this.iter.hasNext()) {
                           this.c6 = this.calc6(b3, 0);
                           this.state = 34;
                           return this.c0;
                        }

                        int b4 = this.iter.next();
                        this.c6 = this.calc6(b3, b4);
                        this.c7 = this.calc7(b4);
                        this.state = 2;
                        return this.c0;
                     }
                  }
               }
            case 1:
            case 9:
            case 17:
            case 25:
            case 33:
               ++this.state;
               return this.c0;
            case 2:
            case 18:
            case 26:
            case 34:
               ++this.state;
               return this.c1;
            case 3:
            case 19:
            case 27:
            case 35:
               ++this.state;
               return this.c2;
            case 4:
            case 28:
            case 36:
               ++this.state;
               return this.c3;
            case 5:
            case 37:
               ++this.state;
               return this.c4;
            case 6:
            case 38:
               ++this.state;
               return this.c5;
            case 7:
               this.state = 8;
               return this.c6;
            case 8:
               this.state = 0;
               return this.c7;
            case 10:
               this.state = this.addPadding ? 11 : 41;
               return this.c1;
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 21:
            case 22:
            case 23:
            case 30:
            case 31:
               ++this.state;
               return 61;
            case 16:
               this.state = 41;
               return 61;
            case 20:
               this.state = this.addPadding ? 21 : 42;
               return this.c3;
            case 24:
               this.state = 42;
               return 61;
            case 29:
               this.state = this.addPadding ? 30 : 43;
               return this.c4;
            case 32:
               this.state = 43;
               return 61;
            case 39:
               this.state = this.addPadding ? 40 : 44;
               return this.c6;
            case 40:
               this.state = 44;
               return 61;
            default:
               throw Assert.impossibleSwitchCase(this.state);
         }
      }
   }

   public int peekNext() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         switch (this.state) {
            case 0:
               assert this.iter.hasNext();

               int b0 = this.iter.next();
               this.c0 = this.calc0(b0);
               if (!this.iter.hasNext()) {
                  this.c1 = this.calc1(b0, 0);
                  this.state = 9;
                  return this.c0;
               } else {
                  int b1 = this.iter.next();
                  this.c1 = this.calc1(b0, b1);
                  this.c2 = this.calc2(b1);
                  if (!this.iter.hasNext()) {
                     this.c3 = this.calc3(b1, 0);
                     this.state = 17;
                     return this.c0;
                  } else {
                     int b2 = this.iter.next();
                     this.c3 = this.calc3(b1, b2);
                     if (!this.iter.hasNext()) {
                        this.c4 = this.calc4(b2, 0);
                        this.state = 25;
                        return this.c0;
                     } else {
                        int b3 = this.iter.next();
                        this.c4 = this.calc4(b2, b3);
                        this.c5 = this.calc5(b3);
                        if (!this.iter.hasNext()) {
                           this.c6 = this.calc6(b3, 0);
                           this.state = 33;
                           return this.c0;
                        }

                        int b4 = this.iter.next();
                        this.c6 = this.calc6(b3, b4);
                        this.c7 = this.calc7(b4);
                        this.state = 1;
                        return this.c0;
                     }
                  }
               }
            case 1:
            case 9:
            case 17:
            case 25:
            case 33:
               return this.c0;
            case 2:
            case 10:
            case 18:
            case 26:
            case 34:
               return this.c1;
            case 3:
            case 19:
            case 27:
            case 35:
               return this.c2;
            case 4:
            case 20:
            case 28:
            case 36:
               return this.c3;
            case 5:
            case 29:
            case 37:
               return this.c4;
            case 6:
            case 38:
               return this.c5;
            case 7:
            case 39:
               return this.c6;
            case 8:
               return this.c7;
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 21:
            case 22:
            case 23:
            case 24:
            case 30:
            case 31:
            case 32:
            case 40:
               return 61;
            default:
               throw Assert.impossibleSwitchCase(this.state);
         }
      }
   }

   public int previous() throws NoSuchElementException {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         --this.offset;
         switch (this.state) {
            case 2:
            case 10:
            case 18:
            case 26:
            case 34:
               --this.state;
               return this.c0;
            case 3:
            case 11:
            case 19:
            case 27:
            case 35:
               --this.state;
               return this.c1;
            case 4:
            case 20:
            case 28:
            case 36:
               --this.state;
               return this.c2;
            case 5:
            case 21:
            case 29:
            case 37:
               --this.state;
               return this.c3;
            case 6:
            case 30:
            case 38:
               --this.state;
               return this.c4;
            case 7:
            case 39:
               --this.state;
               return this.c5;
            case 8:
            case 40:
               --this.state;
               return this.c6;
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 22:
            case 23:
            case 24:
            case 31:
            case 32:
               --this.state;
               return 61;
            case 33:
               this.iter.previous();
            case 25:
               this.iter.previous();
            case 17:
               this.iter.previous();
            case 9:
               this.iter.previous();
            case 0:
            case 1:
            case 45:
               int b4 = this.iter.previous();
               int b3 = this.iter.previous();
               int b2 = this.iter.previous();
               int b1 = this.iter.previous();
               int b0 = this.iter.previous();
               this.c0 = this.calc0(b0);
               this.c1 = this.calc1(b0, b1);
               this.c2 = this.calc2(b1);
               this.c3 = this.calc3(b1, b2);
               this.c4 = this.calc4(b2, b3);
               this.c5 = this.calc5(b3);
               this.c6 = this.calc6(b3, b4);
               this.c7 = this.calc7(b4);
               this.state = 8;
               return this.c7;
            case 41:
               if (this.addPadding) {
                  this.state = 16;
                  return 61;
               }

               this.state = 10;
               return this.c1;
            case 42:
               if (this.addPadding) {
                  this.state = 24;
                  return 61;
               }

               this.state = 20;
               return this.c3;
            case 43:
               if (this.addPadding) {
                  this.state = 32;
                  return 61;
               }

               this.state = 29;
               return this.c4;
            case 44:
               if (this.addPadding) {
                  this.state = 40;
                  return 61;
               }

               this.state = 39;
               return this.c6;
            default:
               throw Assert.impossibleSwitchCase(this.state);
         }
      }
   }

   public int peekPrevious() throws NoSuchElementException {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         switch (this.state) {
            case 2:
            case 10:
            case 18:
            case 26:
            case 34:
               return this.c0;
            case 3:
            case 11:
            case 19:
            case 27:
            case 35:
               return this.c1;
            case 4:
            case 20:
            case 28:
            case 36:
               return this.c2;
            case 5:
            case 21:
            case 29:
            case 37:
               return this.c3;
            case 6:
            case 30:
            case 38:
               return this.c4;
            case 7:
            case 39:
               return this.c5;
            case 8:
            case 40:
               return this.c6;
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 22:
            case 23:
            case 24:
            case 31:
            case 32:
               return 61;
            case 33:
               this.iter.previous();
            case 25:
               this.iter.previous();
            case 17:
               this.iter.previous();
            case 9:
               this.iter.previous();
            case 0:
            case 1:
            case 45:
               int result = this.calc7(this.iter.peekPrevious());
               if (this.state == 9) {
                  this.iter.next();
               } else if (this.state == 17) {
                  this.iter.next();
                  this.iter.next();
               } else if (this.state == 25) {
                  this.iter.next();
                  this.iter.next();
                  this.iter.next();
               } else if (this.state == 33) {
                  this.iter.next();
                  this.iter.next();
                  this.iter.next();
                  this.iter.next();
               }

               return result;
            case 41:
               return this.addPadding ? 61 : this.c1;
            case 42:
               return this.addPadding ? 61 : this.c3;
            case 43:
               return this.addPadding ? 61 : this.c4;
            case 44:
               return this.addPadding ? 61 : this.c6;
            default:
               throw Assert.impossibleSwitchCase(this.state);
         }
      }
   }

   public long getIndex() {
      return (long)this.offset;
   }
}

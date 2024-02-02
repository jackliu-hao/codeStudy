package org.wildfly.common.iteration;

import java.util.NoSuchElementException;
import org.wildfly.common.Assert;

abstract class Base64EncodingIterator extends CodePointIterator {
   private final ByteIterator iter;
   private final boolean addPadding;
   private int c0;
   private int c1;
   private int c2;
   private int c3;
   private int state;
   private int offset;

   Base64EncodingIterator(ByteIterator iter, boolean addPadding) {
      this.iter = iter;
      this.addPadding = addPadding;
   }

   public boolean hasNext() {
      return this.state == 0 && this.iter.hasNext() || this.state > 0 && this.state < 13;
   }

   public boolean hasPrevious() {
      return this.offset > 0;
   }

   abstract int calc0(int var1);

   abstract int calc1(int var1, int var2);

   abstract int calc2(int var1, int var2);

   abstract int calc3(int var1);

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
                  this.state = 6;
                  return this.c0;
               } else {
                  int b1 = this.iter.next();
                  this.c1 = this.calc1(b0, b1);
                  if (!this.iter.hasNext()) {
                     this.c2 = this.calc2(b1, 0);
                     this.state = 10;
                     return this.c0;
                  }

                  int b2 = this.iter.next();
                  this.c2 = this.calc2(b1, b2);
                  this.c3 = this.calc3(b2);
                  this.state = 2;
                  return this.c0;
               }
            case 1:
               this.state = 2;
               return this.c0;
            case 2:
               this.state = 3;
               return this.c1;
            case 3:
               this.state = 4;
               return this.c2;
            case 4:
               this.state = 0;
               return this.c3;
            case 5:
               this.state = 6;
               return this.c0;
            case 6:
               this.state = this.addPadding ? 7 : 13;
               return this.c1;
            case 7:
               this.state = 8;
               return 61;
            case 8:
               this.state = 13;
               return 61;
            case 9:
               this.state = 10;
               return this.c0;
            case 10:
               this.state = 11;
               return this.c1;
            case 11:
               this.state = this.addPadding ? 12 : 14;
               return this.c2;
            case 12:
               this.state = 14;
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
                  this.state = 5;
                  return this.c0;
               } else {
                  int b1 = this.iter.next();
                  this.c1 = this.calc1(b0, b1);
                  if (!this.iter.hasNext()) {
                     this.c2 = this.calc2(b1, 0);
                     this.state = 9;
                     return this.c0;
                  }

                  int b2 = this.iter.next();
                  this.c2 = this.calc2(b1, b2);
                  this.c3 = this.calc3(b2);
                  this.state = 1;
                  return this.c0;
               }
            case 1:
               return this.c0;
            case 2:
               return this.c1;
            case 3:
               return this.c2;
            case 4:
               return this.c3;
            case 5:
               return this.c0;
            case 6:
               return this.c1;
            case 7:
               return 61;
            case 8:
               return 61;
            case 9:
               return this.c0;
            case 10:
               return this.c1;
            case 11:
               return this.c2;
            case 12:
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
            case 0:
            case 1:
            case 5:
            case 9:
            case 15:
               int b2 = this.iter.previous();
               int b1 = this.iter.previous();
               int b0 = this.iter.previous();
               this.c0 = this.calc0(b0);
               this.c1 = this.calc1(b0, b1);
               this.c2 = this.calc2(b1, b2);
               this.c3 = this.calc3(b2);
               this.state = 4;
               return this.c3;
            case 2:
               this.state = 1;
               return this.c0;
            case 3:
               this.state = 2;
               return this.c1;
            case 4:
               this.state = 3;
               return this.c2;
            case 6:
               this.state = 5;
               return this.c0;
            case 7:
               this.state = 6;
               return this.c1;
            case 8:
               this.state = 7;
               return 61;
            case 10:
               this.state = 9;
               return this.c0;
            case 11:
               this.state = 10;
               return this.c1;
            case 12:
               this.state = 11;
               return this.c2;
            case 13:
               this.state = 8;
               return 61;
            case 14:
               this.state = 12;
               return 61;
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
            case 0:
            case 1:
            case 5:
            case 9:
            case 15:
               return this.calc3(this.iter.peekPrevious());
            case 2:
               return this.c0;
            case 3:
               return this.c1;
            case 4:
               return this.c2;
            case 6:
               return this.c0;
            case 7:
               return this.c1;
            case 8:
               return 61;
            case 10:
               return this.c0;
            case 11:
               return this.c1;
            case 12:
               return this.c2;
            case 13:
               return 61;
            case 14:
               return 61;
            default:
               throw Assert.impossibleSwitchCase(this.state);
         }
      }
   }

   public long getIndex() {
      return (long)this.offset;
   }
}

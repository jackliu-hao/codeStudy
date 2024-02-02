package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

class Utf8DecodingIterator extends CodePointIterator {
   private final ByteIterator iter;
   private long offset = 0L;

   Utf8DecodingIterator(ByteIterator iter) {
      this.iter = iter;
   }

   public boolean hasNext() {
      return this.iter.hasNext();
   }

   public boolean hasPrevious() {
      return this.offset > 0L;
   }

   private void seekToNext() {
      while(true) {
         if (this.iter.hasNext()) {
            int b = this.iter.next();
            if ((b & 192) == 128) {
               continue;
            }

            this.iter.previous();
            return;
         }

         return;
      }
   }

   private void seekToPrev() {
      while(true) {
         if (this.iter.hasPrevious()) {
            int b = this.iter.previous();
            if ((b & 192) == 128) {
               continue;
            }

            return;
         }

         return;
      }
   }

   public int next() {
      if (!this.iter.hasNext()) {
         throw new NoSuchElementException();
      } else {
         ++this.offset;
         int a = this.iter.next();
         if ((a & 128) == 0) {
            return a;
         } else if ((a & 192) == 128) {
            this.seekToNext();
            return 65533;
         } else if (!this.iter.hasNext()) {
            return 65533;
         } else {
            int b = this.iter.next();
            if ((b & 192) != 128) {
               this.seekToNext();
               return 65533;
            } else if ((a & 224) == 192) {
               return (a & 31) << 6 | b & 63;
            } else if (!this.iter.hasNext()) {
               return 65533;
            } else {
               int c = this.iter.next();
               if ((c & 192) != 128) {
                  this.seekToNext();
                  return 65533;
               } else if ((a & 240) == 224) {
                  return (a & 15) << 12 | (b & 63) << 6 | c & 63;
               } else if (!this.iter.hasNext()) {
                  return 65533;
               } else {
                  int d = this.iter.next();
                  if ((d & 192) != 128) {
                     this.seekToNext();
                     return 65533;
                  } else if ((a & 248) == 240) {
                     return (a & 7) << 18 | (b & 63) << 12 | (c & 63) << 6 | d & 63;
                  } else {
                     this.seekToNext();
                     return 65533;
                  }
               }
            }
         }
      }
   }

   public int peekNext() throws NoSuchElementException {
      if (!this.iter.hasNext()) {
         throw new NoSuchElementException();
      } else {
         int a = this.iter.peekNext();
         if ((a & 128) == 0) {
            return a;
         } else if ((a & 192) == 128) {
            return 65533;
         } else {
            this.iter.next();
            if (!this.iter.hasNext()) {
               this.iter.previous();
               return 65533;
            } else {
               int b = this.iter.peekNext();
               if ((b & 192) != 128) {
                  this.iter.previous();
                  return 65533;
               } else if ((a & 224) == 192) {
                  this.iter.previous();
                  return (a & 31) << 6 | b & 63;
               } else {
                  this.iter.next();
                  if (!this.iter.hasNext()) {
                     this.iter.previous();
                     this.iter.previous();
                     return 65533;
                  } else {
                     int c = this.iter.peekNext();
                     if ((c & 192) != 128) {
                        this.iter.previous();
                        this.iter.previous();
                        return 65533;
                     } else if ((a & 240) == 224) {
                        this.iter.previous();
                        this.iter.previous();
                        return (a & 15) << 12 | (b & 63) << 6 | c & 63;
                     } else {
                        this.iter.next();
                        if (!this.iter.hasNext()) {
                           this.iter.previous();
                           this.iter.previous();
                           this.iter.previous();
                           return 65533;
                        } else {
                           int d = this.iter.peekNext();
                           if ((d & 192) != 128) {
                              this.iter.previous();
                              this.iter.previous();
                              this.iter.previous();
                              return 65533;
                           } else if ((a & 248) == 240) {
                              this.iter.previous();
                              this.iter.previous();
                              this.iter.previous();
                              return (a & 7) << 18 | (b & 63) << 12 | (c & 63) << 6 | d & 63;
                           } else {
                              this.iter.previous();
                              this.iter.previous();
                              this.iter.previous();
                              return 65533;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public int previous() {
      if (!this.iter.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         --this.offset;
         int a = this.iter.previous();
         if ((a & 128) == 0) {
            return a;
         } else if ((a & 192) != 128) {
            this.seekToPrev();
            return 65533;
         } else {
            int cp = a & 63;
            a = this.iter.previous();
            if ((a & 224) == 192) {
               return (a & 31) << 6 | cp;
            } else if ((a & 192) != 128) {
               this.seekToPrev();
               return 65533;
            } else {
               cp |= (a & 63) << 6;
               a = this.iter.previous();
               if ((a & 240) == 224) {
                  return (a & 15) << 12 | cp;
               } else if ((a & 192) != 128) {
                  this.seekToPrev();
                  return 65533;
               } else {
                  cp |= (a & 63) << 12;
                  a = this.iter.previous();
                  if ((a & 248) == 240) {
                     return (a & 7) << 18 | cp;
                  } else {
                     this.seekToPrev();
                     return 65533;
                  }
               }
            }
         }
      }
   }

   public int peekPrevious() throws NoSuchElementException {
      if (!this.iter.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         int a = this.iter.peekPrevious();
         if ((a & 128) == 0) {
            return a;
         } else if ((a & 192) != 128) {
            return 65533;
         } else {
            int cp = a & 63;
            this.iter.previous();
            a = this.iter.peekPrevious();
            if ((a & 224) == 192) {
               this.iter.next();
               return (a & 31) << 6 | cp;
            } else if ((a & 192) != 128) {
               this.iter.next();
               return 65533;
            } else {
               cp |= (a & 63) << 6;
               this.iter.previous();
               a = this.iter.peekPrevious();
               if ((a & 240) == 224) {
                  this.iter.next();
                  this.iter.next();
                  return (a & 15) << 12 | cp;
               } else if ((a & 192) != 128) {
                  this.iter.next();
                  this.iter.next();
                  return 65533;
               } else {
                  cp |= (a & 63) << 12;
                  this.iter.previous();
                  a = this.iter.peekPrevious();
                  if ((a & 248) == 240) {
                     this.iter.next();
                     this.iter.next();
                     this.iter.next();
                     return (a & 7) << 18 | cp;
                  } else {
                     this.iter.next();
                     this.iter.next();
                     this.iter.next();
                     return 65533;
                  }
               }
            }
         }
      }
   }

   public long getIndex() {
      return this.offset;
   }
}

package com.google.zxing.common;

import java.util.Arrays;

public final class BitArray implements Cloneable {
   private int[] bits;
   private int size;

   public BitArray() {
      this.size = 0;
      this.bits = new int[1];
   }

   public BitArray(int size) {
      this.size = size;
      this.bits = makeArray(size);
   }

   BitArray(int[] bits, int size) {
      this.bits = bits;
      this.size = size;
   }

   public int getSize() {
      return this.size;
   }

   public int getSizeInBytes() {
      return (this.size + 7) / 8;
   }

   private void ensureCapacity(int size) {
      if (size > this.bits.length << 5) {
         int[] newBits = makeArray(size);
         System.arraycopy(this.bits, 0, newBits, 0, this.bits.length);
         this.bits = newBits;
      }

   }

   public boolean get(int i) {
      return (this.bits[i / 32] & 1 << (i & 31)) != 0;
   }

   public void set(int i) {
      int[] var10000 = this.bits;
      var10000[i / 32] |= 1 << (i & 31);
   }

   public void flip(int i) {
      int[] var10000 = this.bits;
      var10000[i / 32] ^= 1 << (i & 31);
   }

   public int getNextSet(int from) {
      if (from >= this.size) {
         return this.size;
      } else {
         int bitsOffset = from / 32;

         int currentBits;
         for(currentBits = this.bits[bitsOffset] & ~((1 << (from & 31)) - 1); currentBits == 0; currentBits = this.bits[bitsOffset]) {
            ++bitsOffset;
            if (bitsOffset == this.bits.length) {
               return this.size;
            }
         }

         int result;
         if ((result = (bitsOffset << 5) + Integer.numberOfTrailingZeros(currentBits)) > this.size) {
            return this.size;
         } else {
            return result;
         }
      }
   }

   public int getNextUnset(int from) {
      if (from >= this.size) {
         return this.size;
      } else {
         int bitsOffset = from / 32;

         int currentBits;
         for(currentBits = ~this.bits[bitsOffset] & ~((1 << (from & 31)) - 1); currentBits == 0; currentBits = ~this.bits[bitsOffset]) {
            ++bitsOffset;
            if (bitsOffset == this.bits.length) {
               return this.size;
            }
         }

         int result;
         if ((result = (bitsOffset << 5) + Integer.numberOfTrailingZeros(currentBits)) > this.size) {
            return this.size;
         } else {
            return result;
         }
      }
   }

   public void setBulk(int i, int newBits) {
      this.bits[i / 32] = newBits;
   }

   public void setRange(int start, int end) {
      if (end >= start && start >= 0 && end <= this.size) {
         if (end != start) {
            --end;
            int firstInt = start / 32;
            int lastInt = end / 32;

            for(int i = firstInt; i <= lastInt; ++i) {
               int firstBit = i > firstInt ? 0 : start & 31;
               int lastBit = i < lastInt ? 31 : end & 31;
               int mask = (2 << lastBit) - (1 << firstBit);
               int[] var10000 = this.bits;
               var10000[i] |= mask;
            }

         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void clear() {
      int max = this.bits.length;

      for(int i = 0; i < max; ++i) {
         this.bits[i] = 0;
      }

   }

   public boolean isRange(int start, int end, boolean value) {
      if (end >= start && start >= 0 && end <= this.size) {
         if (end == start) {
            return true;
         } else {
            --end;
            int firstInt = start / 32;
            int lastInt = end / 32;

            for(int i = firstInt; i <= lastInt; ++i) {
               int firstBit = i > firstInt ? 0 : start & 31;
               int lastBit = i < lastInt ? 31 : end & 31;
               int mask = (2 << lastBit) - (1 << firstBit);
               if ((this.bits[i] & mask) != (value ? mask : 0)) {
                  return false;
               }
            }

            return true;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void appendBit(boolean bit) {
      this.ensureCapacity(this.size + 1);
      if (bit) {
         int[] var10000 = this.bits;
         int var10001 = this.size / 32;
         var10000[var10001] |= 1 << (this.size & 31);
      }

      ++this.size;
   }

   public void appendBits(int value, int numBits) {
      if (numBits >= 0 && numBits <= 32) {
         this.ensureCapacity(this.size + numBits);

         for(int numBitsLeft = numBits; numBitsLeft > 0; --numBitsLeft) {
            this.appendBit((value >> numBitsLeft - 1 & 1) == 1);
         }

      } else {
         throw new IllegalArgumentException("Num bits must be between 0 and 32");
      }
   }

   public void appendBitArray(BitArray other) {
      int otherSize = other.size;
      this.ensureCapacity(this.size + otherSize);

      for(int i = 0; i < otherSize; ++i) {
         this.appendBit(other.get(i));
      }

   }

   public void xor(BitArray other) {
      if (this.size != other.size) {
         throw new IllegalArgumentException("Sizes don't match");
      } else {
         for(int i = 0; i < this.bits.length; ++i) {
            int[] var10000 = this.bits;
            var10000[i] ^= other.bits[i];
         }

      }
   }

   public void toBytes(int bitOffset, byte[] array, int offset, int numBytes) {
      for(int i = 0; i < numBytes; ++i) {
         int theByte = 0;

         for(int j = 0; j < 8; ++j) {
            if (this.get(bitOffset)) {
               theByte |= 1 << 7 - j;
            }

            ++bitOffset;
         }

         array[offset + i] = (byte)theByte;
      }

   }

   public int[] getBitArray() {
      return this.bits;
   }

   public void reverse() {
      int[] newBits = new int[this.bits.length];
      int len;
      int oldBitsLen = (len = (this.size - 1) / 32) + 1;

      int leftOffset;
      for(leftOffset = 0; leftOffset < oldBitsLen; ++leftOffset) {
         long x = (x = (x = (x = (x = (x = (long)this.bits[leftOffset]) >> 1 & 1431655765L | (x & 1431655765L) << 1) >> 2 & 858993459L | (x & 858993459L) << 2) >> 4 & 252645135L | (x & 252645135L) << 4) >> 8 & 16711935L | (x & 16711935L) << 8) >> 16 & 65535L | (x & 65535L) << 16;
         newBits[len - leftOffset] = (int)x;
      }

      if (this.size != oldBitsLen << 5) {
         leftOffset = (oldBitsLen << 5) - this.size;
         int currentInt = newBits[0] >>> leftOffset;

         for(int i = 1; i < oldBitsLen; ++i) {
            int nextInt = newBits[i];
            currentInt |= nextInt << 32 - leftOffset;
            newBits[i - 1] = currentInt;
            currentInt = nextInt >>> leftOffset;
         }

         newBits[oldBitsLen - 1] = currentInt;
      }

      this.bits = newBits;
   }

   private static int[] makeArray(int size) {
      return new int[(size + 31) / 32];
   }

   public boolean equals(Object o) {
      if (!(o instanceof BitArray)) {
         return false;
      } else {
         BitArray other = (BitArray)o;
         return this.size == other.size && Arrays.equals(this.bits, other.bits);
      }
   }

   public int hashCode() {
      return 31 * this.size + Arrays.hashCode(this.bits);
   }

   public String toString() {
      StringBuilder result = new StringBuilder(this.size);

      for(int i = 0; i < this.size; ++i) {
         if ((i & 7) == 0) {
            result.append(' ');
         }

         result.append((char)(this.get(i) ? 'X' : '.'));
      }

      return result.toString();
   }

   public BitArray clone() {
      return new BitArray((int[])this.bits.clone(), this.size);
   }
}

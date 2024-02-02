/*     */ package com.google.zxing.common;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class BitArray
/*     */   implements Cloneable
/*     */ {
/*     */   private int[] bits;
/*     */   private int size;
/*     */   
/*     */   public BitArray() {
/*  32 */     this.size = 0;
/*  33 */     this.bits = new int[1];
/*     */   }
/*     */   
/*     */   public BitArray(int size) {
/*  37 */     this.size = size;
/*  38 */     this.bits = makeArray(size);
/*     */   }
/*     */ 
/*     */   
/*     */   BitArray(int[] bits, int size) {
/*  43 */     this.bits = bits;
/*  44 */     this.size = size;
/*     */   }
/*     */   
/*     */   public int getSize() {
/*  48 */     return this.size;
/*     */   }
/*     */   
/*     */   public int getSizeInBytes() {
/*  52 */     return (this.size + 7) / 8;
/*     */   }
/*     */   
/*     */   private void ensureCapacity(int size) {
/*  56 */     if (size > this.bits.length << 5) {
/*  57 */       int[] newBits = makeArray(size);
/*  58 */       System.arraycopy(this.bits, 0, newBits, 0, this.bits.length);
/*  59 */       this.bits = newBits;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean get(int i) {
/*  68 */     return ((this.bits[i / 32] & 1 << (i & 0x1F)) != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(int i) {
/*  77 */     this.bits[i / 32] = this.bits[i / 32] | 1 << (i & 0x1F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flip(int i) {
/*  86 */     this.bits[i / 32] = this.bits[i / 32] ^ 1 << (i & 0x1F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNextSet(int from) {
/*  96 */     if (from >= this.size) {
/*  97 */       return this.size;
/*     */     }
/*  99 */     int bitsOffset = from / 32;
/*     */ 
/*     */     
/* 102 */     int currentBits = this.bits[bitsOffset] & ((1 << (from & 0x1F)) - 1 ^ 0xFFFFFFFF);
/* 103 */     while (currentBits == 0) {
/* 104 */       if (++bitsOffset == this.bits.length) {
/* 105 */         return this.size;
/*     */       }
/* 107 */       currentBits = this.bits[bitsOffset];
/*     */     } 
/*     */     int result;
/* 110 */     return ((result = (bitsOffset << 5) + Integer.numberOfTrailingZeros(currentBits)) > this.size) ? this.size : result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNextUnset(int from) {
/* 119 */     if (from >= this.size) {
/* 120 */       return this.size;
/*     */     }
/* 122 */     int bitsOffset = from / 32;
/*     */ 
/*     */     
/* 125 */     int currentBits = (this.bits[bitsOffset] ^ 0xFFFFFFFF) & ((1 << (from & 0x1F)) - 1 ^ 0xFFFFFFFF);
/* 126 */     while (currentBits == 0) {
/* 127 */       if (++bitsOffset == this.bits.length) {
/* 128 */         return this.size;
/*     */       }
/* 130 */       currentBits = this.bits[bitsOffset] ^ 0xFFFFFFFF;
/*     */     } 
/*     */     int result;
/* 133 */     return ((result = (bitsOffset << 5) + Integer.numberOfTrailingZeros(currentBits)) > this.size) ? this.size : result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBulk(int i, int newBits) {
/* 144 */     this.bits[i / 32] = newBits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRange(int start, int end) {
/* 154 */     if (end < start || start < 0 || end > this.size) {
/* 155 */       throw new IllegalArgumentException();
/*     */     }
/* 157 */     if (end == start) {
/*     */       return;
/*     */     }
/* 160 */     end--;
/* 161 */     int firstInt = start / 32;
/* 162 */     int lastInt = end / 32;
/* 163 */     for (int i = firstInt; i <= lastInt; i++) {
/* 164 */       int firstBit = (i > firstInt) ? 0 : (start & 0x1F);
/* 165 */       int lastBit = (i < lastInt) ? 31 : (end & 0x1F);
/*     */       
/* 167 */       int mask = (2 << lastBit) - (1 << firstBit);
/* 168 */       this.bits[i] = this.bits[i] | mask;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 176 */     int max = this.bits.length;
/* 177 */     for (int i = 0; i < max; i++) {
/* 178 */       this.bits[i] = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRange(int start, int end, boolean value) {
/* 192 */     if (end < start || start < 0 || end > this.size) {
/* 193 */       throw new IllegalArgumentException();
/*     */     }
/* 195 */     if (end == start) {
/* 196 */       return true;
/*     */     }
/* 198 */     end--;
/* 199 */     int firstInt = start / 32;
/* 200 */     int lastInt = end / 32;
/* 201 */     for (int i = firstInt; i <= lastInt; i++) {
/* 202 */       int firstBit = (i > firstInt) ? 0 : (start & 0x1F);
/* 203 */       int lastBit = (i < lastInt) ? 31 : (end & 0x1F);
/*     */       
/* 205 */       int mask = (2 << lastBit) - (1 << firstBit);
/*     */ 
/*     */ 
/*     */       
/* 209 */       if ((this.bits[i] & mask) != (value ? mask : 0)) {
/* 210 */         return false;
/*     */       }
/*     */     } 
/* 213 */     return true;
/*     */   }
/*     */   
/*     */   public void appendBit(boolean bit) {
/* 217 */     ensureCapacity(this.size + 1);
/* 218 */     if (bit) {
/* 219 */       this.bits[this.size / 32] = this.bits[this.size / 32] | 1 << (this.size & 0x1F);
/*     */     }
/* 221 */     this.size++;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void appendBits(int value, int numBits) {
/* 233 */     if (numBits < 0 || numBits > 32) {
/* 234 */       throw new IllegalArgumentException("Num bits must be between 0 and 32");
/*     */     }
/* 236 */     ensureCapacity(this.size + numBits);
/* 237 */     for (int numBitsLeft = numBits; numBitsLeft > 0; numBitsLeft--) {
/* 238 */       appendBit(((value >> numBitsLeft - 1 & 0x1) == 1));
/*     */     }
/*     */   }
/*     */   
/*     */   public void appendBitArray(BitArray other) {
/* 243 */     int otherSize = other.size;
/* 244 */     ensureCapacity(this.size + otherSize);
/* 245 */     for (int i = 0; i < otherSize; i++) {
/* 246 */       appendBit(other.get(i));
/*     */     }
/*     */   }
/*     */   
/*     */   public void xor(BitArray other) {
/* 251 */     if (this.size != other.size) {
/* 252 */       throw new IllegalArgumentException("Sizes don't match");
/*     */     }
/* 254 */     for (int i = 0; i < this.bits.length; i++)
/*     */     {
/*     */       
/* 257 */       this.bits[i] = this.bits[i] ^ other.bits[i];
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void toBytes(int bitOffset, byte[] array, int offset, int numBytes) {
/* 270 */     for (int i = 0; i < numBytes; i++) {
/* 271 */       int theByte = 0;
/* 272 */       for (int j = 0; j < 8; j++) {
/* 273 */         if (get(bitOffset)) {
/* 274 */           theByte |= 1 << 7 - j;
/*     */         }
/* 276 */         bitOffset++;
/*     */       } 
/* 278 */       array[offset + i] = (byte)theByte;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] getBitArray() {
/* 287 */     return this.bits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reverse() {
/* 294 */     int[] newBits = new int[this.bits.length];
/*     */ 
/*     */     
/* 297 */     int len, oldBitsLen = (len = (this.size - 1) / 32) + 1;
/* 298 */     for (int i = 0; i < oldBitsLen; i++) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 304 */       long x = (x = (x = (x = (x = (x = this.bits[i]) >> 1L & 0x55555555L | (x & 0x55555555L) << 1L) >> 2L & 0x33333333L | (x & 0x33333333L) << 2L) >> 4L & 0xF0F0F0FL | (x & 0xF0F0F0FL) << 4L) >> 8L & 0xFF00FFL | (x & 0xFF00FFL) << 8L) >> 16L & 0xFFFFL | (x & 0xFFFFL) << 16L;
/* 305 */       newBits[len - i] = (int)x;
/*     */     } 
/*     */     
/* 308 */     if (this.size != oldBitsLen << 5) {
/* 309 */       int leftOffset = (oldBitsLen << 5) - this.size;
/* 310 */       int currentInt = newBits[0] >>> leftOffset;
/* 311 */       for (int j = 1; j < oldBitsLen; j++) {
/* 312 */         int nextInt = newBits[j];
/* 313 */         currentInt |= nextInt << 32 - leftOffset;
/* 314 */         newBits[j - 1] = currentInt;
/* 315 */         currentInt = nextInt >>> leftOffset;
/*     */       } 
/* 317 */       newBits[oldBitsLen - 1] = currentInt;
/*     */     } 
/* 319 */     this.bits = newBits;
/*     */   }
/*     */   
/*     */   private static int[] makeArray(int size) {
/* 323 */     return new int[(size + 31) / 32];
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 328 */     if (!(o instanceof BitArray)) {
/* 329 */       return false;
/*     */     }
/* 331 */     BitArray other = (BitArray)o;
/* 332 */     return (this.size == other.size && Arrays.equals(this.bits, other.bits));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 337 */     return 31 * this.size + Arrays.hashCode(this.bits);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 342 */     StringBuilder result = new StringBuilder(this.size);
/* 343 */     for (int i = 0; i < this.size; i++) {
/* 344 */       if ((i & 0x7) == 0) {
/* 345 */         result.append(' ');
/*     */       }
/* 347 */       result.append(get(i) ? 88 : 46);
/*     */     } 
/* 349 */     return result.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public BitArray clone() {
/* 354 */     return new BitArray((int[])this.bits.clone(), this.size);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\BitArray.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
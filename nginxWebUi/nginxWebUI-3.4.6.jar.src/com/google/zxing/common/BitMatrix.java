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
/*     */ public final class BitMatrix
/*     */   implements Cloneable
/*     */ {
/*     */   private final int width;
/*     */   private final int height;
/*     */   private final int rowSize;
/*     */   private final int[] bits;
/*     */   
/*     */   public BitMatrix(int dimension) {
/*  45 */     this(dimension, dimension);
/*     */   }
/*     */   
/*     */   public BitMatrix(int width, int height) {
/*  49 */     if (width <= 0 || height <= 0) {
/*  50 */       throw new IllegalArgumentException("Both dimensions must be greater than 0");
/*     */     }
/*  52 */     this.width = width;
/*  53 */     this.height = height;
/*  54 */     this.rowSize = (width + 31) / 32;
/*  55 */     this.bits = new int[this.rowSize * height];
/*     */   }
/*     */   
/*     */   private BitMatrix(int width, int height, int rowSize, int[] bits) {
/*  59 */     this.width = width;
/*  60 */     this.height = height;
/*  61 */     this.rowSize = rowSize;
/*  62 */     this.bits = bits;
/*     */   }
/*     */   
/*     */   public static BitMatrix parse(String stringRepresentation, String setString, String unsetString) {
/*  66 */     if (stringRepresentation == null) {
/*  67 */       throw new IllegalArgumentException();
/*     */     }
/*     */     
/*  70 */     boolean[] bits = new boolean[stringRepresentation.length()];
/*  71 */     int bitsPos = 0;
/*  72 */     int rowStartPos = 0;
/*  73 */     int rowLength = -1;
/*  74 */     int nRows = 0;
/*  75 */     int pos = 0;
/*  76 */     while (pos < stringRepresentation.length()) {
/*  77 */       if (stringRepresentation.charAt(pos) == '\n' || stringRepresentation
/*  78 */         .charAt(pos) == '\r') {
/*  79 */         if (bitsPos > rowStartPos) {
/*  80 */           if (rowLength == -1) {
/*  81 */             rowLength = bitsPos - rowStartPos;
/*  82 */           } else if (bitsPos - rowStartPos != rowLength) {
/*  83 */             throw new IllegalArgumentException("row lengths do not match");
/*     */           } 
/*  85 */           rowStartPos = bitsPos;
/*  86 */           nRows++;
/*     */         } 
/*  88 */         pos++; continue;
/*  89 */       }  if (stringRepresentation.substring(pos, pos + setString.length()).equals(setString)) {
/*  90 */         pos += setString.length();
/*  91 */         bits[bitsPos] = true;
/*  92 */         bitsPos++; continue;
/*  93 */       }  if (stringRepresentation.substring(pos, pos + unsetString.length()).equals(unsetString)) {
/*  94 */         pos += unsetString.length();
/*  95 */         bits[bitsPos] = false;
/*  96 */         bitsPos++; continue;
/*     */       } 
/*  98 */       throw new IllegalArgumentException("illegal character encountered: " + stringRepresentation
/*  99 */           .substring(pos));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 104 */     if (bitsPos > rowStartPos) {
/* 105 */       if (rowLength == -1) {
/* 106 */         rowLength = bitsPos - rowStartPos;
/* 107 */       } else if (bitsPos - rowStartPos != rowLength) {
/* 108 */         throw new IllegalArgumentException("row lengths do not match");
/*     */       } 
/* 110 */       nRows++;
/*     */     } 
/*     */     
/* 113 */     BitMatrix matrix = new BitMatrix(rowLength, nRows);
/* 114 */     for (int i = 0; i < bitsPos; i++) {
/* 115 */       if (bits[i]) {
/* 116 */         matrix.set(i % rowLength, i / rowLength);
/*     */       }
/*     */     } 
/* 119 */     return matrix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean get(int x, int y) {
/* 130 */     int offset = y * this.rowSize + x / 32;
/* 131 */     return ((this.bits[offset] >>> (x & 0x1F) & 0x1) != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(int x, int y) {
/* 141 */     int offset = y * this.rowSize + x / 32;
/* 142 */     this.bits[offset] = this.bits[offset] | 1 << (x & 0x1F);
/*     */   }
/*     */   
/*     */   public void unset(int x, int y) {
/* 146 */     int offset = y * this.rowSize + x / 32;
/* 147 */     this.bits[offset] = this.bits[offset] & (1 << (x & 0x1F) ^ 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flip(int x, int y) {
/* 157 */     int offset = y * this.rowSize + x / 32;
/* 158 */     this.bits[offset] = this.bits[offset] ^ 1 << (x & 0x1F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void xor(BitMatrix mask) {
/* 168 */     if (this.width != mask.getWidth() || this.height != mask.getHeight() || this.rowSize != mask
/* 169 */       .getRowSize()) {
/* 170 */       throw new IllegalArgumentException("input matrix dimensions do not match");
/*     */     }
/* 172 */     BitArray rowArray = new BitArray(this.width / 32 + 1);
/* 173 */     for (int y = 0; y < this.height; y++) {
/* 174 */       int offset = y * this.rowSize;
/* 175 */       int[] row = mask.getRow(y, rowArray).getBitArray();
/* 176 */       for (int x = 0; x < this.rowSize; x++) {
/* 177 */         this.bits[offset + x] = this.bits[offset + x] ^ row[x];
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 186 */     int max = this.bits.length;
/* 187 */     for (int i = 0; i < max; i++) {
/* 188 */       this.bits[i] = 0;
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
/*     */   public void setRegion(int left, int top, int width, int height) {
/* 201 */     if (top < 0 || left < 0) {
/* 202 */       throw new IllegalArgumentException("Left and top must be nonnegative");
/*     */     }
/* 204 */     if (height <= 0 || width <= 0) {
/* 205 */       throw new IllegalArgumentException("Height and width must be at least 1");
/*     */     }
/* 207 */     int right = left + width;
/*     */     int bottom;
/* 209 */     if ((bottom = top + height) > this.height || right > this.width) {
/* 210 */       throw new IllegalArgumentException("The region must fit inside the matrix");
/*     */     }
/* 212 */     for (int y = top; y < bottom; y++) {
/* 213 */       int offset = y * this.rowSize;
/* 214 */       for (int x = left; x < right; x++) {
/* 215 */         this.bits[offset + x / 32] = this.bits[offset + x / 32] | 1 << (x & 0x1F);
/*     */       }
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
/*     */   public BitArray getRow(int y, BitArray row) {
/* 229 */     if (row == null || row.getSize() < this.width) {
/* 230 */       row = new BitArray(this.width);
/*     */     } else {
/* 232 */       row.clear();
/*     */     } 
/* 234 */     int offset = y * this.rowSize;
/* 235 */     for (int x = 0; x < this.rowSize; x++) {
/* 236 */       row.setBulk(x << 5, this.bits[offset + x]);
/*     */     }
/* 238 */     return row;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRow(int y, BitArray row) {
/* 246 */     System.arraycopy(row.getBitArray(), 0, this.bits, y * this.rowSize, this.rowSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void rotate180() {
/* 253 */     int width = getWidth();
/* 254 */     int height = getHeight();
/* 255 */     BitArray topRow = new BitArray(width);
/* 256 */     BitArray bottomRow = new BitArray(width);
/* 257 */     for (int i = 0; i < (height + 1) / 2; i++) {
/* 258 */       topRow = getRow(i, topRow);
/* 259 */       bottomRow = getRow(height - 1 - i, bottomRow);
/* 260 */       topRow.reverse();
/* 261 */       bottomRow.reverse();
/* 262 */       setRow(i, bottomRow);
/* 263 */       setRow(height - 1 - i, topRow);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] getEnclosingRectangle() {
/* 273 */     int left = this.width;
/* 274 */     int top = this.height;
/* 275 */     int right = -1;
/* 276 */     int bottom = -1;
/*     */     
/* 278 */     for (int y = 0; y < this.height; y++) {
/* 279 */       for (int x32 = 0; x32 < this.rowSize; x32++) {
/*     */         int theBits;
/* 281 */         if ((theBits = this.bits[y * this.rowSize + x32]) != 0) {
/* 282 */           if (y < top) {
/* 283 */             top = y;
/*     */           }
/* 285 */           if (y > bottom) {
/* 286 */             bottom = y;
/*     */           }
/* 288 */           if (x32 << 5 < left) {
/* 289 */             int bit = 0;
/* 290 */             while (theBits << 31 - bit == 0) {
/* 291 */               bit++;
/*     */             }
/* 293 */             if ((x32 << 5) + bit < left) {
/* 294 */               left = (x32 << 5) + bit;
/*     */             }
/*     */           } 
/* 297 */           if ((x32 << 5) + 31 > right) {
/* 298 */             int bit = 31;
/* 299 */             while (theBits >>> bit == 0) {
/* 300 */               bit--;
/*     */             }
/* 302 */             if ((x32 << 5) + bit > right) {
/* 303 */               right = (x32 << 5) + bit;
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 310 */     if (right < left || bottom < top) {
/* 311 */       return null;
/*     */     }
/*     */     
/* 314 */     return new int[] { left, top, right - left + 1, bottom - top + 1 };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] getTopLeftOnBit() {
/* 323 */     int bitsOffset = 0;
/* 324 */     while (bitsOffset < this.bits.length && this.bits[bitsOffset] == 0) {
/* 325 */       bitsOffset++;
/*     */     }
/* 327 */     if (bitsOffset == this.bits.length) {
/* 328 */       return null;
/*     */     }
/* 330 */     int y = bitsOffset / this.rowSize;
/* 331 */     int x = bitsOffset % this.rowSize << 5;
/*     */     
/* 333 */     int theBits = this.bits[bitsOffset];
/* 334 */     int bit = 0;
/* 335 */     while (theBits << 31 - bit == 0) {
/* 336 */       bit++;
/*     */     }
/* 338 */     x += bit;
/* 339 */     return new int[] { x, y };
/*     */   }
/*     */   
/*     */   public int[] getBottomRightOnBit() {
/* 343 */     int bitsOffset = this.bits.length - 1;
/* 344 */     while (bitsOffset >= 0 && this.bits[bitsOffset] == 0) {
/* 345 */       bitsOffset--;
/*     */     }
/* 347 */     if (bitsOffset < 0) {
/* 348 */       return null;
/*     */     }
/*     */     
/* 351 */     int y = bitsOffset / this.rowSize;
/* 352 */     int x = bitsOffset % this.rowSize << 5;
/*     */     
/* 354 */     int theBits = this.bits[bitsOffset];
/* 355 */     int bit = 31;
/* 356 */     while (theBits >>> bit == 0) {
/* 357 */       bit--;
/*     */     }
/* 359 */     x += bit;
/*     */     
/* 361 */     return new int[] { x, y };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWidth() {
/* 368 */     return this.width;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHeight() {
/* 375 */     return this.height;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRowSize() {
/* 382 */     return this.rowSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 387 */     if (!(o instanceof BitMatrix)) {
/* 388 */       return false;
/*     */     }
/* 390 */     BitMatrix other = (BitMatrix)o;
/* 391 */     if (this.width == other.width && this.height == other.height && this.rowSize == other.rowSize && 
/* 392 */       Arrays.equals(this.bits, other.bits)) return true;
/*     */     
/*     */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 401 */     return (((this.width * 31 + this.width) * 31 + this.height) * 31 + this.rowSize) * 31 + Arrays.hashCode(this.bits);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 410 */     return toString("X ", "  ");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString(String setString, String unsetString) {
/* 419 */     return buildToString(setString, unsetString, "\n");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String toString(String setString, String unsetString, String lineSeparator) {
/* 431 */     return buildToString(setString, unsetString, lineSeparator);
/*     */   }
/*     */   
/*     */   private String buildToString(String setString, String unsetString, String lineSeparator) {
/* 435 */     StringBuilder result = new StringBuilder(this.height * (this.width + 1));
/* 436 */     for (int y = 0; y < this.height; y++) {
/* 437 */       for (int x = 0; x < this.width; x++) {
/* 438 */         result.append(get(x, y) ? setString : unsetString);
/*     */       }
/* 440 */       result.append(lineSeparator);
/*     */     } 
/* 442 */     return result.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public BitMatrix clone() {
/* 447 */     return new BitMatrix(this.width, this.height, this.rowSize, (int[])this.bits.clone());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\BitMatrix.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public final class BHSDCodec
/*     */   extends Codec
/*     */ {
/*     */   private final int b;
/*     */   private final int d;
/*     */   private final int h;
/*     */   private final int l;
/*     */   private final int s;
/*     */   private long cardinality;
/*     */   private final long smallest;
/*     */   private final long largest;
/*     */   private final long[] powers;
/*     */   
/*     */   public BHSDCodec(int b, int h) {
/* 111 */     this(b, h, 0, 0);
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
/*     */   public BHSDCodec(int b, int h, int s) {
/* 123 */     this(b, h, s, 0);
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
/*     */   public BHSDCodec(int b, int h, int s, int d) {
/* 136 */     if (b < 1 || b > 5) {
/* 137 */       throw new IllegalArgumentException("1<=b<=5");
/*     */     }
/* 139 */     if (h < 1 || h > 256) {
/* 140 */       throw new IllegalArgumentException("1<=h<=256");
/*     */     }
/* 142 */     if (s < 0 || s > 2) {
/* 143 */       throw new IllegalArgumentException("0<=s<=2");
/*     */     }
/* 145 */     if (d < 0 || d > 1) {
/* 146 */       throw new IllegalArgumentException("0<=d<=1");
/*     */     }
/* 148 */     if (b == 1 && h != 256) {
/* 149 */       throw new IllegalArgumentException("b=1 -> h=256");
/*     */     }
/* 151 */     if (h == 256 && b == 5) {
/* 152 */       throw new IllegalArgumentException("h=256 -> b!=5");
/*     */     }
/* 154 */     this.b = b;
/* 155 */     this.h = h;
/* 156 */     this.s = s;
/* 157 */     this.d = d;
/* 158 */     this.l = 256 - h;
/* 159 */     if (h == 1) {
/* 160 */       this.cardinality = (b * 255 + 1);
/*     */     } else {
/* 162 */       this.cardinality = (long)((long)(this.l * (1.0D - Math.pow(h, b)) / (1 - h)) + Math.pow(h, b));
/*     */     } 
/* 164 */     this.smallest = calculateSmallest();
/* 165 */     this.largest = calculateLargest();
/*     */     
/* 167 */     this.powers = new long[b];
/* 168 */     for (int c = 0; c < b; c++) {
/* 169 */       this.powers[c] = (long)Math.pow(h, c);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long cardinality() {
/* 179 */     return this.cardinality;
/*     */   }
/*     */ 
/*     */   
/*     */   public int decode(InputStream in) throws IOException, Pack200Exception {
/* 184 */     if (this.d != 0) {
/* 185 */       throw new Pack200Exception("Delta encoding used without passing in last value; this is a coding error");
/*     */     }
/* 187 */     return decode(in, 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public int decode(InputStream in, long last) throws IOException, Pack200Exception {
/* 192 */     int n = 0;
/* 193 */     long z = 0L;
/* 194 */     long x = 0L;
/*     */     
/*     */     do {
/* 197 */       x = in.read();
/* 198 */       this.lastBandLength++;
/* 199 */       z += x * this.powers[n];
/* 200 */       n++;
/* 201 */     } while (x >= this.l && n < this.b);
/*     */     
/* 203 */     if (x == -1L) {
/* 204 */       throw new EOFException("End of stream reached whilst decoding");
/*     */     }
/*     */     
/* 207 */     if (isSigned()) {
/* 208 */       int u = (1 << this.s) - 1;
/* 209 */       if ((z & u) == u) {
/* 210 */         z = z >>> this.s ^ 0xFFFFFFFFFFFFFFFFL;
/*     */       } else {
/* 212 */         z -= z >>> this.s;
/*     */       } 
/*     */     } 
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
/* 231 */     if (isDelta()) {
/* 232 */       z += last;
/*     */     }
/* 234 */     return (int)z;
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] decodeInts(int n, InputStream in) throws IOException, Pack200Exception {
/* 239 */     int[] band = super.decodeInts(n, in);
/* 240 */     if (isDelta()) {
/* 241 */       for (int i = 0; i < band.length; i++) {
/* 242 */         while (band[i] > this.largest) {
/* 243 */           band[i] = (int)(band[i] - this.cardinality);
/*     */         }
/* 245 */         while (band[i] < this.smallest) {
/* 246 */           band[i] = (int)(band[i] + this.cardinality);
/*     */         }
/*     */       } 
/*     */     }
/* 250 */     return band;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] decodeInts(int n, InputStream in, int firstValue) throws IOException, Pack200Exception {
/* 256 */     int[] band = super.decodeInts(n, in, firstValue);
/* 257 */     if (isDelta()) {
/* 258 */       for (int i = 0; i < band.length; i++) {
/* 259 */         while (band[i] > this.largest) {
/* 260 */           band[i] = (int)(band[i] - this.cardinality);
/*     */         }
/* 262 */         while (band[i] < this.smallest) {
/* 263 */           band[i] = (int)(band[i] + this.cardinality);
/*     */         }
/*     */       } 
/*     */     }
/* 267 */     return band;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean encodes(long value) {
/* 283 */     return (value >= this.smallest && value <= this.largest);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] encode(int value, int last) throws Pack200Exception {
/* 288 */     if (!encodes(value)) {
/* 289 */       throw new Pack200Exception("The codec " + toString() + " does not encode the value " + value);
/*     */     }
/*     */     
/* 292 */     long z = value;
/* 293 */     if (isDelta()) {
/* 294 */       z -= last;
/*     */     }
/* 296 */     if (isSigned()) {
/* 297 */       if (z < -2147483648L) {
/* 298 */         z += 4294967296L;
/* 299 */       } else if (z > 2147483647L) {
/* 300 */         z -= 4294967296L;
/*     */       } 
/* 302 */       if (z < 0L) {
/* 303 */         z = (-z << this.s) - 1L;
/* 304 */       } else if (this.s == 1) {
/* 305 */         z <<= this.s;
/*     */       } else {
/* 307 */         z += (z - z % 3L) / 3L;
/*     */       } 
/* 309 */     } else if (z < 0L) {
/*     */       
/* 311 */       if (this.cardinality < 4294967296L) {
/* 312 */         z += this.cardinality;
/*     */       } else {
/* 314 */         z += 4294967296L;
/*     */       } 
/*     */     } 
/* 317 */     if (z < 0L) {
/* 318 */       throw new Pack200Exception("unable to encode");
/*     */     }
/*     */     
/* 321 */     List<Byte> byteList = new ArrayList();
/* 322 */     for (int n = 0; n < this.b; n++) {
/*     */       long byteN;
/* 324 */       if (z < this.l) {
/* 325 */         byteN = z;
/*     */       } else {
/* 327 */         byteN = z % this.h;
/* 328 */         while (byteN < this.l) {
/* 329 */           byteN += this.h;
/*     */         }
/*     */       } 
/* 332 */       byteList.add(Byte.valueOf((byte)(int)byteN));
/* 333 */       if (byteN < this.l) {
/*     */         break;
/*     */       }
/* 336 */       z -= byteN;
/* 337 */       z /= this.h;
/*     */     } 
/* 339 */     byte[] bytes = new byte[byteList.size()];
/* 340 */     for (int i = 0; i < bytes.length; i++) {
/* 341 */       bytes[i] = ((Byte)byteList.get(i)).byteValue();
/*     */     }
/* 343 */     return bytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] encode(int value) throws Pack200Exception {
/* 348 */     return encode(value, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDelta() {
/* 357 */     return (this.d != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSigned() {
/* 366 */     return (this.s != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long largest() {
/* 375 */     return this.largest;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private long calculateLargest() {
/*     */     long result;
/* 382 */     if (this.d == 1) {
/* 383 */       BHSDCodec bh0 = new BHSDCodec(this.b, this.h);
/* 384 */       return bh0.largest();
/*     */     } 
/* 386 */     if (this.s == 0) {
/* 387 */       result = cardinality() - 1L;
/* 388 */     } else if (this.s == 1) {
/* 389 */       result = cardinality() / 2L - 1L;
/* 390 */     } else if (this.s == 2) {
/* 391 */       result = 3L * cardinality() / 4L - 1L;
/*     */     } else {
/* 393 */       throw new Error("Unknown s value");
/*     */     } 
/* 395 */     return Math.min(((this.s == 0) ? 4294967294L : 2147483647L) - 1L, result);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long smallest() {
/* 404 */     return this.smallest;
/*     */   }
/*     */   
/*     */   private long calculateSmallest() {
/*     */     long result;
/* 409 */     if (this.d == 1 || !isSigned()) {
/* 410 */       if (this.cardinality >= 4294967296L) {
/* 411 */         result = -2147483648L;
/*     */       } else {
/* 413 */         result = 0L;
/*     */       } 
/*     */     } else {
/* 416 */       result = Math.max(-2147483648L, -cardinality() / (1 << this.s));
/*     */     } 
/* 418 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 426 */     StringBuffer buffer = new StringBuffer(11);
/* 427 */     buffer.append('(');
/* 428 */     buffer.append(this.b);
/* 429 */     buffer.append(',');
/* 430 */     buffer.append(this.h);
/* 431 */     if (this.s != 0 || this.d != 0) {
/* 432 */       buffer.append(',');
/* 433 */       buffer.append(this.s);
/*     */     } 
/* 435 */     if (this.d != 0) {
/* 436 */       buffer.append(',');
/* 437 */       buffer.append(this.d);
/*     */     } 
/* 439 */     buffer.append(')');
/* 440 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getB() {
/* 447 */     return this.b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getH() {
/* 454 */     return this.h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getS() {
/* 461 */     return this.s;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getL() {
/* 468 */     return this.l;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 473 */     if (o instanceof BHSDCodec) {
/* 474 */       BHSDCodec codec = (BHSDCodec)o;
/* 475 */       return (codec.b == this.b && codec.h == this.h && codec.s == this.s && codec.d == this.d);
/*     */     } 
/* 477 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 482 */     return ((this.b * 37 + this.h) * 37 + this.s) * 37 + this.d;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\BHSDCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
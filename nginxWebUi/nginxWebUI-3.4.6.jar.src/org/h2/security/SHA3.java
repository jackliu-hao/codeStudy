/*     */ package org.h2.security;
/*     */ 
/*     */ import java.security.MessageDigest;
/*     */ import java.util.Arrays;
/*     */ import org.h2.util.Bits;
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SHA3
/*     */   extends MessageDigest
/*     */ {
/*     */   private static final long[] ROUND_CONSTANTS;
/*     */   private final int digestLength;
/*     */   private final int rate;
/*     */   private long state00;
/*     */   private long state01;
/*     */   private long state02;
/*     */   private long state03;
/*     */   
/*     */   static {
/*  21 */     long[] arrayOfLong = new long[24];
/*  22 */     byte b = 1;
/*  23 */     for (byte b1 = 0; b1 < 24; b1++) {
/*  24 */       arrayOfLong[b1] = 0L;
/*  25 */       for (byte b2 = 0; b2 < 7; b2++) {
/*  26 */         byte b3 = b;
/*  27 */         b = (byte)(b3 ? (b3 << 1 ^ 0x71) : (b3 << 1));
/*  28 */         if ((b3 & 0x1) != 0) {
/*  29 */           arrayOfLong[b1] = arrayOfLong[b1] ^ 1L << (1 << b2) - 1;
/*     */         }
/*     */       } 
/*     */     } 
/*  33 */     ROUND_CONSTANTS = arrayOfLong;
/*     */   }
/*     */   private long state04; private long state05; private long state06; private long state07; private long state08; private long state09; private long state10; private long state11; private long state12; private long state13; private long state14;
/*     */   private long state15;
/*     */   private long state16;
/*     */   private long state17;
/*     */   private long state18;
/*     */   
/*     */   public static SHA3 getSha3_224() {
/*  42 */     return new SHA3("SHA3-224", 28);
/*     */   }
/*     */   private long state19; private long state20; private long state21; private long state22;
/*     */   private long state23;
/*     */   private long state24;
/*     */   private final byte[] buf;
/*     */   private int bufcnt;
/*     */   
/*     */   public static SHA3 getSha3_256() {
/*  51 */     return new SHA3("SHA3-256", 32);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SHA3 getSha3_384() {
/*  60 */     return new SHA3("SHA3-384", 48);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SHA3 getSha3_512() {
/*  69 */     return new SHA3("SHA3-512", 64);
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
/*     */   private SHA3(String paramString, int paramInt) {
/*  85 */     super(paramString);
/*  86 */     this.digestLength = paramInt;
/*  87 */     this.buf = new byte[this.rate = 200 - paramInt * 2];
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte[] engineDigest() {
/*  92 */     this.buf[this.bufcnt] = 6;
/*  93 */     Arrays.fill(this.buf, this.bufcnt + 1, this.rate, (byte)0);
/*  94 */     this.buf[this.rate - 1] = (byte)(this.buf[this.rate - 1] | 0x80);
/*  95 */     absorbQueue();
/*  96 */     byte[] arrayOfByte = new byte[this.digestLength];
/*  97 */     switch (this.digestLength) {
/*     */       case 64:
/*  99 */         Bits.writeLongLE(arrayOfByte, 56, this.state07);
/* 100 */         Bits.writeLongLE(arrayOfByte, 48, this.state06);
/*     */       
/*     */       case 48:
/* 103 */         Bits.writeLongLE(arrayOfByte, 40, this.state05);
/* 104 */         Bits.writeLongLE(arrayOfByte, 32, this.state04);
/*     */       
/*     */       case 32:
/* 107 */         Bits.writeLongLE(arrayOfByte, 24, this.state03);
/*     */         break;
/*     */       case 28:
/* 110 */         Bits.writeIntLE(arrayOfByte, 24, (int)this.state03); break;
/*     */     } 
/* 112 */     Bits.writeLongLE(arrayOfByte, 16, this.state02);
/* 113 */     Bits.writeLongLE(arrayOfByte, 8, this.state01);
/* 114 */     Bits.writeLongLE(arrayOfByte, 0, this.state00);
/* 115 */     engineReset();
/* 116 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int engineGetDigestLength() {
/* 121 */     return this.digestLength;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void engineReset() {
/* 126 */     this.state24 = this.state23 = this.state22 = this.state21 = this.state20 = this.state19 = this.state18 = this.state17 = this.state16 = this.state15 = this.state14 = this.state13 = this.state12 = this.state11 = this.state10 = this.state09 = this.state08 = this.state07 = this.state06 = this.state05 = this.state04 = this.state03 = this.state02 = this.state01 = this.state00 = 0L;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 131 */     Arrays.fill(this.buf, (byte)0);
/* 132 */     this.bufcnt = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void engineUpdate(byte paramByte) {
/* 137 */     this.buf[this.bufcnt++] = paramByte;
/* 138 */     if (this.bufcnt == this.rate) {
/* 139 */       absorbQueue();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 145 */     label14: while (paramInt2 > 0) {
/* 146 */       if (this.bufcnt == 0 && paramInt2 >= this.rate)
/*     */         while (true) {
/* 148 */           absorb(paramArrayOfbyte, paramInt1);
/* 149 */           paramInt1 += this.rate;
/* 150 */           paramInt2 -= this.rate;
/* 151 */           if (paramInt2 < this.rate)
/*     */             continue label14; 
/* 153 */         }   int i = Math.min(paramInt2, this.rate - this.bufcnt);
/* 154 */       System.arraycopy(paramArrayOfbyte, paramInt1, this.buf, this.bufcnt, i);
/* 155 */       this.bufcnt += i;
/* 156 */       paramInt1 += i;
/* 157 */       paramInt2 -= i;
/* 158 */       if (this.bufcnt == this.rate) {
/* 159 */         absorbQueue();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void absorbQueue() {
/* 166 */     absorb(this.buf, 0);
/* 167 */     this.bufcnt = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void absorb(byte[] paramArrayOfbyte, int paramInt) {
/* 175 */     switch (this.digestLength) {
/*     */       case 28:
/* 177 */         this.state17 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 136);
/*     */       
/*     */       case 32:
/* 180 */         this.state13 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 104);
/* 181 */         this.state14 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 112);
/* 182 */         this.state15 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 120);
/* 183 */         this.state16 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 128);
/*     */       
/*     */       case 48:
/* 186 */         this.state09 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 72);
/* 187 */         this.state10 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 80);
/* 188 */         this.state11 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 88);
/* 189 */         this.state12 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 96); break;
/*     */     } 
/* 191 */     this.state00 ^= Bits.readLongLE(paramArrayOfbyte, paramInt);
/* 192 */     this.state01 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 8);
/* 193 */     this.state02 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 16);
/* 194 */     this.state03 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 24);
/* 195 */     this.state04 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 32);
/* 196 */     this.state05 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 40);
/* 197 */     this.state06 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 48);
/* 198 */     this.state07 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 56);
/* 199 */     this.state08 ^= Bits.readLongLE(paramArrayOfbyte, paramInt + 64);
/* 200 */     for (byte b = 0; b < 24; b++) {
/* 201 */       long l1 = this.state00 ^ this.state05 ^ this.state10 ^ this.state15 ^ this.state20;
/* 202 */       long l2 = this.state01 ^ this.state06 ^ this.state11 ^ this.state16 ^ this.state21;
/* 203 */       long l3 = this.state02 ^ this.state07 ^ this.state12 ^ this.state17 ^ this.state22;
/* 204 */       long l4 = this.state03 ^ this.state08 ^ this.state13 ^ this.state18 ^ this.state23;
/* 205 */       long l5 = this.state04 ^ this.state09 ^ this.state14 ^ this.state19 ^ this.state24;
/* 206 */       long l6 = (l2 << 1L | l2 >>> 63L) ^ l5;
/* 207 */       this.state00 ^= l6;
/* 208 */       this.state05 ^= l6;
/* 209 */       this.state10 ^= l6;
/* 210 */       this.state15 ^= l6;
/* 211 */       this.state20 ^= l6;
/* 212 */       l6 = (l3 << 1L | l3 >>> 63L) ^ l1;
/* 213 */       this.state01 ^= l6;
/* 214 */       this.state06 ^= l6;
/* 215 */       this.state11 ^= l6;
/* 216 */       this.state16 ^= l6;
/* 217 */       this.state21 ^= l6;
/* 218 */       l6 = (l4 << 1L | l4 >>> 63L) ^ l2;
/* 219 */       this.state02 ^= l6;
/* 220 */       this.state07 ^= l6;
/* 221 */       this.state12 ^= l6;
/* 222 */       this.state17 ^= l6;
/* 223 */       this.state22 ^= l6;
/* 224 */       l6 = (l5 << 1L | l5 >>> 63L) ^ l3;
/* 225 */       this.state03 ^= l6;
/* 226 */       this.state08 ^= l6;
/* 227 */       this.state13 ^= l6;
/* 228 */       this.state18 ^= l6;
/* 229 */       this.state23 ^= l6;
/* 230 */       l6 = (l1 << 1L | l1 >>> 63L) ^ l4;
/* 231 */       this.state04 ^= l6;
/* 232 */       this.state09 ^= l6;
/* 233 */       this.state14 ^= l6;
/* 234 */       this.state19 ^= l6;
/* 235 */       this.state24 ^= l6;
/* 236 */       long l7 = this.state00;
/* 237 */       long l8 = this.state06 << 44L | this.state06 >>> 20L;
/* 238 */       long l9 = this.state12 << 43L | this.state12 >>> 21L;
/* 239 */       long l10 = this.state18 << 21L | this.state18 >>> 43L;
/* 240 */       long l11 = this.state24 << 14L | this.state24 >>> 50L;
/* 241 */       long l12 = this.state03 << 28L | this.state03 >>> 36L;
/* 242 */       long l13 = this.state09 << 20L | this.state09 >>> 44L;
/* 243 */       long l14 = this.state10 << 3L | this.state10 >>> 61L;
/* 244 */       long l15 = this.state16 << 45L | this.state16 >>> 19L;
/* 245 */       long l16 = this.state22 << 61L | this.state22 >>> 3L;
/* 246 */       long l17 = this.state01 << 1L | this.state01 >>> 63L;
/* 247 */       long l18 = this.state07 << 6L | this.state07 >>> 58L;
/* 248 */       long l19 = this.state13 << 25L | this.state13 >>> 39L;
/* 249 */       long l20 = this.state19 << 8L | this.state19 >>> 56L;
/* 250 */       long l21 = this.state20 << 18L | this.state20 >>> 46L;
/* 251 */       long l22 = this.state04 << 27L | this.state04 >>> 37L;
/* 252 */       long l23 = this.state05 << 36L | this.state05 >>> 28L;
/* 253 */       long l24 = this.state11 << 10L | this.state11 >>> 54L;
/* 254 */       long l25 = this.state17 << 15L | this.state17 >>> 49L;
/* 255 */       long l26 = this.state23 << 56L | this.state23 >>> 8L;
/* 256 */       long l27 = this.state02 << 62L | this.state02 >>> 2L;
/* 257 */       long l28 = this.state08 << 55L | this.state08 >>> 9L;
/* 258 */       long l29 = this.state14 << 39L | this.state14 >>> 25L;
/* 259 */       long l30 = this.state15 << 41L | this.state15 >>> 23L;
/* 260 */       long l31 = this.state21 << 2L | this.state21 >>> 62L;
/* 261 */       this.state00 = l7 ^ (l8 ^ 0xFFFFFFFFFFFFFFFFL) & l9 ^ ROUND_CONSTANTS[b];
/* 262 */       this.state01 = l8 ^ (l9 ^ 0xFFFFFFFFFFFFFFFFL) & l10;
/* 263 */       this.state02 = l9 ^ (l10 ^ 0xFFFFFFFFFFFFFFFFL) & l11;
/* 264 */       this.state03 = l10 ^ (l11 ^ 0xFFFFFFFFFFFFFFFFL) & l7;
/* 265 */       this.state04 = l11 ^ (l7 ^ 0xFFFFFFFFFFFFFFFFL) & l8;
/* 266 */       this.state05 = l12 ^ (l13 ^ 0xFFFFFFFFFFFFFFFFL) & l14;
/* 267 */       this.state06 = l13 ^ (l14 ^ 0xFFFFFFFFFFFFFFFFL) & l15;
/* 268 */       this.state07 = l14 ^ (l15 ^ 0xFFFFFFFFFFFFFFFFL) & l16;
/* 269 */       this.state08 = l15 ^ (l16 ^ 0xFFFFFFFFFFFFFFFFL) & l12;
/* 270 */       this.state09 = l16 ^ (l12 ^ 0xFFFFFFFFFFFFFFFFL) & l13;
/* 271 */       this.state10 = l17 ^ (l18 ^ 0xFFFFFFFFFFFFFFFFL) & l19;
/* 272 */       this.state11 = l18 ^ (l19 ^ 0xFFFFFFFFFFFFFFFFL) & l20;
/* 273 */       this.state12 = l19 ^ (l20 ^ 0xFFFFFFFFFFFFFFFFL) & l21;
/* 274 */       this.state13 = l20 ^ (l21 ^ 0xFFFFFFFFFFFFFFFFL) & l17;
/* 275 */       this.state14 = l21 ^ (l17 ^ 0xFFFFFFFFFFFFFFFFL) & l18;
/* 276 */       this.state15 = l22 ^ (l23 ^ 0xFFFFFFFFFFFFFFFFL) & l24;
/* 277 */       this.state16 = l23 ^ (l24 ^ 0xFFFFFFFFFFFFFFFFL) & l25;
/* 278 */       this.state17 = l24 ^ (l25 ^ 0xFFFFFFFFFFFFFFFFL) & l26;
/* 279 */       this.state18 = l25 ^ (l26 ^ 0xFFFFFFFFFFFFFFFFL) & l22;
/* 280 */       this.state19 = l26 ^ (l22 ^ 0xFFFFFFFFFFFFFFFFL) & l23;
/* 281 */       this.state20 = l27 ^ (l28 ^ 0xFFFFFFFFFFFFFFFFL) & l29;
/* 282 */       this.state21 = l28 ^ (l29 ^ 0xFFFFFFFFFFFFFFFFL) & l30;
/* 283 */       this.state22 = l29 ^ (l30 ^ 0xFFFFFFFFFFFFFFFFL) & l31;
/* 284 */       this.state23 = l30 ^ (l31 ^ 0xFFFFFFFFFFFFFFFFL) & l27;
/* 285 */       this.state24 = l31 ^ (l27 ^ 0xFFFFFFFFFFFFFFFFL) & l28;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\SHA3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
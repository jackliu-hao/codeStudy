/*     */ package org.h2.security;
/*     */ 
/*     */ import org.h2.util.Bits;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AES
/*     */   implements BlockCipher
/*     */ {
/*  16 */   private static final int[] RCON = new int[10];
/*  17 */   private static final int[] FS = new int[256];
/*  18 */   private static final int[] FT0 = new int[256];
/*  19 */   private static final int[] FT1 = new int[256];
/*  20 */   private static final int[] FT2 = new int[256];
/*  21 */   private static final int[] FT3 = new int[256];
/*  22 */   private static final int[] RS = new int[256];
/*  23 */   private static final int[] RT0 = new int[256];
/*  24 */   private static final int[] RT1 = new int[256];
/*  25 */   private static final int[] RT2 = new int[256];
/*  26 */   private static final int[] RT3 = new int[256];
/*  27 */   private final int[] encKey = new int[44];
/*  28 */   private final int[] decKey = new int[44];
/*     */   
/*     */   private static int rot8(int paramInt) {
/*  31 */     return paramInt >>> 8 | paramInt << 24;
/*     */   }
/*     */   
/*     */   private static int xtime(int paramInt) {
/*  35 */     return (paramInt << 1 ^ (((paramInt & 0x80) != 0) ? 27 : 0)) & 0xFF;
/*     */   }
/*     */   
/*     */   private static int mul(int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt1, int paramInt2) {
/*  39 */     return (paramInt1 != 0 && paramInt2 != 0) ? paramArrayOfint1[(paramArrayOfint2[paramInt1] + paramArrayOfint2[paramInt2]) % 255] : 0;
/*     */   }
/*     */   
/*     */   static {
/*  43 */     int[] arrayOfInt1 = new int[256];
/*  44 */     int[] arrayOfInt2 = new int[256]; byte b; int i;
/*  45 */     for (b = 0, i = 1; b < 'Ā'; b++, i ^= xtime(i)) {
/*  46 */       arrayOfInt1[b] = i;
/*  47 */       arrayOfInt2[i] = b;
/*     */     } 
/*  49 */     for (b = 0, i = 1; b < 10; b++, i = xtime(i)) {
/*  50 */       RCON[b] = i << 24;
/*     */     }
/*  52 */     FS[0] = 99;
/*  53 */     RS[99] = 0;
/*  54 */     for (b = 1; b < 'Ā'; b++) {
/*  55 */       int j = i = arrayOfInt1[255 - arrayOfInt2[b]];
/*  56 */       j = (j << 1 | j >> 7) & 0xFF;
/*  57 */       i ^= j;
/*  58 */       j = (j << 1 | j >> 7) & 0xFF;
/*  59 */       i ^= j;
/*  60 */       j = (j << 1 | j >> 7) & 0xFF;
/*  61 */       i ^= j;
/*  62 */       j = (j << 1 | j >> 7) & 0xFF;
/*  63 */       i ^= j ^ 0x63;
/*  64 */       FS[b] = i & 0xFF;
/*  65 */       RS[i] = b & 0xFF;
/*     */     } 
/*  67 */     for (b = 0; b < 'Ā'; b++) {
/*  68 */       i = FS[b]; int j = xtime(i);
/*  69 */       FT0[b] = i ^ j ^ i << 8 ^ i << 16 ^ j << 24;
/*  70 */       FT1[b] = rot8(FT0[b]);
/*  71 */       FT2[b] = rot8(FT1[b]);
/*  72 */       FT3[b] = rot8(FT2[b]);
/*  73 */       j = RS[b];
/*  74 */       RT0[b] = mul(arrayOfInt1, arrayOfInt2, 11, j) ^ mul(arrayOfInt1, arrayOfInt2, 13, j) << 8 ^ 
/*  75 */         mul(arrayOfInt1, arrayOfInt2, 9, j) << 16 ^ mul(arrayOfInt1, arrayOfInt2, 14, j) << 24;
/*  76 */       RT1[b] = rot8(RT0[b]);
/*  77 */       RT2[b] = rot8(RT1[b]);
/*  78 */       RT3[b] = rot8(RT2[b]);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static int getDec(int paramInt) {
/*  83 */     return RT0[FS[paramInt >> 24 & 0xFF]] ^ RT1[FS[paramInt >> 16 & 0xFF]] ^ RT2[FS[paramInt >> 8 & 0xFF]] ^ RT3[FS[paramInt & 0xFF]];
/*     */   }
/*     */ 
/*     */   
/*     */   public void setKey(byte[] paramArrayOfbyte) {
/*     */     byte b1, b2;
/*  89 */     for (b1 = 0, b2 = 0; b1 < 4; b1++) {
/*  90 */       this.decKey[b1] = (paramArrayOfbyte[b2++] & 0xFF) << 24 | (paramArrayOfbyte[b2++] & 0xFF) << 16 | (paramArrayOfbyte[b2++] & 0xFF) << 8 | paramArrayOfbyte[b2++] & 0xFF; this.encKey[b1] = (paramArrayOfbyte[b2++] & 0xFF) << 24 | (paramArrayOfbyte[b2++] & 0xFF) << 16 | (paramArrayOfbyte[b2++] & 0xFF) << 8 | paramArrayOfbyte[b2++] & 0xFF;
/*     */     } 
/*     */ 
/*     */     
/*  94 */     b1 = 0;
/*  95 */     for (b2 = 0; b2 < 10; b2++, b1 += 4) {
/*  96 */       this.encKey[b1 + 4] = this.encKey[b1] ^ RCON[b2] ^ FS[this.encKey[b1 + 3] >> 16 & 0xFF] << 24 ^ FS[this.encKey[b1 + 3] >> 8 & 0xFF] << 16 ^ FS[this.encKey[b1 + 3] & 0xFF] << 8 ^ FS[this.encKey[b1 + 3] >> 24 & 0xFF];
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 101 */       this.encKey[b1 + 5] = this.encKey[b1 + 1] ^ this.encKey[b1 + 4];
/* 102 */       this.encKey[b1 + 6] = this.encKey[b1 + 2] ^ this.encKey[b1 + 5];
/* 103 */       this.encKey[b1 + 7] = this.encKey[b1 + 3] ^ this.encKey[b1 + 6];
/*     */     } 
/* 105 */     b2 = 0;
/* 106 */     this.decKey[b2++] = this.encKey[b1++];
/* 107 */     this.decKey[b2++] = this.encKey[b1++];
/* 108 */     this.decKey[b2++] = this.encKey[b1++];
/* 109 */     this.decKey[b2++] = this.encKey[b1++];
/* 110 */     for (byte b3 = 1; b3 < 10; b3++) {
/* 111 */       b1 -= 8;
/* 112 */       this.decKey[b2++] = getDec(this.encKey[b1++]);
/* 113 */       this.decKey[b2++] = getDec(this.encKey[b1++]);
/* 114 */       this.decKey[b2++] = getDec(this.encKey[b1++]);
/* 115 */       this.decKey[b2++] = getDec(this.encKey[b1++]);
/*     */     } 
/* 117 */     b1 -= 8;
/* 118 */     this.decKey[b2++] = this.encKey[b1++];
/* 119 */     this.decKey[b2++] = this.encKey[b1++];
/* 120 */     this.decKey[b2++] = this.encKey[b1++];
/* 121 */     this.decKey[b2] = this.encKey[b1];
/*     */   }
/*     */ 
/*     */   
/*     */   public void encrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 126 */     for (int i = paramInt1; i < paramInt1 + paramInt2; i += 16) {
/* 127 */       encryptBlock(paramArrayOfbyte, paramArrayOfbyte, i);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void decrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 133 */     for (int i = paramInt1; i < paramInt1 + paramInt2; i += 16) {
/* 134 */       decryptBlock(paramArrayOfbyte, paramArrayOfbyte, i);
/*     */     }
/*     */   }
/*     */   
/*     */   private void encryptBlock(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
/* 139 */     int[] arrayOfInt = this.encKey;
/* 140 */     int i = Bits.readInt(paramArrayOfbyte1, paramInt) ^ arrayOfInt[0];
/* 141 */     int j = Bits.readInt(paramArrayOfbyte1, paramInt + 4) ^ arrayOfInt[1];
/* 142 */     int k = Bits.readInt(paramArrayOfbyte1, paramInt + 8) ^ arrayOfInt[2];
/* 143 */     int m = Bits.readInt(paramArrayOfbyte1, paramInt + 12) ^ arrayOfInt[3];
/* 144 */     int n = FT0[i >> 24 & 0xFF] ^ FT1[j >> 16 & 0xFF] ^ FT2[k >> 8 & 0xFF] ^ FT3[m & 0xFF] ^ arrayOfInt[4];
/*     */     
/* 146 */     int i1 = FT0[j >> 24 & 0xFF] ^ FT1[k >> 16 & 0xFF] ^ FT2[m >> 8 & 0xFF] ^ FT3[i & 0xFF] ^ arrayOfInt[5];
/*     */     
/* 148 */     int i2 = FT0[k >> 24 & 0xFF] ^ FT1[m >> 16 & 0xFF] ^ FT2[i >> 8 & 0xFF] ^ FT3[j & 0xFF] ^ arrayOfInt[6];
/*     */     
/* 150 */     int i3 = FT0[m >> 24 & 0xFF] ^ FT1[i >> 16 & 0xFF] ^ FT2[j >> 8 & 0xFF] ^ FT3[k & 0xFF] ^ arrayOfInt[7];
/*     */     
/* 152 */     i = FT0[n >> 24 & 0xFF] ^ FT1[i1 >> 16 & 0xFF] ^ FT2[i2 >> 8 & 0xFF] ^ FT3[i3 & 0xFF] ^ arrayOfInt[8];
/*     */     
/* 154 */     j = FT0[i1 >> 24 & 0xFF] ^ FT1[i2 >> 16 & 0xFF] ^ FT2[i3 >> 8 & 0xFF] ^ FT3[n & 0xFF] ^ arrayOfInt[9];
/*     */     
/* 156 */     k = FT0[i2 >> 24 & 0xFF] ^ FT1[i3 >> 16 & 0xFF] ^ FT2[n >> 8 & 0xFF] ^ FT3[i1 & 0xFF] ^ arrayOfInt[10];
/*     */     
/* 158 */     m = FT0[i3 >> 24 & 0xFF] ^ FT1[n >> 16 & 0xFF] ^ FT2[i1 >> 8 & 0xFF] ^ FT3[i2 & 0xFF] ^ arrayOfInt[11];
/*     */     
/* 160 */     n = FT0[i >> 24 & 0xFF] ^ FT1[j >> 16 & 0xFF] ^ FT2[k >> 8 & 0xFF] ^ FT3[m & 0xFF] ^ arrayOfInt[12];
/*     */     
/* 162 */     i1 = FT0[j >> 24 & 0xFF] ^ FT1[k >> 16 & 0xFF] ^ FT2[m >> 8 & 0xFF] ^ FT3[i & 0xFF] ^ arrayOfInt[13];
/*     */     
/* 164 */     i2 = FT0[k >> 24 & 0xFF] ^ FT1[m >> 16 & 0xFF] ^ FT2[i >> 8 & 0xFF] ^ FT3[j & 0xFF] ^ arrayOfInt[14];
/*     */     
/* 166 */     i3 = FT0[m >> 24 & 0xFF] ^ FT1[i >> 16 & 0xFF] ^ FT2[j >> 8 & 0xFF] ^ FT3[k & 0xFF] ^ arrayOfInt[15];
/*     */     
/* 168 */     i = FT0[n >> 24 & 0xFF] ^ FT1[i1 >> 16 & 0xFF] ^ FT2[i2 >> 8 & 0xFF] ^ FT3[i3 & 0xFF] ^ arrayOfInt[16];
/*     */     
/* 170 */     j = FT0[i1 >> 24 & 0xFF] ^ FT1[i2 >> 16 & 0xFF] ^ FT2[i3 >> 8 & 0xFF] ^ FT3[n & 0xFF] ^ arrayOfInt[17];
/*     */     
/* 172 */     k = FT0[i2 >> 24 & 0xFF] ^ FT1[i3 >> 16 & 0xFF] ^ FT2[n >> 8 & 0xFF] ^ FT3[i1 & 0xFF] ^ arrayOfInt[18];
/*     */     
/* 174 */     m = FT0[i3 >> 24 & 0xFF] ^ FT1[n >> 16 & 0xFF] ^ FT2[i1 >> 8 & 0xFF] ^ FT3[i2 & 0xFF] ^ arrayOfInt[19];
/*     */     
/* 176 */     n = FT0[i >> 24 & 0xFF] ^ FT1[j >> 16 & 0xFF] ^ FT2[k >> 8 & 0xFF] ^ FT3[m & 0xFF] ^ arrayOfInt[20];
/*     */     
/* 178 */     i1 = FT0[j >> 24 & 0xFF] ^ FT1[k >> 16 & 0xFF] ^ FT2[m >> 8 & 0xFF] ^ FT3[i & 0xFF] ^ arrayOfInt[21];
/*     */     
/* 180 */     i2 = FT0[k >> 24 & 0xFF] ^ FT1[m >> 16 & 0xFF] ^ FT2[i >> 8 & 0xFF] ^ FT3[j & 0xFF] ^ arrayOfInt[22];
/*     */     
/* 182 */     i3 = FT0[m >> 24 & 0xFF] ^ FT1[i >> 16 & 0xFF] ^ FT2[j >> 8 & 0xFF] ^ FT3[k & 0xFF] ^ arrayOfInt[23];
/*     */     
/* 184 */     i = FT0[n >> 24 & 0xFF] ^ FT1[i1 >> 16 & 0xFF] ^ FT2[i2 >> 8 & 0xFF] ^ FT3[i3 & 0xFF] ^ arrayOfInt[24];
/*     */     
/* 186 */     j = FT0[i1 >> 24 & 0xFF] ^ FT1[i2 >> 16 & 0xFF] ^ FT2[i3 >> 8 & 0xFF] ^ FT3[n & 0xFF] ^ arrayOfInt[25];
/*     */     
/* 188 */     k = FT0[i2 >> 24 & 0xFF] ^ FT1[i3 >> 16 & 0xFF] ^ FT2[n >> 8 & 0xFF] ^ FT3[i1 & 0xFF] ^ arrayOfInt[26];
/*     */     
/* 190 */     m = FT0[i3 >> 24 & 0xFF] ^ FT1[n >> 16 & 0xFF] ^ FT2[i1 >> 8 & 0xFF] ^ FT3[i2 & 0xFF] ^ arrayOfInt[27];
/*     */     
/* 192 */     n = FT0[i >> 24 & 0xFF] ^ FT1[j >> 16 & 0xFF] ^ FT2[k >> 8 & 0xFF] ^ FT3[m & 0xFF] ^ arrayOfInt[28];
/*     */     
/* 194 */     i1 = FT0[j >> 24 & 0xFF] ^ FT1[k >> 16 & 0xFF] ^ FT2[m >> 8 & 0xFF] ^ FT3[i & 0xFF] ^ arrayOfInt[29];
/*     */     
/* 196 */     i2 = FT0[k >> 24 & 0xFF] ^ FT1[m >> 16 & 0xFF] ^ FT2[i >> 8 & 0xFF] ^ FT3[j & 0xFF] ^ arrayOfInt[30];
/*     */     
/* 198 */     i3 = FT0[m >> 24 & 0xFF] ^ FT1[i >> 16 & 0xFF] ^ FT2[j >> 8 & 0xFF] ^ FT3[k & 0xFF] ^ arrayOfInt[31];
/*     */     
/* 200 */     i = FT0[n >> 24 & 0xFF] ^ FT1[i1 >> 16 & 0xFF] ^ FT2[i2 >> 8 & 0xFF] ^ FT3[i3 & 0xFF] ^ arrayOfInt[32];
/*     */     
/* 202 */     j = FT0[i1 >> 24 & 0xFF] ^ FT1[i2 >> 16 & 0xFF] ^ FT2[i3 >> 8 & 0xFF] ^ FT3[n & 0xFF] ^ arrayOfInt[33];
/*     */     
/* 204 */     k = FT0[i2 >> 24 & 0xFF] ^ FT1[i3 >> 16 & 0xFF] ^ FT2[n >> 8 & 0xFF] ^ FT3[i1 & 0xFF] ^ arrayOfInt[34];
/*     */     
/* 206 */     m = FT0[i3 >> 24 & 0xFF] ^ FT1[n >> 16 & 0xFF] ^ FT2[i1 >> 8 & 0xFF] ^ FT3[i2 & 0xFF] ^ arrayOfInt[35];
/*     */     
/* 208 */     n = FT0[i >> 24 & 0xFF] ^ FT1[j >> 16 & 0xFF] ^ FT2[k >> 8 & 0xFF] ^ FT3[m & 0xFF] ^ arrayOfInt[36];
/*     */     
/* 210 */     i1 = FT0[j >> 24 & 0xFF] ^ FT1[k >> 16 & 0xFF] ^ FT2[m >> 8 & 0xFF] ^ FT3[i & 0xFF] ^ arrayOfInt[37];
/*     */     
/* 212 */     i2 = FT0[k >> 24 & 0xFF] ^ FT1[m >> 16 & 0xFF] ^ FT2[i >> 8 & 0xFF] ^ FT3[j & 0xFF] ^ arrayOfInt[38];
/*     */     
/* 214 */     i3 = FT0[m >> 24 & 0xFF] ^ FT1[i >> 16 & 0xFF] ^ FT2[j >> 8 & 0xFF] ^ FT3[k & 0xFF] ^ arrayOfInt[39];
/*     */     
/* 216 */     i = (FS[n >> 24 & 0xFF] << 24 | FS[i1 >> 16 & 0xFF] << 16 | FS[i2 >> 8 & 0xFF] << 8 | FS[i3 & 0xFF]) ^ arrayOfInt[40];
/*     */     
/* 218 */     j = (FS[i1 >> 24 & 0xFF] << 24 | FS[i2 >> 16 & 0xFF] << 16 | FS[i3 >> 8 & 0xFF] << 8 | FS[n & 0xFF]) ^ arrayOfInt[41];
/*     */     
/* 220 */     k = (FS[i2 >> 24 & 0xFF] << 24 | FS[i3 >> 16 & 0xFF] << 16 | FS[n >> 8 & 0xFF] << 8 | FS[i1 & 0xFF]) ^ arrayOfInt[42];
/*     */     
/* 222 */     m = (FS[i3 >> 24 & 0xFF] << 24 | FS[n >> 16 & 0xFF] << 16 | FS[i1 >> 8 & 0xFF] << 8 | FS[i2 & 0xFF]) ^ arrayOfInt[43];
/*     */     
/* 224 */     Bits.writeInt(paramArrayOfbyte2, paramInt, i);
/* 225 */     Bits.writeInt(paramArrayOfbyte2, paramInt + 4, j);
/* 226 */     Bits.writeInt(paramArrayOfbyte2, paramInt + 8, k);
/* 227 */     Bits.writeInt(paramArrayOfbyte2, paramInt + 12, m);
/*     */   }
/*     */   
/*     */   private void decryptBlock(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
/* 231 */     int[] arrayOfInt = this.decKey;
/* 232 */     int i = Bits.readInt(paramArrayOfbyte1, paramInt) ^ arrayOfInt[0];
/* 233 */     int j = Bits.readInt(paramArrayOfbyte1, paramInt + 4) ^ arrayOfInt[1];
/* 234 */     int k = Bits.readInt(paramArrayOfbyte1, paramInt + 8) ^ arrayOfInt[2];
/* 235 */     int m = Bits.readInt(paramArrayOfbyte1, paramInt + 12) ^ arrayOfInt[3];
/* 236 */     int n = RT0[i >> 24 & 0xFF] ^ RT1[m >> 16 & 0xFF] ^ RT2[k >> 8 & 0xFF] ^ RT3[j & 0xFF] ^ arrayOfInt[4];
/*     */     
/* 238 */     int i1 = RT0[j >> 24 & 0xFF] ^ RT1[i >> 16 & 0xFF] ^ RT2[m >> 8 & 0xFF] ^ RT3[k & 0xFF] ^ arrayOfInt[5];
/*     */     
/* 240 */     int i2 = RT0[k >> 24 & 0xFF] ^ RT1[j >> 16 & 0xFF] ^ RT2[i >> 8 & 0xFF] ^ RT3[m & 0xFF] ^ arrayOfInt[6];
/*     */     
/* 242 */     int i3 = RT0[m >> 24 & 0xFF] ^ RT1[k >> 16 & 0xFF] ^ RT2[j >> 8 & 0xFF] ^ RT3[i & 0xFF] ^ arrayOfInt[7];
/*     */     
/* 244 */     i = RT0[n >> 24 & 0xFF] ^ RT1[i3 >> 16 & 0xFF] ^ RT2[i2 >> 8 & 0xFF] ^ RT3[i1 & 0xFF] ^ arrayOfInt[8];
/*     */     
/* 246 */     j = RT0[i1 >> 24 & 0xFF] ^ RT1[n >> 16 & 0xFF] ^ RT2[i3 >> 8 & 0xFF] ^ RT3[i2 & 0xFF] ^ arrayOfInt[9];
/*     */     
/* 248 */     k = RT0[i2 >> 24 & 0xFF] ^ RT1[i1 >> 16 & 0xFF] ^ RT2[n >> 8 & 0xFF] ^ RT3[i3 & 0xFF] ^ arrayOfInt[10];
/*     */     
/* 250 */     m = RT0[i3 >> 24 & 0xFF] ^ RT1[i2 >> 16 & 0xFF] ^ RT2[i1 >> 8 & 0xFF] ^ RT3[n & 0xFF] ^ arrayOfInt[11];
/*     */     
/* 252 */     n = RT0[i >> 24 & 0xFF] ^ RT1[m >> 16 & 0xFF] ^ RT2[k >> 8 & 0xFF] ^ RT3[j & 0xFF] ^ arrayOfInt[12];
/*     */     
/* 254 */     i1 = RT0[j >> 24 & 0xFF] ^ RT1[i >> 16 & 0xFF] ^ RT2[m >> 8 & 0xFF] ^ RT3[k & 0xFF] ^ arrayOfInt[13];
/*     */     
/* 256 */     i2 = RT0[k >> 24 & 0xFF] ^ RT1[j >> 16 & 0xFF] ^ RT2[i >> 8 & 0xFF] ^ RT3[m & 0xFF] ^ arrayOfInt[14];
/*     */     
/* 258 */     i3 = RT0[m >> 24 & 0xFF] ^ RT1[k >> 16 & 0xFF] ^ RT2[j >> 8 & 0xFF] ^ RT3[i & 0xFF] ^ arrayOfInt[15];
/*     */     
/* 260 */     i = RT0[n >> 24 & 0xFF] ^ RT1[i3 >> 16 & 0xFF] ^ RT2[i2 >> 8 & 0xFF] ^ RT3[i1 & 0xFF] ^ arrayOfInt[16];
/*     */     
/* 262 */     j = RT0[i1 >> 24 & 0xFF] ^ RT1[n >> 16 & 0xFF] ^ RT2[i3 >> 8 & 0xFF] ^ RT3[i2 & 0xFF] ^ arrayOfInt[17];
/*     */     
/* 264 */     k = RT0[i2 >> 24 & 0xFF] ^ RT1[i1 >> 16 & 0xFF] ^ RT2[n >> 8 & 0xFF] ^ RT3[i3 & 0xFF] ^ arrayOfInt[18];
/*     */     
/* 266 */     m = RT0[i3 >> 24 & 0xFF] ^ RT1[i2 >> 16 & 0xFF] ^ RT2[i1 >> 8 & 0xFF] ^ RT3[n & 0xFF] ^ arrayOfInt[19];
/*     */     
/* 268 */     n = RT0[i >> 24 & 0xFF] ^ RT1[m >> 16 & 0xFF] ^ RT2[k >> 8 & 0xFF] ^ RT3[j & 0xFF] ^ arrayOfInt[20];
/*     */     
/* 270 */     i1 = RT0[j >> 24 & 0xFF] ^ RT1[i >> 16 & 0xFF] ^ RT2[m >> 8 & 0xFF] ^ RT3[k & 0xFF] ^ arrayOfInt[21];
/*     */     
/* 272 */     i2 = RT0[k >> 24 & 0xFF] ^ RT1[j >> 16 & 0xFF] ^ RT2[i >> 8 & 0xFF] ^ RT3[m & 0xFF] ^ arrayOfInt[22];
/*     */     
/* 274 */     i3 = RT0[m >> 24 & 0xFF] ^ RT1[k >> 16 & 0xFF] ^ RT2[j >> 8 & 0xFF] ^ RT3[i & 0xFF] ^ arrayOfInt[23];
/*     */     
/* 276 */     i = RT0[n >> 24 & 0xFF] ^ RT1[i3 >> 16 & 0xFF] ^ RT2[i2 >> 8 & 0xFF] ^ RT3[i1 & 0xFF] ^ arrayOfInt[24];
/*     */     
/* 278 */     j = RT0[i1 >> 24 & 0xFF] ^ RT1[n >> 16 & 0xFF] ^ RT2[i3 >> 8 & 0xFF] ^ RT3[i2 & 0xFF] ^ arrayOfInt[25];
/*     */     
/* 280 */     k = RT0[i2 >> 24 & 0xFF] ^ RT1[i1 >> 16 & 0xFF] ^ RT2[n >> 8 & 0xFF] ^ RT3[i3 & 0xFF] ^ arrayOfInt[26];
/*     */     
/* 282 */     m = RT0[i3 >> 24 & 0xFF] ^ RT1[i2 >> 16 & 0xFF] ^ RT2[i1 >> 8 & 0xFF] ^ RT3[n & 0xFF] ^ arrayOfInt[27];
/*     */     
/* 284 */     n = RT0[i >> 24 & 0xFF] ^ RT1[m >> 16 & 0xFF] ^ RT2[k >> 8 & 0xFF] ^ RT3[j & 0xFF] ^ arrayOfInt[28];
/*     */     
/* 286 */     i1 = RT0[j >> 24 & 0xFF] ^ RT1[i >> 16 & 0xFF] ^ RT2[m >> 8 & 0xFF] ^ RT3[k & 0xFF] ^ arrayOfInt[29];
/*     */     
/* 288 */     i2 = RT0[k >> 24 & 0xFF] ^ RT1[j >> 16 & 0xFF] ^ RT2[i >> 8 & 0xFF] ^ RT3[m & 0xFF] ^ arrayOfInt[30];
/*     */     
/* 290 */     i3 = RT0[m >> 24 & 0xFF] ^ RT1[k >> 16 & 0xFF] ^ RT2[j >> 8 & 0xFF] ^ RT3[i & 0xFF] ^ arrayOfInt[31];
/*     */     
/* 292 */     i = RT0[n >> 24 & 0xFF] ^ RT1[i3 >> 16 & 0xFF] ^ RT2[i2 >> 8 & 0xFF] ^ RT3[i1 & 0xFF] ^ arrayOfInt[32];
/*     */     
/* 294 */     j = RT0[i1 >> 24 & 0xFF] ^ RT1[n >> 16 & 0xFF] ^ RT2[i3 >> 8 & 0xFF] ^ RT3[i2 & 0xFF] ^ arrayOfInt[33];
/*     */     
/* 296 */     k = RT0[i2 >> 24 & 0xFF] ^ RT1[i1 >> 16 & 0xFF] ^ RT2[n >> 8 & 0xFF] ^ RT3[i3 & 0xFF] ^ arrayOfInt[34];
/*     */     
/* 298 */     m = RT0[i3 >> 24 & 0xFF] ^ RT1[i2 >> 16 & 0xFF] ^ RT2[i1 >> 8 & 0xFF] ^ RT3[n & 0xFF] ^ arrayOfInt[35];
/*     */     
/* 300 */     n = RT0[i >> 24 & 0xFF] ^ RT1[m >> 16 & 0xFF] ^ RT2[k >> 8 & 0xFF] ^ RT3[j & 0xFF] ^ arrayOfInt[36];
/*     */     
/* 302 */     i1 = RT0[j >> 24 & 0xFF] ^ RT1[i >> 16 & 0xFF] ^ RT2[m >> 8 & 0xFF] ^ RT3[k & 0xFF] ^ arrayOfInt[37];
/*     */     
/* 304 */     i2 = RT0[k >> 24 & 0xFF] ^ RT1[j >> 16 & 0xFF] ^ RT2[i >> 8 & 0xFF] ^ RT3[m & 0xFF] ^ arrayOfInt[38];
/*     */     
/* 306 */     i3 = RT0[m >> 24 & 0xFF] ^ RT1[k >> 16 & 0xFF] ^ RT2[j >> 8 & 0xFF] ^ RT3[i & 0xFF] ^ arrayOfInt[39];
/*     */     
/* 308 */     i = (RS[n >> 24 & 0xFF] << 24 | RS[i3 >> 16 & 0xFF] << 16 | RS[i2 >> 8 & 0xFF] << 8 | RS[i1 & 0xFF]) ^ arrayOfInt[40];
/*     */     
/* 310 */     j = (RS[i1 >> 24 & 0xFF] << 24 | RS[n >> 16 & 0xFF] << 16 | RS[i3 >> 8 & 0xFF] << 8 | RS[i2 & 0xFF]) ^ arrayOfInt[41];
/*     */     
/* 312 */     k = (RS[i2 >> 24 & 0xFF] << 24 | RS[i1 >> 16 & 0xFF] << 16 | RS[n >> 8 & 0xFF] << 8 | RS[i3 & 0xFF]) ^ arrayOfInt[42];
/*     */     
/* 314 */     m = (RS[i3 >> 24 & 0xFF] << 24 | RS[i2 >> 16 & 0xFF] << 16 | RS[i1 >> 8 & 0xFF] << 8 | RS[n & 0xFF]) ^ arrayOfInt[43];
/*     */     
/* 316 */     Bits.writeInt(paramArrayOfbyte2, paramInt, i);
/* 317 */     Bits.writeInt(paramArrayOfbyte2, paramInt + 4, j);
/* 318 */     Bits.writeInt(paramArrayOfbyte2, paramInt + 8, k);
/* 319 */     Bits.writeInt(paramArrayOfbyte2, paramInt + 12, m);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getKeyLength() {
/* 324 */     return 16;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\AES.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
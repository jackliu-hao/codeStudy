/*     */ package org.h2.security;
/*     */ 
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.Bits;
/*     */ 
/*     */ public class XTEA
/*     */   implements BlockCipher
/*     */ {
/*     */   private static final int DELTA = -1640531527;
/*     */   private int k0;
/*     */   private int k1;
/*     */   private int k2;
/*     */   private int k3;
/*     */   private int k4;
/*     */   private int k5;
/*     */   private int k6;
/*     */   private int k7;
/*     */   private int k8;
/*     */   private int k9;
/*     */   private int k10;
/*     */   private int k11;
/*     */   private int k12;
/*     */   private int k13;
/*     */   private int k14;
/*     */   
/*     */   public void setKey(byte[] paramArrayOfbyte) {
/*  27 */     int[] arrayOfInt1 = new int[4];
/*  28 */     for (byte b1 = 0; b1 < 16; b1 += 4) {
/*  29 */       arrayOfInt1[b1 / 4] = Bits.readInt(paramArrayOfbyte, b1);
/*     */     }
/*  31 */     int[] arrayOfInt2 = new int[32]; byte b2; int i;
/*  32 */     for (b2 = 0, i = 0; b2 < 32; ) {
/*  33 */       arrayOfInt2[b2++] = i + arrayOfInt1[i & 0x3];
/*  34 */       i -= 1640531527;
/*  35 */       arrayOfInt2[b2++] = i + arrayOfInt1[i >>> 11 & 0x3];
/*     */     } 
/*  37 */     this.k0 = arrayOfInt2[0]; this.k1 = arrayOfInt2[1]; this.k2 = arrayOfInt2[2]; this.k3 = arrayOfInt2[3];
/*  38 */     this.k4 = arrayOfInt2[4]; this.k5 = arrayOfInt2[5]; this.k6 = arrayOfInt2[6]; this.k7 = arrayOfInt2[7];
/*  39 */     this.k8 = arrayOfInt2[8]; this.k9 = arrayOfInt2[9]; this.k10 = arrayOfInt2[10]; this.k11 = arrayOfInt2[11];
/*  40 */     this.k12 = arrayOfInt2[12]; this.k13 = arrayOfInt2[13]; this.k14 = arrayOfInt2[14]; this.k15 = arrayOfInt2[15];
/*  41 */     this.k16 = arrayOfInt2[16]; this.k17 = arrayOfInt2[17]; this.k18 = arrayOfInt2[18]; this.k19 = arrayOfInt2[19];
/*  42 */     this.k20 = arrayOfInt2[20]; this.k21 = arrayOfInt2[21]; this.k22 = arrayOfInt2[22]; this.k23 = arrayOfInt2[23];
/*  43 */     this.k24 = arrayOfInt2[24]; this.k25 = arrayOfInt2[25]; this.k26 = arrayOfInt2[26]; this.k27 = arrayOfInt2[27];
/*  44 */     this.k28 = arrayOfInt2[28]; this.k29 = arrayOfInt2[29]; this.k30 = arrayOfInt2[30]; this.k31 = arrayOfInt2[31];
/*     */   }
/*     */   private int k15; private int k16; private int k17; private int k18; private int k19; private int k20; private int k21; private int k22; private int k23; private int k24; private int k25; private int k26; private int k27; private int k28; private int k29; private int k30; private int k31;
/*     */   
/*     */   public void encrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/*  49 */     if (paramInt2 % 16 != 0) {
/*  50 */       throw DbException.getInternalError("unaligned len " + paramInt2);
/*     */     }
/*  52 */     for (int i = paramInt1; i < paramInt1 + paramInt2; i += 8) {
/*  53 */       encryptBlock(paramArrayOfbyte, paramArrayOfbyte, i);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void decrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/*  59 */     if (paramInt2 % 16 != 0) {
/*  60 */       throw DbException.getInternalError("unaligned len " + paramInt2);
/*     */     }
/*  62 */     for (int i = paramInt1; i < paramInt1 + paramInt2; i += 8) {
/*  63 */       decryptBlock(paramArrayOfbyte, paramArrayOfbyte, i);
/*     */     }
/*     */   }
/*     */   
/*     */   private void encryptBlock(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
/*  68 */     int i = Bits.readInt(paramArrayOfbyte1, paramInt);
/*  69 */     int j = Bits.readInt(paramArrayOfbyte1, paramInt + 4);
/*  70 */     i += (j << 4 ^ j >>> 5) + j ^ this.k0;
/*  71 */     j += (i >>> 5 ^ i << 4) + i ^ this.k1;
/*  72 */     i += (j << 4 ^ j >>> 5) + j ^ this.k2;
/*  73 */     j += (i >>> 5 ^ i << 4) + i ^ this.k3;
/*  74 */     i += (j << 4 ^ j >>> 5) + j ^ this.k4;
/*  75 */     j += (i >>> 5 ^ i << 4) + i ^ this.k5;
/*  76 */     i += (j << 4 ^ j >>> 5) + j ^ this.k6;
/*  77 */     j += (i >>> 5 ^ i << 4) + i ^ this.k7;
/*  78 */     i += (j << 4 ^ j >>> 5) + j ^ this.k8;
/*  79 */     j += (i >>> 5 ^ i << 4) + i ^ this.k9;
/*  80 */     i += (j << 4 ^ j >>> 5) + j ^ this.k10;
/*  81 */     j += (i >>> 5 ^ i << 4) + i ^ this.k11;
/*  82 */     i += (j << 4 ^ j >>> 5) + j ^ this.k12;
/*  83 */     j += (i >>> 5 ^ i << 4) + i ^ this.k13;
/*  84 */     i += (j << 4 ^ j >>> 5) + j ^ this.k14;
/*  85 */     j += (i >>> 5 ^ i << 4) + i ^ this.k15;
/*  86 */     i += (j << 4 ^ j >>> 5) + j ^ this.k16;
/*  87 */     j += (i >>> 5 ^ i << 4) + i ^ this.k17;
/*  88 */     i += (j << 4 ^ j >>> 5) + j ^ this.k18;
/*  89 */     j += (i >>> 5 ^ i << 4) + i ^ this.k19;
/*  90 */     i += (j << 4 ^ j >>> 5) + j ^ this.k20;
/*  91 */     j += (i >>> 5 ^ i << 4) + i ^ this.k21;
/*  92 */     i += (j << 4 ^ j >>> 5) + j ^ this.k22;
/*  93 */     j += (i >>> 5 ^ i << 4) + i ^ this.k23;
/*  94 */     i += (j << 4 ^ j >>> 5) + j ^ this.k24;
/*  95 */     j += (i >>> 5 ^ i << 4) + i ^ this.k25;
/*  96 */     i += (j << 4 ^ j >>> 5) + j ^ this.k26;
/*  97 */     j += (i >>> 5 ^ i << 4) + i ^ this.k27;
/*  98 */     i += (j << 4 ^ j >>> 5) + j ^ this.k28;
/*  99 */     j += (i >>> 5 ^ i << 4) + i ^ this.k29;
/* 100 */     i += (j << 4 ^ j >>> 5) + j ^ this.k30;
/* 101 */     j += (i >>> 5 ^ i << 4) + i ^ this.k31;
/* 102 */     Bits.writeInt(paramArrayOfbyte2, paramInt, i);
/* 103 */     Bits.writeInt(paramArrayOfbyte2, paramInt + 4, j);
/*     */   }
/*     */   
/*     */   private void decryptBlock(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
/* 107 */     int i = Bits.readInt(paramArrayOfbyte1, paramInt);
/* 108 */     int j = Bits.readInt(paramArrayOfbyte1, paramInt + 4);
/* 109 */     j -= (i >>> 5 ^ i << 4) + i ^ this.k31;
/* 110 */     i -= (j << 4 ^ j >>> 5) + j ^ this.k30;
/* 111 */     j -= (i >>> 5 ^ i << 4) + i ^ this.k29;
/* 112 */     i -= (j << 4 ^ j >>> 5) + j ^ this.k28;
/* 113 */     j -= (i >>> 5 ^ i << 4) + i ^ this.k27;
/* 114 */     i -= (j << 4 ^ j >>> 5) + j ^ this.k26;
/* 115 */     j -= (i >>> 5 ^ i << 4) + i ^ this.k25;
/* 116 */     i -= (j << 4 ^ j >>> 5) + j ^ this.k24;
/* 117 */     j -= (i >>> 5 ^ i << 4) + i ^ this.k23;
/* 118 */     i -= (j << 4 ^ j >>> 5) + j ^ this.k22;
/* 119 */     j -= (i >>> 5 ^ i << 4) + i ^ this.k21;
/* 120 */     i -= (j << 4 ^ j >>> 5) + j ^ this.k20;
/* 121 */     j -= (i >>> 5 ^ i << 4) + i ^ this.k19;
/* 122 */     i -= (j << 4 ^ j >>> 5) + j ^ this.k18;
/* 123 */     j -= (i >>> 5 ^ i << 4) + i ^ this.k17;
/* 124 */     i -= (j << 4 ^ j >>> 5) + j ^ this.k16;
/* 125 */     j -= (i >>> 5 ^ i << 4) + i ^ this.k15;
/* 126 */     i -= (j << 4 ^ j >>> 5) + j ^ this.k14;
/* 127 */     j -= (i >>> 5 ^ i << 4) + i ^ this.k13;
/* 128 */     i -= (j << 4 ^ j >>> 5) + j ^ this.k12;
/* 129 */     j -= (i >>> 5 ^ i << 4) + i ^ this.k11;
/* 130 */     i -= (j << 4 ^ j >>> 5) + j ^ this.k10;
/* 131 */     j -= (i >>> 5 ^ i << 4) + i ^ this.k9;
/* 132 */     i -= (j << 4 ^ j >>> 5) + j ^ this.k8;
/* 133 */     j -= (i >>> 5 ^ i << 4) + i ^ this.k7;
/* 134 */     i -= (j << 4 ^ j >>> 5) + j ^ this.k6;
/* 135 */     j -= (i >>> 5 ^ i << 4) + i ^ this.k5;
/* 136 */     i -= (j << 4 ^ j >>> 5) + j ^ this.k4;
/* 137 */     j -= (i >>> 5 ^ i << 4) + i ^ this.k3;
/* 138 */     i -= (j << 4 ^ j >>> 5) + j ^ this.k2;
/* 139 */     j -= (i >>> 5 ^ i << 4) + i ^ this.k1;
/* 140 */     i -= (j << 4 ^ j >>> 5) + j ^ this.k0;
/* 141 */     Bits.writeInt(paramArrayOfbyte2, paramInt, i);
/* 142 */     Bits.writeInt(paramArrayOfbyte2, paramInt + 4, j);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getKeyLength() {
/* 147 */     return 16;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\XTEA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
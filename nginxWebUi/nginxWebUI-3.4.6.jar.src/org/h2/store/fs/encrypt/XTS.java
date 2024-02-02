/*     */ package org.h2.store.fs.encrypt;
/*     */ 
/*     */ import org.h2.security.BlockCipher;
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
/*     */ class XTS
/*     */ {
/*     */   private static final int GF_128_FEEDBACK = 135;
/*     */   private static final int CIPHER_BLOCK_SIZE = 16;
/*     */   private final BlockCipher cipher;
/*     */   
/*     */   XTS(BlockCipher paramBlockCipher) {
/*  31 */     this.cipher = paramBlockCipher;
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
/*     */   void encrypt(long paramLong, int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
/*  43 */     byte[] arrayOfByte = initTweak(paramLong);
/*  44 */     byte b = 0;
/*  45 */     for (; b + 16 <= paramInt1; b += 16) {
/*  46 */       if (b > 0) {
/*  47 */         updateTweak(arrayOfByte);
/*     */       }
/*  49 */       xorTweak(paramArrayOfbyte, b + paramInt2, arrayOfByte);
/*  50 */       this.cipher.encrypt(paramArrayOfbyte, b + paramInt2, 16);
/*  51 */       xorTweak(paramArrayOfbyte, b + paramInt2, arrayOfByte);
/*     */     } 
/*  53 */     if (b < paramInt1) {
/*  54 */       updateTweak(arrayOfByte);
/*  55 */       swap(paramArrayOfbyte, b + paramInt2, b - 16 + paramInt2, paramInt1 - b);
/*  56 */       xorTweak(paramArrayOfbyte, b - 16 + paramInt2, arrayOfByte);
/*  57 */       this.cipher.encrypt(paramArrayOfbyte, b - 16 + paramInt2, 16);
/*  58 */       xorTweak(paramArrayOfbyte, b - 16 + paramInt2, arrayOfByte);
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
/*     */   void decrypt(long paramLong, int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
/*  71 */     byte[] arrayOfByte1 = initTweak(paramLong), arrayOfByte2 = arrayOfByte1;
/*  72 */     byte b = 0;
/*  73 */     for (; b + 16 <= paramInt1; b += 16) {
/*  74 */       if (b > 0) {
/*  75 */         updateTweak(arrayOfByte1);
/*  76 */         if (b + 16 + 16 > paramInt1 && b + 16 < paramInt1) {
/*     */           
/*  78 */           arrayOfByte2 = (byte[])arrayOfByte1.clone();
/*  79 */           updateTweak(arrayOfByte1);
/*     */         } 
/*     */       } 
/*  82 */       xorTweak(paramArrayOfbyte, b + paramInt2, arrayOfByte1);
/*  83 */       this.cipher.decrypt(paramArrayOfbyte, b + paramInt2, 16);
/*  84 */       xorTweak(paramArrayOfbyte, b + paramInt2, arrayOfByte1);
/*     */     } 
/*  86 */     if (b < paramInt1) {
/*  87 */       swap(paramArrayOfbyte, b, b - 16 + paramInt2, paramInt1 - b + paramInt2);
/*  88 */       xorTweak(paramArrayOfbyte, b - 16 + paramInt2, arrayOfByte2);
/*  89 */       this.cipher.decrypt(paramArrayOfbyte, b - 16 + paramInt2, 16);
/*  90 */       xorTweak(paramArrayOfbyte, b - 16 + paramInt2, arrayOfByte2);
/*     */     } 
/*     */   }
/*     */   
/*     */   private byte[] initTweak(long paramLong) {
/*  95 */     byte[] arrayOfByte = new byte[16];
/*  96 */     for (byte b = 0; b < 16; b++, paramLong >>>= 8L) {
/*  97 */       arrayOfByte[b] = (byte)(int)(paramLong & 0xFFL);
/*     */     }
/*  99 */     this.cipher.encrypt(arrayOfByte, 0, 16);
/* 100 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */   private static void xorTweak(byte[] paramArrayOfbyte1, int paramInt, byte[] paramArrayOfbyte2) {
/* 104 */     for (byte b = 0; b < 16; b++) {
/* 105 */       paramArrayOfbyte1[paramInt + b] = (byte)(paramArrayOfbyte1[paramInt + b] ^ paramArrayOfbyte2[b]);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void updateTweak(byte[] paramArrayOfbyte) {
/* 110 */     byte b1 = 0, b2 = 0;
/* 111 */     for (byte b = 0; b < 16; b++) {
/* 112 */       b2 = (byte)(paramArrayOfbyte[b] >> 7 & 0x1);
/* 113 */       paramArrayOfbyte[b] = (byte)((paramArrayOfbyte[b] << 1) + b1 & 0xFF);
/* 114 */       b1 = b2;
/*     */     } 
/* 116 */     if (b2 != 0) {
/* 117 */       paramArrayOfbyte[0] = (byte)(paramArrayOfbyte[0] ^ 0x87);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void swap(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) {
/* 122 */     for (byte b = 0; b < paramInt3; b++) {
/* 123 */       byte b1 = paramArrayOfbyte[paramInt1 + b];
/* 124 */       paramArrayOfbyte[paramInt1 + b] = paramArrayOfbyte[paramInt2 + b];
/* 125 */       paramArrayOfbyte[paramInt2 + b] = b1;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\encrypt\XTS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package org.h2.security;
/*     */ 
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Arrays;
/*     */ import javax.crypto.Mac;
/*     */ import javax.crypto.spec.SecretKeySpec;
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
/*     */ public class SHA256
/*     */ {
/*     */   public static byte[] getHashWithSalt(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
/*  38 */     byte[] arrayOfByte = new byte[paramArrayOfbyte1.length + paramArrayOfbyte2.length];
/*  39 */     System.arraycopy(paramArrayOfbyte1, 0, arrayOfByte, 0, paramArrayOfbyte1.length);
/*  40 */     System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte, paramArrayOfbyte1.length, paramArrayOfbyte2.length);
/*  41 */     return getHash(arrayOfByte, true);
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
/*     */   public static byte[] getKeyPasswordHash(String paramString, char[] paramArrayOfchar) {
/*  57 */     String str = paramString + "@";
/*  58 */     byte[] arrayOfByte = new byte[2 * (str.length() + paramArrayOfchar.length)];
/*  59 */     byte b1 = 0;
/*  60 */     for (byte b2 = 0; b2 < null; b2++) {
/*  61 */       char c = str.charAt(b2);
/*  62 */       arrayOfByte[b1++] = (byte)(c >> 8);
/*  63 */       arrayOfByte[b1++] = (byte)c;
/*     */     } 
/*  65 */     for (char c : paramArrayOfchar) {
/*  66 */       arrayOfByte[b1++] = (byte)(c >> 8);
/*  67 */       arrayOfByte[b1++] = (byte)c;
/*     */     } 
/*  69 */     Arrays.fill(paramArrayOfchar, false);
/*  70 */     return getHash(arrayOfByte, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] getHMAC(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
/*  81 */     return initMac(paramArrayOfbyte1).doFinal(paramArrayOfbyte2);
/*     */   }
/*     */ 
/*     */   
/*     */   private static Mac initMac(byte[] paramArrayOfbyte) {
/*  86 */     if (paramArrayOfbyte.length == 0) {
/*  87 */       paramArrayOfbyte = new byte[1];
/*     */     }
/*     */     try {
/*  90 */       Mac mac = Mac.getInstance("HmacSHA256");
/*  91 */       mac.init(new SecretKeySpec(paramArrayOfbyte, "HmacSHA256"));
/*  92 */       return mac;
/*  93 */     } catch (GeneralSecurityException generalSecurityException) {
/*  94 */       throw new RuntimeException(generalSecurityException);
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
/*     */   
/*     */   public static byte[] getPBKDF2(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt1, int paramInt2) {
/* 109 */     byte[] arrayOfByte1 = new byte[paramInt2];
/* 110 */     Mac mac = initMac(paramArrayOfbyte1);
/* 111 */     int i = 64 + Math.max(32, paramArrayOfbyte2.length + 4);
/* 112 */     byte[] arrayOfByte2 = new byte[i];
/* 113 */     byte[] arrayOfByte3 = null;
/* 114 */     for (byte b1 = 1, b2 = 0; b2 < paramInt2; b1++, b2 += 32) {
/* 115 */       for (byte b = 0; b < paramInt1; b++) {
/* 116 */         if (b == 0) {
/* 117 */           System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte2, 0, paramArrayOfbyte2.length);
/* 118 */           Bits.writeInt(arrayOfByte2, paramArrayOfbyte2.length, b1);
/* 119 */           i = paramArrayOfbyte2.length + 4;
/*     */         } else {
/* 121 */           System.arraycopy(arrayOfByte3, 0, arrayOfByte2, 0, 32);
/* 122 */           i = 32;
/*     */         } 
/* 124 */         mac.update(arrayOfByte2, 0, i);
/* 125 */         arrayOfByte3 = mac.doFinal();
/* 126 */         for (byte b3 = 0; b3 < 32 && b3 + b2 < paramInt2; b3++) {
/* 127 */           arrayOfByte1[b3 + b2] = (byte)(arrayOfByte1[b3 + b2] ^ arrayOfByte3[b3]);
/*     */         }
/*     */       } 
/*     */     } 
/* 131 */     Arrays.fill(paramArrayOfbyte1, (byte)0);
/* 132 */     return arrayOfByte1;
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
/*     */   public static byte[] getHash(byte[] paramArrayOfbyte, boolean paramBoolean) {
/*     */     byte[] arrayOfByte;
/*     */     try {
/* 146 */       arrayOfByte = MessageDigest.getInstance("SHA-256").digest(paramArrayOfbyte);
/* 147 */     } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
/* 148 */       throw new RuntimeException(noSuchAlgorithmException);
/*     */     } 
/* 150 */     if (paramBoolean) {
/* 151 */       Arrays.fill(paramArrayOfbyte, (byte)0);
/*     */     }
/* 153 */     return arrayOfByte;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\SHA256.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
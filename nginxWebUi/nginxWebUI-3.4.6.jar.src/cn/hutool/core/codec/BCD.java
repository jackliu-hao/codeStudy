/*     */ package cn.hutool.core.codec;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
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
/*     */ public class BCD
/*     */ {
/*     */   public static byte[] strToBcd(String asc) {
/*  20 */     int len = asc.length();
/*  21 */     int mod = len % 2;
/*  22 */     if (mod != 0) {
/*  23 */       asc = "0" + asc;
/*  24 */       len = asc.length();
/*     */     } 
/*     */     
/*  27 */     if (len >= 2) {
/*  28 */       len >>= 1;
/*     */     }
/*     */     
/*  31 */     byte[] bbt = new byte[len];
/*  32 */     byte[] abt = asc.getBytes();
/*     */ 
/*     */     
/*  35 */     for (int p = 0; p < asc.length() / 2; p++) {
/*  36 */       int j, k; if (abt[2 * p] >= 48 && abt[2 * p] <= 57) {
/*  37 */         j = abt[2 * p] - 48;
/*  38 */       } else if (abt[2 * p] >= 97 && abt[2 * p] <= 122) {
/*  39 */         j = abt[2 * p] - 97 + 10;
/*     */       } else {
/*  41 */         j = abt[2 * p] - 65 + 10;
/*     */       } 
/*  43 */       if (abt[2 * p + 1] >= 48 && abt[2 * p + 1] <= 57) {
/*  44 */         k = abt[2 * p + 1] - 48;
/*  45 */       } else if (abt[2 * p + 1] >= 97 && abt[2 * p + 1] <= 122) {
/*  46 */         k = abt[2 * p + 1] - 97 + 10;
/*     */       } else {
/*  48 */         k = abt[2 * p + 1] - 65 + 10;
/*     */       } 
/*  50 */       int a = (j << 4) + k;
/*  51 */       byte b = (byte)a;
/*  52 */       bbt[p] = b;
/*     */     } 
/*  54 */     return bbt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] ascToBcd(byte[] ascii) {
/*  63 */     Assert.notNull(ascii, "Ascii must be not null!", new Object[0]);
/*  64 */     return ascToBcd(ascii, ascii.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] ascToBcd(byte[] ascii, int ascLength) {
/*  74 */     Assert.notNull(ascii, "Ascii must be not null!", new Object[0]);
/*  75 */     byte[] bcd = new byte[ascLength / 2];
/*  76 */     int j = 0;
/*  77 */     for (int i = 0; i < (ascLength + 1) / 2; i++) {
/*  78 */       bcd[i] = ascToBcd(ascii[j++]);
/*  79 */       bcd[i] = (byte)(((j >= ascLength) ? 0 : ascToBcd(ascii[j++])) + (bcd[i] << 4));
/*     */     } 
/*  81 */     return bcd;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String bcdToStr(byte[] bytes) {
/*  90 */     Assert.notNull(bytes, "Bcd bytes must be not null!", new Object[0]);
/*  91 */     char[] temp = new char[bytes.length * 2];
/*     */ 
/*     */     
/*  94 */     for (int i = 0; i < bytes.length; i++) {
/*  95 */       char val = (char)((bytes[i] & 0xF0) >> 4 & 0xF);
/*  96 */       temp[i * 2] = (char)((val > '\t') ? (val + 65 - 10) : (val + 48));
/*     */       
/*  98 */       val = (char)(bytes[i] & 0xF);
/*  99 */       temp[i * 2 + 1] = (char)((val > '\t') ? (val + 65 - 10) : (val + 48));
/*     */     } 
/* 101 */     return new String(temp);
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
/*     */   private static byte ascToBcd(byte asc) {
/*     */     byte bcd;
/* 114 */     if (asc >= 48 && asc <= 57) {
/* 115 */       bcd = (byte)(asc - 48);
/* 116 */     } else if (asc >= 65 && asc <= 70) {
/* 117 */       bcd = (byte)(asc - 65 + 10);
/* 118 */     } else if (asc >= 97 && asc <= 102) {
/* 119 */       bcd = (byte)(asc - 97 + 10);
/*     */     } else {
/* 121 */       bcd = (byte)(asc - 48);
/*     */     } 
/* 123 */     return bcd;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\BCD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
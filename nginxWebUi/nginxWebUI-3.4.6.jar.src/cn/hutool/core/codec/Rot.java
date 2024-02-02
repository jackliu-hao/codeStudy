/*     */ package cn.hutool.core.codec;
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
/*     */ public class Rot
/*     */ {
/*     */   private static final char aCHAR = 'a';
/*     */   private static final char zCHAR = 'z';
/*     */   private static final char ACHAR = 'A';
/*     */   private static final char ZCHAR = 'Z';
/*     */   private static final char CHAR0 = '0';
/*     */   private static final char CHAR9 = '9';
/*     */   
/*     */   public static String encode13(String message) {
/*  26 */     return encode13(message, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode13(String message, boolean isEnocdeNumber) {
/*  37 */     return encode(message, 13, isEnocdeNumber);
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
/*     */   public static String encode(String message, int offset, boolean isEnocdeNumber) {
/*  49 */     int len = message.length();
/*  50 */     char[] chars = new char[len];
/*     */     
/*  52 */     for (int i = 0; i < len; i++) {
/*  53 */       chars[i] = encodeChar(message.charAt(i), offset, isEnocdeNumber);
/*     */     }
/*  55 */     return new String(chars);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decode13(String rot) {
/*  65 */     return decode13(rot, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decode13(String rot, boolean isDecodeNumber) {
/*  76 */     return decode(rot, 13, isDecodeNumber);
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
/*     */   public static String decode(String rot, int offset, boolean isDecodeNumber) {
/*  88 */     int len = rot.length();
/*  89 */     char[] chars = new char[len];
/*     */     
/*  91 */     for (int i = 0; i < len; i++) {
/*  92 */       chars[i] = decodeChar(rot.charAt(i), offset, isDecodeNumber);
/*     */     }
/*  94 */     return new String(chars);
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
/*     */   private static char encodeChar(char c, int offset, boolean isDecodeNumber) {
/* 107 */     if (isDecodeNumber && 
/* 108 */       c >= '0' && c <= '9') {
/* 109 */       c = (char)(c - 48);
/* 110 */       c = (char)((c + offset) % 10);
/* 111 */       c = (char)(c + 48);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 116 */     if (c >= 'A' && c <= 'Z') {
/* 117 */       c = (char)(c - 65);
/* 118 */       c = (char)((c + offset) % 26);
/* 119 */       c = (char)(c + 65);
/*     */     
/*     */     }
/* 122 */     else if (c >= 'a' && c <= 'z') {
/* 123 */       c = (char)(c - 97);
/* 124 */       c = (char)((c + offset) % 26);
/* 125 */       c = (char)(c + 97);
/*     */     } 
/* 127 */     return c;
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
/*     */   private static char decodeChar(char c, int offset, boolean isDecodeNumber) {
/* 139 */     int temp = c;
/*     */     
/* 141 */     if (isDecodeNumber && 
/* 142 */       temp >= 48 && temp <= 57) {
/* 143 */       temp -= 48;
/* 144 */       temp -= offset;
/* 145 */       while (temp < 0) {
/* 146 */         temp += 10;
/*     */       }
/* 148 */       temp += 48;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 153 */     if (temp >= 65 && temp <= 90) {
/* 154 */       temp -= 65;
/*     */       
/* 156 */       temp -= offset;
/* 157 */       while (temp < 0) {
/* 158 */         temp = 26 + temp;
/*     */       }
/* 160 */       temp += 65;
/* 161 */     } else if (temp >= 97 && temp <= 122) {
/* 162 */       temp -= 97;
/*     */       
/* 164 */       temp -= offset;
/* 165 */       if (temp < 0) {
/* 166 */         temp = 26 + temp;
/*     */       }
/* 168 */       temp += 97;
/*     */     } 
/* 170 */     return (char)temp;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\Rot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
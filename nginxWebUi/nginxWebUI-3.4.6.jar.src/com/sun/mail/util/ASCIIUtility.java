/*     */ package com.sun.mail.util;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
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
/*     */ public class ASCIIUtility
/*     */ {
/*     */   public static int parseInt(byte[] b, int start, int end, int radix) throws NumberFormatException {
/*  62 */     if (b == null) {
/*  63 */       throw new NumberFormatException("null");
/*     */     }
/*  65 */     int result = 0;
/*  66 */     boolean negative = false;
/*  67 */     int i = start;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  72 */     if (end > start) {
/*  73 */       int limit; if (b[i] == 45) {
/*  74 */         negative = true;
/*  75 */         limit = Integer.MIN_VALUE;
/*  76 */         i++;
/*     */       } else {
/*  78 */         limit = -2147483647;
/*     */       } 
/*  80 */       int multmin = limit / radix;
/*  81 */       if (i < end) {
/*  82 */         int digit = Character.digit((char)b[i++], radix);
/*  83 */         if (digit < 0) {
/*  84 */           throw new NumberFormatException("illegal number: " + toString(b, start, end));
/*     */         }
/*     */ 
/*     */         
/*  88 */         result = -digit;
/*     */       } 
/*     */       
/*  91 */       while (i < end) {
/*     */         
/*  93 */         int digit = Character.digit((char)b[i++], radix);
/*  94 */         if (digit < 0) {
/*  95 */           throw new NumberFormatException("illegal number");
/*     */         }
/*  97 */         if (result < multmin) {
/*  98 */           throw new NumberFormatException("illegal number");
/*     */         }
/* 100 */         result *= radix;
/* 101 */         if (result < limit + digit) {
/* 102 */           throw new NumberFormatException("illegal number");
/*     */         }
/* 104 */         result -= digit;
/*     */       } 
/*     */     } else {
/* 107 */       throw new NumberFormatException("illegal number");
/*     */     } 
/* 109 */     if (negative) {
/* 110 */       if (i > start + 1) {
/* 111 */         return result;
/*     */       }
/* 113 */       throw new NumberFormatException("illegal number");
/*     */     } 
/*     */     
/* 116 */     return -result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int parseInt(byte[] b, int start, int end) throws NumberFormatException {
/* 127 */     return parseInt(b, start, end, 10);
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
/*     */   public static long parseLong(byte[] b, int start, int end, int radix) throws NumberFormatException {
/* 139 */     if (b == null) {
/* 140 */       throw new NumberFormatException("null");
/*     */     }
/* 142 */     long result = 0L;
/* 143 */     boolean negative = false;
/* 144 */     int i = start;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 149 */     if (end > start) {
/* 150 */       long limit; if (b[i] == 45) {
/* 151 */         negative = true;
/* 152 */         limit = Long.MIN_VALUE;
/* 153 */         i++;
/*     */       } else {
/* 155 */         limit = -9223372036854775807L;
/*     */       } 
/* 157 */       long multmin = limit / radix;
/* 158 */       if (i < end) {
/* 159 */         int digit = Character.digit((char)b[i++], radix);
/* 160 */         if (digit < 0) {
/* 161 */           throw new NumberFormatException("illegal number: " + toString(b, start, end));
/*     */         }
/*     */ 
/*     */         
/* 165 */         result = -digit;
/*     */       } 
/*     */       
/* 168 */       while (i < end) {
/*     */         
/* 170 */         int digit = Character.digit((char)b[i++], radix);
/* 171 */         if (digit < 0) {
/* 172 */           throw new NumberFormatException("illegal number");
/*     */         }
/* 174 */         if (result < multmin) {
/* 175 */           throw new NumberFormatException("illegal number");
/*     */         }
/* 177 */         result *= radix;
/* 178 */         if (result < limit + digit) {
/* 179 */           throw new NumberFormatException("illegal number");
/*     */         }
/* 181 */         result -= digit;
/*     */       } 
/*     */     } else {
/* 184 */       throw new NumberFormatException("illegal number");
/*     */     } 
/* 186 */     if (negative) {
/* 187 */       if (i > start + 1) {
/* 188 */         return result;
/*     */       }
/* 190 */       throw new NumberFormatException("illegal number");
/*     */     } 
/*     */     
/* 193 */     return -result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long parseLong(byte[] b, int start, int end) throws NumberFormatException {
/* 204 */     return parseLong(b, start, end, 10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(byte[] b, int start, int end) {
/* 213 */     int size = end - start;
/* 214 */     char[] theChars = new char[size];
/*     */     
/* 216 */     for (int i = 0, j = start; i < size;) {
/* 217 */       theChars[i++] = (char)(b[j++] & 0xFF);
/*     */     }
/* 219 */     return new String(theChars);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(byte[] b) {
/* 228 */     return toString(b, 0, b.length);
/*     */   }
/*     */   
/*     */   public static String toString(ByteArrayInputStream is) {
/* 232 */     int size = is.available();
/* 233 */     char[] theChars = new char[size];
/* 234 */     byte[] bytes = new byte[size];
/*     */     
/* 236 */     is.read(bytes, 0, size);
/* 237 */     for (int i = 0; i < size;) {
/* 238 */       theChars[i] = (char)(bytes[i++] & 0xFF);
/*     */     }
/* 240 */     return new String(theChars);
/*     */   }
/*     */ 
/*     */   
/*     */   public static byte[] getBytes(String s) {
/* 245 */     char[] chars = s.toCharArray();
/* 246 */     int size = chars.length;
/* 247 */     byte[] bytes = new byte[size];
/*     */     
/* 249 */     for (int i = 0; i < size;)
/* 250 */       bytes[i] = (byte)chars[i++]; 
/* 251 */     return bytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public static byte[] getBytes(InputStream is) throws IOException {
/*     */     byte[] buf;
/* 257 */     int size = 1024;
/*     */ 
/*     */ 
/*     */     
/* 261 */     if (is instanceof ByteArrayInputStream) {
/* 262 */       size = is.available();
/* 263 */       buf = new byte[size];
/* 264 */       int len = is.read(buf, 0, size);
/*     */     } else {
/*     */       
/* 267 */       ByteArrayOutputStream bos = new ByteArrayOutputStream();
/* 268 */       buf = new byte[size]; int len;
/* 269 */       while ((len = is.read(buf, 0, size)) != -1)
/* 270 */         bos.write(buf, 0, len); 
/* 271 */       buf = bos.toByteArray();
/*     */     } 
/* 273 */     return buf;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\ASCIIUtility.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */
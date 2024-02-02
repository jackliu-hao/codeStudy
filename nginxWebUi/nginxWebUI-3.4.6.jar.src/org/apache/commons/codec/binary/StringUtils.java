/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ public class StringUtils
/*     */ {
/*     */   public static boolean equals(CharSequence cs1, CharSequence cs2) {
/*  72 */     if (cs1 == cs2) {
/*  73 */       return true;
/*     */     }
/*  75 */     if (cs1 == null || cs2 == null) {
/*  76 */       return false;
/*     */     }
/*  78 */     if (cs1 instanceof String && cs2 instanceof String) {
/*  79 */       return cs1.equals(cs2);
/*     */     }
/*  81 */     return (cs1.length() == cs2.length() && CharSequenceUtils.regionMatches(cs1, false, 0, cs2, 0, cs1.length()));
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
/*     */   private static byte[] getBytes(String string, Charset charset) {
/*  94 */     if (string == null) {
/*  95 */       return null;
/*     */     }
/*  97 */     return string.getBytes(charset);
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
/*     */   private static ByteBuffer getByteBuffer(String string, Charset charset) {
/* 110 */     if (string == null) {
/* 111 */       return null;
/*     */     }
/* 113 */     return ByteBuffer.wrap(string.getBytes(charset));
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
/*     */ 
/*     */   
/*     */   public static ByteBuffer getByteBufferUtf8(String string) {
/* 131 */     return getByteBuffer(string, StandardCharsets.UTF_8);
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
/*     */ 
/*     */   
/*     */   public static byte[] getBytesIso8859_1(String string) {
/* 149 */     return getBytes(string, StandardCharsets.ISO_8859_1);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] getBytesUnchecked(String string, String charsetName) {
/* 173 */     if (string == null) {
/* 174 */       return null;
/*     */     }
/*     */     try {
/* 177 */       return string.getBytes(charsetName);
/* 178 */     } catch (UnsupportedEncodingException e) {
/* 179 */       throw newIllegalStateException(charsetName, e);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] getBytesUsAscii(String string) {
/* 198 */     return getBytes(string, StandardCharsets.US_ASCII);
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
/*     */ 
/*     */   
/*     */   public static byte[] getBytesUtf16(String string) {
/* 216 */     return getBytes(string, StandardCharsets.UTF_16);
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
/*     */ 
/*     */   
/*     */   public static byte[] getBytesUtf16Be(String string) {
/* 234 */     return getBytes(string, StandardCharsets.UTF_16BE);
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
/*     */ 
/*     */   
/*     */   public static byte[] getBytesUtf16Le(String string) {
/* 252 */     return getBytes(string, StandardCharsets.UTF_16LE);
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
/*     */ 
/*     */   
/*     */   public static byte[] getBytesUtf8(String string) {
/* 270 */     return getBytes(string, StandardCharsets.UTF_8);
/*     */   }
/*     */ 
/*     */   
/*     */   private static IllegalStateException newIllegalStateException(String charsetName, UnsupportedEncodingException e) {
/* 275 */     return new IllegalStateException(charsetName + ": " + e);
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
/*     */   private static String newString(byte[] bytes, Charset charset) {
/* 291 */     return (bytes == null) ? null : new String(bytes, charset);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String newString(byte[] bytes, String charsetName) {
/* 314 */     if (bytes == null) {
/* 315 */       return null;
/*     */     }
/*     */     try {
/* 318 */       return new String(bytes, charsetName);
/* 319 */     } catch (UnsupportedEncodingException e) {
/* 320 */       throw newIllegalStateException(charsetName, e);
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
/*     */ 
/*     */   
/*     */   public static String newStringIso8859_1(byte[] bytes) {
/* 337 */     return newString(bytes, StandardCharsets.ISO_8859_1);
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
/*     */   public static String newStringUsAscii(byte[] bytes) {
/* 353 */     return newString(bytes, StandardCharsets.US_ASCII);
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
/*     */   public static String newStringUtf16(byte[] bytes) {
/* 369 */     return newString(bytes, StandardCharsets.UTF_16);
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
/*     */   public static String newStringUtf16Be(byte[] bytes) {
/* 385 */     return newString(bytes, StandardCharsets.UTF_16BE);
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
/*     */   public static String newStringUtf16Le(byte[] bytes) {
/* 401 */     return newString(bytes, StandardCharsets.UTF_16LE);
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
/*     */   public static String newStringUtf8(byte[] bytes) {
/* 417 */     return newString(bytes, StandardCharsets.UTF_8);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\binary\StringUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
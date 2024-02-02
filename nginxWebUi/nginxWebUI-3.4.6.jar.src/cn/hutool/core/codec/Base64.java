/*     */ package cn.hutool.core.codec;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Base64
/*     */ {
/*  22 */   private static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] encode(byte[] arr, boolean lineSep) {
/*  33 */     return lineSep ? 
/*  34 */       java.util.Base64.getMimeEncoder().encode(arr) : 
/*  35 */       java.util.Base64.getEncoder().encode(arr);
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
/*     */   @Deprecated
/*     */   public static byte[] encodeUrlSafe(byte[] arr, boolean lineSep) {
/*  49 */     return Base64Encoder.encodeUrlSafe(arr, lineSep);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(CharSequence source) {
/*  59 */     return encode(source, DEFAULT_CHARSET);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeUrlSafe(CharSequence source) {
/*  70 */     return encodeUrlSafe(source, DEFAULT_CHARSET);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(CharSequence source, String charset) {
/*  81 */     return encode(source, CharsetUtil.charset(charset));
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
/*     */   public static String encodeWithoutPadding(CharSequence source, String charset) {
/*  93 */     return encodeWithoutPadding(StrUtil.bytes(source, charset));
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
/*     */   @Deprecated
/*     */   public static String encodeUrlSafe(CharSequence source, String charset) {
/* 107 */     return encodeUrlSafe(source, CharsetUtil.charset(charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(CharSequence source, Charset charset) {
/* 118 */     return encode(StrUtil.bytes(source, charset));
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
/*     */   public static String encodeUrlSafe(CharSequence source, Charset charset) {
/* 130 */     return encodeUrlSafe(StrUtil.bytes(source, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(byte[] source) {
/* 140 */     return java.util.Base64.getEncoder().encodeToString(source);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeWithoutPadding(byte[] source) {
/* 151 */     return java.util.Base64.getEncoder().withoutPadding().encodeToString(source);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeUrlSafe(byte[] source) {
/* 162 */     return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(source);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(InputStream in) {
/* 173 */     return encode(IoUtil.readBytes(in));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeUrlSafe(InputStream in) {
/* 184 */     return encodeUrlSafe(IoUtil.readBytes(in));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(File file) {
/* 195 */     return encode(FileUtil.readBytes(file));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeUrlSafe(File file) {
/* 206 */     return encodeUrlSafe(FileUtil.readBytes(file));
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
/*     */   public static String encodeStr(byte[] arr, boolean isMultiLine, boolean isUrlSafe) {
/* 220 */     return StrUtil.str(encode(arr, isMultiLine, isUrlSafe), DEFAULT_CHARSET);
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
/*     */   public static byte[] encode(byte[] arr, boolean isMultiLine, boolean isUrlSafe) {
/* 233 */     return Base64Encoder.encode(arr, isMultiLine, isUrlSafe);
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
/*     */   public static String decodeStrGbk(CharSequence source) {
/* 246 */     return Base64Decoder.decodeStr(source, CharsetUtil.CHARSET_GBK);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decodeStr(CharSequence source) {
/* 256 */     return Base64Decoder.decodeStr(source);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decodeStr(CharSequence source, String charset) {
/* 267 */     return decodeStr(source, CharsetUtil.charset(charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decodeStr(CharSequence source, Charset charset) {
/* 278 */     return Base64Decoder.decodeStr(source, charset);
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
/*     */   public static File decodeToFile(CharSequence base64, File destFile) {
/* 290 */     return FileUtil.writeBytes(Base64Decoder.decode(base64), destFile);
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
/*     */   public static void decodeToStream(CharSequence base64, OutputStream out, boolean isCloseOut) {
/* 302 */     IoUtil.write(out, isCloseOut, Base64Decoder.decode(base64));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] decode(CharSequence base64) {
/* 312 */     return Base64Decoder.decode(base64);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] decode(byte[] in) {
/* 322 */     return Base64Decoder.decode(in);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isBase64(CharSequence base64) {
/* 333 */     if (base64 == null || base64.length() < 2) {
/* 334 */       return false;
/*     */     }
/*     */     
/* 337 */     byte[] bytes = StrUtil.utf8Bytes(base64);
/*     */     
/* 339 */     if (bytes.length != base64.length())
/*     */     {
/* 341 */       return false;
/*     */     }
/*     */     
/* 344 */     return isBase64(bytes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isBase64(byte[] base64Bytes) {
/* 355 */     boolean hasPadding = false;
/* 356 */     for (byte base64Byte : base64Bytes) {
/* 357 */       if (hasPadding) {
/* 358 */         if (61 != base64Byte)
/*     */         {
/* 360 */           return false;
/*     */         }
/* 362 */       } else if (61 == base64Byte) {
/*     */         
/* 364 */         hasPadding = true;
/* 365 */       } else if (false == ((Base64Decoder.isBase64Code(base64Byte) || isWhiteSpace(base64Byte)) ? true : false)) {
/* 366 */         return false;
/*     */       } 
/*     */     } 
/* 369 */     return true;
/*     */   }
/*     */   
/*     */   private static boolean isWhiteSpace(byte byteToCheck) {
/* 373 */     switch (byteToCheck) {
/*     */       case 9:
/*     */       case 10:
/*     */       case 13:
/*     */       case 32:
/* 378 */         return true;
/*     */     } 
/* 380 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\Base64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
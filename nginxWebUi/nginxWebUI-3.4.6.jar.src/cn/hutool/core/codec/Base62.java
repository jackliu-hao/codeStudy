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
/*     */ public class Base62
/*     */ {
/*  21 */   private static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(CharSequence source) {
/*  31 */     return encode(source, DEFAULT_CHARSET);
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
/*  42 */     return encode(StrUtil.bytes(source, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(byte[] source) {
/*  52 */     return new String(Base62Codec.INSTANCE.encode(source));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(InputStream in) {
/*  62 */     return encode(IoUtil.readBytes(in));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(File file) {
/*  72 */     return encode(FileUtil.readBytes(file));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeInverted(CharSequence source) {
/*  82 */     return encodeInverted(source, DEFAULT_CHARSET);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeInverted(CharSequence source, Charset charset) {
/*  93 */     return encodeInverted(StrUtil.bytes(source, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeInverted(byte[] source) {
/* 103 */     return new String(Base62Codec.INSTANCE.encode(source, true));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeInverted(InputStream in) {
/* 113 */     return encodeInverted(IoUtil.readBytes(in));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeInverted(File file) {
/* 123 */     return encodeInverted(FileUtil.readBytes(file));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decodeStrGbk(CharSequence source) {
/* 134 */     return decodeStr(source, CharsetUtil.CHARSET_GBK);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decodeStr(CharSequence source) {
/* 144 */     return decodeStr(source, DEFAULT_CHARSET);
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
/* 155 */     return StrUtil.str(decode(source), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static File decodeToFile(CharSequence charSequence, File destFile) {
/* 166 */     return FileUtil.writeBytes(decode(charSequence), destFile);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void decodeToStream(CharSequence base62Str, OutputStream out, boolean isCloseOut) {
/* 177 */     IoUtil.write(out, isCloseOut, decode(base62Str));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] decode(CharSequence base62Str) {
/* 187 */     return decode(StrUtil.bytes(base62Str, DEFAULT_CHARSET));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] decode(byte[] base62bytes) {
/* 197 */     return Base62Codec.INSTANCE.decode(base62bytes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decodeStrInverted(CharSequence source) {
/* 207 */     return decodeStrInverted(source, DEFAULT_CHARSET);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decodeStrInverted(CharSequence source, Charset charset) {
/* 218 */     return StrUtil.str(decodeInverted(source), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static File decodeToFileInverted(CharSequence charSequence, File destFile) {
/* 229 */     return FileUtil.writeBytes(decodeInverted(charSequence), destFile);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void decodeToStreamInverted(CharSequence base62Str, OutputStream out, boolean isCloseOut) {
/* 240 */     IoUtil.write(out, isCloseOut, decodeInverted(base62Str));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] decodeInverted(CharSequence base62Str) {
/* 250 */     return decodeInverted(StrUtil.bytes(base62Str, DEFAULT_CHARSET));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] decodeInverted(byte[] base62bytes) {
/* 260 */     return Base62Codec.INSTANCE.decode(base62bytes, true);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\Base62.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package cn.hutool.core.codec;
/*     */ 
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
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
/*     */ public class Base32
/*     */ {
/*     */   public static String encode(byte[] bytes) {
/*  30 */     return Base32Codec.INSTANCE.encode(bytes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(String source) {
/*  40 */     return encode(source, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(String source, Charset charset) {
/*  51 */     return encode(StrUtil.bytes(source, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeHex(byte[] bytes) {
/*  61 */     return Base32Codec.INSTANCE.encode(bytes, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeHex(String source) {
/*  71 */     return encodeHex(source, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeHex(String source, Charset charset) {
/*  82 */     return encodeHex(StrUtil.bytes(source, charset));
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
/*     */   public static byte[] decode(String base32) {
/*  94 */     return Base32Codec.INSTANCE.decode(base32);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decodeStr(String source) {
/* 104 */     return decodeStr(source, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decodeStr(String source, Charset charset) {
/* 115 */     return StrUtil.str(decode(source), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] decodeHex(String base32) {
/* 125 */     return Base32Codec.INSTANCE.decode(base32, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decodeStrHex(String source) {
/* 135 */     return decodeStrHex(source, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decodeStrHex(String source, Charset charset) {
/* 146 */     return StrUtil.str(decodeHex(source), charset);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\Base32.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
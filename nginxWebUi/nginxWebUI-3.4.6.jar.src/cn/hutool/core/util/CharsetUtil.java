/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.io.CharsetDetector;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.charset.UnsupportedCharsetException;
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
/*     */ public class CharsetUtil
/*     */ {
/*     */   public static final String ISO_8859_1 = "ISO-8859-1";
/*     */   public static final String UTF_8 = "UTF-8";
/*     */   public static final String GBK = "GBK";
/*  35 */   public static final Charset CHARSET_ISO_8859_1 = StandardCharsets.ISO_8859_1;
/*     */ 
/*     */ 
/*     */   
/*  39 */   public static final Charset CHARSET_UTF_8 = StandardCharsets.UTF_8;
/*     */ 
/*     */   
/*     */   public static final Charset CHARSET_GBK;
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  47 */     Charset _CHARSET_GBK = null;
/*     */     try {
/*  49 */       _CHARSET_GBK = Charset.forName("GBK");
/*  50 */     } catch (UnsupportedCharsetException unsupportedCharsetException) {}
/*     */ 
/*     */     
/*  53 */     CHARSET_GBK = _CHARSET_GBK;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Charset charset(String charsetName) throws UnsupportedCharsetException {
/*  64 */     return StrUtil.isBlank(charsetName) ? Charset.defaultCharset() : Charset.forName(charsetName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Charset parse(String charsetName) {
/*  75 */     return parse(charsetName, Charset.defaultCharset());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Charset parse(String charsetName, Charset defaultCharset) {
/*     */     Charset result;
/*  87 */     if (StrUtil.isBlank(charsetName)) {
/*  88 */       return defaultCharset;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/*  93 */       result = Charset.forName(charsetName);
/*  94 */     } catch (UnsupportedCharsetException e) {
/*  95 */       result = defaultCharset;
/*     */     } 
/*     */     
/*  98 */     return result;
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
/*     */   public static String convert(String source, String srcCharset, String destCharset) {
/* 110 */     return convert(source, Charset.forName(srcCharset), Charset.forName(destCharset));
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
/*     */   public static String convert(String source, Charset srcCharset, Charset destCharset) {
/* 129 */     if (null == srcCharset) {
/* 130 */       srcCharset = StandardCharsets.ISO_8859_1;
/*     */     }
/*     */     
/* 133 */     if (null == destCharset) {
/* 134 */       destCharset = StandardCharsets.UTF_8;
/*     */     }
/*     */     
/* 137 */     if (StrUtil.isBlank(source) || srcCharset.equals(destCharset)) {
/* 138 */       return source;
/*     */     }
/* 140 */     return new String(source.getBytes(srcCharset), destCharset);
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
/*     */   public static File convert(File file, Charset srcCharset, Charset destCharset) {
/* 154 */     String str = FileUtil.readString(file, srcCharset);
/* 155 */     return FileUtil.writeString(str, file, destCharset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String systemCharsetName() {
/* 166 */     return systemCharset().name();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Charset systemCharset() {
/* 177 */     return FileUtil.isWindows() ? CHARSET_GBK : defaultCharset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String defaultCharsetName() {
/* 186 */     return defaultCharset().name();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Charset defaultCharset() {
/* 195 */     return Charset.defaultCharset();
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
/*     */   public static Charset defaultCharset(InputStream in, Charset... charsets) {
/* 209 */     return CharsetDetector.detect(in, charsets);
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
/*     */   public static Charset defaultCharset(int bufferSize, InputStream in, Charset... charsets) {
/* 224 */     return CharsetDetector.detect(bufferSize, in, charsets);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\CharsetUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
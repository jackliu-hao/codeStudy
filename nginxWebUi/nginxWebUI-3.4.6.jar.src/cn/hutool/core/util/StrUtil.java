/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.text.CharSequenceUtil;
/*     */ import cn.hutool.core.text.StrBuilder;
/*     */ import cn.hutool.core.text.StrFormatter;
/*     */ import cn.hutool.core.text.StrPool;
/*     */ import cn.hutool.core.text.TextSimilarity;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StrUtil
/*     */   extends CharSequenceUtil
/*     */   implements StrPool
/*     */ {
/*     */   public static boolean isBlankIfStr(Object obj) {
/*  49 */     if (null == obj)
/*  50 */       return true; 
/*  51 */     if (obj instanceof CharSequence) {
/*  52 */       return isBlank((CharSequence)obj);
/*     */     }
/*  54 */     return false;
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
/*     */ 
/*     */   
/*     */   public static boolean isEmptyIfStr(Object obj) {
/*  80 */     if (null == obj)
/*  81 */       return true; 
/*  82 */     if (obj instanceof CharSequence) {
/*  83 */       return (0 == ((CharSequence)obj).length());
/*     */     }
/*  85 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void trim(String[] strs) {
/*  96 */     if (null == strs) {
/*     */       return;
/*     */     }
/*     */     
/* 100 */     for (int i = 0; i < strs.length; i++) {
/* 101 */       String str = strs[i];
/* 102 */       if (null != str) {
/* 103 */         strs[i] = trim(str);
/*     */       }
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
/*     */   public static String utf8Str(Object obj) {
/* 120 */     return str(obj, CharsetUtil.CHARSET_UTF_8);
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
/*     */   @Deprecated
/*     */   public static String str(Object obj, String charsetName) {
/* 138 */     return str(obj, Charset.forName(charsetName));
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
/*     */   public static String str(Object obj, Charset charset) {
/* 153 */     if (null == obj) {
/* 154 */       return null;
/*     */     }
/*     */     
/* 157 */     if (obj instanceof String)
/* 158 */       return (String)obj; 
/* 159 */     if (obj instanceof byte[])
/* 160 */       return str((byte[])obj, charset); 
/* 161 */     if (obj instanceof Byte[])
/* 162 */       return str((Byte[])obj, charset); 
/* 163 */     if (obj instanceof ByteBuffer)
/* 164 */       return str((ByteBuffer)obj, charset); 
/* 165 */     if (ArrayUtil.isArray(obj)) {
/* 166 */       return ArrayUtil.toString(obj);
/*     */     }
/*     */     
/* 169 */     return obj.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String str(byte[] bytes, String charset) {
/* 180 */     return str(bytes, CharsetUtil.charset(charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String str(byte[] data, Charset charset) {
/* 191 */     if (data == null) {
/* 192 */       return null;
/*     */     }
/*     */     
/* 195 */     if (null == charset) {
/* 196 */       return new String(data);
/*     */     }
/* 198 */     return new String(data, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String str(Byte[] bytes, String charset) {
/* 209 */     return str(bytes, CharsetUtil.charset(charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String str(Byte[] data, Charset charset) {
/* 220 */     if (data == null) {
/* 221 */       return null;
/*     */     }
/*     */     
/* 224 */     byte[] bytes = new byte[data.length];
/*     */     
/* 226 */     for (int i = 0; i < data.length; i++) {
/* 227 */       Byte dataByte = data[i];
/* 228 */       bytes[i] = (null == dataByte) ? -1 : dataByte.byteValue();
/*     */     } 
/*     */     
/* 231 */     return str(bytes, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String str(ByteBuffer data, String charset) {
/* 242 */     if (data == null) {
/* 243 */       return null;
/*     */     }
/*     */     
/* 246 */     return str(data, CharsetUtil.charset(charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String str(ByteBuffer data, Charset charset) {
/* 257 */     if (null == charset) {
/* 258 */       charset = Charset.defaultCharset();
/*     */     }
/* 260 */     return charset.decode(data).toString();
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
/*     */   public static String toString(Object obj) {
/* 272 */     return String.valueOf(obj);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toStringOrNull(Object obj) {
/* 283 */     return (null == obj) ? null : obj.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuilder builder() {
/* 292 */     return new StringBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StrBuilder strBuilder() {
/* 302 */     return StrBuilder.create();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuilder builder(int capacity) {
/* 312 */     return new StringBuilder(capacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StrBuilder strBuilder(int capacity) {
/* 323 */     return StrBuilder.create(capacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringReader getReader(CharSequence str) {
/* 333 */     if (null == str) {
/* 334 */       return null;
/*     */     }
/* 336 */     return new StringReader(str.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringWriter getWriter() {
/* 345 */     return new StringWriter();
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
/*     */   public static String reverse(String str) {
/* 357 */     return new String(ArrayUtil.reverse(str.toCharArray()));
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
/*     */   public static String fillBefore(String str, char filledChar, int len) {
/* 373 */     return fill(str, filledChar, len, true);
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
/*     */   public static String fillAfter(String str, char filledChar, int len) {
/* 387 */     return fill(str, filledChar, len, false);
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
/*     */   public static String fill(String str, char filledChar, int len, boolean isPre) {
/* 401 */     int strLen = str.length();
/* 402 */     if (strLen > len) {
/* 403 */       return str;
/*     */     }
/*     */     
/* 406 */     String filledStr = repeat(filledChar, len - strLen);
/* 407 */     return isPre ? filledStr.concat(str) : str.concat(filledStr);
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
/*     */   public static double similar(String str1, String str2) {
/* 419 */     return TextSimilarity.similar(str1, str2);
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
/*     */   public static String similar(String str1, String str2, int scale) {
/* 432 */     return TextSimilarity.similar(str1, str2, scale);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String uuid() {
/* 443 */     return IdUtil.randomUUID();
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
/*     */   public static String format(CharSequence template, Map<?, ?> map) {
/* 455 */     return format(template, map, true);
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
/*     */   public static String format(CharSequence template, Map<?, ?> map, boolean ignoreNull) {
/* 469 */     return StrFormatter.format(template, map, ignoreNull);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\StrUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.codec.Base16Codec;
/*     */ import java.awt.Color;
/*     */ import java.math.BigInteger;
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
/*     */ public class HexUtil
/*     */ {
/*     */   public static boolean isHexNumber(String value) {
/*  30 */     int index = value.startsWith("-") ? 1 : 0;
/*  31 */     if (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index)) {
/*     */       
/*     */       try {
/*  34 */         Long.decode(value);
/*  35 */       } catch (NumberFormatException e) {
/*  36 */         return false;
/*     */       } 
/*  38 */       return true;
/*     */     } 
/*     */     
/*  41 */     return false;
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
/*     */   public static char[] encodeHex(byte[] data) {
/*  53 */     return encodeHex(data, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static char[] encodeHex(String str, Charset charset) {
/*  64 */     return encodeHex(StrUtil.bytes(str, charset), true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static char[] encodeHex(byte[] data, boolean toLowerCase) {
/*  75 */     return (toLowerCase ? Base16Codec.CODEC_LOWER : Base16Codec.CODEC_UPPER).encode(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeHexStr(byte[] data) {
/*  85 */     return encodeHexStr(data, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeHexStr(String data, Charset charset) {
/*  96 */     return encodeHexStr(StrUtil.bytes(data, charset), true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeHexStr(String data) {
/* 106 */     return encodeHexStr(data, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeHexStr(byte[] data, boolean toLowerCase) {
/* 117 */     return new String(encodeHex(data, toLowerCase));
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
/*     */   public static String decodeHexStr(String hexStr) {
/* 129 */     return decodeHexStr(hexStr, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decodeHexStr(String hexStr, Charset charset) {
/* 140 */     if (StrUtil.isEmpty(hexStr)) {
/* 141 */       return hexStr;
/*     */     }
/* 143 */     return StrUtil.str(decodeHex(hexStr), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decodeHexStr(char[] hexData, Charset charset) {
/* 154 */     return StrUtil.str(decodeHex(hexData), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] decodeHex(String hexStr) {
/* 164 */     return decodeHex(hexStr);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] decodeHex(char[] hexData) {
/* 175 */     return decodeHex(String.valueOf(hexData));
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
/*     */   public static byte[] decodeHex(CharSequence hexData) {
/* 187 */     return Base16Codec.CODEC_LOWER.decode(hexData);
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
/*     */   public static String encodeColor(Color color) {
/* 200 */     return encodeColor(color, "#");
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
/*     */   public static String encodeColor(Color color, String prefix) {
/* 212 */     StringBuilder builder = new StringBuilder(prefix);
/*     */     
/* 214 */     String colorHex = Integer.toHexString(color.getRed());
/* 215 */     if (1 == colorHex.length()) {
/* 216 */       builder.append('0');
/*     */     }
/* 218 */     builder.append(colorHex);
/* 219 */     colorHex = Integer.toHexString(color.getGreen());
/* 220 */     if (1 == colorHex.length()) {
/* 221 */       builder.append('0');
/*     */     }
/* 223 */     builder.append(colorHex);
/* 224 */     colorHex = Integer.toHexString(color.getBlue());
/* 225 */     if (1 == colorHex.length()) {
/* 226 */       builder.append('0');
/*     */     }
/* 228 */     builder.append(colorHex);
/* 229 */     return builder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Color decodeColor(String hexColor) {
/* 240 */     return Color.decode(hexColor);
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
/*     */   public static String toUnicodeHex(int value) {
/* 255 */     StringBuilder builder = new StringBuilder(6);
/*     */     
/* 257 */     builder.append("\\u");
/* 258 */     String hex = toHex(value);
/* 259 */     int len = hex.length();
/* 260 */     if (len < 4) {
/* 261 */       builder.append("0000", 0, 4 - len);
/*     */     }
/* 263 */     builder.append(hex);
/*     */     
/* 265 */     return builder.toString();
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
/*     */   public static String toUnicodeHex(char ch) {
/* 281 */     return Base16Codec.CODEC_LOWER.toUnicodeHex(ch);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toHex(int value) {
/* 292 */     return Integer.toHexString(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int hexToInt(String value) {
/* 303 */     return Integer.parseInt(value, 16);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toHex(long value) {
/* 314 */     return Long.toHexString(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long hexToLong(String value) {
/* 325 */     return Long.parseLong(value, 16);
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
/*     */   public static void appendHex(StringBuilder builder, byte b, boolean toLowerCase) {
/* 337 */     (toLowerCase ? Base16Codec.CODEC_LOWER : Base16Codec.CODEC_UPPER).appendHex(builder, b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigInteger toBigInteger(String hexStr) {
/* 348 */     if (null == hexStr) {
/* 349 */       return null;
/*     */     }
/* 351 */     return new BigInteger(hexStr, 16);
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
/*     */   public static String format(String hexStr) {
/* 364 */     int length = hexStr.length();
/* 365 */     StringBuilder builder = StrUtil.builder(length + length / 2);
/* 366 */     builder.append(hexStr.charAt(0)).append(hexStr.charAt(1));
/* 367 */     for (int i = 2; i < length - 1; i += 2) {
/* 368 */       builder.append(' ').append(hexStr.charAt(i)).append(hexStr.charAt(i + 1));
/*     */     }
/* 370 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\HexUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
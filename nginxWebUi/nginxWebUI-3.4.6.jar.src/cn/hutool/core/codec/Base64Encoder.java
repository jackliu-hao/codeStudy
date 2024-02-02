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
/*     */ public class Base64Encoder
/*     */ {
/*  17 */   private static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
/*     */ 
/*     */ 
/*     */   
/*  21 */   private static final byte[] STANDARD_ENCODE_TABLE = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  34 */   private static final byte[] URL_SAFE_ENCODE_TABLE = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*  55 */     return encode(arr, lineSep, false);
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
/*     */   public static byte[] encodeUrlSafe(byte[] arr, boolean lineSep) {
/*  67 */     return encode(arr, lineSep, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(CharSequence source) {
/*  77 */     return encode(source, DEFAULT_CHARSET);
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
/*  88 */     return encodeUrlSafe(source, DEFAULT_CHARSET);
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
/*  99 */     return encode(StrUtil.bytes(source, charset));
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
/* 111 */     return encodeUrlSafe(StrUtil.bytes(source, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(byte[] source) {
/* 121 */     return StrUtil.str(encode(source, false), DEFAULT_CHARSET);
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
/* 132 */     return StrUtil.str(encodeUrlSafe(source, false), DEFAULT_CHARSET);
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
/* 146 */     return StrUtil.str(encode(arr, isMultiLine, isUrlSafe), DEFAULT_CHARSET);
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
/* 159 */     if (null == arr) {
/* 160 */       return null;
/*     */     }
/*     */     
/* 163 */     int len = arr.length;
/* 164 */     if (len == 0) {
/* 165 */       return new byte[0];
/*     */     }
/*     */     
/* 168 */     int evenlen = len / 3 * 3;
/* 169 */     int cnt = (len - 1) / 3 + 1 << 2;
/* 170 */     int destlen = cnt + (isMultiLine ? ((cnt - 1) / 76 << 1) : 0);
/* 171 */     byte[] dest = new byte[destlen];
/*     */     
/* 173 */     byte[] encodeTable = isUrlSafe ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE;
/*     */     int cc;
/* 175 */     for (int s = 0, d = 0; s < evenlen; ) {
/* 176 */       int i = (arr[s++] & 0xFF) << 16 | (arr[s++] & 0xFF) << 8 | arr[s++] & 0xFF;
/*     */       
/* 178 */       dest[d++] = encodeTable[i >>> 18 & 0x3F];
/* 179 */       dest[d++] = encodeTable[i >>> 12 & 0x3F];
/* 180 */       dest[d++] = encodeTable[i >>> 6 & 0x3F];
/* 181 */       dest[d++] = encodeTable[i & 0x3F];
/*     */       
/* 183 */       if (isMultiLine && ++cc == 19 && d < destlen - 2) {
/* 184 */         dest[d++] = 13;
/* 185 */         dest[d++] = 10;
/* 186 */         cc = 0;
/*     */       } 
/*     */     } 
/*     */     
/* 190 */     int left = len - evenlen;
/* 191 */     if (left > 0) {
/* 192 */       int i = (arr[evenlen] & 0xFF) << 10 | ((left == 2) ? ((arr[len - 1] & 0xFF) << 2) : 0);
/*     */       
/* 194 */       dest[destlen - 4] = encodeTable[i >> 12];
/* 195 */       dest[destlen - 3] = encodeTable[i >>> 6 & 0x3F];
/*     */       
/* 197 */       if (isUrlSafe) {
/*     */         
/* 199 */         int urlSafeLen = destlen - 2;
/* 200 */         if (2 == left) {
/* 201 */           dest[destlen - 2] = encodeTable[i & 0x3F];
/* 202 */           urlSafeLen++;
/*     */         } 
/* 204 */         byte[] urlSafeDest = new byte[urlSafeLen];
/* 205 */         System.arraycopy(dest, 0, urlSafeDest, 0, urlSafeLen);
/* 206 */         return urlSafeDest;
/*     */       } 
/* 208 */       dest[destlen - 2] = (left == 2) ? encodeTable[i & 0x3F] : 61;
/* 209 */       dest[destlen - 1] = 61;
/*     */     } 
/*     */     
/* 212 */     return dest;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\Base64Encoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package cn.hutool.core.codec;
/*     */ 
/*     */ import cn.hutool.core.lang.mutable.MutableInt;
/*     */ import cn.hutool.core.util.ArrayUtil;
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
/*     */ public class Base64Decoder
/*     */ {
/*  18 */   private static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
/*     */   
/*     */   private static final byte PADDING = -2;
/*     */   
/*  22 */   private static final byte[] DECODE_TABLE = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decodeStr(CharSequence source) {
/*  41 */     return decodeStr(source, DEFAULT_CHARSET);
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
/*  52 */     return StrUtil.str(decode(source), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] decode(CharSequence source) {
/*  62 */     return decode(StrUtil.bytes(source, DEFAULT_CHARSET));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] decode(byte[] in) {
/*  72 */     if (ArrayUtil.isEmpty(in)) {
/*  73 */       return in;
/*     */     }
/*  75 */     return decode(in, 0, in.length);
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
/*     */   public static byte[] decode(byte[] in, int pos, int length) {
/*  87 */     if (ArrayUtil.isEmpty(in)) {
/*  88 */       return in;
/*     */     }
/*     */     
/*  91 */     MutableInt offset = new MutableInt(pos);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  97 */     int maxPos = pos + length - 1;
/*  98 */     int octetId = 0;
/*  99 */     byte[] octet = new byte[length * 3 / 4];
/* 100 */     while (offset.intValue() <= maxPos) {
/* 101 */       byte sestet0 = getNextValidDecodeByte(in, offset, maxPos);
/* 102 */       byte sestet1 = getNextValidDecodeByte(in, offset, maxPos);
/* 103 */       byte sestet2 = getNextValidDecodeByte(in, offset, maxPos);
/* 104 */       byte sestet3 = getNextValidDecodeByte(in, offset, maxPos);
/*     */       
/* 106 */       if (-2 != sestet1) {
/* 107 */         octet[octetId++] = (byte)(sestet0 << 2 | sestet1 >>> 4);
/*     */       }
/* 109 */       if (-2 != sestet2) {
/* 110 */         octet[octetId++] = (byte)((sestet1 & 0xF) << 4 | sestet2 >>> 2);
/*     */       }
/* 112 */       if (-2 != sestet3) {
/* 113 */         octet[octetId++] = (byte)((sestet2 & 0x3) << 6 | sestet3);
/*     */       }
/*     */     } 
/*     */     
/* 117 */     if (octetId == octet.length) {
/* 118 */       return octet;
/*     */     }
/*     */     
/* 121 */     return (byte[])ArrayUtil.copy(octet, new byte[octetId], octetId);
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
/*     */   public static boolean isBase64Code(byte octet) {
/* 133 */     return (octet == 61 || (octet >= 0 && octet < DECODE_TABLE.length && DECODE_TABLE[octet] != -1));
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
/*     */   private static byte getNextValidDecodeByte(byte[] in, MutableInt pos, int maxPos) {
/* 148 */     while (pos.intValue() <= maxPos) {
/* 149 */       byte base64Byte = in[pos.intValue()];
/* 150 */       pos.increment();
/* 151 */       if (base64Byte > -1) {
/* 152 */         byte decodeByte = DECODE_TABLE[base64Byte];
/* 153 */         if (decodeByte > -1) {
/* 154 */           return decodeByte;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 159 */     return -2;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\Base64Decoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
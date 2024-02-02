/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ import org.apache.commons.codec.BinaryDecoder;
/*     */ import org.apache.commons.codec.BinaryEncoder;
/*     */ import org.apache.commons.codec.DecoderException;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BinaryCodec
/*     */   implements BinaryDecoder, BinaryEncoder
/*     */ {
/*  41 */   private static final char[] EMPTY_CHAR_ARRAY = new char[0];
/*     */ 
/*     */   
/*  44 */   private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
/*     */ 
/*     */   
/*     */   private static final int BIT_0 = 1;
/*     */ 
/*     */   
/*     */   private static final int BIT_1 = 2;
/*     */ 
/*     */   
/*     */   private static final int BIT_2 = 4;
/*     */ 
/*     */   
/*     */   private static final int BIT_3 = 8;
/*     */ 
/*     */   
/*     */   private static final int BIT_4 = 16;
/*     */ 
/*     */   
/*     */   private static final int BIT_5 = 32;
/*     */ 
/*     */   
/*     */   private static final int BIT_6 = 64;
/*     */ 
/*     */   
/*     */   private static final int BIT_7 = 128;
/*     */   
/*  70 */   private static final int[] BITS = new int[] { 1, 2, 4, 8, 16, 32, 64, 128 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] encode(byte[] raw) {
/*  82 */     return toAsciiBytes(raw);
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
/*     */   public Object encode(Object raw) throws EncoderException {
/*  97 */     if (!(raw instanceof byte[])) {
/*  98 */       throw new EncoderException("argument not a byte array");
/*     */     }
/* 100 */     return toAsciiChars((byte[])raw);
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
/*     */   public Object decode(Object ascii) throws DecoderException {
/* 115 */     if (ascii == null) {
/* 116 */       return EMPTY_BYTE_ARRAY;
/*     */     }
/* 118 */     if (ascii instanceof byte[]) {
/* 119 */       return fromAscii((byte[])ascii);
/*     */     }
/* 121 */     if (ascii instanceof char[]) {
/* 122 */       return fromAscii((char[])ascii);
/*     */     }
/* 124 */     if (ascii instanceof String) {
/* 125 */       return fromAscii(((String)ascii).toCharArray());
/*     */     }
/* 127 */     throw new DecoderException("argument not a byte array");
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
/*     */   public byte[] decode(byte[] ascii) {
/* 140 */     return fromAscii(ascii);
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
/*     */   public byte[] toByteArray(String ascii) {
/* 152 */     if (ascii == null) {
/* 153 */       return EMPTY_BYTE_ARRAY;
/*     */     }
/* 155 */     return fromAscii(ascii.toCharArray());
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
/*     */   public static byte[] fromAscii(char[] ascii) {
/* 171 */     if (ascii == null || ascii.length == 0) {
/* 172 */       return EMPTY_BYTE_ARRAY;
/*     */     }
/*     */     
/* 175 */     byte[] l_raw = new byte[ascii.length >> 3];
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 180 */     for (int ii = 0, jj = ascii.length - 1; ii < l_raw.length; ii++, jj -= 8) {
/* 181 */       for (int bits = 0; bits < BITS.length; bits++) {
/* 182 */         if (ascii[jj - bits] == '1') {
/* 183 */           l_raw[ii] = (byte)(l_raw[ii] | BITS[bits]);
/*     */         }
/*     */       } 
/*     */     } 
/* 187 */     return l_raw;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] fromAscii(byte[] ascii) {
/* 198 */     if (isEmpty(ascii)) {
/* 199 */       return EMPTY_BYTE_ARRAY;
/*     */     }
/*     */     
/* 202 */     byte[] l_raw = new byte[ascii.length >> 3];
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 207 */     for (int ii = 0, jj = ascii.length - 1; ii < l_raw.length; ii++, jj -= 8) {
/* 208 */       for (int bits = 0; bits < BITS.length; bits++) {
/* 209 */         if (ascii[jj - bits] == 49) {
/* 210 */           l_raw[ii] = (byte)(l_raw[ii] | BITS[bits]);
/*     */         }
/*     */       } 
/*     */     } 
/* 214 */     return l_raw;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isEmpty(byte[] array) {
/* 225 */     return (array == null || array.length == 0);
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
/*     */   public static byte[] toAsciiBytes(byte[] raw) {
/* 238 */     if (isEmpty(raw)) {
/* 239 */       return EMPTY_BYTE_ARRAY;
/*     */     }
/*     */     
/* 242 */     byte[] l_ascii = new byte[raw.length << 3];
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 247 */     for (int ii = 0, jj = l_ascii.length - 1; ii < raw.length; ii++, jj -= 8) {
/* 248 */       for (int bits = 0; bits < BITS.length; bits++) {
/* 249 */         if ((raw[ii] & BITS[bits]) == 0) {
/* 250 */           l_ascii[jj - bits] = 48;
/*     */         } else {
/* 252 */           l_ascii[jj - bits] = 49;
/*     */         } 
/*     */       } 
/*     */     } 
/* 256 */     return l_ascii;
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
/*     */   public static char[] toAsciiChars(byte[] raw) {
/* 268 */     if (isEmpty(raw)) {
/* 269 */       return EMPTY_CHAR_ARRAY;
/*     */     }
/*     */     
/* 272 */     char[] l_ascii = new char[raw.length << 3];
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 277 */     for (int ii = 0, jj = l_ascii.length - 1; ii < raw.length; ii++, jj -= 8) {
/* 278 */       for (int bits = 0; bits < BITS.length; bits++) {
/* 279 */         if ((raw[ii] & BITS[bits]) == 0) {
/* 280 */           l_ascii[jj - bits] = '0';
/*     */         } else {
/* 282 */           l_ascii[jj - bits] = '1';
/*     */         } 
/*     */       } 
/*     */     } 
/* 286 */     return l_ascii;
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
/*     */   public static String toAsciiString(byte[] raw) {
/* 298 */     return new String(toAsciiChars(raw));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\binary\BinaryCodec.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
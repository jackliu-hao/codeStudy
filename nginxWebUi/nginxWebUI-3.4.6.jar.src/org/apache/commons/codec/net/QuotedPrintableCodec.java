/*     */ package org.apache.commons.codec.net;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.IllegalCharsetNameException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.charset.UnsupportedCharsetException;
/*     */ import java.util.BitSet;
/*     */ import org.apache.commons.codec.BinaryDecoder;
/*     */ import org.apache.commons.codec.BinaryEncoder;
/*     */ import org.apache.commons.codec.DecoderException;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringDecoder;
/*     */ import org.apache.commons.codec.StringEncoder;
/*     */ import org.apache.commons.codec.binary.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class QuotedPrintableCodec
/*     */   implements BinaryEncoder, BinaryDecoder, StringEncoder, StringDecoder
/*     */ {
/*     */   private final Charset charset;
/*     */   private final boolean strict;
/*  80 */   private static final BitSet PRINTABLE_CHARS = new BitSet(256);
/*     */ 
/*     */   
/*     */   private static final byte ESCAPE_CHAR = 61;
/*     */ 
/*     */   
/*     */   private static final byte TAB = 9;
/*     */ 
/*     */   
/*     */   private static final byte SPACE = 32;
/*     */   
/*     */   private static final byte CR = 13;
/*     */   
/*     */   private static final byte LF = 10;
/*     */   
/*     */   private static final int SAFE_LENGTH = 73;
/*     */ 
/*     */   
/*     */   static {
/*     */     int i;
/* 100 */     for (i = 33; i <= 60; i++) {
/* 101 */       PRINTABLE_CHARS.set(i);
/*     */     }
/* 103 */     for (i = 62; i <= 126; i++) {
/* 104 */       PRINTABLE_CHARS.set(i);
/*     */     }
/* 106 */     PRINTABLE_CHARS.set(9);
/* 107 */     PRINTABLE_CHARS.set(32);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QuotedPrintableCodec() {
/* 114 */     this(StandardCharsets.UTF_8, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QuotedPrintableCodec(boolean strict) {
/* 125 */     this(StandardCharsets.UTF_8, strict);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QuotedPrintableCodec(Charset charset) {
/* 136 */     this(charset, false);
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
/*     */   public QuotedPrintableCodec(Charset charset, boolean strict) {
/* 149 */     this.charset = charset;
/* 150 */     this.strict = strict;
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
/*     */   public QuotedPrintableCodec(String charsetName) throws IllegalCharsetNameException, IllegalArgumentException, UnsupportedCharsetException {
/* 170 */     this(Charset.forName(charsetName), false);
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
/*     */   private static final int encodeQuotedPrintable(int b, ByteArrayOutputStream buffer) {
/* 183 */     buffer.write(61);
/* 184 */     char hex1 = Utils.hexDigit(b >> 4);
/* 185 */     char hex2 = Utils.hexDigit(b);
/* 186 */     buffer.write(hex1);
/* 187 */     buffer.write(hex2);
/* 188 */     return 3;
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
/*     */   private static int getUnsignedOctet(int index, byte[] bytes) {
/* 202 */     int b = bytes[index];
/* 203 */     if (b < 0) {
/* 204 */       b = 256 + b;
/*     */     }
/* 206 */     return b;
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
/*     */   private static int encodeByte(int b, boolean encode, ByteArrayOutputStream buffer) {
/* 222 */     if (encode) {
/* 223 */       return encodeQuotedPrintable(b, buffer);
/*     */     }
/* 225 */     buffer.write(b);
/* 226 */     return 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isWhitespace(int b) {
/* 237 */     return (b == 32 || b == 9);
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
/*     */   public static final byte[] encodeQuotedPrintable(BitSet printable, byte[] bytes) {
/* 253 */     return encodeQuotedPrintable(printable, bytes, false);
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
/*     */   public static final byte[] encodeQuotedPrintable(BitSet printable, byte[] bytes, boolean strict) {
/* 273 */     if (bytes == null) {
/* 274 */       return null;
/*     */     }
/* 276 */     if (printable == null) {
/* 277 */       printable = PRINTABLE_CHARS;
/*     */     }
/* 279 */     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
/*     */     
/* 281 */     if (strict) {
/* 282 */       int pos = 1;
/*     */ 
/*     */       
/* 285 */       for (int i = 0; i < bytes.length - 3; i++) {
/* 286 */         int k = getUnsignedOctet(i, bytes);
/* 287 */         if (pos < 73) {
/*     */           
/* 289 */           pos += encodeByte(k, !printable.get(k), buffer);
/*     */         } else {
/*     */           
/* 292 */           encodeByte(k, (!printable.get(k) || isWhitespace(k)), buffer);
/*     */ 
/*     */           
/* 295 */           buffer.write(61);
/* 296 */           buffer.write(13);
/* 297 */           buffer.write(10);
/* 298 */           pos = 1;
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 304 */       int b = getUnsignedOctet(bytes.length - 3, bytes);
/* 305 */       boolean encode = (!printable.get(b) || (isWhitespace(b) && pos > 68));
/* 306 */       pos += encodeByte(b, encode, buffer);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 311 */       if (pos > 71) {
/* 312 */         buffer.write(61);
/* 313 */         buffer.write(13);
/* 314 */         buffer.write(10);
/*     */       } 
/* 316 */       for (int j = bytes.length - 2; j < bytes.length; j++) {
/* 317 */         b = getUnsignedOctet(j, bytes);
/*     */         
/* 319 */         encode = (!printable.get(b) || (j > bytes.length - 2 && isWhitespace(b)));
/* 320 */         encodeByte(b, encode, buffer);
/*     */       } 
/*     */     } else {
/* 323 */       for (byte c : bytes) {
/* 324 */         int b = c;
/* 325 */         if (b < 0) {
/* 326 */           b = 256 + b;
/*     */         }
/* 328 */         if (printable.get(b)) {
/* 329 */           buffer.write(b);
/*     */         } else {
/* 331 */           encodeQuotedPrintable(b, buffer);
/*     */         } 
/*     */       } 
/*     */     } 
/* 335 */     return buffer.toByteArray();
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
/*     */   public static final byte[] decodeQuotedPrintable(byte[] bytes) throws DecoderException {
/* 352 */     if (bytes == null) {
/* 353 */       return null;
/*     */     }
/* 355 */     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
/* 356 */     for (int i = 0; i < bytes.length; i++) {
/* 357 */       int b = bytes[i];
/* 358 */       if (b == 61) {
/*     */         
/*     */         try {
/* 361 */           if (bytes[++i] != 13)
/*     */           
/*     */           { 
/* 364 */             int u = Utils.digit16(bytes[i]);
/* 365 */             int l = Utils.digit16(bytes[++i]);
/* 366 */             buffer.write((char)((u << 4) + l)); } 
/* 367 */         } catch (ArrayIndexOutOfBoundsException e) {
/* 368 */           throw new DecoderException("Invalid quoted-printable encoding", e);
/*     */         } 
/* 370 */       } else if (b != 13 && b != 10) {
/*     */         
/* 372 */         buffer.write(b);
/*     */       } 
/*     */     } 
/* 375 */     return buffer.toByteArray();
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
/*     */   public byte[] encode(byte[] bytes) {
/* 391 */     return encodeQuotedPrintable(PRINTABLE_CHARS, bytes, this.strict);
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
/*     */   public byte[] decode(byte[] bytes) throws DecoderException {
/* 409 */     return decodeQuotedPrintable(bytes);
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
/*     */   public String encode(String sourceStr) throws EncoderException {
/* 429 */     return encode(sourceStr, getCharset());
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
/*     */   public String decode(String sourceStr, Charset sourceCharset) throws DecoderException {
/* 446 */     if (sourceStr == null) {
/* 447 */       return null;
/*     */     }
/* 449 */     return new String(decode(StringUtils.getBytesUsAscii(sourceStr)), sourceCharset);
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
/*     */   public String decode(String sourceStr, String sourceCharset) throws DecoderException, UnsupportedEncodingException {
/* 468 */     if (sourceStr == null) {
/* 469 */       return null;
/*     */     }
/* 471 */     return new String(decode(StringUtils.getBytesUsAscii(sourceStr)), sourceCharset);
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
/*     */   public String decode(String sourceStr) throws DecoderException {
/* 487 */     return decode(sourceStr, getCharset());
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
/*     */   public Object encode(Object obj) throws EncoderException {
/* 502 */     if (obj == null)
/* 503 */       return null; 
/* 504 */     if (obj instanceof byte[])
/* 505 */       return encode((byte[])obj); 
/* 506 */     if (obj instanceof String) {
/* 507 */       return encode((String)obj);
/*     */     }
/* 509 */     throw new EncoderException("Objects of type " + obj
/* 510 */         .getClass().getName() + " cannot be quoted-printable encoded");
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
/*     */   public Object decode(Object obj) throws DecoderException {
/* 528 */     if (obj == null)
/* 529 */       return null; 
/* 530 */     if (obj instanceof byte[])
/* 531 */       return decode((byte[])obj); 
/* 532 */     if (obj instanceof String) {
/* 533 */       return decode((String)obj);
/*     */     }
/* 535 */     throw new DecoderException("Objects of type " + obj
/* 536 */         .getClass().getName() + " cannot be quoted-printable decoded");
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
/*     */   public Charset getCharset() {
/* 548 */     return this.charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDefaultCharset() {
/* 557 */     return this.charset.name();
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
/*     */   public String encode(String sourceStr, Charset sourceCharset) {
/* 575 */     if (sourceStr == null) {
/* 576 */       return null;
/*     */     }
/* 578 */     return StringUtils.newStringUsAscii(encode(sourceStr.getBytes(sourceCharset)));
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
/*     */   public String encode(String sourceStr, String sourceCharset) throws UnsupportedEncodingException {
/* 597 */     if (sourceStr == null) {
/* 598 */       return null;
/*     */     }
/* 600 */     return StringUtils.newStringUsAscii(encode(sourceStr.getBytes(sourceCharset)));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\net\QuotedPrintableCodec.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
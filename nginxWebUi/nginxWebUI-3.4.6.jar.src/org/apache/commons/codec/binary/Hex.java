/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ 
/*     */ 
/*     */ public class Hex
/*     */   implements BinaryEncoder, BinaryDecoder
/*     */ {
/*  46 */   public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DEFAULT_CHARSET_NAME = "UTF-8";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   private static final char[] DIGITS_LOWER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  64 */   private static final char[] DIGITS_UPPER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Charset charset;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] decodeHex(String data) throws DecoderException {
/*  78 */     return decodeHex(data.toCharArray());
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
/*     */   public static byte[] decodeHex(char[] data) throws DecoderException {
/*  92 */     int len = data.length;
/*     */     
/*  94 */     if ((len & 0x1) != 0) {
/*  95 */       throw new DecoderException("Odd number of characters.");
/*     */     }
/*     */     
/*  98 */     byte[] out = new byte[len >> 1];
/*     */ 
/*     */     
/* 101 */     for (int i = 0, j = 0; j < len; i++) {
/* 102 */       int f = toDigit(data[j], j) << 4;
/* 103 */       j++;
/* 104 */       f |= toDigit(data[j], j);
/* 105 */       j++;
/* 106 */       out[i] = (byte)(f & 0xFF);
/*     */     } 
/*     */     
/* 109 */     return out;
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
/* 121 */     return encodeHex(data, true);
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
/*     */   public static char[] encodeHex(ByteBuffer data) {
/* 137 */     return encodeHex(data, true);
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
/*     */   public static char[] encodeHex(byte[] data, boolean toLowerCase) {
/* 151 */     return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
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
/*     */   public static char[] encodeHex(ByteBuffer data, boolean toLowerCase) {
/* 168 */     return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
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
/*     */   protected static char[] encodeHex(byte[] data, char[] toDigits) {
/* 183 */     int l = data.length;
/* 184 */     char[] out = new char[l << 1];
/*     */     
/* 186 */     for (int i = 0, j = 0; i < l; i++) {
/* 187 */       out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
/* 188 */       out[j++] = toDigits[0xF & data[i]];
/*     */     } 
/* 190 */     return out;
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
/*     */   protected static char[] encodeHex(ByteBuffer byteBuffer, char[] toDigits) {
/* 208 */     return encodeHex(toByteArray(byteBuffer), toDigits);
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
/*     */   public static String encodeHexString(byte[] data) {
/* 220 */     return new String(encodeHex(data));
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
/*     */   public static String encodeHexString(byte[] data, boolean toLowerCase) {
/* 233 */     return new String(encodeHex(data, toLowerCase));
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
/*     */   public static String encodeHexString(ByteBuffer data) {
/* 248 */     return new String(encodeHex(data));
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
/*     */   public static String encodeHexString(ByteBuffer data, boolean toLowerCase) {
/* 264 */     return new String(encodeHex(data, toLowerCase));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static byte[] toByteArray(ByteBuffer byteBuffer) {
/* 275 */     int remaining = byteBuffer.remaining();
/*     */     
/* 277 */     if (byteBuffer.hasArray()) {
/* 278 */       byte[] arrayOfByte = byteBuffer.array();
/* 279 */       if (remaining == arrayOfByte.length) {
/* 280 */         byteBuffer.position(remaining);
/* 281 */         return arrayOfByte;
/*     */       } 
/*     */     } 
/*     */     
/* 285 */     byte[] byteArray = new byte[remaining];
/* 286 */     byteBuffer.get(byteArray);
/* 287 */     return byteArray;
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
/*     */   protected static int toDigit(char ch, int index) throws DecoderException {
/* 299 */     int digit = Character.digit(ch, 16);
/* 300 */     if (digit == -1) {
/* 301 */       throw new DecoderException("Illegal hexadecimal character " + ch + " at index " + index);
/*     */     }
/* 303 */     return digit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Hex() {
/* 313 */     this.charset = DEFAULT_CHARSET;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Hex(Charset charset) {
/* 323 */     this.charset = charset;
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
/*     */   public Hex(String charsetName) {
/* 335 */     this(Charset.forName(charsetName));
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
/*     */   public byte[] decode(byte[] array) throws DecoderException {
/* 350 */     return decodeHex((new String(array, getCharset())).toCharArray());
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
/*     */   public byte[] decode(ByteBuffer buffer) throws DecoderException {
/* 368 */     return decodeHex((new String(toByteArray(buffer), getCharset())).toCharArray());
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
/*     */   public Object decode(Object object) throws DecoderException {
/* 384 */     if (object instanceof String)
/* 385 */       return decode(((String)object).toCharArray()); 
/* 386 */     if (object instanceof byte[])
/* 387 */       return decode((byte[])object); 
/* 388 */     if (object instanceof ByteBuffer) {
/* 389 */       return decode((ByteBuffer)object);
/*     */     }
/*     */     try {
/* 392 */       return decodeHex((char[])object);
/* 393 */     } catch (ClassCastException e) {
/* 394 */       throw new DecoderException(e.getMessage(), e);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] encode(byte[] array) {
/* 415 */     return encodeHexString(array).getBytes(getCharset());
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
/*     */   public byte[] encode(ByteBuffer array) {
/* 435 */     return encodeHexString(array).getBytes(getCharset());
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
/*     */   public Object encode(Object object) throws EncoderException {
/*     */     byte[] byteArray;
/* 455 */     if (object instanceof String) {
/* 456 */       byteArray = ((String)object).getBytes(getCharset());
/* 457 */     } else if (object instanceof ByteBuffer) {
/* 458 */       byteArray = toByteArray((ByteBuffer)object);
/*     */     } else {
/*     */       try {
/* 461 */         byteArray = (byte[])object;
/* 462 */       } catch (ClassCastException e) {
/* 463 */         throw new EncoderException(e.getMessage(), e);
/*     */       } 
/*     */     } 
/* 466 */     return encodeHex(byteArray);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Charset getCharset() {
/* 476 */     return this.charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCharsetName() {
/* 486 */     return this.charset.name();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 496 */     return super.toString() + "[charsetName=" + this.charset + "]";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\binary\Hex.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package cn.hutool.core.codec;
/*     */ 
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Base16Codec
/*     */   implements Encoder<byte[], char[]>, Decoder<CharSequence, byte[]>
/*     */ {
/*  16 */   public static final Base16Codec CODEC_LOWER = new Base16Codec(true);
/*  17 */   public static final Base16Codec CODEC_UPPER = new Base16Codec(false);
/*     */ 
/*     */ 
/*     */   
/*     */   private final char[] alphabets;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base16Codec(boolean lowerCase) {
/*  27 */     this.alphabets = (lowerCase ? "0123456789abcdef" : "0123456789ABCDEF").toCharArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public char[] encode(byte[] data) {
/*  32 */     int len = data.length;
/*  33 */     char[] out = new char[len << 1];
/*     */     
/*  35 */     for (int i = 0, j = 0; i < len; i++) {
/*  36 */       out[j++] = this.alphabets[(0xF0 & data[i]) >>> 4];
/*  37 */       out[j++] = this.alphabets[0xF & data[i]];
/*     */     } 
/*  39 */     return out;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] decode(CharSequence encoded) {
/*  44 */     if (StrUtil.isEmpty(encoded)) {
/*  45 */       return null;
/*     */     }
/*     */     
/*  48 */     encoded = StrUtil.cleanBlank(encoded);
/*  49 */     int len = encoded.length();
/*     */     
/*  51 */     if ((len & 0x1) != 0) {
/*     */       
/*  53 */       encoded = "0" + encoded;
/*  54 */       len = encoded.length();
/*     */     } 
/*     */     
/*  57 */     byte[] out = new byte[len >> 1];
/*     */ 
/*     */     
/*  60 */     for (int i = 0, j = 0; j < len; i++) {
/*  61 */       int f = toDigit(encoded.charAt(j), j) << 4;
/*  62 */       j++;
/*  63 */       f |= toDigit(encoded.charAt(j), j);
/*  64 */       j++;
/*  65 */       out[i] = (byte)(f & 0xFF);
/*     */     } 
/*     */     
/*  68 */     return out;
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
/*     */   public String toUnicodeHex(char ch) {
/*  83 */     return "\\u" + this.alphabets[ch >> 12 & 0xF] + this.alphabets[ch >> 8 & 0xF] + this.alphabets[ch >> 4 & 0xF] + this.alphabets[ch & 0xF];
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
/*     */   public void appendHex(StringBuilder builder, byte b) {
/*  97 */     int high = (b & 0xF0) >>> 4;
/*  98 */     int low = b & 0xF;
/*  99 */     builder.append(this.alphabets[high]);
/* 100 */     builder.append(this.alphabets[low]);
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
/*     */   private static int toDigit(char ch, int index) {
/* 112 */     int digit = Character.digit(ch, 16);
/* 113 */     if (digit < 0) {
/* 114 */       throw new UtilException("Illegal hexadecimal character {} at index {}", new Object[] { Character.valueOf(ch), Integer.valueOf(index) });
/*     */     }
/* 116 */     return digit;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\Base16Codec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
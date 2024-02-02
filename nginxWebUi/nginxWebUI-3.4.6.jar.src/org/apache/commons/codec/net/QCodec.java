/*     */ package org.apache.commons.codec.net;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.BitSet;
/*     */ import org.apache.commons.codec.DecoderException;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringDecoder;
/*     */ import org.apache.commons.codec.StringEncoder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class QCodec
/*     */   extends RFC1522Codec
/*     */   implements StringEncoder, StringDecoder
/*     */ {
/*     */   private final Charset charset;
/*  60 */   private static final BitSet PRINTABLE_CHARS = new BitSet(256);
/*     */   private static final byte SPACE = 32;
/*     */   
/*     */   static {
/*  64 */     PRINTABLE_CHARS.set(32);
/*  65 */     PRINTABLE_CHARS.set(33);
/*  66 */     PRINTABLE_CHARS.set(34);
/*  67 */     PRINTABLE_CHARS.set(35);
/*  68 */     PRINTABLE_CHARS.set(36);
/*  69 */     PRINTABLE_CHARS.set(37);
/*  70 */     PRINTABLE_CHARS.set(38);
/*  71 */     PRINTABLE_CHARS.set(39);
/*  72 */     PRINTABLE_CHARS.set(40);
/*  73 */     PRINTABLE_CHARS.set(41);
/*  74 */     PRINTABLE_CHARS.set(42);
/*  75 */     PRINTABLE_CHARS.set(43);
/*  76 */     PRINTABLE_CHARS.set(44);
/*  77 */     PRINTABLE_CHARS.set(45);
/*  78 */     PRINTABLE_CHARS.set(46);
/*  79 */     PRINTABLE_CHARS.set(47); int i;
/*  80 */     for (i = 48; i <= 57; i++) {
/*  81 */       PRINTABLE_CHARS.set(i);
/*     */     }
/*  83 */     PRINTABLE_CHARS.set(58);
/*  84 */     PRINTABLE_CHARS.set(59);
/*  85 */     PRINTABLE_CHARS.set(60);
/*  86 */     PRINTABLE_CHARS.set(62);
/*  87 */     PRINTABLE_CHARS.set(64);
/*  88 */     for (i = 65; i <= 90; i++) {
/*  89 */       PRINTABLE_CHARS.set(i);
/*     */     }
/*  91 */     PRINTABLE_CHARS.set(91);
/*  92 */     PRINTABLE_CHARS.set(92);
/*  93 */     PRINTABLE_CHARS.set(93);
/*  94 */     PRINTABLE_CHARS.set(94);
/*  95 */     PRINTABLE_CHARS.set(96);
/*  96 */     for (i = 97; i <= 122; i++) {
/*  97 */       PRINTABLE_CHARS.set(i);
/*     */     }
/*  99 */     PRINTABLE_CHARS.set(123);
/* 100 */     PRINTABLE_CHARS.set(124);
/* 101 */     PRINTABLE_CHARS.set(125);
/* 102 */     PRINTABLE_CHARS.set(126);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final byte UNDERSCORE = 95;
/*     */ 
/*     */   
/*     */   private boolean encodeBlanks = false;
/*     */ 
/*     */ 
/*     */   
/*     */   public QCodec() {
/* 115 */     this(StandardCharsets.UTF_8);
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
/*     */   public QCodec(Charset charset) {
/* 129 */     this.charset = charset;
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
/*     */   public QCodec(String charsetName) {
/* 143 */     this(Charset.forName(charsetName));
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getEncoding() {
/* 148 */     return "Q";
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte[] doEncoding(byte[] bytes) {
/* 153 */     if (bytes == null) {
/* 154 */       return null;
/*     */     }
/* 156 */     byte[] data = QuotedPrintableCodec.encodeQuotedPrintable(PRINTABLE_CHARS, bytes);
/* 157 */     if (this.encodeBlanks) {
/* 158 */       for (int i = 0; i < data.length; i++) {
/* 159 */         if (data[i] == 32) {
/* 160 */           data[i] = 95;
/*     */         }
/*     */       } 
/*     */     }
/* 164 */     return data;
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte[] doDecoding(byte[] bytes) throws DecoderException {
/* 169 */     if (bytes == null) {
/* 170 */       return null;
/*     */     }
/* 172 */     boolean hasUnderscores = false;
/* 173 */     for (byte b : bytes) {
/* 174 */       if (b == 95) {
/* 175 */         hasUnderscores = true;
/*     */         break;
/*     */       } 
/*     */     } 
/* 179 */     if (hasUnderscores) {
/* 180 */       byte[] tmp = new byte[bytes.length];
/* 181 */       for (int i = 0; i < bytes.length; i++) {
/* 182 */         byte b = bytes[i];
/* 183 */         if (b != 95) {
/* 184 */           tmp[i] = b;
/*     */         } else {
/* 186 */           tmp[i] = 32;
/*     */         } 
/*     */       } 
/* 189 */       return QuotedPrintableCodec.decodeQuotedPrintable(tmp);
/*     */     } 
/* 191 */     return QuotedPrintableCodec.decodeQuotedPrintable(bytes);
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
/*     */   public String encode(String sourceStr, Charset sourceCharset) throws EncoderException {
/* 207 */     if (sourceStr == null) {
/* 208 */       return null;
/*     */     }
/* 210 */     return encodeText(sourceStr, sourceCharset);
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
/*     */   public String encode(String sourceStr, String sourceCharset) throws EncoderException {
/* 225 */     if (sourceStr == null) {
/* 226 */       return null;
/*     */     }
/*     */     try {
/* 229 */       return encodeText(sourceStr, sourceCharset);
/* 230 */     } catch (UnsupportedEncodingException e) {
/* 231 */       throw new EncoderException(e.getMessage(), e);
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
/*     */   public String encode(String sourceStr) throws EncoderException {
/* 246 */     if (sourceStr == null) {
/* 247 */       return null;
/*     */     }
/* 249 */     return encode(sourceStr, getCharset());
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
/*     */   public String decode(String str) throws DecoderException {
/* 264 */     if (str == null) {
/* 265 */       return null;
/*     */     }
/*     */     try {
/* 268 */       return decodeText(str);
/* 269 */     } catch (UnsupportedEncodingException e) {
/* 270 */       throw new DecoderException(e.getMessage(), e);
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
/*     */   public Object encode(Object obj) throws EncoderException {
/* 285 */     if (obj == null)
/* 286 */       return null; 
/* 287 */     if (obj instanceof String) {
/* 288 */       return encode((String)obj);
/*     */     }
/* 290 */     throw new EncoderException("Objects of type " + obj
/* 291 */         .getClass().getName() + " cannot be encoded using Q codec");
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
/* 309 */     if (obj == null)
/* 310 */       return null; 
/* 311 */     if (obj instanceof String) {
/* 312 */       return decode((String)obj);
/*     */     }
/* 314 */     throw new DecoderException("Objects of type " + obj
/* 315 */         .getClass().getName() + " cannot be decoded using Q codec");
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
/* 327 */     return this.charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDefaultCharset() {
/* 336 */     return this.charset.name();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEncodeBlanks() {
/* 345 */     return this.encodeBlanks;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEncodeBlanks(boolean b) {
/* 355 */     this.encodeBlanks = b;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\net\QCodec.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
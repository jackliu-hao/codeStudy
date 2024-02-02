/*     */ package org.apache.commons.codec.net;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.UnsupportedEncodingException;
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
/*     */ public class URLCodec
/*     */   implements BinaryEncoder, BinaryDecoder, StringEncoder, StringDecoder
/*     */ {
/*     */   @Deprecated
/*     */   protected volatile String charset;
/*     */   protected static final byte ESCAPE_CHAR = 37;
/*     */   @Deprecated
/*     */   protected static final BitSet WWW_FORM_URL;
/*  71 */   private static final BitSet WWW_FORM_URL_SAFE = new BitSet(256);
/*     */ 
/*     */   
/*     */   static {
/*     */     int i;
/*  76 */     for (i = 97; i <= 122; i++) {
/*  77 */       WWW_FORM_URL_SAFE.set(i);
/*     */     }
/*  79 */     for (i = 65; i <= 90; i++) {
/*  80 */       WWW_FORM_URL_SAFE.set(i);
/*     */     }
/*     */     
/*  83 */     for (i = 48; i <= 57; i++) {
/*  84 */       WWW_FORM_URL_SAFE.set(i);
/*     */     }
/*     */     
/*  87 */     WWW_FORM_URL_SAFE.set(45);
/*  88 */     WWW_FORM_URL_SAFE.set(95);
/*  89 */     WWW_FORM_URL_SAFE.set(46);
/*  90 */     WWW_FORM_URL_SAFE.set(42);
/*     */     
/*  92 */     WWW_FORM_URL_SAFE.set(32);
/*     */ 
/*     */     
/*  95 */     WWW_FORM_URL = (BitSet)WWW_FORM_URL_SAFE.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URLCodec() {
/* 103 */     this("UTF-8");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URLCodec(String charset) {
/* 113 */     this.charset = charset;
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
/*     */   public static final byte[] encodeUrl(BitSet urlsafe, byte[] bytes) {
/* 126 */     if (bytes == null) {
/* 127 */       return null;
/*     */     }
/* 129 */     if (urlsafe == null) {
/* 130 */       urlsafe = WWW_FORM_URL_SAFE;
/*     */     }
/*     */     
/* 133 */     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
/* 134 */     for (byte c : bytes) {
/* 135 */       int b = c;
/* 136 */       if (b < 0) {
/* 137 */         b = 256 + b;
/*     */       }
/* 139 */       if (urlsafe.get(b)) {
/* 140 */         if (b == 32) {
/* 141 */           b = 43;
/*     */         }
/* 143 */         buffer.write(b);
/*     */       } else {
/* 145 */         buffer.write(37);
/* 146 */         char hex1 = Utils.hexDigit(b >> 4);
/* 147 */         char hex2 = Utils.hexDigit(b);
/* 148 */         buffer.write(hex1);
/* 149 */         buffer.write(hex2);
/*     */       } 
/*     */     } 
/* 152 */     return buffer.toByteArray();
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
/*     */   public static final byte[] decodeUrl(byte[] bytes) throws DecoderException {
/* 166 */     if (bytes == null) {
/* 167 */       return null;
/*     */     }
/* 169 */     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
/* 170 */     for (int i = 0; i < bytes.length; i++) {
/* 171 */       int b = bytes[i];
/* 172 */       if (b == 43) {
/* 173 */         buffer.write(32);
/* 174 */       } else if (b == 37) {
/*     */         try {
/* 176 */           int u = Utils.digit16(bytes[++i]);
/* 177 */           int l = Utils.digit16(bytes[++i]);
/* 178 */           buffer.write((char)((u << 4) + l));
/* 179 */         } catch (ArrayIndexOutOfBoundsException e) {
/* 180 */           throw new DecoderException("Invalid URL encoding: ", e);
/*     */         } 
/*     */       } else {
/* 183 */         buffer.write(b);
/*     */       } 
/*     */     } 
/* 186 */     return buffer.toByteArray();
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
/*     */   public byte[] encode(byte[] bytes) {
/* 198 */     return encodeUrl(WWW_FORM_URL_SAFE, bytes);
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
/*     */   public byte[] decode(byte[] bytes) throws DecoderException {
/* 214 */     return decodeUrl(bytes);
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
/*     */   public String encode(String str, String charsetName) throws UnsupportedEncodingException {
/* 229 */     if (str == null) {
/* 230 */       return null;
/*     */     }
/* 232 */     return StringUtils.newStringUsAscii(encode(str.getBytes(charsetName)));
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
/*     */   public String encode(String str) throws EncoderException {
/* 248 */     if (str == null) {
/* 249 */       return null;
/*     */     }
/*     */     try {
/* 252 */       return encode(str, getDefaultCharset());
/* 253 */     } catch (UnsupportedEncodingException e) {
/* 254 */       throw new EncoderException(e.getMessage(), e);
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
/*     */   public String decode(String str, String charsetName) throws DecoderException, UnsupportedEncodingException {
/* 275 */     if (str == null) {
/* 276 */       return null;
/*     */     }
/* 278 */     return new String(decode(StringUtils.getBytesUsAscii(str)), charsetName);
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
/*     */   public String decode(String str) throws DecoderException {
/* 294 */     if (str == null) {
/* 295 */       return null;
/*     */     }
/*     */     try {
/* 298 */       return decode(str, getDefaultCharset());
/* 299 */     } catch (UnsupportedEncodingException e) {
/* 300 */       throw new DecoderException(e.getMessage(), e);
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
/* 315 */     if (obj == null)
/* 316 */       return null; 
/* 317 */     if (obj instanceof byte[])
/* 318 */       return encode((byte[])obj); 
/* 319 */     if (obj instanceof String) {
/* 320 */       return encode((String)obj);
/*     */     }
/* 322 */     throw new EncoderException("Objects of type " + obj.getClass().getName() + " cannot be URL encoded");
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
/* 340 */     if (obj == null)
/* 341 */       return null; 
/* 342 */     if (obj instanceof byte[])
/* 343 */       return decode((byte[])obj); 
/* 344 */     if (obj instanceof String) {
/* 345 */       return decode((String)obj);
/*     */     }
/* 347 */     throw new DecoderException("Objects of type " + obj.getClass().getName() + " cannot be URL decoded");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDefaultCharset() {
/* 358 */     return this.charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String getEncoding() {
/* 370 */     return this.charset;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\net\URLCodec.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
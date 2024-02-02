/*     */ package org.apache.commons.codec.net;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import org.apache.commons.codec.DecoderException;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringDecoder;
/*     */ import org.apache.commons.codec.StringEncoder;
/*     */ import org.apache.commons.codec.binary.Base64;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BCodec
/*     */   extends RFC1522Codec
/*     */   implements StringEncoder, StringDecoder
/*     */ {
/*     */   private final Charset charset;
/*     */   
/*     */   public BCodec() {
/*  55 */     this(StandardCharsets.UTF_8);
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
/*     */   public BCodec(Charset charset) {
/*  68 */     this.charset = charset;
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
/*     */   public BCodec(String charsetName) {
/*  82 */     this(Charset.forName(charsetName));
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getEncoding() {
/*  87 */     return "B";
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte[] doEncoding(byte[] bytes) {
/*  92 */     if (bytes == null) {
/*  93 */       return null;
/*     */     }
/*  95 */     return Base64.encodeBase64(bytes);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte[] doDecoding(byte[] bytes) {
/* 100 */     if (bytes == null) {
/* 101 */       return null;
/*     */     }
/* 103 */     return Base64.decodeBase64(bytes);
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
/*     */   public String encode(String strSource, Charset sourceCharset) throws EncoderException {
/* 119 */     if (strSource == null) {
/* 120 */       return null;
/*     */     }
/* 122 */     return encodeText(strSource, sourceCharset);
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
/*     */   public String encode(String strSource, String sourceCharset) throws EncoderException {
/* 137 */     if (strSource == null) {
/* 138 */       return null;
/*     */     }
/*     */     try {
/* 141 */       return encodeText(strSource, sourceCharset);
/* 142 */     } catch (UnsupportedEncodingException e) {
/* 143 */       throw new EncoderException(e.getMessage(), e);
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
/*     */   public String encode(String strSource) throws EncoderException {
/* 158 */     if (strSource == null) {
/* 159 */       return null;
/*     */     }
/* 161 */     return encode(strSource, getCharset());
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
/*     */   public String decode(String value) throws DecoderException {
/* 176 */     if (value == null) {
/* 177 */       return null;
/*     */     }
/*     */     try {
/* 180 */       return decodeText(value);
/* 181 */     } catch (UnsupportedEncodingException|IllegalArgumentException e) {
/* 182 */       throw new DecoderException(e.getMessage(), e);
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
/*     */   public Object encode(Object value) throws EncoderException {
/* 197 */     if (value == null)
/* 198 */       return null; 
/* 199 */     if (value instanceof String) {
/* 200 */       return encode((String)value);
/*     */     }
/* 202 */     throw new EncoderException("Objects of type " + value
/* 203 */         .getClass().getName() + " cannot be encoded using BCodec");
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
/*     */   public Object decode(Object value) throws DecoderException {
/* 221 */     if (value == null)
/* 222 */       return null; 
/* 223 */     if (value instanceof String) {
/* 224 */       return decode((String)value);
/*     */     }
/* 226 */     throw new DecoderException("Objects of type " + value
/* 227 */         .getClass().getName() + " cannot be decoded using BCodec");
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
/* 239 */     return this.charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDefaultCharset() {
/* 248 */     return this.charset.name();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\net\BCodec.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package org.apache.commons.codec.net;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import org.apache.commons.codec.DecoderException;
/*     */ import org.apache.commons.codec.EncoderException;
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
/*     */ abstract class RFC1522Codec
/*     */ {
/*     */   protected static final char SEP = '?';
/*     */   protected static final String POSTFIX = "?=";
/*     */   protected static final String PREFIX = "=?";
/*     */   
/*     */   protected String encodeText(String text, Charset charset) throws EncoderException {
/*  68 */     if (text == null) {
/*  69 */       return null;
/*     */     }
/*  71 */     StringBuilder buffer = new StringBuilder();
/*  72 */     buffer.append("=?");
/*  73 */     buffer.append(charset);
/*  74 */     buffer.append('?');
/*  75 */     buffer.append(getEncoding());
/*  76 */     buffer.append('?');
/*  77 */     byte[] rawData = doEncoding(text.getBytes(charset));
/*  78 */     buffer.append(StringUtils.newStringUsAscii(rawData));
/*  79 */     buffer.append("?=");
/*  80 */     return buffer.toString();
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected String encodeText(String text, String charsetName) throws EncoderException, UnsupportedEncodingException {
/* 103 */     if (text == null) {
/* 104 */       return null;
/*     */     }
/* 106 */     return encodeText(text, Charset.forName(charsetName));
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
/*     */   protected String decodeText(String text) throws DecoderException, UnsupportedEncodingException {
/* 125 */     if (text == null) {
/* 126 */       return null;
/*     */     }
/* 128 */     if (!text.startsWith("=?") || !text.endsWith("?=")) {
/* 129 */       throw new DecoderException("RFC 1522 violation: malformed encoded content");
/*     */     }
/* 131 */     int terminator = text.length() - 2;
/* 132 */     int from = 2;
/* 133 */     int to = text.indexOf('?', from);
/* 134 */     if (to == terminator) {
/* 135 */       throw new DecoderException("RFC 1522 violation: charset token not found");
/*     */     }
/* 137 */     String charset = text.substring(from, to);
/* 138 */     if (charset.equals("")) {
/* 139 */       throw new DecoderException("RFC 1522 violation: charset not specified");
/*     */     }
/* 141 */     from = to + 1;
/* 142 */     to = text.indexOf('?', from);
/* 143 */     if (to == terminator) {
/* 144 */       throw new DecoderException("RFC 1522 violation: encoding token not found");
/*     */     }
/* 146 */     String encoding = text.substring(from, to);
/* 147 */     if (!getEncoding().equalsIgnoreCase(encoding)) {
/* 148 */       throw new DecoderException("This codec cannot decode " + encoding + " encoded content");
/*     */     }
/* 150 */     from = to + 1;
/* 151 */     to = text.indexOf('?', from);
/* 152 */     byte[] data = StringUtils.getBytesUsAscii(text.substring(from, to));
/* 153 */     data = doDecoding(data);
/* 154 */     return new String(data, charset);
/*     */   }
/*     */   
/*     */   protected abstract String getEncoding();
/*     */   
/*     */   protected abstract byte[] doEncoding(byte[] paramArrayOfbyte) throws EncoderException;
/*     */   
/*     */   protected abstract byte[] doDecoding(byte[] paramArrayOfbyte) throws DecoderException;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\net\RFC1522Codec.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
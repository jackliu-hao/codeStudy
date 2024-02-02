/*     */ package org.apache.http.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.UnsupportedCharsetException;
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.NameValuePair;
/*     */ import org.apache.http.ParseException;
/*     */ import org.apache.http.entity.ContentType;
/*     */ import org.apache.http.protocol.HTTP;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class EntityUtils
/*     */ {
/*     */   private static final int DEFAULT_BUFFER_SIZE = 4096;
/*     */   
/*     */   public static void consumeQuietly(HttpEntity entity) {
/*     */     try {
/*  69 */       consume(entity);
/*  70 */     } catch (IOException ignore) {}
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
/*     */   public static void consume(HttpEntity entity) throws IOException {
/*  84 */     if (entity == null) {
/*     */       return;
/*     */     }
/*  87 */     if (entity.isStreaming()) {
/*  88 */       InputStream inStream = entity.getContent();
/*  89 */       if (inStream != null) {
/*  90 */         inStream.close();
/*     */       }
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
/*     */   public static void updateEntity(HttpResponse response, HttpEntity entity) throws IOException {
/* 108 */     Args.notNull(response, "Response");
/* 109 */     consume(response.getEntity());
/* 110 */     response.setEntity(entity);
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
/*     */   public static byte[] toByteArray(HttpEntity entity) throws IOException {
/* 123 */     Args.notNull(entity, "Entity");
/* 124 */     InputStream inStream = entity.getContent();
/* 125 */     if (inStream == null) {
/* 126 */       return null;
/*     */     }
/*     */     try {
/* 129 */       Args.check((entity.getContentLength() <= 2147483647L), "HTTP entity too large to be buffered in memory");
/*     */       
/* 131 */       int capacity = (int)entity.getContentLength();
/* 132 */       if (capacity < 0) {
/* 133 */         capacity = 4096;
/*     */       }
/* 135 */       ByteArrayBuffer buffer = new ByteArrayBuffer(capacity);
/* 136 */       byte[] tmp = new byte[4096];
/*     */       int l;
/* 138 */       while ((l = inStream.read(tmp)) != -1) {
/* 139 */         buffer.append(tmp, 0, l);
/*     */       }
/* 141 */       return buffer.toByteArray();
/*     */     } finally {
/* 143 */       inStream.close();
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
/*     */   @Deprecated
/*     */   public static String getContentCharSet(HttpEntity entity) throws ParseException {
/* 159 */     Args.notNull(entity, "Entity");
/* 160 */     String charset = null;
/* 161 */     if (entity.getContentType() != null) {
/* 162 */       HeaderElement[] values = entity.getContentType().getElements();
/* 163 */       if (values.length > 0) {
/* 164 */         NameValuePair param = values[0].getParameterByName("charset");
/* 165 */         if (param != null) {
/* 166 */           charset = param.getValue();
/*     */         }
/*     */       } 
/*     */     } 
/* 170 */     return charset;
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
/*     */   @Deprecated
/*     */   public static String getContentMimeType(HttpEntity entity) throws ParseException {
/* 187 */     Args.notNull(entity, "Entity");
/* 188 */     String mimeType = null;
/* 189 */     if (entity.getContentType() != null) {
/* 190 */       HeaderElement[] values = entity.getContentType().getElements();
/* 191 */       if (values.length > 0) {
/* 192 */         mimeType = values[0].getName();
/*     */       }
/*     */     } 
/* 195 */     return mimeType;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String toString(HttpEntity entity, ContentType contentType) throws IOException {
/* 201 */     InputStream inStream = entity.getContent();
/* 202 */     if (inStream == null) {
/* 203 */       return null;
/*     */     }
/*     */     try {
/* 206 */       Args.check((entity.getContentLength() <= 2147483647L), "HTTP entity too large to be buffered in memory");
/*     */       
/* 208 */       int capacity = (int)entity.getContentLength();
/* 209 */       if (capacity < 0) {
/* 210 */         capacity = 4096;
/*     */       }
/* 212 */       Charset charset = null;
/* 213 */       if (contentType != null) {
/* 214 */         charset = contentType.getCharset();
/* 215 */         if (charset == null) {
/* 216 */           ContentType defaultContentType = ContentType.getByMimeType(contentType.getMimeType());
/* 217 */           charset = (defaultContentType != null) ? defaultContentType.getCharset() : null;
/*     */         } 
/*     */       } 
/* 220 */       if (charset == null) {
/* 221 */         charset = HTTP.DEF_CONTENT_CHARSET;
/*     */       }
/* 223 */       Reader reader = new InputStreamReader(inStream, charset);
/* 224 */       CharArrayBuffer buffer = new CharArrayBuffer(capacity);
/* 225 */       char[] tmp = new char[1024];
/*     */       int l;
/* 227 */       while ((l = reader.read(tmp)) != -1) {
/* 228 */         buffer.append(tmp, 0, l);
/*     */       }
/* 230 */       return buffer.toString();
/*     */     } finally {
/* 232 */       inStream.close();
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
/*     */   
/*     */   public static String toString(HttpEntity entity, Charset defaultCharset) throws IOException, ParseException {
/* 254 */     Args.notNull(entity, "Entity");
/* 255 */     ContentType contentType = null;
/*     */     try {
/* 257 */       contentType = ContentType.get(entity);
/* 258 */     } catch (UnsupportedCharsetException ex) {
/* 259 */       if (defaultCharset == null) {
/* 260 */         throw new UnsupportedEncodingException(ex.getMessage());
/*     */       }
/*     */     } 
/* 263 */     if (contentType != null) {
/* 264 */       if (contentType.getCharset() == null) {
/* 265 */         contentType = contentType.withCharset(defaultCharset);
/*     */       }
/*     */     } else {
/* 268 */       contentType = ContentType.DEFAULT_TEXT.withCharset(defaultCharset);
/*     */     } 
/* 270 */     return toString(entity, contentType);
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
/*     */   public static String toString(HttpEntity entity, String defaultCharset) throws IOException, ParseException {
/* 290 */     return toString(entity, (defaultCharset != null) ? Charset.forName(defaultCharset) : null);
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
/*     */   public static String toString(HttpEntity entity) throws IOException, ParseException {
/* 307 */     Args.notNull(entity, "Entity");
/* 308 */     return toString(entity, ContentType.get(entity));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\htt\\util\EntityUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */
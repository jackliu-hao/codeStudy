/*     */ package org.apache.http.entity;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.UnsupportedCharsetException;
/*     */ import org.apache.http.protocol.HTTP;
/*     */ import org.apache.http.util.Args;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StringEntity
/*     */   extends AbstractHttpEntity
/*     */   implements Cloneable
/*     */ {
/*     */   protected final byte[] content;
/*     */   
/*     */   public StringEntity(String string, ContentType contentType) throws UnsupportedCharsetException {
/*  65 */     Args.notNull(string, "Source string");
/*  66 */     Charset charset = (contentType != null) ? contentType.getCharset() : null;
/*  67 */     if (charset == null) {
/*  68 */       charset = HTTP.DEF_CONTENT_CHARSET;
/*     */     }
/*  70 */     this.content = string.getBytes(charset);
/*  71 */     if (contentType != null) {
/*  72 */       setContentType(contentType.toString());
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
/*     */   @Deprecated
/*     */   public StringEntity(String string, String mimeType, String charset) throws UnsupportedEncodingException {
/*  95 */     Args.notNull(string, "Source string");
/*  96 */     String mt = (mimeType != null) ? mimeType : "text/plain";
/*  97 */     String cs = (charset != null) ? charset : "ISO-8859-1";
/*  98 */     this.content = string.getBytes(cs);
/*  99 */     setContentType(mt + "; charset=" + cs);
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
/*     */   public StringEntity(String string, String charset) throws UnsupportedCharsetException {
/* 116 */     this(string, ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset));
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
/*     */   public StringEntity(String string, Charset charset) {
/* 132 */     this(string, ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset));
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
/*     */   public StringEntity(String string) throws UnsupportedEncodingException {
/* 146 */     this(string, ContentType.DEFAULT_TEXT);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRepeatable() {
/* 151 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getContentLength() {
/* 156 */     return this.content.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getContent() throws IOException {
/* 161 */     return new ByteArrayInputStream(this.content);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(OutputStream outStream) throws IOException {
/* 166 */     Args.notNull(outStream, "Output stream");
/* 167 */     outStream.write(this.content);
/* 168 */     outStream.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isStreaming() {
/* 178 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 183 */     return super.clone();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\entity\StringEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */
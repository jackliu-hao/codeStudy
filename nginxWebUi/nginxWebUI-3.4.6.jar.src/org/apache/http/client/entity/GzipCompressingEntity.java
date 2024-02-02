/*     */ package org.apache.http.client.entity;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.entity.HttpEntityWrapper;
/*     */ import org.apache.http.message.BasicHeader;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GzipCompressingEntity
/*     */   extends HttpEntityWrapper
/*     */ {
/*     */   private static final String GZIP_CODEC = "gzip";
/*     */   
/*     */   public GzipCompressingEntity(HttpEntity entity) {
/*  79 */     super(entity);
/*     */   }
/*     */ 
/*     */   
/*     */   public Header getContentEncoding() {
/*  84 */     return (Header)new BasicHeader("Content-Encoding", "gzip");
/*     */   }
/*     */ 
/*     */   
/*     */   public long getContentLength() {
/*  89 */     return -1L;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isChunked() {
/*  95 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getContent() throws IOException {
/* 100 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(OutputStream outStream) throws IOException {
/* 105 */     Args.notNull(outStream, "Output stream");
/* 106 */     GZIPOutputStream gzip = new GZIPOutputStream(outStream);
/* 107 */     this.wrappedEntity.writeTo(gzip);
/*     */ 
/*     */     
/* 110 */     gzip.close();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\entity\GzipCompressingEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */
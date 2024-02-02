/*     */ package org.apache.http.client.entity;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.entity.HttpEntityWrapper;
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
/*     */ public class DecompressingEntity
/*     */   extends HttpEntityWrapper
/*     */ {
/*     */   private static final int BUFFER_SIZE = 2048;
/*     */   private final InputStreamFactory inputStreamFactory;
/*     */   private InputStream content;
/*     */   
/*     */   public DecompressingEntity(HttpEntity wrapped, InputStreamFactory inputStreamFactory) {
/*  66 */     super(wrapped);
/*  67 */     this.inputStreamFactory = inputStreamFactory;
/*     */   }
/*     */   
/*     */   private InputStream getDecompressingStream() throws IOException {
/*  71 */     InputStream in = this.wrappedEntity.getContent();
/*  72 */     return new LazyDecompressingInputStream(in, this.inputStreamFactory);
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getContent() throws IOException {
/*  77 */     if (this.wrappedEntity.isStreaming()) {
/*  78 */       if (this.content == null) {
/*  79 */         this.content = getDecompressingStream();
/*     */       }
/*  81 */       return this.content;
/*     */     } 
/*  83 */     return getDecompressingStream();
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(OutputStream outStream) throws IOException {
/*  88 */     Args.notNull(outStream, "Output stream");
/*  89 */     InputStream inStream = getContent();
/*     */     try {
/*  91 */       byte[] buffer = new byte[2048];
/*     */       int l;
/*  93 */       while ((l = inStream.read(buffer)) != -1) {
/*  94 */         outStream.write(buffer, 0, l);
/*     */       }
/*     */     } finally {
/*  97 */       inStream.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Header getContentEncoding() {
/* 104 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getContentLength() {
/* 110 */     return -1L;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\entity\DecompressingEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */
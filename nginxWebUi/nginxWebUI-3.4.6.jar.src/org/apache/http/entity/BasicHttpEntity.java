/*     */ package org.apache.http.entity;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.http.impl.io.EmptyInputStream;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.Asserts;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BasicHttpEntity
/*     */   extends AbstractHttpEntity
/*     */ {
/*     */   private InputStream content;
/*  56 */   private long length = -1L;
/*     */ 
/*     */ 
/*     */   
/*     */   public long getContentLength() {
/*  61 */     return this.length;
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
/*     */   public InputStream getContent() throws IllegalStateException {
/*  75 */     Asserts.check((this.content != null), "Content has not been provided");
/*  76 */     return this.content;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRepeatable() {
/*  86 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setContentLength(long len) {
/*  96 */     this.length = len;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setContent(InputStream inStream) {
/* 106 */     this.content = inStream;
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(OutputStream outStream) throws IOException {
/* 111 */     Args.notNull(outStream, "Output stream");
/* 112 */     InputStream inStream = getContent();
/*     */     
/*     */     try {
/* 115 */       byte[] tmp = new byte[4096]; int l;
/* 116 */       while ((l = inStream.read(tmp)) != -1) {
/* 117 */         outStream.write(tmp, 0, l);
/*     */       }
/*     */     } finally {
/* 120 */       inStream.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isStreaming() {
/* 126 */     return (this.content != null && this.content != EmptyInputStream.INSTANCE);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\entity\BasicHttpEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */
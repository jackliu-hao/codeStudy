/*     */ package org.apache.http.entity;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
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
/*     */ public class InputStreamEntity
/*     */   extends AbstractHttpEntity
/*     */ {
/*     */   private final InputStream content;
/*     */   private final long length;
/*     */   
/*     */   public InputStreamEntity(InputStream inStream) {
/*  56 */     this(inStream, -1L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStreamEntity(InputStream inStream, long length) {
/*  67 */     this(inStream, length, null);
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
/*     */   public InputStreamEntity(InputStream inStream, ContentType contentType) {
/*  80 */     this(inStream, -1L, contentType);
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
/*     */   public InputStreamEntity(InputStream inStream, long length, ContentType contentType) {
/*  92 */     this.content = (InputStream)Args.notNull(inStream, "Source input stream");
/*  93 */     this.length = length;
/*  94 */     if (contentType != null) {
/*  95 */       setContentType(contentType.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRepeatable() {
/* 101 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getContentLength() {
/* 109 */     return this.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getContent() throws IOException {
/* 114 */     return this.content;
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
/*     */   public void writeTo(OutputStream outStream) throws IOException {
/* 126 */     Args.notNull(outStream, "Output stream");
/* 127 */     InputStream inStream = this.content;
/*     */     try {
/* 129 */       byte[] buffer = new byte[4096];
/*     */       
/* 131 */       if (this.length < 0L) {
/*     */         int readLen;
/* 133 */         while ((readLen = inStream.read(buffer)) != -1) {
/* 134 */           outStream.write(buffer, 0, readLen);
/*     */         }
/*     */       } else {
/*     */         
/* 138 */         long remaining = this.length;
/* 139 */         while (remaining > 0L) {
/* 140 */           int readLen = inStream.read(buffer, 0, (int)Math.min(4096L, remaining));
/* 141 */           if (readLen == -1) {
/*     */             break;
/*     */           }
/* 144 */           outStream.write(buffer, 0, readLen);
/* 145 */           remaining -= readLen;
/*     */         } 
/*     */       } 
/*     */     } finally {
/* 149 */       inStream.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isStreaming() {
/* 155 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\entity\InputStreamEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */
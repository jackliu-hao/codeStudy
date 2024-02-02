/*     */ package org.apache.http.impl.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.http.io.SessionOutputBuffer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ContentLengthOutputStream
/*     */   extends OutputStream
/*     */ {
/*     */   private final SessionOutputBuffer out;
/*     */   private final long contentLength;
/*     */   private long total;
/*     */   private boolean closed;
/*     */   
/*     */   public ContentLengthOutputStream(SessionOutputBuffer out, long contentLength) {
/*  80 */     this.out = (SessionOutputBuffer)Args.notNull(out, "Session output buffer");
/*  81 */     this.contentLength = Args.notNegative(contentLength, "Content length");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  91 */     if (!this.closed) {
/*  92 */       this.closed = true;
/*  93 */       this.out.flush();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/*  99 */     this.out.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 104 */     if (this.closed) {
/* 105 */       throw new IOException("Attempted write to closed stream.");
/*     */     }
/* 107 */     if (this.total < this.contentLength) {
/* 108 */       long max = this.contentLength - this.total;
/* 109 */       int chunk = len;
/* 110 */       if (chunk > max) {
/* 111 */         chunk = (int)max;
/*     */       }
/* 113 */       this.out.write(b, off, chunk);
/* 114 */       this.total += chunk;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/* 120 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 125 */     if (this.closed) {
/* 126 */       throw new IOException("Attempted write to closed stream.");
/*     */     }
/* 128 */     if (this.total < this.contentLength) {
/* 129 */       this.out.write(b);
/* 130 */       this.total++;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\ContentLengthOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */
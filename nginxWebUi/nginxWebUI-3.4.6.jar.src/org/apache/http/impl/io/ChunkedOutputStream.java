/*     */ package org.apache.http.impl.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.http.io.SessionOutputBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ChunkedOutputStream
/*     */   extends OutputStream
/*     */ {
/*     */   private final SessionOutputBuffer out;
/*     */   private final byte[] cache;
/*  54 */   private int cachePosition = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean wroteLastChunk = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean closed = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ChunkedOutputStream(SessionOutputBuffer out, int bufferSize) throws IOException {
/*  73 */     this(bufferSize, out);
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
/*     */   public ChunkedOutputStream(SessionOutputBuffer out) throws IOException {
/*  88 */     this(2048, out);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChunkedOutputStream(int bufferSize, SessionOutputBuffer out) {
/*  99 */     this.cache = new byte[bufferSize];
/* 100 */     this.out = out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void flushCache() throws IOException {
/* 107 */     if (this.cachePosition > 0) {
/* 108 */       this.out.writeLine(Integer.toHexString(this.cachePosition));
/* 109 */       this.out.write(this.cache, 0, this.cachePosition);
/* 110 */       this.out.writeLine("");
/* 111 */       this.cachePosition = 0;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void flushCacheWithAppend(byte[] bufferToAppend, int off, int len) throws IOException {
/* 120 */     this.out.writeLine(Integer.toHexString(this.cachePosition + len));
/* 121 */     this.out.write(this.cache, 0, this.cachePosition);
/* 122 */     this.out.write(bufferToAppend, off, len);
/* 123 */     this.out.writeLine("");
/* 124 */     this.cachePosition = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeClosingChunk() throws IOException {
/* 129 */     this.out.writeLine("0");
/* 130 */     this.out.writeLine("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void finish() throws IOException {
/* 140 */     if (!this.wroteLastChunk) {
/* 141 */       flushCache();
/* 142 */       writeClosingChunk();
/* 143 */       this.wroteLastChunk = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 150 */     if (this.closed) {
/* 151 */       throw new IOException("Attempted write to closed stream.");
/*     */     }
/* 153 */     this.cache[this.cachePosition] = (byte)b;
/* 154 */     this.cachePosition++;
/* 155 */     if (this.cachePosition == this.cache.length) {
/* 156 */       flushCache();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/* 166 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] src, int off, int len) throws IOException {
/* 175 */     if (this.closed) {
/* 176 */       throw new IOException("Attempted write to closed stream.");
/*     */     }
/* 178 */     if (len >= this.cache.length - this.cachePosition) {
/* 179 */       flushCacheWithAppend(src, off, len);
/*     */     } else {
/* 181 */       System.arraycopy(src, off, this.cache, this.cachePosition, len);
/* 182 */       this.cachePosition += len;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 191 */     flushCache();
/* 192 */     this.out.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 200 */     if (!this.closed) {
/* 201 */       this.closed = true;
/* 202 */       finish();
/* 203 */       this.out.flush();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\ChunkedOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */
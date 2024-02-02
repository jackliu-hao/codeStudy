/*     */ package org.xnio.streams;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.xnio._private.Messages;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class LimitedInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private final InputStream delegate;
/*     */   private long remaining;
/*  35 */   private long mark = -1L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LimitedInputStream(InputStream delegate, long size) {
/*  44 */     this.delegate = delegate;
/*  45 */     this.remaining = size;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  50 */     long remaining = this.remaining;
/*  51 */     if (remaining > 0L) {
/*  52 */       int b = this.delegate.read();
/*  53 */       if (b >= 0) this.remaining = remaining - 1L; 
/*  54 */       return b;
/*     */     } 
/*  56 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/*  62 */     long remaining = this.remaining;
/*  63 */     if (remaining == 0L) {
/*  64 */       return -1;
/*     */     }
/*  66 */     int cnt = this.delegate.read(b, off, (int)Math.min(len, remaining));
/*  67 */     if (cnt == -1) {
/*  68 */       return -1;
/*     */     }
/*  70 */     this.remaining = remaining - cnt;
/*  71 */     return cnt;
/*     */   }
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/*  76 */     long remaining = this.remaining;
/*  77 */     if (remaining == 0L || n <= 0L) {
/*  78 */       return 0L;
/*     */     }
/*  80 */     long cnt = this.delegate.skip(Math.min(n, remaining));
/*  81 */     if (cnt > 0L) {
/*  82 */       this.remaining = remaining - cnt;
/*     */     }
/*  84 */     return cnt;
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/*  89 */     return Math.min(this.delegate.available(), (int)Math.min(2147483647L, this.remaining));
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  94 */     this.remaining = 0L;
/*  95 */     this.delegate.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public void mark(int limit) {
/* 100 */     if (markSupported()) {
/* 101 */       this.delegate.mark(limit);
/* 102 */       this.mark = this.remaining;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() throws IOException {
/* 108 */     long mark = this.mark;
/* 109 */     if (mark == -1L) {
/* 110 */       throw Messages.msg.markNotSet();
/*     */     }
/* 112 */     this.delegate.reset();
/* 113 */     this.remaining = mark;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean markSupported() {
/* 118 */     return this.delegate.markSupported();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\streams\LimitedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
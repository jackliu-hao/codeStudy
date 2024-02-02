/*     */ package org.h2.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import org.h2.util.IOUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class RangeReader
/*     */   extends Reader
/*     */ {
/*     */   private final Reader r;
/*     */   private long limit;
/*     */   
/*     */   public RangeReader(Reader paramReader, long paramLong1, long paramLong2) throws IOException {
/*  34 */     this.r = paramReader;
/*  35 */     this.limit = paramLong2;
/*  36 */     IOUtils.skipFully(paramReader, paramLong1);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  41 */     if (this.limit <= 0L) {
/*  42 */       return -1;
/*     */     }
/*  44 */     int i = this.r.read();
/*  45 */     if (i >= 0) {
/*  46 */       this.limit--;
/*     */     }
/*  48 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws IOException {
/*  53 */     if (this.limit <= 0L) {
/*  54 */       return -1;
/*     */     }
/*  56 */     if (paramInt2 > this.limit) {
/*  57 */       paramInt2 = (int)this.limit;
/*     */     }
/*  59 */     int i = this.r.read(paramArrayOfchar, paramInt1, paramInt2);
/*  60 */     if (i > 0) {
/*  61 */       this.limit -= i;
/*     */     }
/*  63 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public long skip(long paramLong) throws IOException {
/*  68 */     if (paramLong > this.limit) {
/*  69 */       paramLong = (int)this.limit;
/*     */     }
/*  71 */     paramLong = this.r.skip(paramLong);
/*  72 */     this.limit -= paramLong;
/*  73 */     return paramLong;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean ready() throws IOException {
/*  78 */     if (this.limit > 0L) {
/*  79 */       return this.r.ready();
/*     */     }
/*  81 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean markSupported() {
/*  86 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void mark(int paramInt) throws IOException {
/*  91 */     throw new IOException("mark() not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() throws IOException {
/*  96 */     throw new IOException("reset() not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 101 */     this.r.close();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\RangeReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
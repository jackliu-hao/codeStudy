/*     */ package org.h2.store;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
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
/*     */ public final class RangeInputStream
/*     */   extends FilterInputStream
/*     */ {
/*     */   private long limit;
/*     */   
/*     */   public RangeInputStream(InputStream paramInputStream, long paramLong1, long paramLong2) throws IOException {
/*  33 */     super(paramInputStream);
/*  34 */     this.limit = paramLong2;
/*  35 */     IOUtils.skipFully(paramInputStream, paramLong1);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  40 */     if (this.limit <= 0L) {
/*  41 */       return -1;
/*     */     }
/*  43 */     int i = this.in.read();
/*  44 */     if (i >= 0) {
/*  45 */       this.limit--;
/*     */     }
/*  47 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
/*  52 */     if (this.limit <= 0L) {
/*  53 */       return -1;
/*     */     }
/*  55 */     if (paramInt2 > this.limit) {
/*  56 */       paramInt2 = (int)this.limit;
/*     */     }
/*  58 */     int i = this.in.read(paramArrayOfbyte, paramInt1, paramInt2);
/*  59 */     if (i > 0) {
/*  60 */       this.limit -= i;
/*     */     }
/*  62 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public long skip(long paramLong) throws IOException {
/*  67 */     if (paramLong > this.limit) {
/*  68 */       paramLong = (int)this.limit;
/*     */     }
/*  70 */     paramLong = this.in.skip(paramLong);
/*  71 */     this.limit -= paramLong;
/*  72 */     return paramLong;
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/*  77 */     int i = this.in.available();
/*  78 */     if (i > this.limit) {
/*  79 */       return (int)this.limit;
/*     */     }
/*  81 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  86 */     this.in.close();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void mark(int paramInt) {}
/*     */ 
/*     */   
/*     */   public synchronized void reset() throws IOException {
/*  95 */     throw new IOException("mark/reset not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean markSupported() {
/* 100 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\RangeInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
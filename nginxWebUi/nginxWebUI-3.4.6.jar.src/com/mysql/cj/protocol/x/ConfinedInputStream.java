/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfinedInputStream
/*     */   extends FilterInputStream
/*     */ {
/*  40 */   private int limit = 0;
/*  41 */   private int consumed = 0;
/*     */   
/*     */   private boolean closed = false;
/*     */   
/*     */   protected ConfinedInputStream(InputStream in) {
/*  46 */     this(in, 0);
/*     */   }
/*     */   
/*     */   protected ConfinedInputStream(InputStream in, int lim) {
/*  50 */     super(in);
/*  51 */     this.limit = lim;
/*  52 */     this.consumed = 0;
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
/*     */   public int available() throws IOException {
/*  64 */     ensureOpen();
/*  65 */     return this.limit - this.consumed;
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
/*     */   public void close() throws IOException {
/*  77 */     if (!this.closed) {
/*  78 */       dumpLeftovers();
/*  79 */       this.closed = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  88 */     ensureOpen();
/*  89 */     int read = super.read();
/*  90 */     if (read >= 0) {
/*  91 */       this.consumed++;
/*     */     }
/*  93 */     return read;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/* 103 */     ensureOpen();
/* 104 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 114 */     ensureOpen();
/* 115 */     if (this.consumed >= this.limit) {
/* 116 */       return -1;
/*     */     }
/* 118 */     int toRead = Math.min(len, available());
/* 119 */     int read = super.read(b, off, toRead);
/* 120 */     if (read > 0) {
/* 121 */       this.consumed += read;
/*     */     }
/* 123 */     return read;
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
/*     */   public int resetLimit(int len) {
/* 135 */     int remaining = 0;
/*     */     try {
/* 137 */       remaining = available();
/* 138 */     } catch (IOException iOException) {}
/*     */ 
/*     */     
/* 141 */     this.limit = len;
/* 142 */     this.consumed = 0;
/* 143 */     return remaining;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long dumpLeftovers() throws IOException {
/* 154 */     long skipped = skip(available());
/* 155 */     this.consumed = (int)(this.consumed + skipped);
/* 156 */     return skipped;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void ensureOpen() throws IOException {
/* 166 */     if (this.closed)
/* 167 */       throw new IOException("Stream closed"); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\ConfinedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
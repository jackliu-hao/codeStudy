/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Queue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ContinuousInputStream
/*     */   extends FilterInputStream
/*     */ {
/*  43 */   private Queue<InputStream> inputStreams = new LinkedList<>();
/*     */   
/*     */   private boolean closed = false;
/*     */   
/*     */   protected ContinuousInputStream(InputStream in) {
/*  48 */     super(in);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/*  59 */     ensureOpen();
/*  60 */     int available = super.available();
/*  61 */     if (available == 0 && nextInLine()) {
/*  62 */       return available();
/*     */     }
/*  64 */     return available;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  74 */     if (!this.closed) {
/*  75 */       this.closed = true;
/*  76 */       super.close();
/*  77 */       for (InputStream is : this.inputStreams) {
/*  78 */         is.close();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  90 */     ensureOpen();
/*  91 */     int read = super.read();
/*  92 */     if (read >= 0) {
/*  93 */       return read;
/*     */     }
/*  95 */     if (nextInLine()) {
/*  96 */       return read();
/*     */     }
/*  98 */     return read;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/* 108 */     ensureOpen();
/* 109 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 119 */     ensureOpen();
/* 120 */     int toRead = Math.min(len, available());
/* 121 */     int read = super.read(b, off, toRead);
/* 122 */     if (read > 0) {
/* 123 */       return read;
/*     */     }
/* 125 */     if (nextInLine()) {
/* 126 */       return read(b, off, len);
/*     */     }
/* 128 */     return read;
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
/*     */   protected boolean addInputStream(InputStream newIn) {
/* 140 */     return this.inputStreams.offer(newIn);
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
/*     */   private boolean nextInLine() throws IOException {
/* 152 */     InputStream nextInputStream = this.inputStreams.poll();
/* 153 */     if (nextInputStream != null) {
/* 154 */       super.close();
/* 155 */       this.in = nextInputStream;
/* 156 */       return true;
/*     */     } 
/* 158 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void ensureOpen() throws IOException {
/* 168 */     if (this.closed)
/* 169 */       throw new IOException("Stream closed"); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\ContinuousInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
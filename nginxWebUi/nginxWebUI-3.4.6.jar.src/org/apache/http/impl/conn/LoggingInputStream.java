/*     */ package org.apache.http.impl.conn;
/*     */ 
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
/*     */ 
/*     */ 
/*     */ class LoggingInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private final InputStream in;
/*     */   private final Wire wire;
/*     */   
/*     */   public LoggingInputStream(InputStream in, Wire wire) {
/*  45 */     this.in = in;
/*  46 */     this.wire = wire;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*     */     try {
/*  52 */       int b = this.in.read();
/*  53 */       if (b == -1) {
/*  54 */         this.wire.input("end of stream");
/*     */       } else {
/*  56 */         this.wire.input(b);
/*     */       } 
/*  58 */       return b;
/*  59 */     } catch (IOException ex) {
/*  60 */       this.wire.input("[read] I/O error: " + ex.getMessage());
/*  61 */       throw ex;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/*     */     try {
/*  68 */       int bytesRead = this.in.read(b);
/*  69 */       if (bytesRead == -1) {
/*  70 */         this.wire.input("end of stream");
/*  71 */       } else if (bytesRead > 0) {
/*  72 */         this.wire.input(b, 0, bytesRead);
/*     */       } 
/*  74 */       return bytesRead;
/*  75 */     } catch (IOException ex) {
/*  76 */       this.wire.input("[read] I/O error: " + ex.getMessage());
/*  77 */       throw ex;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/*     */     try {
/*  84 */       int bytesRead = this.in.read(b, off, len);
/*  85 */       if (bytesRead == -1) {
/*  86 */         this.wire.input("end of stream");
/*  87 */       } else if (bytesRead > 0) {
/*  88 */         this.wire.input(b, off, bytesRead);
/*     */       } 
/*  90 */       return bytesRead;
/*  91 */     } catch (IOException ex) {
/*  92 */       this.wire.input("[read] I/O error: " + ex.getMessage());
/*  93 */       throw ex;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/*     */     try {
/* 100 */       return super.skip(n);
/* 101 */     } catch (IOException ex) {
/* 102 */       this.wire.input("[skip] I/O error: " + ex.getMessage());
/* 103 */       throw ex;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/*     */     try {
/* 110 */       return this.in.available();
/* 111 */     } catch (IOException ex) {
/* 112 */       this.wire.input("[available] I/O error : " + ex.getMessage());
/* 113 */       throw ex;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void mark(int readlimit) {
/* 119 */     super.mark(readlimit);
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() throws IOException {
/* 124 */     super.reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean markSupported() {
/* 129 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 135 */       this.in.close();
/* 136 */     } catch (IOException ex) {
/* 137 */       this.wire.input("[close] I/O error: " + ex.getMessage());
/* 138 */       throw ex;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\LoggingInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */
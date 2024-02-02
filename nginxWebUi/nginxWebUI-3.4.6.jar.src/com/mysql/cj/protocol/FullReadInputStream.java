/*     */ package com.mysql.cj.protocol;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import java.io.EOFException;
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
/*     */ 
/*     */ 
/*     */ public class FullReadInputStream
/*     */   extends FilterInputStream
/*     */ {
/*     */   public FullReadInputStream(InputStream underlyingStream) {
/*  45 */     super(underlyingStream);
/*     */   }
/*     */   
/*     */   public InputStream getUnderlyingStream() {
/*  49 */     return this.in;
/*     */   }
/*     */   
/*     */   public int readFully(byte[] b) throws IOException {
/*  53 */     return readFully(b, 0, b.length);
/*     */   }
/*     */   
/*     */   public int readFully(byte[] b, int off, int len) throws IOException {
/*  57 */     if (len < 0) {
/*  58 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */     
/*  61 */     int n = 0;
/*     */     
/*  63 */     while (n < len) {
/*  64 */       int count = read(b, off + n, len - n);
/*     */       
/*  66 */       if (count < 0) {
/*  67 */         throw new EOFException(Messages.getString("MysqlIO.EOF", new Object[] { Integer.valueOf(len), Integer.valueOf(n) }));
/*     */       }
/*     */       
/*  70 */       n += count;
/*     */     } 
/*     */     
/*  73 */     return n;
/*     */   }
/*     */   
/*     */   public long skipFully(long len) throws IOException {
/*  77 */     if (len < 0L) {
/*  78 */       throw new IOException(Messages.getString("MysqlIO.105"));
/*     */     }
/*     */     
/*  81 */     long n = 0L;
/*     */     
/*  83 */     while (n < len) {
/*  84 */       long count = skip(len - n);
/*     */       
/*  86 */       if (count < 0L) {
/*  87 */         throw new EOFException(Messages.getString("MysqlIO.EOF", new Object[] { Long.valueOf(len), Long.valueOf(n) }));
/*     */       }
/*     */       
/*  90 */       n += count;
/*     */     } 
/*     */     
/*  93 */     return n;
/*     */   }
/*     */   
/*     */   public int skipLengthEncodedInteger() throws IOException {
/*  97 */     int sw = read() & 0xFF;
/*     */     
/*  99 */     switch (sw) {
/*     */       case 252:
/* 101 */         return (int)skipFully(2L) + 1;
/*     */       
/*     */       case 253:
/* 104 */         return (int)skipFully(3L) + 1;
/*     */       
/*     */       case 254:
/* 107 */         return (int)skipFully(8L) + 1;
/*     */     } 
/*     */     
/* 110 */     return 1;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\FullReadInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
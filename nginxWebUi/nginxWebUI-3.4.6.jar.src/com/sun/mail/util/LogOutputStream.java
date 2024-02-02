/*     */ package com.sun.mail.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.logging.Level;
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
/*     */ public class LogOutputStream
/*     */   extends OutputStream
/*     */ {
/*     */   protected MailLogger logger;
/*     */   protected Level level;
/*  54 */   private int lastb = -1;
/*  55 */   private byte[] buf = new byte[80];
/*  56 */   private int pos = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LogOutputStream(MailLogger logger) {
/*  62 */     this.logger = logger;
/*  63 */     this.level = Level.FINEST;
/*     */   }
/*     */   
/*     */   public void write(int b) throws IOException {
/*  67 */     if (!this.logger.isLoggable(this.level)) {
/*     */       return;
/*     */     }
/*  70 */     if (b == 13) {
/*  71 */       logBuf();
/*  72 */     } else if (b == 10) {
/*  73 */       if (this.lastb != 13)
/*  74 */         logBuf(); 
/*     */     } else {
/*  76 */       expandCapacity(1);
/*  77 */       this.buf[this.pos++] = (byte)b;
/*     */     } 
/*  79 */     this.lastb = b;
/*     */   }
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/*  83 */     write(b, 0, b.length);
/*     */   }
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/*  87 */     int start = off;
/*     */     
/*  89 */     if (!this.logger.isLoggable(this.level))
/*     */       return; 
/*  91 */     len += off;
/*  92 */     for (int i = start; i < len; i++) {
/*  93 */       if (b[i] == 13) {
/*  94 */         expandCapacity(i - start);
/*  95 */         System.arraycopy(b, start, this.buf, this.pos, i - start);
/*  96 */         this.pos += i - start;
/*  97 */         logBuf();
/*  98 */         start = i + 1;
/*  99 */       } else if (b[i] == 10) {
/* 100 */         if (this.lastb != 13) {
/* 101 */           expandCapacity(i - start);
/* 102 */           System.arraycopy(b, start, this.buf, this.pos, i - start);
/* 103 */           this.pos += i - start;
/* 104 */           logBuf();
/*     */         } 
/* 106 */         start = i + 1;
/*     */       } 
/* 108 */       this.lastb = b[i];
/*     */     } 
/* 110 */     if (len - start > 0) {
/* 111 */       expandCapacity(len - start);
/* 112 */       System.arraycopy(b, start, this.buf, this.pos, len - start);
/* 113 */       this.pos += len - start;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void log(String msg) {
/* 122 */     this.logger.log(this.level, msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void logBuf() {
/* 129 */     String msg = new String(this.buf, 0, this.pos);
/* 130 */     this.pos = 0;
/* 131 */     log(msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void expandCapacity(int len) {
/* 139 */     while (this.pos + len > this.buf.length) {
/* 140 */       byte[] nb = new byte[this.buf.length * 2];
/* 141 */       System.arraycopy(this.buf, 0, nb, 0, this.pos);
/* 142 */       this.buf = nb;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\LogOutputStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */
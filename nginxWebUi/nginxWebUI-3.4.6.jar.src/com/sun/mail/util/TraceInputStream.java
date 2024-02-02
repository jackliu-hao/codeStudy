/*     */ package com.sun.mail.util;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TraceInputStream
/*     */   extends FilterInputStream
/*     */ {
/*     */   private boolean trace = false;
/*     */   private boolean quote = false;
/*     */   private OutputStream traceOut;
/*     */   
/*     */   public TraceInputStream(InputStream in, MailLogger logger) {
/*  68 */     super(in);
/*  69 */     this.trace = logger.isLoggable(Level.FINEST);
/*  70 */     this.traceOut = new LogOutputStream(logger);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TraceInputStream(InputStream in, OutputStream traceOut) {
/*  81 */     super(in);
/*  82 */     this.traceOut = traceOut;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTrace(boolean trace) {
/*  90 */     this.trace = trace;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setQuote(boolean quote) {
/*  98 */     this.quote = quote;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 107 */     int b = this.in.read();
/* 108 */     if (this.trace && b != -1)
/* 109 */       if (this.quote) {
/* 110 */         writeByte(b);
/*     */       } else {
/* 112 */         this.traceOut.write(b);
/*     */       }  
/* 114 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 124 */     int count = this.in.read(b, off, len);
/* 125 */     if (this.trace && count != -1)
/* 126 */       if (this.quote) {
/* 127 */         for (int i = 0; i < count; i++)
/* 128 */           writeByte(b[off + i]); 
/*     */       } else {
/* 130 */         this.traceOut.write(b, off, count);
/*     */       }  
/* 132 */     return count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final void writeByte(int b) throws IOException {
/* 139 */     b &= 0xFF;
/* 140 */     if (b > 127) {
/* 141 */       this.traceOut.write(77);
/* 142 */       this.traceOut.write(45);
/* 143 */       b &= 0x7F;
/*     */     } 
/* 145 */     if (b == 13) {
/* 146 */       this.traceOut.write(92);
/* 147 */       this.traceOut.write(114);
/* 148 */     } else if (b == 10) {
/* 149 */       this.traceOut.write(92);
/* 150 */       this.traceOut.write(110);
/* 151 */       this.traceOut.write(10);
/* 152 */     } else if (b == 9) {
/* 153 */       this.traceOut.write(92);
/* 154 */       this.traceOut.write(116);
/* 155 */     } else if (b < 32) {
/* 156 */       this.traceOut.write(94);
/* 157 */       this.traceOut.write(64 + b);
/*     */     } else {
/* 159 */       this.traceOut.write(b);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\TraceInputStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */
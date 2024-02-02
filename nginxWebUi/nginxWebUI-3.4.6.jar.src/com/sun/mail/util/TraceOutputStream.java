/*     */ package com.sun.mail.util;
/*     */ 
/*     */ import java.io.FilterOutputStream;
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
/*     */ public class TraceOutputStream
/*     */   extends FilterOutputStream
/*     */ {
/*     */   private boolean trace = false;
/*     */   private boolean quote = false;
/*     */   private OutputStream traceOut;
/*     */   
/*     */   public TraceOutputStream(OutputStream out, MailLogger logger) {
/*  68 */     super(out);
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
/*     */   public TraceOutputStream(OutputStream out, OutputStream traceOut) {
/*  81 */     super(out);
/*  82 */     this.traceOut = traceOut;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTrace(boolean trace) {
/*  89 */     this.trace = trace;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setQuote(boolean quote) {
/*  97 */     this.quote = quote;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 106 */     if (this.trace)
/* 107 */       if (this.quote) {
/* 108 */         writeByte(b);
/*     */       } else {
/* 110 */         this.traceOut.write(b);
/*     */       }  
/* 112 */     this.out.write(b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 121 */     if (this.trace)
/* 122 */       if (this.quote) {
/* 123 */         for (int i = 0; i < len; i++)
/* 124 */           writeByte(b[off + i]); 
/*     */       } else {
/* 126 */         this.traceOut.write(b, off, len);
/*     */       }  
/* 128 */     this.out.write(b, off, len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final void writeByte(int b) throws IOException {
/* 135 */     b &= 0xFF;
/* 136 */     if (b > 127) {
/* 137 */       this.traceOut.write(77);
/* 138 */       this.traceOut.write(45);
/* 139 */       b &= 0x7F;
/*     */     } 
/* 141 */     if (b == 13) {
/* 142 */       this.traceOut.write(92);
/* 143 */       this.traceOut.write(114);
/* 144 */     } else if (b == 10) {
/* 145 */       this.traceOut.write(92);
/* 146 */       this.traceOut.write(110);
/* 147 */       this.traceOut.write(10);
/* 148 */     } else if (b == 9) {
/* 149 */       this.traceOut.write(92);
/* 150 */       this.traceOut.write(116);
/* 151 */     } else if (b < 32) {
/* 152 */       this.traceOut.write(94);
/* 153 */       this.traceOut.write(64 + b);
/*     */     } else {
/* 155 */       this.traceOut.write(b);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\TraceOutputStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package com.sun.mail.util;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PushbackInputStream;
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
/*     */ public class QPDecoderStream
/*     */   extends FilterInputStream
/*     */ {
/*  55 */   protected byte[] ba = new byte[2];
/*  56 */   protected int spaces = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QPDecoderStream(InputStream in) {
/*  64 */     super(new PushbackInputStream(in, 2));
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  80 */     if (this.spaces > 0) {
/*     */       
/*  82 */       this.spaces--;
/*  83 */       return 32;
/*     */     } 
/*     */     
/*  86 */     int c = this.in.read();
/*     */     
/*  88 */     if (c == 32) {
/*     */       
/*  90 */       while ((c = this.in.read()) == 32) {
/*  91 */         this.spaces++;
/*     */       }
/*  93 */       if (c == 13 || c == 10 || c == -1) {
/*     */ 
/*     */         
/*  96 */         this.spaces = 0;
/*     */       } else {
/*     */         
/*  99 */         ((PushbackInputStream)this.in).unread(c);
/* 100 */         c = 32;
/*     */       } 
/* 102 */       return c;
/*     */     } 
/* 104 */     if (c == 61) {
/*     */       
/* 106 */       int a = this.in.read();
/*     */       
/* 108 */       if (a == 10)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 114 */         return read(); } 
/* 115 */       if (a == 13) {
/*     */         
/* 117 */         int b = this.in.read();
/* 118 */         if (b != 10)
/*     */         {
/*     */ 
/*     */           
/* 122 */           ((PushbackInputStream)this.in).unread(b); } 
/* 123 */         return read();
/* 124 */       }  if (a == -1)
/*     */       {
/* 126 */         return -1;
/*     */       }
/* 128 */       this.ba[0] = (byte)a;
/* 129 */       this.ba[1] = (byte)this.in.read();
/*     */       try {
/* 131 */         return ASCIIUtility.parseInt(this.ba, 0, 2, 16);
/* 132 */       } catch (NumberFormatException nex) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 140 */         ((PushbackInputStream)this.in).unread(this.ba);
/* 141 */         return c;
/*     */       } 
/*     */     } 
/*     */     
/* 145 */     return c;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] buf, int off, int len) throws IOException {
/*     */     int i;
/* 164 */     for (i = 0; i < len; i++) {
/* 165 */       int c; if ((c = read()) == -1) {
/* 166 */         if (i == 0)
/* 167 */           i = -1; 
/*     */         break;
/*     */       } 
/* 170 */       buf[off + i] = (byte)c;
/*     */     } 
/* 172 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/* 179 */     long skipped = 0L;
/* 180 */     while (n-- > 0L && read() >= 0)
/* 181 */       skipped++; 
/* 182 */     return skipped;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean markSupported() {
/* 190 */     return false;
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
/*     */   
/*     */   public int available() throws IOException {
/* 203 */     return this.in.available();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\QPDecoderStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */
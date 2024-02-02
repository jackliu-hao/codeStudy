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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LineInputStream
/*     */   extends FilterInputStream
/*     */ {
/*  60 */   private char[] lineBuffer = null;
/*  61 */   private static int MAX_INCR = 1048576;
/*     */   
/*     */   public LineInputStream(InputStream in) {
/*  64 */     super(in);
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
/*     */   public String readLine() throws IOException {
/*  80 */     char[] buf = this.lineBuffer;
/*     */     
/*  82 */     if (buf == null) {
/*  83 */       buf = this.lineBuffer = new char[128];
/*     */     }
/*     */     
/*  86 */     int room = buf.length;
/*  87 */     int offset = 0;
/*     */     int c1;
/*  89 */     while ((c1 = this.in.read()) != -1 && 
/*  90 */       c1 != 10) {
/*     */       
/*  92 */       if (c1 == 13) {
/*     */         
/*  94 */         boolean twoCRs = false;
/*  95 */         if (this.in.markSupported())
/*  96 */           this.in.mark(2); 
/*  97 */         int c2 = this.in.read();
/*  98 */         if (c2 == 13) {
/*  99 */           twoCRs = true;
/* 100 */           c2 = this.in.read();
/*     */         } 
/* 102 */         if (c2 != 10) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 114 */           if (this.in.markSupported()) {
/* 115 */             this.in.reset(); break;
/*     */           } 
/* 117 */           if (!(this.in instanceof PushbackInputStream))
/* 118 */             this.in = new PushbackInputStream(this.in, 2); 
/* 119 */           if (c2 != -1)
/* 120 */             ((PushbackInputStream)this.in).unread(c2); 
/* 121 */           if (twoCRs) {
/* 122 */             ((PushbackInputStream)this.in).unread(13);
/*     */           }
/*     */         } 
/*     */ 
/*     */         
/*     */         break;
/*     */       } 
/*     */       
/* 130 */       if (--room < 0) {
/* 131 */         if (buf.length < MAX_INCR) {
/* 132 */           buf = new char[buf.length * 2];
/*     */         } else {
/* 134 */           buf = new char[buf.length + MAX_INCR];
/* 135 */         }  room = buf.length - offset - 1;
/* 136 */         System.arraycopy(this.lineBuffer, 0, buf, 0, offset);
/* 137 */         this.lineBuffer = buf;
/*     */       } 
/* 139 */       buf[offset++] = (char)c1;
/*     */     } 
/*     */     
/* 142 */     if (c1 == -1 && offset == 0) {
/* 143 */       return null;
/*     */     }
/* 145 */     return String.copyValueOf(buf, 0, offset);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\LineInputStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */
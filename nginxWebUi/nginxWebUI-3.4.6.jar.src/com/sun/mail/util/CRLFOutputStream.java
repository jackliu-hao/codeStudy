/*     */ package com.sun.mail.util;
/*     */ 
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CRLFOutputStream
/*     */   extends FilterOutputStream
/*     */ {
/*  53 */   protected int lastb = -1;
/*     */   protected boolean atBOL = true;
/*  55 */   private static final byte[] newline = new byte[] { 13, 10 };
/*     */   
/*     */   public CRLFOutputStream(OutputStream os) {
/*  58 */     super(os);
/*     */   }
/*     */   
/*     */   public void write(int b) throws IOException {
/*  62 */     if (b == 13) {
/*  63 */       writeln();
/*  64 */     } else if (b == 10) {
/*  65 */       if (this.lastb != 13)
/*  66 */         writeln(); 
/*     */     } else {
/*  68 */       this.out.write(b);
/*  69 */       this.atBOL = false;
/*     */     } 
/*  71 */     this.lastb = b;
/*     */   }
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/*  75 */     write(b, 0, b.length);
/*     */   }
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/*  79 */     int start = off;
/*     */     
/*  81 */     len += off;
/*  82 */     for (int i = start; i < len; i++) {
/*  83 */       if (b[i] == 13) {
/*  84 */         this.out.write(b, start, i - start);
/*  85 */         writeln();
/*  86 */         start = i + 1;
/*  87 */       } else if (b[i] == 10) {
/*  88 */         if (this.lastb != 13) {
/*  89 */           this.out.write(b, start, i - start);
/*  90 */           writeln();
/*     */         } 
/*  92 */         start = i + 1;
/*     */       } 
/*  94 */       this.lastb = b[i];
/*     */     } 
/*  96 */     if (len - start > 0) {
/*  97 */       this.out.write(b, start, len - start);
/*  98 */       this.atBOL = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeln() throws IOException {
/* 106 */     this.out.write(newline);
/* 107 */     this.atBOL = true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\CRLFOutputStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */
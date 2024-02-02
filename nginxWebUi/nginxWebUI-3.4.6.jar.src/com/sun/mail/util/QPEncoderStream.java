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
/*     */ 
/*     */ 
/*     */ public class QPEncoderStream
/*     */   extends FilterOutputStream
/*     */ {
/*  55 */   private int count = 0;
/*     */ 
/*     */   
/*     */   private int bytesPerLine;
/*     */ 
/*     */   
/*     */   private boolean gotSpace = false;
/*     */ 
/*     */   
/*     */   private boolean gotCR = false;
/*     */ 
/*     */   
/*     */   public QPEncoderStream(OutputStream out, int bytesPerLine) {
/*  68 */     super(out);
/*     */ 
/*     */     
/*  71 */     this.bytesPerLine = bytesPerLine - 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QPEncoderStream(OutputStream out) {
/*  80 */     this(out, 76);
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
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/*  94 */     for (int i = 0; i < len; i++) {
/*  95 */       write(b[off + i]);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/* 104 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(int c) throws IOException {
/* 113 */     c &= 0xFF;
/* 114 */     if (this.gotSpace) {
/* 115 */       if (c == 13 || c == 10) {
/*     */         
/* 117 */         output(32, true);
/*     */       } else {
/* 119 */         output(32, false);
/* 120 */       }  this.gotSpace = false;
/*     */     } 
/*     */     
/* 123 */     if (c == 13) {
/* 124 */       this.gotCR = true;
/* 125 */       outputCRLF();
/*     */     } else {
/* 127 */       if (c == 10) {
/* 128 */         if (!this.gotCR)
/*     */         {
/*     */ 
/*     */ 
/*     */           
/* 133 */           outputCRLF(); } 
/* 134 */       } else if (c == 32) {
/* 135 */         this.gotSpace = true;
/* 136 */       } else if (c < 32 || c >= 127 || c == 61) {
/*     */         
/* 138 */         output(c, true);
/*     */       } else {
/* 140 */         output(c, false);
/*     */       } 
/* 142 */       this.gotCR = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 152 */     this.out.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 160 */     if (this.gotSpace) {
/* 161 */       output(32, true);
/* 162 */       this.gotSpace = false;
/*     */     } 
/* 164 */     this.out.close();
/*     */   }
/*     */   
/*     */   private void outputCRLF() throws IOException {
/* 168 */     this.out.write(13);
/* 169 */     this.out.write(10);
/* 170 */     this.count = 0;
/*     */   }
/*     */ 
/*     */   
/* 174 */   private static final char[] hex = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void output(int c, boolean encode) throws IOException {
/* 180 */     if (encode) {
/* 181 */       if ((this.count += 3) > this.bytesPerLine) {
/* 182 */         this.out.write(61);
/* 183 */         this.out.write(13);
/* 184 */         this.out.write(10);
/* 185 */         this.count = 3;
/*     */       } 
/* 187 */       this.out.write(61);
/* 188 */       this.out.write(hex[c >> 4]);
/* 189 */       this.out.write(hex[c & 0xF]);
/*     */     } else {
/* 191 */       if (++this.count > this.bytesPerLine) {
/* 192 */         this.out.write(61);
/* 193 */         this.out.write(13);
/* 194 */         this.out.write(10);
/* 195 */         this.count = 1;
/*     */       } 
/* 197 */       this.out.write(c);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\QPEncoderStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */
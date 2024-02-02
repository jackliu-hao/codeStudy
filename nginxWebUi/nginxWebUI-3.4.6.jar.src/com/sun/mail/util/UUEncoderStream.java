/*     */ package com.sun.mail.util;
/*     */ 
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UUEncoderStream
/*     */   extends FilterOutputStream
/*     */ {
/*     */   private byte[] buffer;
/*  56 */   private int bufsize = 0;
/*     */ 
/*     */   
/*     */   private boolean wrotePrefix = false;
/*     */   
/*     */   protected String name;
/*     */   
/*     */   protected int mode;
/*     */ 
/*     */   
/*     */   public UUEncoderStream(OutputStream out) {
/*  67 */     this(out, "encoder.buf", 644);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UUEncoderStream(OutputStream out, String name) {
/*  76 */     this(out, name, 644);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UUEncoderStream(OutputStream out, String name, int mode) {
/*  86 */     super(out);
/*  87 */     this.name = name;
/*  88 */     this.mode = mode;
/*  89 */     this.buffer = new byte[45];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNameMode(String name, int mode) {
/*  98 */     this.name = name;
/*  99 */     this.mode = mode;
/*     */   }
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 103 */     for (int i = 0; i < len; i++)
/* 104 */       write(b[off + i]); 
/*     */   }
/*     */   
/*     */   public void write(byte[] data) throws IOException {
/* 108 */     write(data, 0, data.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(int c) throws IOException {
/* 116 */     this.buffer[this.bufsize++] = (byte)c;
/* 117 */     if (this.bufsize == 45) {
/* 118 */       writePrefix();
/* 119 */       encode();
/* 120 */       this.bufsize = 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void flush() throws IOException {
/* 125 */     if (this.bufsize > 0) {
/* 126 */       writePrefix();
/* 127 */       encode();
/*     */     } 
/* 129 */     writeSuffix();
/* 130 */     this.out.flush();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 134 */     flush();
/* 135 */     this.out.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writePrefix() throws IOException {
/* 142 */     if (!this.wrotePrefix) {
/*     */       
/* 144 */       PrintStream ps = new PrintStream(this.out, false, "utf-8");
/* 145 */       ps.println("begin " + this.mode + " " + this.name);
/* 146 */       ps.flush();
/* 147 */       this.wrotePrefix = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeSuffix() throws IOException {
/* 156 */     PrintStream ps = new PrintStream(this.out, false, "us-ascii");
/* 157 */     ps.println(" \nend");
/* 158 */     ps.flush();
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
/*     */   private void encode() throws IOException {
/* 173 */     int i = 0;
/*     */ 
/*     */     
/* 176 */     this.out.write((this.bufsize & 0x3F) + 32);
/*     */     
/* 178 */     while (i < this.bufsize) {
/* 179 */       byte b, c, a = this.buffer[i++];
/* 180 */       if (i < this.bufsize) {
/* 181 */         b = this.buffer[i++];
/* 182 */         if (i < this.bufsize) {
/* 183 */           c = this.buffer[i++];
/*     */         } else {
/* 185 */           c = 1;
/*     */         } 
/*     */       } else {
/* 188 */         b = 1;
/* 189 */         c = 1;
/*     */       } 
/*     */       
/* 192 */       int c1 = a >>> 2 & 0x3F;
/* 193 */       int c2 = a << 4 & 0x30 | b >>> 4 & 0xF;
/* 194 */       int c3 = b << 2 & 0x3C | c >>> 6 & 0x3;
/* 195 */       int c4 = c & 0x3F;
/* 196 */       this.out.write(c1 + 32);
/* 197 */       this.out.write(c2 + 32);
/* 198 */       this.out.write(c3 + 32);
/* 199 */       this.out.write(c4 + 32);
/*     */     } 
/*     */     
/* 202 */     this.out.write(10);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\UUEncoderStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */
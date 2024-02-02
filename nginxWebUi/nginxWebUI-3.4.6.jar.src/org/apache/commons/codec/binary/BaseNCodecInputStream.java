/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BaseNCodecInputStream
/*     */   extends FilterInputStream
/*     */ {
/*     */   private final BaseNCodec baseNCodec;
/*     */   private final boolean doEncode;
/*  40 */   private final byte[] singleByte = new byte[1];
/*     */   
/*  42 */   private final BaseNCodec.Context context = new BaseNCodec.Context();
/*     */   
/*     */   protected BaseNCodecInputStream(InputStream input, BaseNCodec baseNCodec, boolean doEncode) {
/*  45 */     super(input);
/*  46 */     this.doEncode = doEncode;
/*  47 */     this.baseNCodec = baseNCodec;
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
/*     */   public int available() throws IOException {
/*  64 */     return this.context.eof ? 0 : 1;
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
/*     */   public synchronized void mark(int readLimit) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean markSupported() {
/*  87 */     return false;
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
/*     */   public int read() throws IOException {
/*  99 */     int r = read(this.singleByte, 0, 1);
/* 100 */     while (r == 0) {
/* 101 */       r = read(this.singleByte, 0, 1);
/*     */     }
/* 103 */     if (r > 0) {
/* 104 */       byte b = this.singleByte[0];
/* 105 */       return (b < 0) ? (256 + b) : b;
/*     */     } 
/* 107 */     return -1;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] array, int offset, int len) throws IOException {
/* 131 */     Objects.requireNonNull(array, "array");
/* 132 */     if (offset < 0 || len < 0)
/* 133 */       throw new IndexOutOfBoundsException(); 
/* 134 */     if (offset > array.length || offset + len > array.length)
/* 135 */       throw new IndexOutOfBoundsException(); 
/* 136 */     if (len == 0) {
/* 137 */       return 0;
/*     */     }
/* 139 */     int readLen = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 156 */     while (readLen == 0) {
/* 157 */       if (!this.baseNCodec.hasData(this.context)) {
/* 158 */         byte[] buf = new byte[this.doEncode ? 4096 : 8192];
/* 159 */         int c = this.in.read(buf);
/* 160 */         if (this.doEncode) {
/* 161 */           this.baseNCodec.encode(buf, 0, c, this.context);
/*     */         } else {
/* 163 */           this.baseNCodec.decode(buf, 0, c, this.context);
/*     */         } 
/*     */       } 
/* 166 */       readLen = this.baseNCodec.readResults(array, offset, len, this.context);
/*     */     } 
/* 168 */     return readLen;
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
/*     */   public synchronized void reset() throws IOException {
/* 182 */     throw new IOException("mark/reset not supported");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/* 193 */     if (n < 0L) {
/* 194 */       throw new IllegalArgumentException("Negative skip length: " + n);
/*     */     }
/*     */ 
/*     */     
/* 198 */     byte[] b = new byte[512];
/* 199 */     long todo = n;
/*     */     
/* 201 */     while (todo > 0L) {
/* 202 */       int len = (int)Math.min(b.length, todo);
/* 203 */       len = read(b, 0, len);
/* 204 */       if (len == -1) {
/*     */         break;
/*     */       }
/* 207 */       todo -= len;
/*     */     } 
/*     */     
/* 210 */     return n - todo;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\binary\BaseNCodecInputStream.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
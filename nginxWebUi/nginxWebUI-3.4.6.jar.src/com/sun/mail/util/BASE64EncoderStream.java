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
/*     */ 
/*     */ public class BASE64EncoderStream
/*     */   extends FilterOutputStream
/*     */ {
/*     */   private byte[] buffer;
/*  57 */   private int bufsize = 0;
/*     */   private byte[] outbuf;
/*  59 */   private int count = 0;
/*     */   
/*     */   private int bytesPerLine;
/*     */   private int lineLimit;
/*     */   private boolean noCRLF = false;
/*  64 */   private static byte[] newline = new byte[] { 13, 10 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BASE64EncoderStream(OutputStream out, int bytesPerLine) {
/*  77 */     super(out);
/*  78 */     this.buffer = new byte[3];
/*  79 */     if (bytesPerLine == Integer.MAX_VALUE || bytesPerLine < 4) {
/*  80 */       this.noCRLF = true;
/*  81 */       bytesPerLine = 76;
/*     */     } 
/*  83 */     bytesPerLine = bytesPerLine / 4 * 4;
/*  84 */     this.bytesPerLine = bytesPerLine;
/*  85 */     this.lineLimit = bytesPerLine / 4 * 3;
/*     */     
/*  87 */     if (this.noCRLF) {
/*  88 */       this.outbuf = new byte[bytesPerLine];
/*     */     } else {
/*  90 */       this.outbuf = new byte[bytesPerLine + 2];
/*  91 */       this.outbuf[bytesPerLine] = 13;
/*  92 */       this.outbuf[bytesPerLine + 1] = 10;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BASE64EncoderStream(OutputStream out) {
/* 103 */     this(out, 76);
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
/*     */   public synchronized void write(byte[] b, int off, int len) throws IOException {
/* 118 */     int end = off + len;
/*     */ 
/*     */     
/* 121 */     while (this.bufsize != 0 && off < end) {
/* 122 */       write(b[off++]);
/*     */     }
/*     */     
/* 125 */     int blen = (this.bytesPerLine - this.count) / 4 * 3;
/* 126 */     if (off + blen <= end) {
/*     */       
/* 128 */       int outlen = encodedSize(blen);
/* 129 */       if (!this.noCRLF) {
/* 130 */         this.outbuf[outlen++] = 13;
/* 131 */         this.outbuf[outlen++] = 10;
/*     */       } 
/* 133 */       this.out.write(encode(b, off, blen, this.outbuf), 0, outlen);
/* 134 */       off += blen;
/* 135 */       this.count = 0;
/*     */     } 
/*     */ 
/*     */     
/* 139 */     for (; off + this.lineLimit <= end; off += this.lineLimit) {
/* 140 */       this.out.write(encode(b, off, this.lineLimit, this.outbuf));
/*     */     }
/*     */     
/* 143 */     if (off + 3 <= end) {
/* 144 */       blen = end - off;
/* 145 */       blen = blen / 3 * 3;
/*     */       
/* 147 */       int outlen = encodedSize(blen);
/* 148 */       this.out.write(encode(b, off, blen, this.outbuf), 0, outlen);
/* 149 */       off += blen;
/* 150 */       this.count += outlen;
/*     */     } 
/*     */ 
/*     */     
/* 154 */     for (; off < end; off++) {
/* 155 */       write(b[off]);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/* 165 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void write(int c) throws IOException {
/* 175 */     this.buffer[this.bufsize++] = (byte)c;
/* 176 */     if (this.bufsize == 3) {
/* 177 */       encode();
/* 178 */       this.bufsize = 0;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void flush() throws IOException {
/* 189 */     if (this.bufsize > 0) {
/* 190 */       encode();
/* 191 */       this.bufsize = 0;
/*     */     } 
/* 193 */     this.out.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void close() throws IOException {
/* 201 */     flush();
/* 202 */     if (this.count > 0 && !this.noCRLF) {
/* 203 */       this.out.write(newline);
/* 204 */       this.out.flush();
/*     */     } 
/* 206 */     this.out.close();
/*     */   }
/*     */ 
/*     */   
/* 210 */   private static final char[] pem_array = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/* 229 */     int osize = encodedSize(this.bufsize);
/* 230 */     this.out.write(encode(this.buffer, 0, this.bufsize, this.outbuf), 0, osize);
/*     */     
/* 232 */     this.count += osize;
/*     */ 
/*     */     
/* 235 */     if (this.count >= this.bytesPerLine) {
/* 236 */       if (!this.noCRLF)
/* 237 */         this.out.write(newline); 
/* 238 */       this.count = 0;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] encode(byte[] inbuf) {
/* 249 */     if (inbuf.length == 0)
/* 250 */       return inbuf; 
/* 251 */     return encode(inbuf, 0, inbuf.length, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static byte[] encode(byte[] inbuf, int off, int size, byte[] outbuf) {
/* 261 */     if (outbuf == null)
/* 262 */       outbuf = new byte[encodedSize(size)]; 
/*     */     int inpos;
/*     */     int outpos;
/* 265 */     for (inpos = off, outpos = 0; size >= 3; size -= 3, outpos += 4) {
/* 266 */       int val = inbuf[inpos++] & 0xFF;
/* 267 */       val <<= 8;
/* 268 */       val |= inbuf[inpos++] & 0xFF;
/* 269 */       val <<= 8;
/* 270 */       val |= inbuf[inpos++] & 0xFF;
/* 271 */       outbuf[outpos + 3] = (byte)pem_array[val & 0x3F];
/* 272 */       val >>= 6;
/* 273 */       outbuf[outpos + 2] = (byte)pem_array[val & 0x3F];
/* 274 */       val >>= 6;
/* 275 */       outbuf[outpos + 1] = (byte)pem_array[val & 0x3F];
/* 276 */       val >>= 6;
/* 277 */       outbuf[outpos + 0] = (byte)pem_array[val & 0x3F];
/*     */     } 
/*     */     
/* 280 */     if (size == 1) {
/* 281 */       int val = inbuf[inpos++] & 0xFF;
/* 282 */       val <<= 4;
/* 283 */       outbuf[outpos + 3] = 61;
/* 284 */       outbuf[outpos + 2] = 61;
/* 285 */       outbuf[outpos + 1] = (byte)pem_array[val & 0x3F];
/* 286 */       val >>= 6;
/* 287 */       outbuf[outpos + 0] = (byte)pem_array[val & 0x3F];
/* 288 */     } else if (size == 2) {
/* 289 */       int val = inbuf[inpos++] & 0xFF;
/* 290 */       val <<= 8;
/* 291 */       val |= inbuf[inpos++] & 0xFF;
/* 292 */       val <<= 2;
/* 293 */       outbuf[outpos + 3] = 61;
/* 294 */       outbuf[outpos + 2] = (byte)pem_array[val & 0x3F];
/* 295 */       val >>= 6;
/* 296 */       outbuf[outpos + 1] = (byte)pem_array[val & 0x3F];
/* 297 */       val >>= 6;
/* 298 */       outbuf[outpos + 0] = (byte)pem_array[val & 0x3F];
/*     */     } 
/* 300 */     return outbuf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int encodedSize(int size) {
/* 308 */     return (size + 2) / 3 * 4;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\BASE64EncoderStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */
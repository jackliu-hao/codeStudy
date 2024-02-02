/*     */ package org.apache.http.client.entity;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PushbackInputStream;
/*     */ import java.util.zip.Inflater;
/*     */ import java.util.zip.InflaterInputStream;
/*     */ import java.util.zip.ZipException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DeflateInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private final InputStream sourceStream;
/*     */   
/*     */   public DeflateInputStream(InputStream wrapped) throws IOException {
/*  46 */     PushbackInputStream pushback = new PushbackInputStream(wrapped, 2);
/*  47 */     int i1 = pushback.read();
/*  48 */     int i2 = pushback.read();
/*  49 */     if (i1 == -1 || i2 == -1) {
/*  50 */       throw new ZipException("Unexpected end of stream");
/*     */     }
/*     */     
/*  53 */     pushback.unread(i2);
/*  54 */     pushback.unread(i1);
/*     */     
/*  56 */     boolean nowrap = true;
/*  57 */     int b1 = i1 & 0xFF;
/*  58 */     int compressionMethod = b1 & 0xF;
/*  59 */     int compressionInfo = b1 >> 4 & 0xF;
/*  60 */     int b2 = i2 & 0xFF;
/*  61 */     if (compressionMethod == 8 && compressionInfo <= 7 && (b1 << 8 | b2) % 31 == 0) {
/*  62 */       nowrap = false;
/*     */     }
/*  64 */     this.sourceStream = new DeflateStream(pushback, new Inflater(nowrap));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  72 */     return this.sourceStream.read();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/*  80 */     return this.sourceStream.read(b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/*  88 */     return this.sourceStream.read(b, off, len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/*  96 */     return this.sourceStream.skip(n);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 104 */     return this.sourceStream.available();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void mark(int readLimit) {
/* 112 */     this.sourceStream.mark(readLimit);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() throws IOException {
/* 120 */     this.sourceStream.reset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean markSupported() {
/* 128 */     return this.sourceStream.markSupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 136 */     this.sourceStream.close();
/*     */   }
/*     */   
/*     */   static class DeflateStream
/*     */     extends InflaterInputStream {
/*     */     private boolean closed = false;
/*     */     
/*     */     public DeflateStream(InputStream in, Inflater inflater) {
/* 144 */       super(in, inflater);
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 149 */       if (this.closed) {
/*     */         return;
/*     */       }
/* 152 */       this.closed = true;
/* 153 */       this.inf.end();
/* 154 */       super.close();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\entity\DeflateInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */
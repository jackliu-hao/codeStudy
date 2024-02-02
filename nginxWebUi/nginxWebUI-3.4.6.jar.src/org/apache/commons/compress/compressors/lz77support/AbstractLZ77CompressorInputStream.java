/*     */ package org.apache.commons.compress.compressors.lz77support;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*     */ import org.apache.commons.compress.utils.ByteUtils;
/*     */ import org.apache.commons.compress.utils.CountingInputStream;
/*     */ import org.apache.commons.compress.utils.IOUtils;
/*     */ import org.apache.commons.compress.utils.InputStreamStatistics;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractLZ77CompressorInputStream
/*     */   extends CompressorInputStream
/*     */   implements InputStreamStatistics
/*     */ {
/*     */   private final int windowSize;
/*     */   private final byte[] buf;
/*     */   private int writeIndex;
/*     */   private int readIndex;
/*     */   private final CountingInputStream in;
/*     */   private long bytesRemaining;
/*     */   private int backReferenceOffset;
/*     */   private int size;
/* 112 */   private final byte[] oneByte = new byte[1];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 117 */   protected final ByteUtils.ByteSupplier supplier = this::readOneByte;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractLZ77CompressorInputStream(InputStream is, int windowSize) throws IOException {
/* 131 */     this.in = new CountingInputStream(is);
/* 132 */     if (windowSize <= 0) {
/* 133 */       throw new IllegalArgumentException("windowSize must be bigger than 0");
/*     */     }
/* 135 */     this.windowSize = windowSize;
/* 136 */     this.buf = new byte[3 * windowSize];
/* 137 */     this.writeIndex = this.readIndex = 0;
/* 138 */     this.bytesRemaining = 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 144 */     return (read(this.oneByte, 0, 1) == -1) ? -1 : (this.oneByte[0] & 0xFF);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 150 */     this.in.close();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int available() {
/* 156 */     return this.writeIndex - this.readIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSize() {
/* 165 */     return this.size;
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
/*     */   public void prefill(byte[] data) {
/* 180 */     if (this.writeIndex != 0) {
/* 181 */       throw new IllegalStateException("The stream has already been read from, can't prefill anymore");
/*     */     }
/*     */     
/* 184 */     int len = Math.min(this.windowSize, data.length);
/*     */     
/* 186 */     System.arraycopy(data, data.length - len, this.buf, 0, len);
/* 187 */     this.writeIndex += len;
/* 188 */     this.readIndex += len;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCompressedCount() {
/* 196 */     return this.in.getBytesRead();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void startLiteral(long length) {
/* 206 */     if (length < 0L) {
/* 207 */       throw new IllegalArgumentException("length must not be negative");
/*     */     }
/* 209 */     this.bytesRemaining = length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean hasMoreDataInBlock() {
/* 217 */     return (this.bytesRemaining > 0L);
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
/*     */   protected final int readLiteral(byte[] b, int off, int len) throws IOException {
/* 236 */     int avail = available();
/* 237 */     if (len > avail) {
/* 238 */       tryToReadLiteral(len - avail);
/*     */     }
/* 240 */     return readFromBuffer(b, off, len);
/*     */   }
/*     */ 
/*     */   
/*     */   private void tryToReadLiteral(int bytesToRead) throws IOException {
/* 245 */     int reallyTryToRead = Math.min((int)Math.min(bytesToRead, this.bytesRemaining), this.buf.length - this.writeIndex);
/*     */ 
/*     */     
/* 248 */     int bytesRead = (reallyTryToRead > 0) ? IOUtils.readFully((InputStream)this.in, this.buf, this.writeIndex, reallyTryToRead) : 0;
/*     */     
/* 250 */     count(bytesRead);
/* 251 */     if (reallyTryToRead != bytesRead) {
/* 252 */       throw new IOException("Premature end of stream reading literal");
/*     */     }
/* 254 */     this.writeIndex += reallyTryToRead;
/* 255 */     this.bytesRemaining -= reallyTryToRead;
/*     */   }
/*     */   
/*     */   private int readFromBuffer(byte[] b, int off, int len) {
/* 259 */     int readable = Math.min(len, available());
/* 260 */     if (readable > 0) {
/* 261 */       System.arraycopy(this.buf, this.readIndex, b, off, readable);
/* 262 */       this.readIndex += readable;
/* 263 */       if (this.readIndex > 2 * this.windowSize) {
/* 264 */         slideBuffer();
/*     */       }
/*     */     } 
/* 267 */     this.size += readable;
/* 268 */     return readable;
/*     */   }
/*     */   
/*     */   private void slideBuffer() {
/* 272 */     System.arraycopy(this.buf, this.windowSize, this.buf, 0, this.windowSize * 2);
/* 273 */     this.writeIndex -= this.windowSize;
/* 274 */     this.readIndex -= this.windowSize;
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
/*     */   protected final void startBackReference(int offset, long length) {
/* 286 */     if (offset <= 0 || offset > this.writeIndex) {
/* 287 */       throw new IllegalArgumentException("offset must be bigger than 0 but not bigger than the number of bytes available for back-references");
/*     */     }
/*     */     
/* 290 */     if (length < 0L) {
/* 291 */       throw new IllegalArgumentException("length must not be negative");
/*     */     }
/* 293 */     this.backReferenceOffset = offset;
/* 294 */     this.bytesRemaining = length;
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
/*     */   protected final int readBackReference(byte[] b, int off, int len) {
/* 310 */     int avail = available();
/* 311 */     if (len > avail) {
/* 312 */       tryToCopy(len - avail);
/*     */     }
/* 314 */     return readFromBuffer(b, off, len);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void tryToCopy(int bytesToCopy) {
/* 320 */     int copy = Math.min((int)Math.min(bytesToCopy, this.bytesRemaining), this.buf.length - this.writeIndex);
/*     */     
/* 322 */     if (copy != 0)
/*     */     {
/* 324 */       if (this.backReferenceOffset == 1) {
/* 325 */         byte last = this.buf[this.writeIndex - 1];
/* 326 */         Arrays.fill(this.buf, this.writeIndex, this.writeIndex + copy, last);
/* 327 */         this.writeIndex += copy;
/* 328 */       } else if (copy < this.backReferenceOffset) {
/* 329 */         System.arraycopy(this.buf, this.writeIndex - this.backReferenceOffset, this.buf, this.writeIndex, copy);
/* 330 */         this.writeIndex += copy;
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 335 */         int fullRots = copy / this.backReferenceOffset;
/* 336 */         for (int i = 0; i < fullRots; i++) {
/* 337 */           System.arraycopy(this.buf, this.writeIndex - this.backReferenceOffset, this.buf, this.writeIndex, this.backReferenceOffset);
/* 338 */           this.writeIndex += this.backReferenceOffset;
/*     */         } 
/*     */         
/* 341 */         int pad = copy - this.backReferenceOffset * fullRots;
/* 342 */         if (pad > 0) {
/* 343 */           System.arraycopy(this.buf, this.writeIndex - this.backReferenceOffset, this.buf, this.writeIndex, pad);
/* 344 */           this.writeIndex += pad;
/*     */         } 
/*     */       }  } 
/* 347 */     this.bytesRemaining -= copy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int readOneByte() throws IOException {
/* 357 */     int b = this.in.read();
/* 358 */     if (b != -1) {
/* 359 */       count(1);
/* 360 */       return b & 0xFF;
/*     */     } 
/* 362 */     return -1;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\lz77support\AbstractLZ77CompressorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package com.github.jaiimageio.stream;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.DoubleBuffer;
/*     */ import java.nio.FloatBuffer;
/*     */ import java.nio.IntBuffer;
/*     */ import java.nio.LongBuffer;
/*     */ import java.nio.MappedByteBuffer;
/*     */ import java.nio.ShortBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import javax.imageio.stream.ImageInputStreamImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FileChannelImageInputStream
/*     */   extends ImageInputStreamImpl
/*     */ {
/*     */   private FileChannel channel;
/*     */   private MappedByteBuffer mappedBuffer;
/*     */   private long mappedPos;
/*     */   private long mappedUpperBound;
/*     */   
/*     */   public FileChannelImageInputStream(FileChannel channel) throws IOException {
/* 112 */     if (channel == null)
/* 113 */       throw new IllegalArgumentException("channel == null"); 
/* 114 */     if (!channel.isOpen()) {
/* 115 */       throw new IllegalArgumentException("channel.isOpen() == false");
/*     */     }
/*     */ 
/*     */     
/* 119 */     this.channel = channel;
/*     */ 
/*     */     
/* 122 */     long channelPosition = channel.position();
/*     */ 
/*     */     
/* 125 */     this.streamPos = this.flushedPos = channelPosition;
/*     */ 
/*     */     
/* 128 */     long fullSize = channel.size() - channelPosition;
/* 129 */     long mappedSize = Math.min(fullSize, 2147483647L);
/*     */ 
/*     */     
/* 132 */     this.mappedPos = 0L;
/* 133 */     this.mappedUpperBound = this.mappedPos + mappedSize;
/*     */ 
/*     */     
/* 136 */     this.mappedBuffer = channel.map(FileChannel.MapMode.READ_ONLY, channelPosition, mappedSize);
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
/*     */   private MappedByteBuffer getMappedBuffer(int len) throws IOException {
/* 152 */     if (this.streamPos < this.mappedPos || this.streamPos + len >= this.mappedUpperBound) {
/*     */ 
/*     */       
/* 155 */       this.mappedPos = this.streamPos;
/*     */ 
/*     */       
/* 158 */       long mappedSize = Math.min(this.channel.size() - this.mappedPos, 2147483647L);
/*     */ 
/*     */ 
/*     */       
/* 162 */       this.mappedUpperBound = this.mappedPos + mappedSize;
/*     */ 
/*     */       
/* 165 */       this.mappedBuffer = this.channel.map(FileChannel.MapMode.READ_ONLY, this.mappedPos, mappedSize);
/*     */ 
/*     */ 
/*     */       
/* 169 */       this.mappedBuffer.order(getByteOrder());
/*     */     } 
/*     */ 
/*     */     
/* 173 */     return this.mappedBuffer;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 179 */     checkClosed();
/* 180 */     this.bitOffset = 0;
/*     */ 
/*     */     
/* 183 */     ByteBuffer byteBuffer = getMappedBuffer(1);
/*     */ 
/*     */     
/* 186 */     if (byteBuffer.remaining() < 1)
/*     */     {
/* 188 */       return -1;
/*     */     }
/*     */ 
/*     */     
/* 192 */     int value = byteBuffer.get() & 0xFF;
/*     */ 
/*     */     
/* 195 */     this.streamPos++;
/*     */ 
/*     */ 
/*     */     
/* 199 */     return value;
/*     */   }
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 203 */     if (off < 0 || len < 0 || off + len > b.length)
/*     */     {
/* 205 */       throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > b.length");
/*     */     }
/* 207 */     if (len == 0) {
/* 208 */       return 0;
/*     */     }
/*     */     
/* 211 */     checkClosed();
/* 212 */     this.bitOffset = 0;
/*     */ 
/*     */     
/* 215 */     ByteBuffer byteBuffer = getMappedBuffer(len);
/*     */ 
/*     */     
/* 218 */     int numBytesRemaining = byteBuffer.remaining();
/*     */ 
/*     */     
/* 221 */     if (numBytesRemaining < 1)
/*     */     {
/* 223 */       return -1; } 
/* 224 */     if (len > numBytesRemaining)
/*     */     {
/*     */       
/* 227 */       len = numBytesRemaining;
/*     */     }
/*     */ 
/*     */     
/* 231 */     byteBuffer.get(b, off, len);
/*     */ 
/*     */     
/* 234 */     this.streamPos += len;
/*     */     
/* 236 */     return len;
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
/*     */   public void close() throws IOException {
/* 249 */     super.close();
/* 250 */     this.channel = null;
/*     */   }
/*     */   
/*     */   public void readFully(char[] c, int off, int len) throws IOException {
/* 254 */     if (off < 0 || len < 0 || off + len > c.length)
/*     */     {
/* 256 */       throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length");
/*     */     }
/* 258 */     if (len == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 263 */     int byteLen = 2 * len;
/*     */ 
/*     */     
/* 266 */     ByteBuffer byteBuffer = getMappedBuffer(byteLen);
/*     */ 
/*     */     
/* 269 */     if (byteBuffer.remaining() < byteLen) {
/* 270 */       throw new EOFException();
/*     */     }
/*     */ 
/*     */     
/* 274 */     CharBuffer viewBuffer = byteBuffer.asCharBuffer();
/*     */ 
/*     */     
/* 277 */     viewBuffer.get(c, off, len);
/*     */ 
/*     */     
/* 280 */     seek(this.streamPos + byteLen);
/*     */   }
/*     */   
/*     */   public void readFully(short[] s, int off, int len) throws IOException {
/* 284 */     if (off < 0 || len < 0 || off + len > s.length)
/*     */     {
/* 286 */       throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > s.length");
/*     */     }
/* 288 */     if (len == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 293 */     int byteLen = 2 * len;
/*     */ 
/*     */     
/* 296 */     ByteBuffer byteBuffer = getMappedBuffer(byteLen);
/*     */ 
/*     */     
/* 299 */     if (byteBuffer.remaining() < byteLen) {
/* 300 */       throw new EOFException();
/*     */     }
/*     */ 
/*     */     
/* 304 */     ShortBuffer viewBuffer = byteBuffer.asShortBuffer();
/*     */ 
/*     */     
/* 307 */     viewBuffer.get(s, off, len);
/*     */ 
/*     */     
/* 310 */     seek(this.streamPos + byteLen);
/*     */   }
/*     */   
/*     */   public void readFully(int[] i, int off, int len) throws IOException {
/* 314 */     if (off < 0 || len < 0 || off + len > i.length)
/*     */     {
/* 316 */       throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > i.length");
/*     */     }
/* 318 */     if (len == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 323 */     int byteLen = 4 * len;
/*     */ 
/*     */     
/* 326 */     ByteBuffer byteBuffer = getMappedBuffer(byteLen);
/*     */ 
/*     */     
/* 329 */     if (byteBuffer.remaining() < byteLen) {
/* 330 */       throw new EOFException();
/*     */     }
/*     */ 
/*     */     
/* 334 */     IntBuffer viewBuffer = byteBuffer.asIntBuffer();
/*     */ 
/*     */     
/* 337 */     viewBuffer.get(i, off, len);
/*     */ 
/*     */     
/* 340 */     seek(this.streamPos + byteLen);
/*     */   }
/*     */   
/*     */   public void readFully(long[] l, int off, int len) throws IOException {
/* 344 */     if (off < 0 || len < 0 || off + len > l.length)
/*     */     {
/* 346 */       throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > l.length");
/*     */     }
/* 348 */     if (len == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 353 */     int byteLen = 8 * len;
/*     */ 
/*     */     
/* 356 */     ByteBuffer byteBuffer = getMappedBuffer(byteLen);
/*     */ 
/*     */     
/* 359 */     if (byteBuffer.remaining() < byteLen) {
/* 360 */       throw new EOFException();
/*     */     }
/*     */ 
/*     */     
/* 364 */     LongBuffer viewBuffer = byteBuffer.asLongBuffer();
/*     */ 
/*     */     
/* 367 */     viewBuffer.get(l, off, len);
/*     */ 
/*     */     
/* 370 */     seek(this.streamPos + byteLen);
/*     */   }
/*     */   
/*     */   public void readFully(float[] f, int off, int len) throws IOException {
/* 374 */     if (off < 0 || len < 0 || off + len > f.length)
/*     */     {
/* 376 */       throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > f.length");
/*     */     }
/* 378 */     if (len == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 383 */     int byteLen = 4 * len;
/*     */ 
/*     */     
/* 386 */     ByteBuffer byteBuffer = getMappedBuffer(byteLen);
/*     */ 
/*     */     
/* 389 */     if (byteBuffer.remaining() < byteLen) {
/* 390 */       throw new EOFException();
/*     */     }
/*     */ 
/*     */     
/* 394 */     FloatBuffer viewBuffer = byteBuffer.asFloatBuffer();
/*     */ 
/*     */     
/* 397 */     viewBuffer.get(f, off, len);
/*     */ 
/*     */     
/* 400 */     seek(this.streamPos + byteLen);
/*     */   }
/*     */   
/*     */   public void readFully(double[] d, int off, int len) throws IOException {
/* 404 */     if (off < 0 || len < 0 || off + len > d.length)
/*     */     {
/* 406 */       throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > d.length");
/*     */     }
/* 408 */     if (len == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 413 */     int byteLen = 8 * len;
/*     */ 
/*     */     
/* 416 */     ByteBuffer byteBuffer = getMappedBuffer(byteLen);
/*     */ 
/*     */     
/* 419 */     if (byteBuffer.remaining() < byteLen) {
/* 420 */       throw new EOFException();
/*     */     }
/*     */ 
/*     */     
/* 424 */     DoubleBuffer viewBuffer = byteBuffer.asDoubleBuffer();
/*     */ 
/*     */     
/* 427 */     viewBuffer.get(d, off, len);
/*     */ 
/*     */     
/* 430 */     seek(this.streamPos + byteLen);
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
/*     */   public long length() {
/* 443 */     long length = -1L;
/*     */ 
/*     */     
/*     */     try {
/* 447 */       length = this.channel.size();
/* 448 */     } catch (IOException iOException) {}
/*     */ 
/*     */ 
/*     */     
/* 452 */     return length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void seek(long pos) throws IOException {
/* 462 */     super.seek(pos);
/*     */     
/* 464 */     if (pos >= this.mappedPos && pos < this.mappedUpperBound) {
/*     */       
/* 466 */       this.mappedBuffer.position((int)(pos - this.mappedPos));
/*     */     }
/*     */     else {
/*     */       
/* 470 */       int len = (int)Math.min(this.channel.size() - pos, 2147483647L);
/* 471 */       this.mappedBuffer = getMappedBuffer(len);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setByteOrder(ByteOrder networkByteOrder) {
/* 476 */     super.setByteOrder(networkByteOrder);
/* 477 */     this.mappedBuffer.order(networkByteOrder);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\stream\FileChannelImageInputStream.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */
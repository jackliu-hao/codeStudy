/*     */ package com.github.jaiimageio.stream;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.DoubleBuffer;
/*     */ import java.nio.FloatBuffer;
/*     */ import java.nio.IntBuffer;
/*     */ import java.nio.LongBuffer;
/*     */ import java.nio.ShortBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import javax.imageio.stream.ImageInputStream;
/*     */ import javax.imageio.stream.ImageOutputStreamImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FileChannelImageOutputStream
/*     */   extends ImageOutputStreamImpl
/*     */ {
/*     */   private static final int DEFAULT_WRITE_BUFFER_SIZE = 1048576;
/*     */   private FileChannel channel;
/*     */   private ByteBuffer byteBuffer;
/*  92 */   private ImageInputStream readStream = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileChannelImageOutputStream(FileChannel channel) throws IOException {
/* 192 */     if (channel == null)
/* 193 */       throw new IllegalArgumentException("channel == null"); 
/* 194 */     if (!channel.isOpen()) {
/* 195 */       throw new IllegalArgumentException("channel.isOpen() == false");
/*     */     }
/*     */ 
/*     */     
/* 199 */     this.channel = channel;
/*     */ 
/*     */     
/* 202 */     this.streamPos = this.flushedPos = channel.position();
/*     */ 
/*     */     
/* 205 */     this.byteBuffer = ByteBuffer.allocateDirect(1048576);
/*     */ 
/*     */     
/* 208 */     this.readStream = new FileChannelImageInputStream(channel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ImageInputStream getImageInputStream() throws IOException {
/* 218 */     flushBuffer();
/*     */ 
/*     */     
/* 221 */     this.readStream.setByteOrder(this.byteOrder);
/* 222 */     this.readStream.seek(this.streamPos);
/* 223 */     this.readStream.flushBefore(this.flushedPos);
/* 224 */     this.readStream.setBitOffset(this.bitOffset);
/*     */     
/* 226 */     return this.readStream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void flushBuffer() throws IOException {
/* 234 */     if (this.byteBuffer.position() != 0) {
/*     */       
/* 236 */       this.byteBuffer.limit(this.byteBuffer.position());
/*     */ 
/*     */       
/* 239 */       this.byteBuffer.position(0);
/*     */ 
/*     */       
/* 242 */       this.channel.write(this.byteBuffer);
/*     */ 
/*     */       
/* 245 */       this.byteBuffer.clear();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 252 */     checkClosed();
/* 253 */     this.bitOffset = 0;
/*     */     
/* 255 */     ImageInputStream inputStream = getImageInputStream();
/*     */     
/* 257 */     this.streamPos++;
/*     */     
/* 259 */     return inputStream.read();
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 264 */     if (off < 0 || len < 0 || off + len > b.length) {
/* 265 */       throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > b.length");
/*     */     }
/* 267 */     if (len == 0) {
/* 268 */       return 0;
/*     */     }
/*     */     
/* 271 */     checkClosed();
/* 272 */     this.bitOffset = 0;
/*     */     
/* 274 */     ImageInputStream inputStream = getImageInputStream();
/*     */     
/* 276 */     int numBytesRead = inputStream.read(b, off, len);
/*     */     
/* 278 */     this.streamPos += numBytesRead;
/*     */     
/* 280 */     return numBytesRead;
/*     */   }
/*     */   
/*     */   public void write(int b) throws IOException {
/* 284 */     write(new byte[] { (byte)(b & 0xFF) }, 0, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 290 */     if (off < 0 || len < 0 || off + len > b.length)
/*     */     {
/* 292 */       throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > b.length");
/*     */     }
/* 294 */     if (len == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 299 */     flushBits();
/*     */ 
/*     */     
/* 302 */     int numPut = 0;
/*     */ 
/*     */ 
/*     */     
/*     */     do {
/* 307 */       int numToPut = Math.min(len - numPut, this.byteBuffer
/* 308 */           .remaining());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 313 */       if (numToPut == 0)
/* 314 */       { flushBuffer();
/*     */          }
/*     */       
/*     */       else
/*     */       
/* 319 */       { this.byteBuffer.put(b, off + numPut, numToPut);
/*     */ 
/*     */         
/* 322 */         numPut += numToPut; } 
/* 323 */     } while (numPut < len);
/*     */ 
/*     */     
/* 326 */     this.streamPos += len;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void readFully(char[] c, int off, int len) throws IOException {
/* 334 */     getImageInputStream().readFully(c, off, len);
/* 335 */     this.streamPos += (2 * len);
/*     */   }
/*     */   
/*     */   public void readFully(short[] s, int off, int len) throws IOException {
/* 339 */     getImageInputStream().readFully(s, off, len);
/* 340 */     this.streamPos += (2 * len);
/*     */   }
/*     */   
/*     */   public void readFully(int[] i, int off, int len) throws IOException {
/* 344 */     getImageInputStream().readFully(i, off, len);
/* 345 */     this.streamPos += (4 * len);
/*     */   }
/*     */   
/*     */   public void readFully(long[] l, int off, int len) throws IOException {
/* 349 */     getImageInputStream().readFully(l, off, len);
/* 350 */     this.streamPos += (8 * len);
/*     */   }
/*     */   
/*     */   public void readFully(float[] f, int off, int len) throws IOException {
/* 354 */     getImageInputStream().readFully(f, off, len);
/* 355 */     this.streamPos += (4 * len);
/*     */   }
/*     */   
/*     */   public void readFully(double[] d, int off, int len) throws IOException {
/* 359 */     getImageInputStream().readFully(d, off, len);
/* 360 */     this.streamPos += (8 * len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeChars(char[] c, int off, int len) throws IOException {
/* 368 */     if (off < 0 || len < 0 || off + len > c.length)
/*     */     {
/* 370 */       throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length");
/*     */     }
/* 372 */     if (len == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 377 */     flushBits();
/*     */ 
/*     */     
/* 380 */     int numPut = 0;
/*     */ 
/*     */     
/* 383 */     CharBuffer viewBuffer = this.byteBuffer.asCharBuffer();
/*     */ 
/*     */ 
/*     */     
/*     */     do {
/* 388 */       int numToPut = Math.min(len - numPut, viewBuffer
/* 389 */           .remaining());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 394 */       if (numToPut == 0)
/* 395 */       { flushBuffer();
/*     */          }
/*     */       
/*     */       else
/*     */       
/* 400 */       { viewBuffer.put(c, off + numPut, numToPut);
/*     */ 
/*     */         
/* 403 */         this.byteBuffer.position(this.byteBuffer.position() + 2 * numToPut);
/*     */ 
/*     */         
/* 406 */         numPut += numToPut; } 
/* 407 */     } while (numPut < len);
/*     */ 
/*     */     
/* 410 */     this.streamPos += (2 * len);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeShorts(short[] s, int off, int len) throws IOException {
/* 416 */     if (off < 0 || len < 0 || off + len > s.length)
/*     */     {
/* 418 */       throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length");
/*     */     }
/* 420 */     if (len == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 425 */     flushBits();
/*     */ 
/*     */     
/* 428 */     int numPut = 0;
/*     */ 
/*     */     
/* 431 */     ShortBuffer viewBuffer = this.byteBuffer.asShortBuffer();
/*     */ 
/*     */ 
/*     */     
/*     */     do {
/* 436 */       int numToPut = Math.min(len - numPut, viewBuffer
/* 437 */           .remaining());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 442 */       if (numToPut == 0)
/* 443 */       { flushBuffer();
/*     */          }
/*     */       
/*     */       else
/*     */       
/* 448 */       { viewBuffer.put(s, off + numPut, numToPut);
/*     */ 
/*     */         
/* 451 */         this.byteBuffer.position(this.byteBuffer.position() + 2 * numToPut);
/*     */ 
/*     */         
/* 454 */         numPut += numToPut; } 
/* 455 */     } while (numPut < len);
/*     */ 
/*     */     
/* 458 */     this.streamPos += (2 * len);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeInts(int[] i, int off, int len) throws IOException {
/* 464 */     if (off < 0 || len < 0 || off + len > i.length)
/*     */     {
/* 466 */       throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length");
/*     */     }
/* 468 */     if (len == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 473 */     flushBits();
/*     */ 
/*     */     
/* 476 */     int numPut = 0;
/*     */ 
/*     */     
/* 479 */     IntBuffer viewBuffer = this.byteBuffer.asIntBuffer();
/*     */ 
/*     */ 
/*     */     
/*     */     do {
/* 484 */       int numToPut = Math.min(len - numPut, viewBuffer
/* 485 */           .remaining());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 490 */       if (numToPut == 0)
/* 491 */       { flushBuffer();
/*     */          }
/*     */       
/*     */       else
/*     */       
/* 496 */       { viewBuffer.put(i, off + numPut, numToPut);
/*     */ 
/*     */         
/* 499 */         this.byteBuffer.position(this.byteBuffer.position() + 4 * numToPut);
/*     */ 
/*     */         
/* 502 */         numPut += numToPut; } 
/* 503 */     } while (numPut < len);
/*     */ 
/*     */     
/* 506 */     this.streamPos += (4 * len);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeLongs(long[] l, int off, int len) throws IOException {
/* 512 */     if (off < 0 || len < 0 || off + len > l.length)
/*     */     {
/* 514 */       throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length");
/*     */     }
/* 516 */     if (len == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 521 */     flushBits();
/*     */ 
/*     */     
/* 524 */     int numPut = 0;
/*     */ 
/*     */     
/* 527 */     LongBuffer viewBuffer = this.byteBuffer.asLongBuffer();
/*     */ 
/*     */ 
/*     */     
/*     */     do {
/* 532 */       int numToPut = Math.min(len - numPut, viewBuffer
/* 533 */           .remaining());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 538 */       if (numToPut == 0)
/* 539 */       { flushBuffer();
/*     */          }
/*     */       
/*     */       else
/*     */       
/* 544 */       { viewBuffer.put(l, off + numPut, numToPut);
/*     */ 
/*     */         
/* 547 */         this.byteBuffer.position(this.byteBuffer.position() + 8 * numToPut);
/*     */ 
/*     */         
/* 550 */         numPut += numToPut; } 
/* 551 */     } while (numPut < len);
/*     */ 
/*     */     
/* 554 */     this.streamPos += (8 * len);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeFloats(float[] f, int off, int len) throws IOException {
/* 560 */     if (off < 0 || len < 0 || off + len > f.length)
/*     */     {
/* 562 */       throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > f.length");
/*     */     }
/* 564 */     if (len == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 569 */     flushBits();
/*     */ 
/*     */     
/* 572 */     int numPut = 0;
/*     */ 
/*     */     
/* 575 */     FloatBuffer viewBuffer = this.byteBuffer.asFloatBuffer();
/*     */ 
/*     */ 
/*     */     
/*     */     do {
/* 580 */       int numToPut = Math.min(len - numPut, viewBuffer
/* 581 */           .remaining());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 586 */       if (numToPut == 0)
/* 587 */       { flushBuffer();
/*     */          }
/*     */       
/*     */       else
/*     */       
/* 592 */       { viewBuffer.put(f, off + numPut, numToPut);
/*     */ 
/*     */         
/* 595 */         this.byteBuffer.position(this.byteBuffer.position() + 4 * numToPut);
/*     */ 
/*     */         
/* 598 */         numPut += numToPut; } 
/* 599 */     } while (numPut < len);
/*     */ 
/*     */     
/* 602 */     this.streamPos += (4 * len);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeDoubles(double[] d, int off, int len) throws IOException {
/* 608 */     if (off < 0 || len < 0 || off + len > d.length)
/*     */     {
/* 610 */       throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > d.length");
/*     */     }
/* 612 */     if (len == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 617 */     flushBits();
/*     */ 
/*     */     
/* 620 */     int numPut = 0;
/*     */ 
/*     */     
/* 623 */     DoubleBuffer viewBuffer = this.byteBuffer.asDoubleBuffer();
/*     */ 
/*     */ 
/*     */     
/*     */     do {
/* 628 */       int numToPut = Math.min(len - numPut, viewBuffer
/* 629 */           .remaining());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 634 */       if (numToPut == 0)
/* 635 */       { flushBuffer();
/*     */          }
/*     */       
/*     */       else
/*     */       
/* 640 */       { viewBuffer.put(d, off + numPut, numToPut);
/*     */ 
/*     */         
/* 643 */         this.byteBuffer.position(this.byteBuffer.position() + 8 * numToPut);
/*     */ 
/*     */         
/* 646 */         numPut += numToPut; } 
/* 647 */     } while (numPut < len);
/*     */ 
/*     */     
/* 650 */     this.streamPos += (8 * len);
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
/*     */   public void close() throws IOException {
/* 667 */     flushBuffer();
/*     */ 
/*     */     
/* 670 */     this.readStream.close();
/* 671 */     this.readStream = null;
/*     */ 
/*     */     
/* 674 */     this.channel = null;
/*     */ 
/*     */     
/* 677 */     this.byteBuffer = null;
/*     */ 
/*     */     
/* 680 */     super.close();
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
/* 693 */     long length = -1L;
/*     */ 
/*     */     
/*     */     try {
/* 697 */       length = this.channel.size();
/* 698 */     } catch (IOException iOException) {}
/*     */ 
/*     */ 
/*     */     
/* 702 */     return length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void seek(long pos) throws IOException {
/* 710 */     super.seek(pos);
/*     */ 
/*     */     
/* 713 */     flushBuffer();
/*     */ 
/*     */     
/* 716 */     this.channel.position(pos);
/*     */   }
/*     */   
/*     */   public void setByteOrder(ByteOrder networkByteOrder) {
/* 720 */     super.setByteOrder(networkByteOrder);
/* 721 */     this.byteBuffer.order(networkByteOrder);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\stream\FileChannelImageOutputStream.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */
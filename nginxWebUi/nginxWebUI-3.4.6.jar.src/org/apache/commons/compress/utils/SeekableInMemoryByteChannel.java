/*     */ package org.apache.commons.compress.utils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.util.Arrays;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SeekableInMemoryByteChannel
/*     */   implements SeekableByteChannel
/*     */ {
/*     */   private static final int NAIVE_RESIZE_LIMIT = 1073741823;
/*     */   private byte[] data;
/*  44 */   private final AtomicBoolean closed = new AtomicBoolean();
/*     */ 
/*     */ 
/*     */   
/*     */   private int position;
/*     */ 
/*     */   
/*     */   private int size;
/*     */ 
/*     */ 
/*     */   
/*     */   public SeekableInMemoryByteChannel(byte[] data) {
/*  56 */     this.data = data;
/*  57 */     this.size = data.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SeekableInMemoryByteChannel() {
/*  64 */     this(ByteUtils.EMPTY_BYTE_ARRAY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SeekableInMemoryByteChannel(int size) {
/*  75 */     this(new byte[size]);
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
/*     */   public long position() {
/*  87 */     return this.position;
/*     */   }
/*     */ 
/*     */   
/*     */   public SeekableByteChannel position(long newPosition) throws IOException {
/*  92 */     ensureOpen();
/*  93 */     if (newPosition < 0L || newPosition > 2147483647L) {
/*  94 */       throw new IOException("Position has to be in range 0.. 2147483647");
/*     */     }
/*  96 */     this.position = (int)newPosition;
/*  97 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long size() {
/* 108 */     return this.size;
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
/*     */   public SeekableByteChannel truncate(long newSize) {
/* 121 */     if (newSize < 0L || newSize > 2147483647L) {
/* 122 */       throw new IllegalArgumentException("Size has to be in range 0.. 2147483647");
/*     */     }
/* 124 */     if (this.size > newSize) {
/* 125 */       this.size = (int)newSize;
/*     */     }
/* 127 */     if (this.position > newSize) {
/* 128 */       this.position = (int)newSize;
/*     */     }
/* 130 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer buf) throws IOException {
/* 135 */     ensureOpen();
/* 136 */     int wanted = buf.remaining();
/* 137 */     int possible = this.size - this.position;
/* 138 */     if (possible <= 0) {
/* 139 */       return -1;
/*     */     }
/* 141 */     if (wanted > possible) {
/* 142 */       wanted = possible;
/*     */     }
/* 144 */     buf.put(this.data, this.position, wanted);
/* 145 */     this.position += wanted;
/* 146 */     return wanted;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 151 */     this.closed.set(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 156 */     return !this.closed.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer b) throws IOException {
/* 161 */     ensureOpen();
/* 162 */     int wanted = b.remaining();
/* 163 */     int possibleWithoutResize = this.size - this.position;
/* 164 */     if (wanted > possibleWithoutResize) {
/* 165 */       int newSize = this.position + wanted;
/* 166 */       if (newSize < 0) {
/* 167 */         resize(2147483647);
/* 168 */         wanted = Integer.MAX_VALUE - this.position;
/*     */       } else {
/* 170 */         resize(newSize);
/*     */       } 
/*     */     } 
/* 173 */     b.get(this.data, this.position, wanted);
/* 174 */     this.position += wanted;
/* 175 */     if (this.size < this.position) {
/* 176 */       this.size = this.position;
/*     */     }
/* 178 */     return wanted;
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
/*     */   public byte[] array() {
/* 191 */     return this.data;
/*     */   }
/*     */   
/*     */   private void resize(int newLength) {
/* 195 */     int len = this.data.length;
/* 196 */     if (len <= 0) {
/* 197 */       len = 1;
/*     */     }
/* 199 */     if (newLength < 1073741823) {
/* 200 */       while (len < newLength) {
/* 201 */         len <<= 1;
/*     */       }
/*     */     } else {
/* 204 */       len = newLength;
/*     */     } 
/* 206 */     this.data = Arrays.copyOf(this.data, len);
/*     */   }
/*     */   
/*     */   private void ensureOpen() throws ClosedChannelException {
/* 210 */     if (!isOpen())
/* 211 */       throw new ClosedChannelException(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\SeekableInMemoryByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
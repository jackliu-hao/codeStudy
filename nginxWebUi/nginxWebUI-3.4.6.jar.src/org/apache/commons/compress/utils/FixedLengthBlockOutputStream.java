/*     */ package org.apache.commons.compress.utils;
/*     */ 
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.WritableByteChannel;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FixedLengthBlockOutputStream
/*     */   extends OutputStream
/*     */   implements WritableByteChannel
/*     */ {
/*     */   private final WritableByteChannel out;
/*     */   private final int blockSize;
/*     */   private final ByteBuffer buffer;
/*  55 */   private final AtomicBoolean closed = new AtomicBoolean(false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FixedLengthBlockOutputStream(OutputStream os, int blockSize) {
/*  63 */     if (os instanceof FileOutputStream) {
/*  64 */       FileOutputStream fileOutputStream = (FileOutputStream)os;
/*  65 */       this.out = fileOutputStream.getChannel();
/*  66 */       this.buffer = ByteBuffer.allocateDirect(blockSize);
/*     */     } else {
/*  68 */       this.out = new BufferAtATimeOutputChannel(os);
/*  69 */       this.buffer = ByteBuffer.allocate(blockSize);
/*     */     } 
/*  71 */     this.blockSize = blockSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FixedLengthBlockOutputStream(WritableByteChannel out, int blockSize) {
/*  79 */     this.out = out;
/*  80 */     this.blockSize = blockSize;
/*  81 */     this.buffer = ByteBuffer.allocateDirect(blockSize);
/*     */   }
/*     */   
/*     */   private void maybeFlush() throws IOException {
/*  85 */     if (!this.buffer.hasRemaining()) {
/*  86 */       writeBlock();
/*     */     }
/*     */   }
/*     */   
/*     */   private void writeBlock() throws IOException {
/*  91 */     this.buffer.flip();
/*  92 */     int i = this.out.write(this.buffer);
/*  93 */     boolean hasRemaining = this.buffer.hasRemaining();
/*  94 */     if (i != this.blockSize || hasRemaining) {
/*     */       
/*  96 */       String msg = String.format("Failed to write %,d bytes atomically. Only wrote  %,d", new Object[] {
/*  97 */             Integer.valueOf(this.blockSize), Integer.valueOf(i) });
/*  98 */       throw new IOException(msg);
/*     */     } 
/* 100 */     this.buffer.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 105 */     if (!isOpen()) {
/* 106 */       throw new ClosedChannelException();
/*     */     }
/* 108 */     this.buffer.put((byte)b);
/* 109 */     maybeFlush();
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int offset, int length) throws IOException {
/* 114 */     if (!isOpen()) {
/* 115 */       throw new ClosedChannelException();
/*     */     }
/* 117 */     int off = offset;
/* 118 */     int len = length;
/* 119 */     while (len > 0) {
/* 120 */       int n = Math.min(len, this.buffer.remaining());
/* 121 */       this.buffer.put(b, off, n);
/* 122 */       maybeFlush();
/* 123 */       len -= n;
/* 124 */       off += n;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 130 */     if (!isOpen()) {
/* 131 */       throw new ClosedChannelException();
/*     */     }
/* 133 */     int srcRemaining = src.remaining();
/*     */     
/* 135 */     if (srcRemaining < this.buffer.remaining()) {
/*     */       
/* 137 */       this.buffer.put(src);
/*     */     } else {
/* 139 */       int srcLeft = srcRemaining;
/* 140 */       int savedLimit = src.limit();
/*     */ 
/*     */       
/* 143 */       if (this.buffer.position() != 0) {
/* 144 */         int n = this.buffer.remaining();
/* 145 */         src.limit(src.position() + n);
/* 146 */         this.buffer.put(src);
/* 147 */         writeBlock();
/* 148 */         srcLeft -= n;
/*     */       } 
/*     */ 
/*     */       
/* 152 */       while (srcLeft >= this.blockSize) {
/* 153 */         src.limit(src.position() + this.blockSize);
/* 154 */         this.out.write(src);
/* 155 */         srcLeft -= this.blockSize;
/*     */       } 
/*     */       
/* 158 */       src.limit(savedLimit);
/* 159 */       this.buffer.put(src);
/*     */     } 
/* 161 */     return srcRemaining;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 166 */     if (!this.out.isOpen()) {
/* 167 */       this.closed.set(true);
/*     */     }
/* 169 */     return !this.closed.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flushBlock() throws IOException {
/* 177 */     if (this.buffer.position() != 0) {
/* 178 */       padBlock();
/* 179 */       writeBlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 185 */     if (this.closed.compareAndSet(false, true)) {
/*     */       try {
/* 187 */         flushBlock();
/*     */       } finally {
/* 189 */         this.out.close();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void padBlock() {
/* 195 */     this.buffer.order(ByteOrder.nativeOrder());
/* 196 */     int bytesToWrite = this.buffer.remaining();
/* 197 */     if (bytesToWrite > 8) {
/* 198 */       int align = this.buffer.position() & 0x7;
/* 199 */       if (align != 0) {
/* 200 */         int limit = 8 - align;
/* 201 */         for (int i = 0; i < limit; i++) {
/* 202 */           this.buffer.put((byte)0);
/*     */         }
/* 204 */         bytesToWrite -= limit;
/*     */       } 
/*     */       
/* 207 */       while (bytesToWrite >= 8) {
/* 208 */         this.buffer.putLong(0L);
/* 209 */         bytesToWrite -= 8;
/*     */       } 
/*     */     } 
/* 212 */     while (this.buffer.hasRemaining()) {
/* 213 */       this.buffer.put((byte)0);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class BufferAtATimeOutputChannel
/*     */     implements WritableByteChannel
/*     */   {
/*     */     private final OutputStream out;
/*     */ 
/*     */     
/* 226 */     private final AtomicBoolean closed = new AtomicBoolean(false);
/*     */     
/*     */     private BufferAtATimeOutputChannel(OutputStream out) {
/* 229 */       this.out = out;
/*     */     }
/*     */ 
/*     */     
/*     */     public int write(ByteBuffer buffer) throws IOException {
/* 234 */       if (!isOpen()) {
/* 235 */         throw new ClosedChannelException();
/*     */       }
/* 237 */       if (!buffer.hasArray()) {
/* 238 */         throw new IOException("Direct buffer somehow written to BufferAtATimeOutputChannel");
/*     */       }
/*     */       
/*     */       try {
/* 242 */         int pos = buffer.position();
/* 243 */         int len = buffer.limit() - pos;
/* 244 */         this.out.write(buffer.array(), buffer.arrayOffset() + pos, len);
/* 245 */         buffer.position(buffer.limit());
/* 246 */         return len;
/* 247 */       } catch (IOException e) {
/*     */         try {
/* 249 */           close();
/* 250 */         } catch (IOException iOException) {}
/*     */         
/* 252 */         throw e;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isOpen() {
/* 258 */       return !this.closed.get();
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 263 */       if (this.closed.compareAndSet(false, true))
/* 264 */         this.out.close(); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\FixedLengthBlockOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
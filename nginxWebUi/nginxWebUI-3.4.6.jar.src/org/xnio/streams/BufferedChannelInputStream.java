/*     */ package org.xnio.streams;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio._private.Messages;
/*     */ import org.xnio.channels.Channels;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BufferedChannelInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private final StreamSourceChannel channel;
/*     */   private final ByteBuffer buffer;
/*     */   private volatile int flags;
/*     */   private volatile long timeout;
/*  50 */   private static final AtomicIntegerFieldUpdater<BufferedChannelInputStream> flagsUpdater = AtomicIntegerFieldUpdater.newUpdater(BufferedChannelInputStream.class, "flags");
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int FLAG_EOF = 2;
/*     */ 
/*     */   
/*     */   private static final int FLAG_ENTERED = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedChannelInputStream(StreamSourceChannel channel, int bufferSize) {
/*  62 */     if (channel == null) {
/*  63 */       throw Messages.msg.nullParameter("channel");
/*     */     }
/*  65 */     if (bufferSize < 1) {
/*  66 */       throw Messages.msg.parameterOutOfRange("bufferSize");
/*     */     }
/*  68 */     this.channel = channel;
/*  69 */     this.buffer = ByteBuffer.allocate(bufferSize);
/*  70 */     this.buffer.limit(0);
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
/*     */   public BufferedChannelInputStream(StreamSourceChannel channel, int bufferSize, long timeout, TimeUnit unit) {
/*  82 */     if (channel == null) {
/*  83 */       throw Messages.msg.nullParameter("channel");
/*     */     }
/*  85 */     if (unit == null) {
/*  86 */       throw Messages.msg.nullParameter("unit");
/*     */     }
/*  88 */     if (bufferSize < 1) {
/*  89 */       throw Messages.msg.parameterOutOfRange("bufferSize");
/*     */     }
/*  91 */     if (timeout < 0L) {
/*  92 */       throw Messages.msg.parameterOutOfRange("timeout");
/*     */     }
/*  94 */     this.channel = channel;
/*  95 */     this.buffer = ByteBuffer.allocate(bufferSize);
/*  96 */     this.buffer.limit(0);
/*  97 */     long calcTimeout = unit.toNanos(timeout);
/*  98 */     this.timeout = (timeout == 0L) ? 0L : ((calcTimeout < 1L) ? 1L : calcTimeout);
/*     */   }
/*     */   
/*     */   private boolean enter() {
/* 102 */     int old = this.flags;
/*     */     while (true) {
/* 104 */       if (Bits.allAreSet(old, 1)) {
/* 105 */         throw Messages.msg.concurrentAccess();
/*     */       }
/* 107 */       if (flagsUpdater.compareAndSet(this, old, old | 0x1))
/* 108 */         return Bits.allAreSet(old, 2); 
/*     */     } 
/*     */   } private void exit(boolean setEof) {
/*     */     int oldFlags;
/*     */     int newFlags;
/*     */     do {
/* 114 */       oldFlags = this.flags;
/* 115 */       newFlags = oldFlags & 0xFFFFFFFE;
/* 116 */       if (!setEof)
/* 117 */         continue;  newFlags |= 0x2;
/*     */     }
/* 119 */     while (!flagsUpdater.compareAndSet(this, oldFlags, newFlags));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getReadTimeout(TimeUnit unit) {
/* 129 */     if (unit == null) {
/* 130 */       throw Messages.msg.nullParameter("unit");
/*     */     }
/* 132 */     return unit.convert(this.timeout, TimeUnit.NANOSECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReadTimeout(long timeout, TimeUnit unit) {
/* 142 */     if (timeout < 0L) {
/* 143 */       throw Messages.msg.parameterOutOfRange("timeout");
/*     */     }
/* 145 */     if (unit == null) {
/* 146 */       throw Messages.msg.nullParameter("unit");
/*     */     }
/* 148 */     long calcTimeout = unit.toNanos(timeout);
/* 149 */     this.timeout = (timeout == 0L) ? 0L : ((calcTimeout < 1L) ? 1L : calcTimeout);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 159 */     boolean eof = enter();
/*     */     try {
/* 161 */       StreamSourceChannel channel = this.channel;
/* 162 */       ByteBuffer buffer = this.buffer;
/*     */       
/* 164 */       if (buffer.hasRemaining()) {
/* 165 */         return buffer.get() & 0xFF;
/*     */       }
/* 167 */       if (eof) {
/* 168 */         return -1;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 173 */       long start = System.nanoTime();
/* 174 */       long elapsed = 0L; while (true) {
/*     */         int res;
/* 176 */         buffer.clear();
/*     */         try {
/* 178 */           res = channel.read(buffer);
/*     */         } finally {
/* 180 */           buffer.flip();
/*     */         } 
/* 182 */         if (res == -1) {
/* 183 */           eof = true;
/* 184 */           return -1;
/*     */         } 
/* 186 */         if (res > 0) {
/* 187 */           return buffer.get() & 0xFF;
/*     */         }
/* 189 */         long timeout = this.timeout;
/* 190 */         if (timeout == 0L)
/* 191 */         { channel.awaitReadable(); }
/* 192 */         else { if (timeout < elapsed) {
/* 193 */             throw Messages.msg.readTimeout();
/*     */           }
/* 195 */           channel.awaitReadable(timeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */         
/* 197 */         elapsed = System.nanoTime() - start;
/*     */       } 
/*     */     } finally {
/* 200 */       exit(eof);
/*     */     } 
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
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 214 */     if (len < 1) {
/* 215 */       return 0;
/*     */     }
/* 217 */     boolean eof = enter();
/*     */     try {
/* 219 */       int total = 0;
/*     */       
/* 221 */       ByteBuffer buffer = this.buffer;
/* 222 */       ByteBuffer userBuffer = ByteBuffer.wrap(b, off, len);
/* 223 */       if (buffer.hasRemaining()) {
/* 224 */         total += Buffers.copy(userBuffer, buffer);
/*     */         
/* 226 */         if (!userBuffer.hasRemaining()) {
/* 227 */           return total;
/*     */         }
/*     */       } 
/*     */       
/* 231 */       assert !buffer.hasRemaining();
/* 232 */       assert userBuffer.hasRemaining();
/* 233 */       if (eof) return (total == 0) ? -1 : total;
/*     */       
/* 235 */       StreamSourceChannel channel = this.channel;
/*     */       
/* 237 */       long start = System.nanoTime();
/* 238 */       long elapsed = 0L;
/*     */       
/*     */       while (true) {
/* 241 */         int res = channel.read(userBuffer);
/* 242 */         if (res == -1) {
/* 243 */           eof = true;
/* 244 */           return (total == 0) ? -1 : total;
/*     */         } 
/* 246 */         total += res;
/* 247 */         if (total > 0) {
/* 248 */           return total;
/*     */         }
/* 250 */         long timeout = this.timeout;
/*     */         try {
/* 252 */           if (timeout == 0L)
/* 253 */           { channel.awaitReadable(); }
/* 254 */           else { if (timeout < elapsed) {
/* 255 */               throw Messages.msg.readTimeout();
/*     */             }
/* 257 */             channel.awaitReadable(timeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */         
/* 259 */         } catch (InterruptedIOException e) {
/* 260 */           e.bytesTransferred = total;
/* 261 */           throw e;
/*     */         } 
/* 263 */         elapsed = System.nanoTime() - start;
/*     */       } 
/*     */     } finally {
/* 266 */       exit(eof);
/*     */     } 
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
/* 278 */     if (n < 1L) {
/* 279 */       return 0L;
/*     */     }
/* 281 */     boolean eof = enter();
/*     */     
/*     */     try {
/* 284 */       n = Math.min(n, 2147483647L);
/* 285 */       long total = 0L;
/* 286 */       ByteBuffer buffer = this.buffer;
/* 287 */       if (buffer.hasRemaining()) {
/* 288 */         int cnt = (int)Math.min(buffer.remaining(), n);
/* 289 */         Buffers.skip(buffer, cnt);
/* 290 */         total += cnt;
/* 291 */         n -= cnt;
/* 292 */         assert n == 0L || !buffer.hasRemaining();
/* 293 */         if (n == 0L) {
/* 294 */           return total;
/*     */         }
/*     */       } 
/* 297 */       assert !buffer.hasRemaining();
/* 298 */       if (eof) {
/* 299 */         return total;
/*     */       }
/*     */       
/* 302 */       long start = System.nanoTime();
/* 303 */       long elapsed = 0L;
/*     */       
/*     */       while (true) {
/* 306 */         if (n == 0L) return total; 
/* 307 */         long res = Channels.drain(this.channel, n);
/* 308 */         if (res == -1L)
/* 309 */           return total; 
/* 310 */         if (res == 0L) {
/* 311 */           long timeout = this.timeout;
/*     */           try {
/* 313 */             if (timeout == 0L)
/* 314 */             { this.channel.awaitReadable(); }
/* 315 */             else { if (timeout < elapsed) {
/* 316 */                 throw Messages.msg.readTimeout();
/*     */               }
/* 318 */               this.channel.awaitReadable(timeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */           
/* 320 */           } catch (InterruptedIOException e) {
/* 321 */             assert total < 2147483647L;
/* 322 */             e.bytesTransferred = (int)total;
/* 323 */             throw e;
/*     */           } 
/* 325 */           elapsed = System.nanoTime() - start; continue;
/*     */         } 
/* 327 */         total += res;
/* 328 */         n -= res;
/*     */       } 
/*     */     } finally {
/*     */       
/* 332 */       exit(eof);
/*     */     } 
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
/*     */   public int available() throws IOException {
/* 345 */     boolean eof = enter();
/*     */     try {
/* 347 */       ByteBuffer buffer = this.buffer;
/* 348 */       int rem = buffer.remaining();
/* 349 */       if (rem > 0 || eof) {
/* 350 */         return rem;
/*     */       }
/* 352 */       buffer.clear();
/*     */       try {
/* 354 */         this.channel.read(buffer);
/* 355 */       } catch (IOException e) {
/* 356 */         throw e;
/*     */       } finally {
/* 358 */         buffer.flip();
/*     */       } 
/* 360 */       return buffer.remaining();
/*     */     } finally {
/* 362 */       exit(eof);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 372 */     enter();
/*     */     try {
/* 374 */       this.buffer.clear().flip();
/* 375 */       this.channel.shutdownReads();
/*     */     } finally {
/* 377 */       exit(true);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\streams\BufferedChannelInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package org.xnio.channels;
/*     */ 
/*     */ import java.io.Flushable;
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ByteChannel;
/*     */ import java.nio.channels.GatheringByteChannel;
/*     */ import java.nio.channels.ScatteringByteChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio._private.Messages;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BlockingByteChannel
/*     */   implements ScatteringByteChannel, GatheringByteChannel, ByteChannel, Flushable
/*     */ {
/*     */   private final StreamChannel delegate;
/*     */   private volatile long readTimeout;
/*     */   private volatile long writeTimeout;
/*     */   
/*     */   public BlockingByteChannel(StreamChannel delegate) {
/*  49 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BlockingByteChannel(StreamChannel delegate, long timeout, TimeUnit timeoutUnit) {
/*  60 */     this(delegate, timeout, timeoutUnit, timeout, timeoutUnit);
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
/*     */   public BlockingByteChannel(StreamChannel delegate, long readTimeout, TimeUnit readTimeoutUnit, long writeTimeout, TimeUnit writeTimeoutUnit) {
/*  73 */     if (readTimeout < 0L) {
/*  74 */       throw Messages.msg.parameterOutOfRange("readTimeout");
/*     */     }
/*  76 */     if (writeTimeout < 0L) {
/*  77 */       throw Messages.msg.parameterOutOfRange("writeTimeout");
/*     */     }
/*  79 */     long calcReadTimeout = readTimeoutUnit.toNanos(readTimeout);
/*  80 */     this.readTimeout = (readTimeout == 0L) ? 0L : ((calcReadTimeout < 1L) ? 1L : calcReadTimeout);
/*  81 */     long calcWriteTimeout = writeTimeoutUnit.toNanos(writeTimeout);
/*  82 */     this.writeTimeout = (writeTimeout == 0L) ? 0L : ((calcWriteTimeout < 1L) ? 1L : calcWriteTimeout);
/*  83 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReadTimeout(long readTimeout, TimeUnit readTimeoutUnit) {
/*  93 */     if (readTimeout < 0L) {
/*  94 */       throw Messages.msg.parameterOutOfRange("readTimeout");
/*     */     }
/*  96 */     long calcTimeout = readTimeoutUnit.toNanos(readTimeout);
/*  97 */     this.readTimeout = (readTimeout == 0L) ? 0L : ((calcTimeout < 1L) ? 1L : calcTimeout);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWriteTimeout(long writeTimeout, TimeUnit writeTimeoutUnit) {
/* 107 */     if (writeTimeout < 0L) {
/* 108 */       throw Messages.msg.parameterOutOfRange("writeTimeout");
/*     */     }
/* 110 */     long calcTimeout = writeTimeoutUnit.toNanos(writeTimeout);
/* 111 */     this.writeTimeout = (writeTimeout == 0L) ? 0L : ((calcTimeout < 1L) ? 1L : calcTimeout);
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
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 124 */     if (!Buffers.hasRemaining((Buffer[])dsts, offset, length)) {
/* 125 */       return 0L;
/*     */     }
/* 127 */     StreamSourceChannel delegate = this.delegate;
/*     */     long res;
/* 129 */     if ((res = delegate.read(dsts, offset, length)) == 0L) {
/* 130 */       long start = System.nanoTime();
/* 131 */       long elapsed = 0L;
/*     */       do {
/* 133 */         long readTimeout = this.readTimeout;
/* 134 */         if (readTimeout == 0L || readTimeout == Long.MAX_VALUE)
/* 135 */         { delegate.awaitReadable(); }
/* 136 */         else { if (readTimeout <= elapsed) {
/* 137 */             throw Messages.msg.readTimeout();
/*     */           }
/* 139 */           delegate.awaitReadable(readTimeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */         
/* 141 */         elapsed = System.nanoTime() - start;
/* 142 */       } while ((res = delegate.read(dsts, offset, length)) == 0L);
/*     */     } 
/* 144 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts) throws IOException {
/* 155 */     return read(dsts, 0, dsts.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/* 166 */     if (!dst.hasRemaining()) {
/* 167 */       return 0;
/*     */     }
/* 169 */     StreamSourceChannel delegate = this.delegate;
/*     */     int res;
/* 171 */     if ((res = delegate.read(dst)) == 0) {
/* 172 */       long start = System.nanoTime();
/* 173 */       long elapsed = 0L;
/*     */       do {
/* 175 */         long readTimeout = this.readTimeout;
/* 176 */         if (readTimeout == 0L || readTimeout == Long.MAX_VALUE)
/* 177 */         { delegate.awaitReadable(); }
/* 178 */         else { if (readTimeout <= elapsed) {
/* 179 */             throw Messages.msg.readTimeout();
/*     */           }
/* 181 */           delegate.awaitReadable(readTimeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */         
/* 183 */         elapsed = System.nanoTime() - start;
/* 184 */       } while ((res = delegate.read(dst)) == 0);
/*     */     } 
/* 186 */     return res;
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
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 199 */     if (!Buffers.hasRemaining((Buffer[])srcs, offset, length)) {
/* 200 */       return 0L;
/*     */     }
/* 202 */     StreamSinkChannel delegate = this.delegate;
/*     */     long res;
/* 204 */     if ((res = delegate.write(srcs, offset, length)) == 0L) {
/* 205 */       long start = System.nanoTime();
/* 206 */       long elapsed = 0L;
/*     */       do {
/* 208 */         long writeTimeout = this.writeTimeout;
/* 209 */         if (writeTimeout == 0L || writeTimeout == Long.MAX_VALUE)
/* 210 */         { delegate.awaitWritable(); }
/* 211 */         else { if (writeTimeout <= elapsed) {
/* 212 */             throw Messages.msg.writeTimeout();
/*     */           }
/* 214 */           delegate.awaitWritable(writeTimeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */         
/* 216 */         elapsed = System.nanoTime() - start;
/* 217 */       } while ((res = delegate.write(srcs, offset, length)) == 0L);
/*     */     } 
/* 219 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs) throws IOException {
/* 230 */     return write(srcs, 0, srcs.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 241 */     if (!src.hasRemaining()) {
/* 242 */       return 0;
/*     */     }
/* 244 */     StreamSinkChannel delegate = this.delegate;
/*     */     int res;
/* 246 */     if ((res = delegate.write(src)) == 0L) {
/* 247 */       long start = System.nanoTime();
/* 248 */       long elapsed = 0L;
/*     */       do {
/* 250 */         long writeTimeout = this.writeTimeout;
/* 251 */         if (writeTimeout == 0L || writeTimeout == Long.MAX_VALUE)
/* 252 */         { delegate.awaitWritable(); }
/* 253 */         else { if (writeTimeout <= elapsed) {
/* 254 */             throw Messages.msg.writeTimeout();
/*     */           }
/* 256 */           delegate.awaitWritable(writeTimeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */         
/* 258 */         elapsed = System.nanoTime() - start;
/* 259 */       } while ((res = delegate.write(src)) == 0L);
/*     */     } 
/* 261 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 266 */     return this.delegate.isOpen();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 271 */     StreamSinkChannel delegate = this.delegate;
/* 272 */     if (!delegate.flush()) {
/* 273 */       long start = System.nanoTime();
/* 274 */       long elapsed = 0L;
/*     */       do {
/* 276 */         long writeTimeout = this.writeTimeout;
/* 277 */         if (writeTimeout == 0L || writeTimeout == Long.MAX_VALUE)
/* 278 */         { delegate.awaitWritable(); }
/* 279 */         else { if (writeTimeout <= elapsed) {
/* 280 */             throw Messages.msg.writeTimeout();
/*     */           }
/* 282 */           delegate.awaitWritable(writeTimeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */         
/* 284 */         elapsed = System.nanoTime() - start;
/* 285 */       } while (!delegate.flush());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 291 */     this.delegate.close();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\BlockingByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package org.xnio.channels;
/*     */ 
/*     */ import java.io.Flushable;
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.GatheringByteChannel;
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
/*     */ public class BlockingWritableByteChannel
/*     */   implements GatheringByteChannel, Flushable
/*     */ {
/*     */   private final StreamSinkChannel delegate;
/*     */   private volatile long writeTimeout;
/*     */   
/*     */   public BlockingWritableByteChannel(StreamSinkChannel delegate) {
/*  45 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BlockingWritableByteChannel(StreamSinkChannel delegate, long writeTimeout, TimeUnit writeTimeoutUnit) {
/*  56 */     if (writeTimeout < 0L) {
/*  57 */       throw Messages.msg.parameterOutOfRange("writeTimeout");
/*     */     }
/*  59 */     this.delegate = delegate;
/*  60 */     long calcTimeout = writeTimeoutUnit.toNanos(writeTimeout);
/*  61 */     this.writeTimeout = (writeTimeout == 0L) ? 0L : ((calcTimeout < 1L) ? 1L : calcTimeout);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWriteTimeout(long writeTimeout, TimeUnit writeTimeoutUnit) {
/*  71 */     if (writeTimeout < 0L) {
/*  72 */       throw Messages.msg.parameterOutOfRange("writeTimeout");
/*     */     }
/*  74 */     long calcTimeout = writeTimeoutUnit.toNanos(writeTimeout);
/*  75 */     this.writeTimeout = (writeTimeout == 0L) ? 0L : ((calcTimeout < 1L) ? 1L : calcTimeout);
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
/*  88 */     if (!Buffers.hasRemaining((Buffer[])srcs, offset, length)) {
/*  89 */       return 0L;
/*     */     }
/*  91 */     StreamSinkChannel delegate = this.delegate;
/*     */     long res;
/*  93 */     if ((res = delegate.write(srcs, offset, length)) == 0L) {
/*  94 */       long start = System.nanoTime();
/*  95 */       long elapsed = 0L;
/*     */       do {
/*  97 */         long writeTimeout = this.writeTimeout;
/*  98 */         if (writeTimeout == 0L || writeTimeout == Long.MAX_VALUE)
/*  99 */         { delegate.awaitWritable(); }
/* 100 */         else { if (writeTimeout <= elapsed) {
/* 101 */             throw Messages.msg.writeTimeout();
/*     */           }
/* 103 */           delegate.awaitWritable(writeTimeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */         
/* 105 */         elapsed = System.nanoTime() - start;
/* 106 */       } while ((res = delegate.write(srcs, offset, length)) == 0L);
/*     */     } 
/* 108 */     return res;
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
/* 119 */     return write(srcs, 0, srcs.length);
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
/* 130 */     if (!src.hasRemaining()) {
/* 131 */       return 0;
/*     */     }
/* 133 */     StreamSinkChannel delegate = this.delegate;
/*     */     int res;
/* 135 */     if ((res = delegate.write(src)) == 0L) {
/* 136 */       long start = System.nanoTime();
/* 137 */       long elapsed = 0L;
/*     */       do {
/* 139 */         long writeTimeout = this.writeTimeout;
/* 140 */         if (writeTimeout == 0L || writeTimeout == Long.MAX_VALUE)
/* 141 */         { delegate.awaitWritable(); }
/* 142 */         else { if (writeTimeout <= elapsed) {
/* 143 */             throw Messages.msg.writeTimeout();
/*     */           }
/* 145 */           delegate.awaitWritable(writeTimeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */         
/* 147 */         elapsed = System.nanoTime() - start;
/* 148 */       } while ((res = delegate.write(src)) == 0L);
/*     */     } 
/* 150 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 155 */     return this.delegate.isOpen();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 160 */     StreamSinkChannel delegate = this.delegate;
/* 161 */     if (!delegate.flush()) {
/* 162 */       long start = System.nanoTime();
/* 163 */       long elapsed = 0L;
/*     */       do {
/* 165 */         long writeTimeout = this.writeTimeout;
/* 166 */         if (writeTimeout == 0L || writeTimeout == Long.MAX_VALUE)
/* 167 */         { delegate.awaitWritable(); }
/* 168 */         else { if (writeTimeout <= elapsed) {
/* 169 */             throw Messages.msg.writeTimeout();
/*     */           }
/* 171 */           delegate.awaitWritable(writeTimeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */         
/* 173 */         elapsed = System.nanoTime() - start;
/* 174 */       } while (!delegate.flush());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 180 */     this.delegate.close();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\BlockingWritableByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
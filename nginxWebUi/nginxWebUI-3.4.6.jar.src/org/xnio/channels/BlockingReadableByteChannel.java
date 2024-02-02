/*     */ package org.xnio.channels;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
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
/*     */ public class BlockingReadableByteChannel
/*     */   implements ScatteringByteChannel
/*     */ {
/*     */   private final StreamSourceChannel delegate;
/*     */   private volatile long readTimeout;
/*     */   
/*     */   public BlockingReadableByteChannel(StreamSourceChannel delegate) {
/*  44 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BlockingReadableByteChannel(StreamSourceChannel delegate, long readTimeout, TimeUnit readTimeoutUnit) {
/*  55 */     if (readTimeout < 0L) {
/*  56 */       throw Messages.msg.parameterOutOfRange("readTimeout");
/*     */     }
/*  58 */     this.delegate = delegate;
/*  59 */     long calcTimeout = readTimeoutUnit.toNanos(readTimeout);
/*  60 */     this.readTimeout = (readTimeout == 0L) ? 0L : ((calcTimeout < 1L) ? 1L : calcTimeout);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReadTimeout(long readTimeout, TimeUnit readTimeoutUnit) {
/*  70 */     if (readTimeout < 0L) {
/*  71 */       throw Messages.msg.parameterOutOfRange("readTimeout");
/*     */     }
/*  73 */     long calcTimeout = readTimeoutUnit.toNanos(readTimeout);
/*  74 */     this.readTimeout = (readTimeout == 0L) ? 0L : ((calcTimeout < 1L) ? 1L : calcTimeout);
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
/*  87 */     if (!Buffers.hasRemaining((Buffer[])dsts, offset, length)) {
/*  88 */       return 0L;
/*     */     }
/*  90 */     StreamSourceChannel delegate = this.delegate;
/*     */     long res;
/*  92 */     if ((res = delegate.read(dsts, offset, length)) == 0L) {
/*  93 */       long start = System.nanoTime();
/*  94 */       long elapsed = 0L;
/*     */       do {
/*  96 */         long readTimeout = this.readTimeout;
/*  97 */         if (readTimeout == 0L || readTimeout == Long.MAX_VALUE)
/*  98 */         { delegate.awaitReadable(); }
/*  99 */         else { if (readTimeout <= elapsed) {
/* 100 */             throw Messages.msg.readTimeout();
/*     */           }
/* 102 */           delegate.awaitReadable(readTimeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */         
/* 104 */         elapsed = System.nanoTime() - start;
/* 105 */       } while ((res = delegate.read(dsts, offset, length)) == 0L);
/*     */     } 
/* 107 */     return res;
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
/* 118 */     return read(dsts, 0, dsts.length);
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
/* 129 */     if (!dst.hasRemaining()) {
/* 130 */       return 0;
/*     */     }
/* 132 */     StreamSourceChannel delegate = this.delegate;
/*     */     int res;
/* 134 */     if ((res = delegate.read(dst)) == 0) {
/* 135 */       long start = System.nanoTime();
/* 136 */       long elapsed = 0L;
/*     */       do {
/* 138 */         long readTimeout = this.readTimeout;
/* 139 */         if (readTimeout == 0L || readTimeout == Long.MAX_VALUE)
/* 140 */         { delegate.awaitReadable(); }
/* 141 */         else { if (readTimeout <= elapsed) {
/* 142 */             throw Messages.msg.readTimeout();
/*     */           }
/* 144 */           delegate.awaitReadable(readTimeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */         
/* 146 */         elapsed = System.nanoTime() - start;
/* 147 */       } while ((res = delegate.read(dst)) == 0);
/*     */     } 
/* 149 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 154 */     return this.delegate.isOpen();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 159 */     this.delegate.close();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\BlockingReadableByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package org.xnio.ssl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio._private.Messages;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.conduits.AbstractStreamSourceConduit;
/*     */ import org.xnio.conduits.ConduitReadableByteChannel;
/*     */ import org.xnio.conduits.Conduits;
/*     */ import org.xnio.conduits.StreamSourceConduit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class JsseSslStreamSourceConduit
/*     */   extends AbstractStreamSourceConduit<StreamSourceConduit>
/*     */ {
/*     */   private final JsseSslConduitEngine sslEngine;
/*     */   private volatile boolean tls;
/*     */   
/*     */   protected JsseSslStreamSourceConduit(StreamSourceConduit next, JsseSslConduitEngine sslEngine, boolean tls) {
/*  45 */     super(next);
/*  46 */     if (sslEngine == null) {
/*  47 */       throw Messages.msg.nullParameter("sslEngine");
/*     */     }
/*  49 */     this.sslEngine = sslEngine;
/*  50 */     this.tls = tls;
/*     */   }
/*     */   
/*     */   void enableTls() {
/*  54 */     this.tls = true;
/*  55 */     if (isReadResumed()) {
/*  56 */       wakeupReads();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*  62 */     return target.transferFrom((ReadableByteChannel)new ConduitReadableByteChannel((StreamSourceConduit)this), position, count);
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*  67 */     return Conduits.transfer((StreamSourceConduit)this, count, throughBuffer, (WritableByteChannel)target);
/*     */   }
/*     */   public int read(ByteBuffer dst) throws IOException {
/*     */     boolean attemptToUnwrapFirst;
/*     */     int readResult;
/*  72 */     if (!this.tls) {
/*  73 */       int res = super.read(dst);
/*  74 */       if (res == -1) {
/*  75 */         terminateReads();
/*     */       }
/*  77 */       return res;
/*     */     } 
/*  79 */     if ((!this.sslEngine.isDataAvailable() && this.sslEngine.isInboundClosed()) || this.sslEngine.isClosed()) {
/*  80 */       return -1;
/*     */     }
/*     */     
/*  83 */     synchronized (this.sslEngine.getUnwrapLock()) {
/*  84 */       attemptToUnwrapFirst = (this.sslEngine.getUnwrapBuffer().remaining() > 0);
/*     */     } 
/*  86 */     if (attemptToUnwrapFirst) {
/*  87 */       int i = this.sslEngine.unwrap(dst);
/*  88 */       if (i > 0) {
/*  89 */         return i;
/*     */       }
/*     */     } 
/*     */     
/*  93 */     synchronized (this.sslEngine.getUnwrapLock()) {
/*  94 */       ByteBuffer unwrapBuffer = this.sslEngine.getUnwrapBuffer().compact();
/*     */       try {
/*  96 */         readResult = super.read(unwrapBuffer);
/*     */       } finally {
/*  98 */         unwrapBuffer.flip();
/*     */       } 
/*     */     } 
/* 101 */     int unwrapResult = this.sslEngine.unwrap(dst);
/* 102 */     if (unwrapResult == 0 && readResult == -1) {
/* 103 */       terminateReads();
/* 104 */       return -1;
/*     */     } 
/* 106 */     return unwrapResult;
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/*     */     int readResult;
/* 111 */     if (!this.tls) {
/* 112 */       long res = super.read(dsts, offs, len);
/* 113 */       if (res == -1L) {
/* 114 */         terminateReads();
/*     */       }
/* 116 */       return res;
/*     */     } 
/* 118 */     if (offs < 0 || offs > len || len < 0 || offs + len > dsts.length) {
/* 119 */       throw new ArrayIndexOutOfBoundsException();
/*     */     }
/* 121 */     if (this.sslEngine.isClosed() || (!this.sslEngine.isDataAvailable() && this.sslEngine.isInboundClosed())) {
/* 122 */       return -1L;
/*     */     }
/*     */ 
/*     */     
/* 126 */     synchronized (this.sslEngine.getUnwrapLock()) {
/*     */       
/* 128 */       ByteBuffer unwrapBuffer = this.sslEngine.getUnwrapBuffer().compact();
/*     */       try {
/* 130 */         readResult = super.read(unwrapBuffer);
/*     */       } finally {
/* 132 */         unwrapBuffer.flip();
/*     */       } 
/*     */     } 
/* 135 */     long unwrapResult = this.sslEngine.unwrap(dsts, offs, len);
/* 136 */     if (unwrapResult == 0L && readResult == -1) {
/* 137 */       terminateReads();
/* 138 */       return -1L;
/*     */     } 
/* 140 */     return unwrapResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public void resumeReads() {
/* 145 */     if (this.tls && this.sslEngine.isFirstHandshake()) {
/* 146 */       wakeupReads();
/*     */     } else {
/* 148 */       super.resumeReads();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateReads() throws IOException {
/* 154 */     if (!this.tls) {
/* 155 */       super.terminateReads();
/*     */       return;
/*     */     } 
/*     */     try {
/* 159 */       this.sslEngine.closeInbound();
/* 160 */     } catch (IOException ex) {
/*     */       try {
/* 162 */         super.terminateReads();
/* 163 */       } catch (IOException e2) {
/* 164 */         e2.addSuppressed(ex);
/* 165 */         throw e2;
/*     */       } 
/* 167 */       throw ex;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 173 */     if (this.tls) {
/* 174 */       this.sslEngine.awaitCanUnwrap();
/*     */     }
/* 176 */     if (this.sslEngine.isDataAvailable()) {
/*     */       return;
/*     */     }
/* 179 */     super.awaitReadable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 184 */     if (!this.tls) {
/* 185 */       super.awaitReadable(time, timeUnit);
/*     */       return;
/*     */     } 
/* 188 */     synchronized (this.sslEngine.getUnwrapLock()) {
/* 189 */       if (this.sslEngine.getUnwrapBuffer().hasRemaining()) {
/*     */         return;
/*     */       }
/*     */     } 
/* 193 */     long duration = timeUnit.toNanos(time);
/* 194 */     long awaited = System.nanoTime();
/* 195 */     this.sslEngine.awaitCanUnwrap(time, timeUnit);
/* 196 */     awaited = System.nanoTime() - awaited;
/* 197 */     if (awaited > duration) {
/*     */       return;
/*     */     }
/* 200 */     super.awaitReadable(duration - awaited, TimeUnit.NANOSECONDS);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ssl\JsseSslStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
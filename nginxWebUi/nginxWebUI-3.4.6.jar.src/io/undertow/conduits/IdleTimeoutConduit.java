/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.util.WorkerUtils;
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.channels.SuspendableReadChannel;
/*     */ import org.xnio.channels.SuspendableWriteChannel;
/*     */ import org.xnio.conduits.ReadReadyHandler;
/*     */ import org.xnio.conduits.StreamSinkConduit;
/*     */ import org.xnio.conduits.StreamSourceConduit;
/*     */ import org.xnio.conduits.WriteReadyHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IdleTimeoutConduit
/*     */   implements StreamSinkConduit, StreamSourceConduit
/*     */ {
/*     */   private static final int DELTA = 100;
/*     */   private volatile XnioExecutor.Key handle;
/*     */   private volatile long idleTimeout;
/*  51 */   private volatile long expireTime = -1L;
/*     */   
/*     */   private volatile boolean timedOut = false;
/*     */   
/*     */   private final StreamSinkConduit sink;
/*     */   private final StreamSourceConduit source;
/*     */   private volatile WriteReadyHandler writeReadyHandler;
/*     */   private volatile ReadReadyHandler readReadyHandler;
/*     */   
/*  60 */   private final Runnable timeoutCommand = new Runnable()
/*     */     {
/*     */       public void run() {
/*  63 */         IdleTimeoutConduit.this.handle = null;
/*  64 */         if (IdleTimeoutConduit.this.expireTime == -1L) {
/*     */           return;
/*     */         }
/*  67 */         long current = System.currentTimeMillis();
/*  68 */         if (current < IdleTimeoutConduit.this.expireTime) {
/*     */           
/*  70 */           IdleTimeoutConduit.this.handle = WorkerUtils.executeAfter(IdleTimeoutConduit.this.getWriteThread(), IdleTimeoutConduit.this.timeoutCommand, IdleTimeoutConduit.this.expireTime - current + 100L, TimeUnit.MILLISECONDS);
/*     */           
/*     */           return;
/*     */         } 
/*  74 */         UndertowLogger.REQUEST_LOGGER.trace("Timing out channel due to inactivity");
/*  75 */         IdleTimeoutConduit.this.timedOut = true;
/*  76 */         IdleTimeoutConduit.this.doClose();
/*  77 */         if (IdleTimeoutConduit.this.sink.isWriteResumed() && 
/*  78 */           IdleTimeoutConduit.this.writeReadyHandler != null) {
/*  79 */           IdleTimeoutConduit.this.writeReadyHandler.writeReady();
/*     */         }
/*     */         
/*  82 */         if (IdleTimeoutConduit.this.source.isReadResumed() && 
/*  83 */           IdleTimeoutConduit.this.readReadyHandler != null) {
/*  84 */           IdleTimeoutConduit.this.readReadyHandler.readReady();
/*     */         }
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   protected void doClose() {
/*  91 */     safeClose(this.sink);
/*  92 */     safeClose(this.source);
/*     */   }
/*     */   
/*     */   public IdleTimeoutConduit(StreamConnection connection) {
/*  96 */     this.sink = connection.getSinkChannel().getConduit();
/*  97 */     this.source = connection.getSourceChannel().getConduit();
/*  98 */     setWriteReadyHandler((WriteReadyHandler)new WriteReadyHandler.ChannelListenerHandler((SuspendableWriteChannel)connection.getSinkChannel()));
/*  99 */     setReadReadyHandler((ReadReadyHandler)new ReadReadyHandler.ChannelListenerHandler((SuspendableReadChannel)connection.getSourceChannel()));
/*     */   }
/*     */   
/*     */   private void handleIdleTimeout() throws ClosedChannelException {
/* 103 */     if (this.timedOut) {
/*     */       return;
/*     */     }
/* 106 */     long idleTimeout = this.idleTimeout;
/* 107 */     if (idleTimeout <= 0L) {
/*     */       return;
/*     */     }
/* 110 */     long currentTime = System.currentTimeMillis();
/* 111 */     long expireTimeVar = this.expireTime;
/* 112 */     if (expireTimeVar != -1L && currentTime > expireTimeVar) {
/* 113 */       this.timedOut = true;
/* 114 */       doClose();
/* 115 */       throw new ClosedChannelException();
/*     */     } 
/* 117 */     this.expireTime = currentTime + idleTimeout;
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 122 */     handleIdleTimeout();
/* 123 */     int w = this.sink.write(src);
/* 124 */     return w;
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 129 */     handleIdleTimeout();
/* 130 */     long w = this.sink.write(srcs, offset, length);
/* 131 */     return w;
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 136 */     handleIdleTimeout();
/* 137 */     int w = this.sink.writeFinal(src);
/* 138 */     if (this.source.isReadShutdown() && !src.hasRemaining() && 
/* 139 */       this.handle != null) {
/* 140 */       this.handle.remove();
/* 141 */       this.handle = null;
/*     */     } 
/*     */     
/* 144 */     return w;
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 149 */     handleIdleTimeout();
/* 150 */     long w = this.sink.writeFinal(srcs, offset, length);
/* 151 */     if (this.source.isReadShutdown() && !Buffers.hasRemaining((Buffer[])srcs, offset, length) && 
/* 152 */       this.handle != null) {
/* 153 */       this.handle.remove();
/* 154 */       this.handle = null;
/*     */     } 
/*     */     
/* 157 */     return w;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 162 */     handleIdleTimeout();
/* 163 */     long w = this.source.transferTo(position, count, target);
/* 164 */     if (this.sink.isWriteShutdown() && w == -1L && 
/* 165 */       this.handle != null) {
/* 166 */       this.handle.remove();
/* 167 */       this.handle = null;
/*     */     } 
/*     */     
/* 170 */     return w;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 175 */     handleIdleTimeout();
/* 176 */     long w = this.source.transferTo(count, throughBuffer, target);
/* 177 */     if (this.sink.isWriteShutdown() && w == -1L && 
/* 178 */       this.handle != null) {
/* 179 */       this.handle.remove();
/* 180 */       this.handle = null;
/*     */     } 
/*     */     
/* 183 */     return w;
/*     */   }
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 188 */     handleIdleTimeout();
/* 189 */     long r = this.source.read(dsts, offset, length);
/* 190 */     if (this.sink.isWriteShutdown() && r == -1L && 
/* 191 */       this.handle != null) {
/* 192 */       this.handle.remove();
/* 193 */       this.handle = null;
/*     */     } 
/*     */     
/* 196 */     return r;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/* 201 */     handleIdleTimeout();
/* 202 */     int r = this.source.read(dst);
/* 203 */     if (this.sink.isWriteShutdown() && r == -1 && 
/* 204 */       this.handle != null) {
/* 205 */       this.handle.remove();
/* 206 */       this.handle = null;
/*     */     } 
/*     */     
/* 209 */     return r;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 214 */     handleIdleTimeout();
/* 215 */     return this.sink.transferFrom(src, position, count);
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 220 */     handleIdleTimeout();
/* 221 */     return this.sink.transferFrom(source, count, throughBuffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public void suspendReads() {
/* 226 */     this.source.suspendReads();
/* 227 */     XnioExecutor.Key handle = this.handle;
/* 228 */     if (handle != null && !isWriteResumed()) {
/* 229 */       handle.remove();
/* 230 */       this.handle = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateReads() throws IOException {
/* 236 */     this.source.terminateReads();
/* 237 */     if (this.sink.isWriteShutdown() && 
/* 238 */       this.handle != null) {
/* 239 */       this.handle.remove();
/* 240 */       this.handle = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isReadShutdown() {
/* 247 */     return this.source.isReadShutdown();
/*     */   }
/*     */ 
/*     */   
/*     */   public void resumeReads() {
/* 252 */     this.source.resumeReads();
/* 253 */     handleResumeTimeout();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadResumed() {
/* 258 */     return this.source.isReadResumed();
/*     */   }
/*     */ 
/*     */   
/*     */   public void wakeupReads() {
/* 263 */     this.source.wakeupReads();
/* 264 */     handleResumeTimeout();
/*     */   }
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 268 */     this.source.awaitReadable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 273 */     this.source.awaitReadable(time, timeUnit);
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioIoThread getReadThread() {
/* 278 */     return this.source.getReadThread();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setReadReadyHandler(ReadReadyHandler handler) {
/* 283 */     this.readReadyHandler = handler;
/* 284 */     this.source.setReadReadyHandler(handler);
/*     */   }
/*     */   
/*     */   private static void safeClose(StreamSourceConduit sink) {
/*     */     try {
/* 289 */       sink.terminateReads();
/* 290 */     } catch (IOException iOException) {}
/*     */   }
/*     */ 
/*     */   
/*     */   private static void safeClose(StreamSinkConduit sink) {
/*     */     try {
/* 296 */       sink.truncateWrites();
/* 297 */     } catch (IOException iOException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 303 */     this.sink.terminateWrites();
/* 304 */     if (this.source.isReadShutdown() && 
/* 305 */       this.handle != null) {
/* 306 */       this.handle.remove();
/* 307 */       this.handle = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWriteShutdown() {
/* 314 */     return this.sink.isWriteShutdown();
/*     */   }
/*     */ 
/*     */   
/*     */   public void resumeWrites() {
/* 319 */     this.sink.resumeWrites();
/* 320 */     handleResumeTimeout();
/*     */   }
/*     */ 
/*     */   
/*     */   public void suspendWrites() {
/* 325 */     this.sink.suspendWrites();
/* 326 */     XnioExecutor.Key handle = this.handle;
/* 327 */     if (handle != null && !isReadResumed()) {
/* 328 */       handle.remove();
/* 329 */       this.handle = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void wakeupWrites() {
/* 336 */     this.sink.wakeupWrites();
/* 337 */     handleResumeTimeout();
/*     */   }
/*     */   
/*     */   private void handleResumeTimeout() {
/* 341 */     long timeout = getIdleTimeout();
/* 342 */     if (timeout <= 0L) {
/*     */       return;
/*     */     }
/* 345 */     long currentTime = System.currentTimeMillis();
/* 346 */     long newExpireTime = currentTime + timeout;
/* 347 */     boolean shorter = (newExpireTime < this.expireTime);
/* 348 */     if (shorter && this.handle != null) {
/* 349 */       this.handle.remove();
/* 350 */       this.handle = null;
/*     */     } 
/* 352 */     this.expireTime = newExpireTime;
/* 353 */     XnioExecutor.Key key = this.handle;
/* 354 */     if (key == null) {
/* 355 */       this.handle = WorkerUtils.executeAfter(getWriteThread(), this.timeoutCommand, timeout, TimeUnit.MILLISECONDS);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWriteResumed() {
/* 361 */     return this.sink.isWriteResumed();
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 366 */     this.sink.awaitWritable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 371 */     this.sink.awaitWritable();
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioIoThread getWriteThread() {
/* 376 */     return this.sink.getWriteThread();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWriteReadyHandler(WriteReadyHandler handler) {
/* 381 */     this.writeReadyHandler = handler;
/* 382 */     this.sink.setWriteReadyHandler(handler);
/*     */   }
/*     */ 
/*     */   
/*     */   public void truncateWrites() throws IOException {
/* 387 */     this.sink.truncateWrites();
/* 388 */     if (this.source.isReadShutdown() && 
/* 389 */       this.handle != null) {
/* 390 */       this.handle.remove();
/* 391 */       this.handle = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean flush() throws IOException {
/* 399 */     return this.sink.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioWorker getWorker() {
/* 404 */     return this.sink.getWorker();
/*     */   }
/*     */   
/*     */   public long getIdleTimeout() {
/* 408 */     return this.idleTimeout;
/*     */   }
/*     */   
/*     */   public void setIdleTimeout(long idleTimeout) {
/* 412 */     this.idleTimeout = idleTimeout;
/* 413 */     if (idleTimeout > 0L) {
/* 414 */       this.expireTime = System.currentTimeMillis() + idleTimeout;
/* 415 */       if (isReadResumed() || isWriteResumed()) {
/* 416 */         handleResumeTimeout();
/*     */       }
/*     */     } else {
/* 419 */       this.expireTime = -1L;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\IdleTimeoutConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
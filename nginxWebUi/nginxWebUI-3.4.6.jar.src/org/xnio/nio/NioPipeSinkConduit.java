/*     */ package org.xnio.nio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.Pipe;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import org.xnio.Xnio;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.channels.WriteTimeoutException;
/*     */ import org.xnio.conduits.Conduits;
/*     */ import org.xnio.conduits.StreamSinkConduit;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ final class NioPipeSinkConduit
/*     */   extends NioHandle
/*     */   implements StreamSinkConduit
/*     */ {
/*     */   private final Pipe.SinkChannel sinkChannel;
/*     */   private final NioPipeStreamConnection connection;
/*     */   private WriteReadyHandler writeReadyHandler;
/*     */   private volatile int writeTimeout;
/*     */   private long lastWrite;
/*  53 */   private static final AtomicIntegerFieldUpdater<NioPipeSinkConduit> writeTimeoutUpdater = AtomicIntegerFieldUpdater.newUpdater(NioPipeSinkConduit.class, "writeTimeout");
/*     */   
/*     */   NioPipeSinkConduit(WorkerThread workerThread, SelectionKey selectionKey, NioPipeStreamConnection connection) {
/*  56 */     super(workerThread, selectionKey);
/*  57 */     this.connection = connection;
/*  58 */     this.sinkChannel = (Pipe.SinkChannel)selectionKey.channel();
/*     */   }
/*     */   
/*     */   void handleReady(int ops) {
/*     */     try {
/*  63 */       this.writeReadyHandler.writeReady();
/*  64 */     } catch (Throwable throwable) {}
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioWorker getWorker() {
/*  69 */     return getWorkerThread().getWorker();
/*     */   }
/*     */   
/*     */   void forceTermination() {
/*  73 */     WriteReadyHandler write = this.writeReadyHandler;
/*  74 */     if (write != null) write.forceTermination(); 
/*     */   }
/*     */   
/*     */   void terminated() {
/*  78 */     WriteReadyHandler write = this.writeReadyHandler;
/*  79 */     if (write != null) write.terminated();
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   int getAndSetWriteTimeout(int newVal) {
/*  85 */     return writeTimeoutUpdater.getAndSet(this, newVal);
/*     */   }
/*     */   
/*     */   int getWriteTimeout() {
/*  89 */     return this.writeTimeout;
/*     */   }
/*     */   
/*     */   private void checkWriteTimeout(boolean xfer) throws WriteTimeoutException {
/*  93 */     int timeout = this.writeTimeout;
/*  94 */     if (timeout > 0) {
/*  95 */       if (xfer) {
/*  96 */         this.lastWrite = System.nanoTime();
/*     */       } else {
/*  98 */         long lastWrite = this.lastWrite;
/*  99 */         if (lastWrite > 0L && (System.nanoTime() - lastWrite) / 1000000L > timeout) {
/* 100 */           throw Log.log.writeTimeout();
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public final long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 107 */     long res = src.transferTo(position, count, this.sinkChannel);
/* 108 */     checkWriteTimeout((res > 0L));
/* 109 */     return res;
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 113 */     return Conduits.transfer((ReadableByteChannel)source, count, throughBuffer, this);
/*     */   }
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 117 */     int res = this.sinkChannel.write(src);
/* 118 */     checkWriteTimeout((res > 0));
/* 119 */     return res;
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 123 */     if (length == 1) {
/* 124 */       return write(srcs[offset]);
/*     */     }
/* 126 */     long res = this.sinkChannel.write(srcs, offset, length);
/* 127 */     checkWriteTimeout((res > 0L));
/* 128 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 133 */     return Conduits.writeFinalBasic(this, src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 138 */     return Conduits.writeFinalBasic(this, srcs, offset, length);
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/* 142 */     return true;
/*     */   }
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 146 */     if (this.connection.writeClosed())
/* 147 */       try { this.sinkChannel.close(); }
/* 148 */       catch (ClosedChannelException closedChannelException) {  }
/*     */       finally
/* 150 */       { writeTerminated(); }
/*     */        
/*     */   }
/*     */   
/*     */   public void truncateWrites() throws IOException {
/* 155 */     terminateWrites();
/*     */   }
/*     */   
/*     */   void writeTerminated() {
/* 159 */     WriteReadyHandler writeReadyHandler = this.writeReadyHandler;
/* 160 */     if (writeReadyHandler != null)
/* 161 */       try { writeReadyHandler.terminated(); }
/* 162 */       catch (Throwable throwable) {} 
/*     */   }
/*     */   
/*     */   public boolean isWriteShutdown() {
/* 166 */     return this.connection.isWriteShutdown();
/*     */   }
/*     */   
/*     */   public void resumeWrites() {
/* 170 */     resume(4);
/*     */   }
/*     */   
/*     */   public void suspendWrites() {
/* 174 */     suspend(4);
/*     */   }
/*     */   
/*     */   public void wakeupWrites() {
/* 178 */     wakeup(4);
/*     */   }
/*     */   
/*     */   public boolean isWriteResumed() {
/* 182 */     return isResumed(4);
/*     */   }
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 186 */     Xnio.checkBlockingAllowed();
/* 187 */     SelectorUtils.await((NioXnio)getWorker().getXnio(), this.sinkChannel, 4);
/*     */   }
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 191 */     Xnio.checkBlockingAllowed();
/* 192 */     SelectorUtils.await((NioXnio)getWorker().getXnio(), this.sinkChannel, 4, time, timeUnit);
/*     */   }
/*     */   
/*     */   public XnioIoThread getWriteThread() {
/* 196 */     return getWorkerThread();
/*     */   }
/*     */   
/*     */   public void setWriteReadyHandler(WriteReadyHandler handler) {
/* 200 */     this.writeReadyHandler = handler;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\NioPipeSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
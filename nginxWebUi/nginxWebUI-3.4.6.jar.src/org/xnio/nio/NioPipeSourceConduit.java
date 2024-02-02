/*     */ package org.xnio.nio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.CancelledKeyException;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.Pipe;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import org.xnio.Xnio;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.ReadTimeoutException;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.conduits.Conduits;
/*     */ import org.xnio.conduits.ReadReadyHandler;
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
/*     */ 
/*     */ 
/*     */ final class NioPipeSourceConduit
/*     */   extends NioHandle
/*     */   implements StreamSourceConduit
/*     */ {
/*     */   private final Pipe.SourceChannel sourceChannel;
/*     */   private final NioPipeStreamConnection connection;
/*     */   private ReadReadyHandler readReadyHandler;
/*     */   private volatile int readTimeout;
/*     */   private long lastRead;
/*  55 */   private static final AtomicIntegerFieldUpdater<NioPipeSourceConduit> readTimeoutUpdater = AtomicIntegerFieldUpdater.newUpdater(NioPipeSourceConduit.class, "readTimeout");
/*     */   
/*     */   NioPipeSourceConduit(WorkerThread workerThread, SelectionKey selectionKey, NioPipeStreamConnection connection) {
/*  58 */     super(workerThread, selectionKey);
/*  59 */     this.connection = connection;
/*  60 */     this.sourceChannel = (Pipe.SourceChannel)selectionKey.channel();
/*     */   }
/*     */   
/*     */   void handleReady(int ops) {
/*     */     try {
/*  65 */       this.readReadyHandler.readReady();
/*  66 */     } catch (CancelledKeyException cancelledKeyException) {}
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/*  70 */     return getWorkerThread().getWorker();
/*     */   }
/*     */   
/*     */   void forceTermination() {
/*  74 */     ReadReadyHandler read = this.readReadyHandler;
/*  75 */     if (read != null) read.forceTermination(); 
/*     */   }
/*     */   
/*     */   void terminated() {
/*  79 */     ReadReadyHandler read = this.readReadyHandler;
/*  80 */     if (read != null) read.terminated();
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   int getAndSetReadTimeout(int newVal) {
/*  86 */     return readTimeoutUpdater.getAndSet(this, newVal);
/*     */   }
/*     */   
/*     */   int getReadTimeout() {
/*  90 */     return this.readTimeout;
/*     */   }
/*     */   
/*     */   private void checkReadTimeout(boolean xfer) throws ReadTimeoutException {
/*  94 */     int timeout = this.readTimeout;
/*  95 */     if (timeout > 0) {
/*  96 */       if (xfer) {
/*  97 */         this.lastRead = System.nanoTime();
/*     */       } else {
/*  99 */         long lastRead = this.lastRead;
/* 100 */         if (lastRead > 0L && (System.nanoTime() - lastRead) / 1000000L > timeout) {
/* 101 */           throw Log.log.readTimeout();
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 108 */     long res = target.transferFrom(this.sourceChannel, position, count);
/* 109 */     checkReadTimeout((res > 0L));
/* 110 */     return res;
/*     */   }
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 114 */     return Conduits.transfer(this, count, throughBuffer, (WritableByteChannel)target);
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/*     */     int res;
/*     */     try {
/* 120 */       res = this.sourceChannel.read(dst);
/* 121 */     } catch (ClosedChannelException e) {
/* 122 */       return -1;
/*     */     } 
/* 124 */     if (res != -1) checkReadTimeout((res > 0)); 
/* 125 */     return res;
/*     */   }
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/*     */     long res;
/* 129 */     if (length == 1) {
/* 130 */       return read(dsts[offset]);
/*     */     }
/*     */     
/*     */     try {
/* 134 */       res = this.sourceChannel.read(dsts, offset, length);
/* 135 */     } catch (ClosedChannelException e) {
/* 136 */       return -1L;
/*     */     } 
/* 138 */     if (res != -1L) checkReadTimeout((res > 0L)); 
/* 139 */     return res;
/*     */   }
/*     */   
/*     */   public void terminateReads() throws IOException {
/* 143 */     if (this.connection.readClosed())
/* 144 */       try { this.sourceChannel.close(); }
/* 145 */       catch (ClosedChannelException closedChannelException) {  }
/*     */       finally
/* 147 */       { readTerminated(); }
/*     */        
/*     */   }
/*     */   
/*     */   void readTerminated() {
/* 152 */     ReadReadyHandler readReadyHandler = this.readReadyHandler;
/* 153 */     if (readReadyHandler != null)
/* 154 */       try { readReadyHandler.terminated(); }
/* 155 */       catch (Throwable throwable) {} 
/*     */   }
/*     */   
/*     */   public boolean isReadShutdown() {
/* 159 */     return this.connection.isReadShutdown();
/*     */   }
/*     */   
/*     */   public void resumeReads() {
/* 163 */     resume(1);
/*     */   }
/*     */   
/*     */   public void suspendReads() {
/* 167 */     suspend(1);
/*     */   }
/*     */   
/*     */   public void wakeupReads() {
/* 171 */     wakeup(1);
/*     */   }
/*     */   
/*     */   public boolean isReadResumed() {
/* 175 */     return isResumed(1);
/*     */   }
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 179 */     Xnio.checkBlockingAllowed();
/* 180 */     SelectorUtils.await((NioXnio)getWorker().getXnio(), this.sourceChannel, 1);
/*     */   }
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 184 */     Xnio.checkBlockingAllowed();
/* 185 */     SelectorUtils.await((NioXnio)getWorker().getXnio(), this.sourceChannel, 1, time, timeUnit);
/*     */   }
/*     */   
/*     */   public XnioIoThread getReadThread() {
/* 189 */     return getWorkerThread();
/*     */   }
/*     */   
/*     */   public void setReadReadyHandler(ReadReadyHandler handler) {
/* 193 */     this.readReadyHandler = handler;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\NioPipeSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package org.xnio.nio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.SocketException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.CancelledKeyException;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.Xnio;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.ReadTimeoutException;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.channels.WriteTimeoutException;
/*     */ import org.xnio.conduits.Conduits;
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
/*     */ final class NioSocketConduit
/*     */   extends NioHandle
/*     */   implements StreamSourceConduit, StreamSinkConduit
/*     */ {
/*     */   private final SocketChannel socketChannel;
/*     */   private final NioSocketStreamConnection connection;
/*     */   private ReadReadyHandler readReadyHandler;
/*     */   private WriteReadyHandler writeReadyHandler;
/*     */   private volatile int readTimeout;
/*     */   private long lastRead;
/*  58 */   private static final AtomicIntegerFieldUpdater<NioSocketConduit> readTimeoutUpdater = AtomicIntegerFieldUpdater.newUpdater(NioSocketConduit.class, "readTimeout");
/*     */ 
/*     */   
/*     */   private volatile int writeTimeout;
/*     */   
/*     */   private long lastWrite;
/*     */   
/*  65 */   private static final AtomicIntegerFieldUpdater<NioSocketConduit> writeTimeoutUpdater = AtomicIntegerFieldUpdater.newUpdater(NioSocketConduit.class, "writeTimeout");
/*     */   
/*     */   NioSocketConduit(WorkerThread workerThread, SelectionKey selectionKey, NioSocketStreamConnection connection) {
/*  68 */     super(workerThread, selectionKey);
/*  69 */     this.connection = connection;
/*  70 */     this.socketChannel = (SocketChannel)selectionKey.channel();
/*     */   }
/*     */   
/*     */   void handleReady(int ops) {
/*     */     try {
/*  75 */       if (ops == 0) {
/*     */         
/*  77 */         SelectionKey key = getSelectionKey();
/*  78 */         int interestOps = key.interestOps();
/*  79 */         if (interestOps != 0) {
/*  80 */           ops = interestOps;
/*     */         } else {
/*     */           
/*  83 */           forceTermination();
/*     */           return;
/*     */         } 
/*     */       } 
/*  87 */       if (Bits.allAreSet(ops, 1)) {
/*  88 */         try { if (isReadShutdown()) suspendReads(); 
/*  89 */           this.readReadyHandler.readReady(); }
/*  90 */         catch (Throwable throwable) {}
/*     */       }
/*  92 */       if (Bits.allAreSet(ops, 4)) {
/*  93 */         try { if (isWriteShutdown()) suspendWrites(); 
/*  94 */           this.writeReadyHandler.writeReady(); }
/*  95 */         catch (Throwable throwable) {}
/*     */       }
/*  97 */     } catch (CancelledKeyException cancelledKeyException) {}
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 101 */     return getWorkerThread().getWorker();
/*     */   }
/*     */   
/*     */   void forceTermination() {
/* 105 */     ReadReadyHandler read = this.readReadyHandler;
/* 106 */     if (read != null) read.forceTermination(); 
/* 107 */     WriteReadyHandler write = this.writeReadyHandler;
/* 108 */     if (write != null) write.forceTermination(); 
/*     */   }
/*     */   
/*     */   void terminated() {
/* 112 */     ReadReadyHandler read = this.readReadyHandler;
/* 113 */     if (read != null) read.terminated(); 
/* 114 */     WriteReadyHandler write = this.writeReadyHandler;
/* 115 */     if (write != null) write.terminated();
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   int getAndSetWriteTimeout(int newVal) {
/* 121 */     return writeTimeoutUpdater.getAndSet(this, newVal);
/*     */   }
/*     */   
/*     */   int getWriteTimeout() {
/* 125 */     return this.writeTimeout;
/*     */   }
/*     */   
/*     */   private void checkWriteTimeout(boolean xfer) throws WriteTimeoutException {
/* 129 */     int timeout = this.writeTimeout;
/* 130 */     if (timeout > 0) {
/* 131 */       if (xfer) {
/* 132 */         this.lastWrite = System.nanoTime();
/*     */       } else {
/* 134 */         long lastRead = this.lastWrite;
/* 135 */         if (lastRead > 0L && (System.nanoTime() - lastRead) / 1000000L > timeout) {
/* 136 */           throw Log.log.writeTimeout();
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public final long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 143 */     long res = src.transferTo(position, count, this.socketChannel);
/* 144 */     checkWriteTimeout((res > 0L));
/* 145 */     return res;
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 149 */     return Conduits.transfer((ReadableByteChannel)source, count, throughBuffer, this);
/*     */   }
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 153 */     int res = this.socketChannel.write(src);
/* 154 */     checkWriteTimeout((res > 0));
/* 155 */     return res;
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 159 */     if (length == 1) {
/* 160 */       return write(srcs[offset]);
/*     */     }
/* 162 */     long res = this.socketChannel.write(srcs, offset, length);
/* 163 */     checkWriteTimeout((res > 0L));
/* 164 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 169 */     return Conduits.writeFinalBasic(this, src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 174 */     return Conduits.writeFinalBasic(this, srcs, offset, length);
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/* 178 */     return true;
/*     */   }
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 182 */     if (this.connection.writeClosed())
/* 183 */       try { if (getSelectionKey().isValid()) {
/* 184 */           suspend(4);
/*     */         }
/* 186 */         if (this.socketChannel.isOpen()) {
/* 187 */           try { this.socketChannel.socket().shutdownOutput(); }
/* 188 */           catch (SocketException socketException) {}
/*     */         } }
/*     */       
/* 191 */       catch (ClosedChannelException closedChannelException) {  }
/*     */       finally
/* 193 */       { writeTerminated(); }
/*     */        
/*     */   }
/*     */   
/*     */   public void truncateWrites() throws IOException {
/* 198 */     terminateWrites();
/*     */   }
/*     */   
/*     */   void writeTerminated() {
/* 202 */     WriteReadyHandler writeReadyHandler = this.writeReadyHandler;
/* 203 */     if (writeReadyHandler != null)
/* 204 */       try { writeReadyHandler.terminated(); }
/* 205 */       catch (Throwable throwable) {} 
/*     */   }
/*     */   
/*     */   public boolean isWriteShutdown() {
/* 209 */     return this.connection.isWriteShutdown();
/*     */   }
/*     */   
/*     */   public void resumeWrites() {
/* 213 */     resume(4);
/*     */   }
/*     */   
/*     */   public void suspendWrites() {
/* 217 */     suspend(4);
/*     */   }
/*     */   
/*     */   public void wakeupWrites() {
/* 221 */     wakeup(4);
/*     */   }
/*     */   
/*     */   public boolean isWriteResumed() {
/* 225 */     return isResumed(4);
/*     */   }
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 229 */     Xnio.checkBlockingAllowed();
/* 230 */     if (isWriteShutdown()) {
/*     */       return;
/*     */     }
/* 233 */     SelectorUtils.await((NioXnio)getWorker().getXnio(), this.socketChannel, 4);
/*     */   }
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 237 */     Xnio.checkBlockingAllowed();
/* 238 */     if (isWriteShutdown()) {
/*     */       return;
/*     */     }
/* 241 */     SelectorUtils.await((NioXnio)getWorker().getXnio(), this.socketChannel, 4, time, timeUnit);
/*     */   }
/*     */   
/*     */   public XnioIoThread getWriteThread() {
/* 245 */     return getWorkerThread();
/*     */   }
/*     */   
/*     */   public void setWriteReadyHandler(WriteReadyHandler handler) {
/* 249 */     this.writeReadyHandler = handler;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   int getAndSetReadTimeout(int newVal) {
/* 255 */     return readTimeoutUpdater.getAndSet(this, newVal);
/*     */   }
/*     */   
/*     */   int getReadTimeout() {
/* 259 */     return this.readTimeout;
/*     */   }
/*     */   
/*     */   private void checkReadTimeout(boolean xfer) throws ReadTimeoutException {
/* 263 */     int timeout = this.readTimeout;
/* 264 */     if (timeout > 0) {
/* 265 */       if (xfer) {
/* 266 */         this.lastRead = System.nanoTime();
/*     */       } else {
/* 268 */         long lastRead = this.lastRead;
/* 269 */         if (lastRead > 0L && (System.nanoTime() - lastRead) / 1000000L > timeout) {
/* 270 */           throw Log.log.readTimeout();
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 277 */     long res = target.transferFrom(this.socketChannel, position, count);
/* 278 */     checkReadTimeout((res > 0L));
/* 279 */     return res;
/*     */   }
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 283 */     return Conduits.transfer(this, count, throughBuffer, (WritableByteChannel)target);
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/*     */     int res;
/*     */     try {
/* 289 */       res = this.socketChannel.read(dst);
/* 290 */     } catch (ClosedChannelException e) {
/* 291 */       return -1;
/*     */     } 
/* 293 */     if (res != -1) { checkReadTimeout((res > 0)); }
/* 294 */     else { terminateReads(); }
/* 295 */      return res;
/*     */   }
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/*     */     long res;
/* 299 */     if (length == 1) {
/* 300 */       return read(dsts[offset]);
/*     */     }
/*     */     
/*     */     try {
/* 304 */       res = this.socketChannel.read(dsts, offset, length);
/* 305 */     } catch (ClosedChannelException e) {
/* 306 */       return -1L;
/*     */     } 
/* 308 */     if (res != -1L) { checkReadTimeout((res > 0L)); }
/* 309 */     else { terminateReads(); }
/* 310 */      return res;
/*     */   }
/*     */   
/*     */   public void terminateReads() throws IOException {
/* 314 */     if (this.connection.readClosed())
/* 315 */       try { if (getSelectionKey().isValid()) {
/* 316 */           suspend(1);
/*     */         }
/* 318 */         if (this.socketChannel.isOpen()) {
/* 319 */           try { this.socketChannel.socket().shutdownInput(); }
/* 320 */           catch (SocketException socketException) {}
/*     */         } }
/*     */       
/* 323 */       catch (ClosedChannelException closedChannelException) {  }
/*     */       finally
/* 325 */       { readTerminated(); }
/*     */        
/*     */   }
/*     */   
/*     */   void readTerminated() {
/* 330 */     ReadReadyHandler readReadyHandler = this.readReadyHandler;
/* 331 */     if (readReadyHandler != null)
/* 332 */       try { readReadyHandler.terminated(); }
/* 333 */       catch (Throwable throwable) {} 
/*     */   }
/*     */   
/*     */   public boolean isReadShutdown() {
/* 337 */     return this.connection.isReadShutdown();
/*     */   }
/*     */   
/*     */   public void resumeReads() {
/* 341 */     resume(1);
/*     */   }
/*     */   
/*     */   public void suspendReads() {
/* 345 */     suspend(1);
/*     */   }
/*     */   
/*     */   public void wakeupReads() {
/* 349 */     wakeup(1);
/*     */   }
/*     */   
/*     */   public boolean isReadResumed() {
/* 353 */     return isResumed(1);
/*     */   }
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 357 */     Xnio.checkBlockingAllowed();
/* 358 */     SelectorUtils.await((NioXnio)getWorker().getXnio(), this.socketChannel, 1);
/*     */   }
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 362 */     Xnio.checkBlockingAllowed();
/* 363 */     SelectorUtils.await((NioXnio)getWorker().getXnio(), this.socketChannel, 1, time, timeUnit);
/*     */   }
/*     */   
/*     */   public XnioIoThread getReadThread() {
/* 367 */     return getWorkerThread();
/*     */   }
/*     */   
/*     */   public void setReadReadyHandler(ReadReadyHandler handler) {
/* 371 */     this.readReadyHandler = handler;
/*     */   }
/*     */   
/*     */   SocketChannel getSocketChannel() {
/* 375 */     return this.socketChannel;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\NioSocketConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package org.xnio.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
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
/*     */ public final class NullStreamSinkConduit
/*     */   implements StreamSinkConduit
/*     */ {
/*     */   private final XnioWorker worker;
/*     */   private final XnioIoThread writeThread;
/*     */   private WriteReadyHandler writeReadyHandler;
/*     */   private boolean shutdown;
/*     */   private boolean resumed;
/*     */   
/*     */   public NullStreamSinkConduit(XnioIoThread writeThread) {
/*  49 */     this.worker = writeThread.getWorker();
/*  50 */     this.writeThread = writeThread;
/*     */   }
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/*  54 */     return Channels.drain(src, position, count);
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/*  58 */     throughBuffer.limit(0);
/*  59 */     return Channels.drain(source, count);
/*     */   }
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*     */     try {
/*  64 */       return src.remaining();
/*     */     } finally {
/*  66 */       src.position(src.limit());
/*     */     } 
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
/*  71 */     long t = 0L;
/*  72 */     for (int i = 0; i < len; i++) {
/*  73 */       t += write(srcs[i + offs]);
/*     */     }
/*  75 */     return t;
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/*  80 */     return Conduits.writeFinalBasic(this, src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/*  85 */     return Conduits.writeFinalBasic(this, srcs, offset, length);
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/*  89 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isWriteShutdown() {
/*  93 */     return this.shutdown;
/*     */   }
/*     */   
/*     */   public void suspendWrites() {
/*  97 */     this.resumed = false;
/*     */   }
/*     */   
/*     */   public void resumeWrites() {
/* 101 */     this.resumed = true;
/* 102 */     WriteReadyHandler handler = this.writeReadyHandler;
/* 103 */     this.writeThread.execute(new WriteReadyHandler.ReadyTask(handler));
/*     */   }
/*     */   
/*     */   public void wakeupWrites() {
/* 107 */     resumeWrites();
/*     */   }
/*     */   
/*     */   public boolean isWriteResumed() {
/* 111 */     return this.resumed;
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable() throws IOException {}
/*     */ 
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {}
/*     */   
/*     */   public XnioIoThread getWriteThread() {
/* 121 */     return this.writeThread;
/*     */   }
/*     */   
/*     */   public void setWriteReadyHandler(WriteReadyHandler handler) {
/* 125 */     this.writeReadyHandler = handler;
/*     */   }
/*     */   
/*     */   public void truncateWrites() throws IOException {
/* 129 */     terminateWrites();
/*     */   }
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 133 */     if (!this.shutdown) {
/* 134 */       this.shutdown = true;
/* 135 */       this.writeReadyHandler.terminated();
/*     */     } 
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 140 */     return this.worker;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\NullStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
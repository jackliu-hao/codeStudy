/*     */ package org.xnio.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.StreamSinkChannel;
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
/*     */ public final class EmptyStreamSourceConduit
/*     */   implements StreamSourceConduit
/*     */ {
/*     */   private final XnioWorker worker;
/*     */   private final XnioIoThread readThread;
/*     */   private ReadReadyHandler readReadyHandler;
/*     */   private boolean shutdown;
/*     */   private boolean resumed;
/*     */   
/*     */   public EmptyStreamSourceConduit(XnioIoThread readThread) {
/*  47 */     this.worker = readThread.getWorker();
/*  48 */     this.readThread = readThread;
/*     */   }
/*     */   
/*     */   public void setReadReadyHandler(ReadReadyHandler handler) {
/*  52 */     this.readReadyHandler = handler;
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*  56 */     return 0L;
/*     */   }
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*  60 */     this.resumed = false;
/*  61 */     return -1L;
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/*  65 */     this.resumed = false;
/*  66 */     return -1;
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/*  70 */     this.resumed = false;
/*  71 */     return -1L;
/*     */   }
/*     */   
/*     */   public boolean isReadShutdown() {
/*  75 */     return this.shutdown;
/*     */   }
/*     */   
/*     */   public void resumeReads() {
/*  79 */     this.resumed = true;
/*  80 */     this.readThread.execute(new Runnable() {
/*     */           public void run() {
/*  82 */             ReadReadyHandler handler = EmptyStreamSourceConduit.this.readReadyHandler;
/*  83 */             if (handler != null) {
/*  84 */               handler.readReady();
/*     */             }
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public void suspendReads() {
/*  91 */     this.resumed = false;
/*     */   }
/*     */   
/*     */   public void wakeupReads() {
/*  95 */     resumeReads();
/*     */   }
/*     */   
/*     */   public boolean isReadResumed() {
/*  99 */     return this.resumed;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void awaitReadable() throws IOException {}
/*     */ 
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {}
/*     */ 
/*     */   
/*     */   public void terminateReads() throws IOException {
/* 111 */     if (!this.shutdown) {
/* 112 */       this.shutdown = true;
/* 113 */       this.readReadyHandler.terminated();
/*     */     } 
/*     */   }
/*     */   
/*     */   public XnioIoThread getReadThread() {
/* 118 */     return this.readThread;
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 122 */     return this.worker;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\EmptyStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.StreamSinkChannel;
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
/*  52 */     this.worker = readThread.getWorker();
/*  53 */     this.readThread = readThread;
/*     */   }
/*     */   
/*     */   public void setReadReadyHandler(ReadReadyHandler handler) {
/*  57 */     this.readReadyHandler = handler;
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*  61 */     return 0L;
/*     */   }
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*  65 */     this.resumed = false;
/*  66 */     return -1L;
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/*  70 */     this.resumed = false;
/*  71 */     return -1;
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/*  75 */     this.resumed = false;
/*  76 */     return -1L;
/*     */   }
/*     */   
/*     */   public boolean isReadShutdown() {
/*  80 */     return this.shutdown;
/*     */   }
/*     */   
/*     */   public void resumeReads() {
/*  84 */     this.resumed = true;
/*  85 */     this.readThread.execute(new Runnable() {
/*     */           public void run() {
/*  87 */             ReadReadyHandler handler = EmptyStreamSourceConduit.this.readReadyHandler;
/*  88 */             if (handler != null) {
/*  89 */               handler.readReady();
/*     */             }
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public void suspendReads() {
/*  96 */     this.resumed = false;
/*     */   }
/*     */   
/*     */   public void wakeupReads() {
/* 100 */     resumeReads();
/*     */   }
/*     */   
/*     */   public boolean isReadResumed() {
/* 104 */     return this.resumed;
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
/* 116 */     if (!this.shutdown) {
/* 117 */       this.shutdown = true;
/* 118 */       if (this.readReadyHandler != null) {
/* 119 */         this.readReadyHandler.terminated();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public XnioIoThread getReadThread() {
/* 125 */     return this.readThread;
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 129 */     return this.worker;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\EmptyStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
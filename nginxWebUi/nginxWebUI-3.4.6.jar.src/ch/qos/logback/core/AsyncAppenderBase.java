/*     */ package ch.qos.logback.core;
/*     */ 
/*     */ import ch.qos.logback.core.spi.AppenderAttachable;
/*     */ import ch.qos.logback.core.spi.AppenderAttachableImpl;
/*     */ import ch.qos.logback.core.util.InterruptUtil;
/*     */ import java.util.Iterator;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ import java.util.concurrent.BlockingQueue;
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
/*     */ public class AsyncAppenderBase<E>
/*     */   extends UnsynchronizedAppenderBase<E>
/*     */   implements AppenderAttachable<E>
/*     */ {
/*  42 */   AppenderAttachableImpl<E> aai = new AppenderAttachableImpl();
/*     */ 
/*     */   
/*     */   BlockingQueue<E> blockingQueue;
/*     */   
/*     */   public static final int DEFAULT_QUEUE_SIZE = 256;
/*     */   
/*  49 */   int queueSize = 256;
/*     */   
/*  51 */   int appenderCount = 0;
/*     */   
/*     */   static final int UNDEFINED = -1;
/*  54 */   int discardingThreshold = -1;
/*     */   
/*     */   boolean neverBlock = false;
/*  57 */   Worker worker = new Worker();
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_MAX_FLUSH_TIME = 1000;
/*     */ 
/*     */ 
/*     */   
/*  65 */   int maxFlushTime = 1000;
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
/*     */   protected boolean isDiscardable(E eventObject) {
/*  78 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void preprocess(E eventObject) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/*  92 */     if (isStarted())
/*     */       return; 
/*  94 */     if (this.appenderCount == 0) {
/*  95 */       addError("No attached appenders found.");
/*     */       return;
/*     */     } 
/*  98 */     if (this.queueSize < 1) {
/*  99 */       addError("Invalid queue size [" + this.queueSize + "]");
/*     */       return;
/*     */     } 
/* 102 */     this.blockingQueue = new ArrayBlockingQueue<E>(this.queueSize);
/*     */     
/* 104 */     if (this.discardingThreshold == -1)
/* 105 */       this.discardingThreshold = this.queueSize / 5; 
/* 106 */     addInfo("Setting discardingThreshold to " + this.discardingThreshold);
/* 107 */     this.worker.setDaemon(true);
/* 108 */     this.worker.setName("AsyncAppender-Worker-" + getName());
/*     */     
/* 110 */     super.start();
/* 111 */     this.worker.start();
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() {
/* 116 */     if (!isStarted()) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 122 */     super.stop();
/*     */ 
/*     */ 
/*     */     
/* 126 */     this.worker.interrupt();
/*     */     
/* 128 */     InterruptUtil interruptUtil = new InterruptUtil(this.context);
/*     */     
/*     */     try {
/* 131 */       interruptUtil.maskInterruptFlag();
/*     */       
/* 133 */       this.worker.join(this.maxFlushTime);
/*     */ 
/*     */       
/* 136 */       if (this.worker.isAlive()) {
/* 137 */         addWarn("Max queue flush timeout (" + this.maxFlushTime + " ms) exceeded. Approximately " + this.blockingQueue.size() + " queued events were possibly discarded.");
/*     */       } else {
/*     */         
/* 140 */         addInfo("Queue flush finished successfully within timeout.");
/*     */       }
/*     */     
/* 143 */     } catch (InterruptedException e) {
/* 144 */       int remaining = this.blockingQueue.size();
/* 145 */       addError("Failed to join worker thread. " + remaining + " queued events may be discarded.", e);
/*     */     } finally {
/* 147 */       interruptUtil.unmaskInterruptFlag();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void append(E eventObject) {
/* 157 */     if (isQueueBelowDiscardingThreshold() && isDiscardable(eventObject)) {
/*     */       return;
/*     */     }
/* 160 */     preprocess(eventObject);
/* 161 */     put(eventObject);
/*     */   }
/*     */   
/*     */   private boolean isQueueBelowDiscardingThreshold() {
/* 165 */     return (this.blockingQueue.remainingCapacity() < this.discardingThreshold);
/*     */   }
/*     */   
/*     */   private void put(E eventObject) {
/* 169 */     if (this.neverBlock) {
/* 170 */       this.blockingQueue.offer(eventObject);
/*     */     } else {
/* 172 */       putUninterruptibly(eventObject);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void putUninterruptibly(E eventObject) {
/* 177 */     boolean interrupted = false;
/*     */     try {
/*     */       while (true) {
/*     */         try {
/* 181 */           this.blockingQueue.put(eventObject);
/*     */           break;
/* 183 */         } catch (InterruptedException e) {
/* 184 */           interrupted = true;
/*     */         } 
/*     */       } 
/*     */     } finally {
/* 188 */       if (interrupted) {
/* 189 */         Thread.currentThread().interrupt();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getQueueSize() {
/* 195 */     return this.queueSize;
/*     */   }
/*     */   
/*     */   public void setQueueSize(int queueSize) {
/* 199 */     this.queueSize = queueSize;
/*     */   }
/*     */   
/*     */   public int getDiscardingThreshold() {
/* 203 */     return this.discardingThreshold;
/*     */   }
/*     */   
/*     */   public void setDiscardingThreshold(int discardingThreshold) {
/* 207 */     this.discardingThreshold = discardingThreshold;
/*     */   }
/*     */   
/*     */   public int getMaxFlushTime() {
/* 211 */     return this.maxFlushTime;
/*     */   }
/*     */   
/*     */   public void setMaxFlushTime(int maxFlushTime) {
/* 215 */     this.maxFlushTime = maxFlushTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumberOfElementsInQueue() {
/* 224 */     return this.blockingQueue.size();
/*     */   }
/*     */   
/*     */   public void setNeverBlock(boolean neverBlock) {
/* 228 */     this.neverBlock = neverBlock;
/*     */   }
/*     */   
/*     */   public boolean isNeverBlock() {
/* 232 */     return this.neverBlock;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRemainingCapacity() {
/* 242 */     return this.blockingQueue.remainingCapacity();
/*     */   }
/*     */   
/*     */   public void addAppender(Appender<E> newAppender) {
/* 246 */     if (this.appenderCount == 0) {
/* 247 */       this.appenderCount++;
/* 248 */       addInfo("Attaching appender named [" + newAppender.getName() + "] to AsyncAppender.");
/* 249 */       this.aai.addAppender(newAppender);
/*     */     } else {
/* 251 */       addWarn("One and only one appender may be attached to AsyncAppender.");
/* 252 */       addWarn("Ignoring additional appender named [" + newAppender.getName() + "]");
/*     */     } 
/*     */   }
/*     */   
/*     */   public Iterator<Appender<E>> iteratorForAppenders() {
/* 257 */     return this.aai.iteratorForAppenders();
/*     */   }
/*     */   
/*     */   public Appender<E> getAppender(String name) {
/* 261 */     return this.aai.getAppender(name);
/*     */   }
/*     */   
/*     */   public boolean isAttached(Appender<E> eAppender) {
/* 265 */     return this.aai.isAttached(eAppender);
/*     */   }
/*     */   
/*     */   public void detachAndStopAllAppenders() {
/* 269 */     this.aai.detachAndStopAllAppenders();
/*     */   }
/*     */   
/*     */   public boolean detachAppender(Appender<E> eAppender) {
/* 273 */     return this.aai.detachAppender(eAppender);
/*     */   }
/*     */   
/*     */   public boolean detachAppender(String name) {
/* 277 */     return this.aai.detachAppender(name);
/*     */   }
/*     */   
/*     */   class Worker
/*     */     extends Thread {
/*     */     public void run() {
/* 283 */       AsyncAppenderBase<E> parent = AsyncAppenderBase.this;
/* 284 */       AppenderAttachableImpl<E> aai = parent.aai;
/*     */ 
/*     */       
/* 287 */       while (parent.isStarted()) {
/*     */         try {
/* 289 */           E e = parent.blockingQueue.take();
/* 290 */           aai.appendLoopOnAppenders(e);
/* 291 */         } catch (InterruptedException ie) {
/*     */           break;
/*     */         } 
/*     */       } 
/*     */       
/* 296 */       AsyncAppenderBase.this.addInfo("Worker thread will flush remaining events before exiting. ");
/*     */       
/* 298 */       for (E e : parent.blockingQueue) {
/* 299 */         aai.appendLoopOnAppenders(e);
/* 300 */         parent.blockingQueue.remove(e);
/*     */       } 
/*     */       
/* 303 */       aai.detachAndStopAllAppenders();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\AsyncAppenderBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */
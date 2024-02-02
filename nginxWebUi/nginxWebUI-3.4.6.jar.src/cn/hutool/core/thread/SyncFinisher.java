/*     */ package cn.hutool.core.thread;
/*     */ 
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CountDownLatch;
/*     */ import java.util.concurrent.ExecutorService;
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
/*     */ public class SyncFinisher
/*     */   implements Closeable
/*     */ {
/*     */   private final Set<Worker> workers;
/*     */   private final int threadSize;
/*     */   private ExecutorService executorService;
/*     */   private boolean isBeginAtSameTime;
/*     */   private final CountDownLatch beginLatch;
/*     */   private CountDownLatch endLatch;
/*     */   
/*     */   public SyncFinisher(int threadSize) {
/*  48 */     this.beginLatch = new CountDownLatch(1);
/*  49 */     this.threadSize = threadSize;
/*  50 */     this.workers = new LinkedHashSet<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SyncFinisher setBeginAtSameTime(boolean isBeginAtSameTime) {
/*  60 */     this.isBeginAtSameTime = isBeginAtSameTime;
/*  61 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SyncFinisher addRepeatWorker(final Runnable runnable) {
/*  71 */     for (int i = 0; i < this.threadSize; i++) {
/*  72 */       addWorker(new Worker()
/*     */           {
/*     */             public void work() {
/*  75 */               runnable.run();
/*     */             }
/*     */           });
/*     */     } 
/*  79 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SyncFinisher addWorker(final Runnable runnable) {
/*  89 */     return addWorker(new Worker()
/*     */         {
/*     */           public void work() {
/*  92 */             runnable.run();
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized SyncFinisher addWorker(Worker worker) {
/* 104 */     this.workers.add(worker);
/* 105 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/* 113 */     start(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start(boolean sync) {
/* 124 */     this.endLatch = new CountDownLatch(this.workers.size());
/*     */     
/* 126 */     if (null == this.executorService || this.executorService.isShutdown()) {
/* 127 */       this.executorService = ThreadUtil.newExecutor(this.threadSize);
/*     */     }
/* 129 */     for (Worker worker : this.workers) {
/* 130 */       this.executorService.submit(worker);
/*     */     }
/*     */     
/* 133 */     this.beginLatch.countDown();
/*     */     
/* 135 */     if (sync) {
/*     */       try {
/* 137 */         this.endLatch.await();
/* 138 */       } catch (InterruptedException e) {
/* 139 */         throw new UtilException(e);
/*     */       } 
/*     */     }
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
/*     */   public void stop() {
/* 154 */     if (null != this.executorService) {
/* 155 */       this.executorService.shutdown();
/*     */     }
/* 157 */     this.executorService = null;
/*     */     
/* 159 */     clearWorker();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearWorker() {
/* 166 */     this.workers.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long count() {
/* 175 */     return this.endLatch.getCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 180 */     stop();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract class Worker
/*     */     implements Runnable
/*     */   {
/*     */     public void run() {
/* 193 */       if (SyncFinisher.this.isBeginAtSameTime) {
/*     */         try {
/* 195 */           SyncFinisher.this.beginLatch.await();
/* 196 */         } catch (InterruptedException e) {
/* 197 */           throw new UtilException(e);
/*     */         } 
/*     */       }
/*     */       try {
/* 201 */         work();
/*     */       } finally {
/* 203 */         SyncFinisher.this.endLatch.countDown();
/*     */       } 
/*     */     }
/*     */     
/*     */     public abstract void work();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\SyncFinisher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
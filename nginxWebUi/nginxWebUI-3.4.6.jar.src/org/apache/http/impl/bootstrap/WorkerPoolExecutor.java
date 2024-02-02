/*    */ package org.apache.http.impl.bootstrap;
/*    */ 
/*    */ import java.util.HashSet;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.concurrent.BlockingQueue;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import java.util.concurrent.ThreadPoolExecutor;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class WorkerPoolExecutor
/*    */   extends ThreadPoolExecutor
/*    */ {
/*    */   private final Map<Worker, Boolean> workerSet;
/*    */   
/*    */   public WorkerPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
/* 52 */     super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
/* 53 */     this.workerSet = new ConcurrentHashMap<Worker, Boolean>();
/*    */   }
/*    */ 
/*    */   
/*    */   protected void beforeExecute(Thread t, Runnable r) {
/* 58 */     if (r instanceof Worker) {
/* 59 */       this.workerSet.put((Worker)r, Boolean.TRUE);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   protected void afterExecute(Runnable r, Throwable t) {
/* 65 */     if (r instanceof Worker) {
/* 66 */       this.workerSet.remove(r);
/*    */     }
/*    */   }
/*    */   
/*    */   public Set<Worker> getWorkers() {
/* 71 */     return new HashSet<Worker>(this.workerSet.keySet());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\bootstrap\WorkerPoolExecutor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */
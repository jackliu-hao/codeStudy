/*    */ package cn.hutool.cache;
/*    */ 
/*    */ import cn.hutool.core.thread.ThreadUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.ScheduledExecutorService;
/*    */ import java.util.concurrent.ScheduledFuture;
/*    */ import java.util.concurrent.ScheduledThreadPoolExecutor;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum GlobalPruneTimer
/*    */ {
/* 22 */   INSTANCE;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 27 */   private final AtomicInteger cacheTaskNumber = new AtomicInteger(1);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private ScheduledExecutorService pruneTimer;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   GlobalPruneTimer() {
/* 38 */     create();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ScheduledFuture<?> schedule(Runnable task, long delay) {
/* 49 */     return this.pruneTimer.scheduleAtFixedRate(task, delay, delay, TimeUnit.MILLISECONDS);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void create() {
/* 56 */     if (null != this.pruneTimer) {
/* 57 */       shutdownNow();
/*    */     }
/* 59 */     this.pruneTimer = new ScheduledThreadPoolExecutor(1, r -> ThreadUtil.newThread(r, StrUtil.format("Pure-Timer-{}", new Object[] { Integer.valueOf(this.cacheTaskNumber.getAndIncrement()) })));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void shutdown() {
/* 66 */     if (null != this.pruneTimer) {
/* 67 */       this.pruneTimer.shutdown();
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<Runnable> shutdownNow() {
/* 77 */     if (null != this.pruneTimer) {
/* 78 */       return this.pruneTimer.shutdownNow();
/*    */     }
/* 80 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\GlobalPruneTimer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
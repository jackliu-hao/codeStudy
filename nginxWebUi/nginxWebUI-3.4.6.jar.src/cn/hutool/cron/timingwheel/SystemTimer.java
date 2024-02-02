/*     */ package cn.hutool.cron.timingwheel;
/*     */ 
/*     */ import cn.hutool.core.thread.ThreadUtil;
/*     */ import java.util.concurrent.DelayQueue;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ public class SystemTimer
/*     */ {
/*     */   private final TimingWheel timeWheel;
/*  23 */   private final DelayQueue<TimerTaskList> delayQueue = new DelayQueue<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  28 */   private long delayQueueTimeout = 100L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ExecutorService bossThreadPool;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SystemTimer() {
/*  39 */     this.timeWheel = new TimingWheel(1L, 20, this.delayQueue::offer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SystemTimer setDelayQueueTimeout(long delayQueueTimeout) {
/*  48 */     this.delayQueueTimeout = delayQueueTimeout;
/*  49 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SystemTimer start() {
/*  58 */     this.bossThreadPool = ThreadUtil.newSingleExecutor();
/*  59 */     this.bossThreadPool.submit(() -> {
/*     */           do {
/*     */           
/*     */           } while (false != advanceClock());
/*     */         });
/*     */ 
/*     */     
/*  66 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {
/*  73 */     this.bossThreadPool.shutdown();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addTask(TimerTask timerTask) {
/*  83 */     if (false == this.timeWheel.addTask(timerTask)) {
/*  84 */       ThreadUtil.execAsync(timerTask.getTask());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean advanceClock() {
/*     */     try {
/*  95 */       TimerTaskList timerTaskList = poll();
/*  96 */       if (null != timerTaskList) {
/*     */         
/*  98 */         this.timeWheel.advanceClock(timerTaskList.getExpire());
/*     */         
/* 100 */         timerTaskList.flush(this::addTask);
/*     */       } 
/* 102 */     } catch (InterruptedException ignore) {
/* 103 */       return false;
/*     */     } 
/* 105 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TimerTaskList poll() throws InterruptedException {
/* 114 */     return (this.delayQueueTimeout > 0L) ? this.delayQueue
/* 115 */       .poll(this.delayQueueTimeout, TimeUnit.MILLISECONDS) : this.delayQueue
/* 116 */       .poll();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\timingwheel\SystemTimer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
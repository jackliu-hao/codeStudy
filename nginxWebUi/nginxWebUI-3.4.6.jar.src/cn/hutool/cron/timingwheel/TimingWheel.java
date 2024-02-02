/*     */ package cn.hutool.cron.timingwheel;
/*     */ 
/*     */ import cn.hutool.log.StaticLog;
/*     */ import java.util.function.Consumer;
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
/*     */ public class TimingWheel
/*     */ {
/*     */   private final long tickMs;
/*     */   private final int wheelSize;
/*     */   private final long interval;
/*     */   private final TimerTaskList[] timerTaskLists;
/*     */   private long currentTime;
/*     */   private volatile TimingWheel overflowWheel;
/*     */   private final Consumer<TimerTaskList> consumer;
/*     */   
/*     */   public TimingWheel(long tickMs, int wheelSize, Consumer<TimerTaskList> consumer) {
/*  59 */     this(tickMs, wheelSize, System.currentTimeMillis(), consumer);
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
/*     */   public TimingWheel(long tickMs, int wheelSize, long currentTime, Consumer<TimerTaskList> consumer) {
/*  71 */     this.tickMs = tickMs;
/*  72 */     this.wheelSize = wheelSize;
/*  73 */     this.interval = tickMs * wheelSize;
/*  74 */     this.timerTaskLists = new TimerTaskList[wheelSize];
/*     */     
/*  76 */     this.currentTime = currentTime - currentTime % tickMs;
/*  77 */     this.consumer = consumer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean addTask(TimerTask timerTask) {
/*  87 */     long expiration = timerTask.getDelayMs();
/*     */     
/*  89 */     if (expiration < this.currentTime + this.tickMs)
/*  90 */       return false; 
/*  91 */     if (expiration < this.currentTime + this.interval) {
/*     */       
/*  93 */       long virtualId = expiration / this.tickMs;
/*  94 */       int index = (int)(virtualId % this.wheelSize);
/*  95 */       StaticLog.debug("tickMs: {} ------index: {} ------expiration: {}", new Object[] { Long.valueOf(this.tickMs), Integer.valueOf(index), Long.valueOf(expiration) });
/*     */       
/*  97 */       TimerTaskList timerTaskList = this.timerTaskLists[index];
/*  98 */       if (null == timerTaskList) {
/*  99 */         timerTaskList = new TimerTaskList();
/* 100 */         this.timerTaskLists[index] = timerTaskList;
/*     */       } 
/* 102 */       timerTaskList.addTask(timerTask);
/* 103 */       if (timerTaskList.setExpiration(virtualId * this.tickMs))
/*     */       {
/* 105 */         this.consumer.accept(timerTaskList);
/*     */       }
/*     */     } else {
/*     */       
/* 109 */       TimingWheel timeWheel = getOverflowWheel();
/* 110 */       timeWheel.addTask(timerTask);
/*     */     } 
/* 112 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void advanceClock(long timestamp) {
/* 121 */     if (timestamp >= this.currentTime + this.tickMs) {
/* 122 */       this.currentTime = timestamp - timestamp % this.tickMs;
/* 123 */       if (this.overflowWheel != null)
/*     */       {
/* 125 */         getOverflowWheel().advanceClock(timestamp);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TimingWheel getOverflowWheel() {
/* 134 */     if (this.overflowWheel == null) {
/* 135 */       synchronized (this) {
/* 136 */         if (this.overflowWheel == null) {
/* 137 */           this.overflowWheel = new TimingWheel(this.interval, this.wheelSize, this.currentTime, this.consumer);
/*     */         }
/*     */       } 
/*     */     }
/* 141 */     return this.overflowWheel;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\timingwheel\TimingWheel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
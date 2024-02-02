/*     */ package cn.hutool.cron.timingwheel;
/*     */ 
/*     */ import java.util.concurrent.Delayed;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicLong;
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
/*     */ public class TimerTaskList
/*     */   implements Delayed
/*     */ {
/*     */   private final AtomicLong expire;
/*     */   private final TimerTask root;
/*     */   
/*     */   public TimerTaskList() {
/*  29 */     this.expire = new AtomicLong(-1L);
/*     */     
/*  31 */     this.root = new TimerTask(null, -1L);
/*  32 */     this.root.prev = this.root;
/*  33 */     this.root.next = this.root;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean setExpiration(long expire) {
/*  43 */     return (this.expire.getAndSet(expire) != expire);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getExpire() {
/*  51 */     return this.expire.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addTask(TimerTask timerTask) {
/*  60 */     synchronized (this) {
/*  61 */       if (timerTask.timerTaskList == null) {
/*  62 */         timerTask.timerTaskList = this;
/*  63 */         TimerTask tail = this.root.prev;
/*  64 */         timerTask.next = this.root;
/*  65 */         timerTask.prev = tail;
/*  66 */         tail.next = timerTask;
/*  67 */         this.root.prev = timerTask;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeTask(TimerTask timerTask) {
/*  78 */     synchronized (this) {
/*  79 */       if (equals(timerTask.timerTaskList)) {
/*  80 */         timerTask.next.prev = timerTask.prev;
/*  81 */         timerTask.prev.next = timerTask.next;
/*  82 */         timerTask.timerTaskList = null;
/*  83 */         timerTask.next = null;
/*  84 */         timerTask.prev = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void flush(Consumer<TimerTask> flush) {
/*  95 */     TimerTask timerTask = this.root.next;
/*  96 */     while (false == timerTask.equals(this.root)) {
/*  97 */       removeTask(timerTask);
/*  98 */       flush.accept(timerTask);
/*  99 */       timerTask = this.root.next;
/*     */     } 
/* 101 */     this.expire.set(-1L);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getDelay(TimeUnit unit) {
/* 106 */     return Math.max(0L, unit.convert(this.expire.get() - System.currentTimeMillis(), TimeUnit.MILLISECONDS));
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(Delayed o) {
/* 111 */     if (o instanceof TimerTaskList) {
/* 112 */       return Long.compare(this.expire.get(), ((TimerTaskList)o).expire.get());
/*     */     }
/* 114 */     return 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\timingwheel\TimerTaskList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
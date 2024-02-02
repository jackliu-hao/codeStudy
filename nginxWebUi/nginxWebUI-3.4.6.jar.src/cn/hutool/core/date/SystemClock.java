/*    */ package cn.hutool.core.date;
/*    */ 
/*    */ import java.sql.Timestamp;
/*    */ import java.util.concurrent.Executors;
/*    */ import java.util.concurrent.ScheduledExecutorService;
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
/*    */ public class SystemClock
/*    */ {
/*    */   private final long period;
/*    */   private volatile long now;
/*    */   
/*    */   public SystemClock(long period) {
/* 30 */     this.period = period;
/* 31 */     this.now = System.currentTimeMillis();
/* 32 */     scheduleClockUpdating();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void scheduleClockUpdating() {
/* 39 */     ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
/*    */           Thread thread = new Thread(runnable, "System Clock");
/*    */           thread.setDaemon(true);
/*    */           return thread;
/*    */         });
/* 44 */     scheduler.scheduleAtFixedRate(() -> this.now = System.currentTimeMillis(), this.period, this.period, TimeUnit.MILLISECONDS);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private long currentTimeMillis() {
/* 51 */     return this.now;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static class InstanceHolder
/*    */   {
/* 61 */     public static final SystemClock INSTANCE = new SystemClock(1L);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static long now() {
/* 68 */     return InstanceHolder.INSTANCE.currentTimeMillis();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String nowDate() {
/* 75 */     return (new Timestamp(InstanceHolder.INSTANCE.currentTimeMillis())).toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\SystemClock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
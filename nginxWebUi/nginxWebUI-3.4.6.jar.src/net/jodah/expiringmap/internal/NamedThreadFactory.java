/*    */ package net.jodah.expiringmap.internal;
/*    */ 
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ 
/*    */ 
/*    */ public class NamedThreadFactory
/*    */   implements ThreadFactory
/*    */ {
/* 10 */   private final AtomicInteger threadNumber = new AtomicInteger(1);
/*    */ 
/*    */   
/*    */   private final String nameFormat;
/*    */ 
/*    */ 
/*    */   
/*    */   public NamedThreadFactory(String nameFormat) {
/* 18 */     this.nameFormat = nameFormat;
/*    */   }
/*    */ 
/*    */   
/*    */   public Thread newThread(Runnable r) {
/* 23 */     Thread thread = new Thread(r, String.format(this.nameFormat, new Object[] { Integer.valueOf(this.threadNumber.getAndIncrement()) }));
/* 24 */     thread.setDaemon(true);
/* 25 */     return thread;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\net\jodah\expiringmap\internal\NamedThreadFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */
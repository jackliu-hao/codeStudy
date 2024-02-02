/*    */ package cn.hutool.core.thread;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.util.concurrent.ThreadFactory;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NamedThreadFactory
/*    */   implements ThreadFactory
/*    */ {
/*    */   private final String prefix;
/*    */   private final ThreadGroup group;
/* 27 */   private final AtomicInteger threadNumber = new AtomicInteger(1);
/*    */ 
/*    */ 
/*    */   
/*    */   private final boolean isDaemon;
/*    */ 
/*    */ 
/*    */   
/*    */   private final Thread.UncaughtExceptionHandler handler;
/*    */ 
/*    */ 
/*    */   
/*    */   public NamedThreadFactory(String prefix, boolean isDaemon) {
/* 40 */     this(prefix, null, isDaemon);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public NamedThreadFactory(String prefix, ThreadGroup threadGroup, boolean isDaemon) {
/* 51 */     this(prefix, threadGroup, isDaemon, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public NamedThreadFactory(String prefix, ThreadGroup threadGroup, boolean isDaemon, Thread.UncaughtExceptionHandler handler) {
/* 63 */     this.prefix = StrUtil.isBlank(prefix) ? "Hutool" : prefix;
/* 64 */     if (null == threadGroup) {
/* 65 */       threadGroup = ThreadUtil.currentThreadGroup();
/*    */     }
/* 67 */     this.group = threadGroup;
/* 68 */     this.isDaemon = isDaemon;
/* 69 */     this.handler = handler;
/*    */   }
/*    */ 
/*    */   
/*    */   public Thread newThread(Runnable r) {
/* 74 */     Thread t = new Thread(this.group, r, StrUtil.format("{}{}", new Object[] { this.prefix, Integer.valueOf(this.threadNumber.getAndIncrement()) }));
/*    */ 
/*    */     
/* 77 */     if (false == t.isDaemon()) {
/* 78 */       if (this.isDaemon)
/*    */       {
/* 80 */         t.setDaemon(true);
/*    */       }
/* 82 */     } else if (false == this.isDaemon) {
/*    */       
/* 84 */       t.setDaemon(false);
/*    */     } 
/*    */     
/* 87 */     if (null != this.handler) {
/* 88 */       t.setUncaughtExceptionHandler(this.handler);
/*    */     }
/*    */     
/* 91 */     if (5 != t.getPriority())
/*    */     {
/* 93 */       t.setPriority(5);
/*    */     }
/* 95 */     return t;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\NamedThreadFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
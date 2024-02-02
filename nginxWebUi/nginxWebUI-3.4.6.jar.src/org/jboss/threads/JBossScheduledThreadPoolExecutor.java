/*     */ package org.jboss.threads;
/*     */ 
/*     */ import java.util.concurrent.RejectedExecutionHandler;
/*     */ import java.util.concurrent.ScheduledThreadPoolExecutor;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
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
/*     */ public final class JBossScheduledThreadPoolExecutor
/*     */   extends ScheduledThreadPoolExecutor
/*     */ {
/*  30 */   private final AtomicInteger rejectCount = new AtomicInteger();
/*     */   private final Runnable terminationTask;
/*     */   
/*     */   public JBossScheduledThreadPoolExecutor(int corePoolSize, Runnable terminationTask) {
/*  34 */     super(corePoolSize);
/*  35 */     this.terminationTask = terminationTask;
/*  36 */     setRejectedExecutionHandler(super.getRejectedExecutionHandler());
/*     */   }
/*     */   
/*     */   public JBossScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, Runnable terminationTask) {
/*  40 */     super(corePoolSize, threadFactory);
/*  41 */     this.terminationTask = terminationTask;
/*  42 */     setRejectedExecutionHandler(super.getRejectedExecutionHandler());
/*     */   }
/*     */   
/*     */   public JBossScheduledThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler, Runnable terminationTask) {
/*  46 */     super(corePoolSize);
/*  47 */     this.terminationTask = terminationTask;
/*  48 */     setRejectedExecutionHandler(handler);
/*     */   }
/*     */   
/*     */   public JBossScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler, Runnable terminationTask) {
/*  52 */     super(corePoolSize, threadFactory);
/*  53 */     this.terminationTask = terminationTask;
/*  54 */     setRejectedExecutionHandler(handler);
/*     */   }
/*     */   
/*     */   public long getKeepAliveTime() {
/*  58 */     return getKeepAliveTime(TimeUnit.MILLISECONDS);
/*     */   }
/*     */   
/*     */   public void setKeepAliveTime(long milliseconds) {
/*  62 */     super.setKeepAliveTime(milliseconds, TimeUnit.MILLISECONDS);
/*  63 */     allowCoreThreadTimeOut((milliseconds < Long.MAX_VALUE));
/*     */   }
/*     */   
/*     */   public void setKeepAliveTime(long time, TimeUnit unit) {
/*  67 */     super.setKeepAliveTime(time, unit);
/*  68 */     allowCoreThreadTimeOut((time < Long.MAX_VALUE));
/*     */   }
/*     */   
/*     */   public int getRejectedCount() {
/*  72 */     return this.rejectCount.get();
/*     */   }
/*     */   
/*     */   public int getCurrentThreadCount() {
/*  76 */     return getActiveCount();
/*     */   }
/*     */   
/*     */   public int getLargestThreadCount() {
/*  80 */     return getLargestPoolSize();
/*     */   }
/*     */   
/*     */   public int getMaxThreads() {
/*  84 */     return getCorePoolSize();
/*     */   }
/*     */   
/*     */   public void setMaxThreads(int newSize) {
/*  88 */     setCorePoolSize(newSize);
/*     */   }
/*     */   
/*     */   public RejectedExecutionHandler getRejectedExecutionHandler() {
/*  92 */     return ((CountingRejectHandler)super.getRejectedExecutionHandler()).getDelegate();
/*     */   }
/*     */   
/*     */   public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
/*  96 */     super.setRejectedExecutionHandler(new CountingRejectHandler(handler));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getQueueSize() {
/* 101 */     return getQueue().size();
/*     */   }
/*     */   
/*     */   protected void terminated() {
/* 105 */     this.terminationTask.run();
/*     */   }
/*     */   
/*     */   private final class CountingRejectHandler implements RejectedExecutionHandler {
/*     */     private final RejectedExecutionHandler delegate;
/*     */     
/*     */     public CountingRejectHandler(RejectedExecutionHandler delegate) {
/* 112 */       this.delegate = delegate;
/*     */     }
/*     */     
/*     */     public RejectedExecutionHandler getDelegate() {
/* 116 */       return this.delegate;
/*     */     }
/*     */     
/*     */     public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
/* 120 */       JBossScheduledThreadPoolExecutor.this.rejectCount.incrementAndGet();
/* 121 */       if (JBossScheduledThreadPoolExecutor.this.isShutdown()) {
/* 122 */         throw Messages.msg.shutDownInitiated();
/*     */       }
/* 124 */       this.delegate.rejectedExecution(r, executor);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\JBossScheduledThreadPoolExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
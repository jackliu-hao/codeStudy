/*     */ package org.jboss.threads;
/*     */ 
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.RejectedExecutionHandler;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.jboss.threads.management.ManageableThreadPoolExecutorService;
/*     */ import org.jboss.threads.management.StandardThreadPoolMXBean;
/*     */ import org.wildfly.common.Assert;
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
/*     */ public final class ManagedThreadPoolExecutor
/*     */   extends ThreadPoolExecutor
/*     */   implements ManageableThreadPoolExecutorService
/*     */ {
/*     */   private final Runnable terminationTask;
/*  38 */   private final StandardThreadPoolMXBean mxBean = new MXBeanImpl();
/*  39 */   private volatile Executor handoffExecutor = JBossExecutors.rejectingExecutor();
/*  40 */   private static final RejectedExecutionHandler HANDLER = new RejectedExecutionHandler() {
/*     */       public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
/*  42 */         ((ManagedThreadPoolExecutor)executor).reject(r);
/*     */       }
/*     */     };
/*     */   
/*     */   public ManagedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, Runnable terminationTask) {
/*  47 */     super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, HANDLER);
/*  48 */     this.terminationTask = terminationTask;
/*     */   }
/*     */   
/*     */   public ManagedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, Runnable terminationTask) {
/*  52 */     super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, HANDLER);
/*  53 */     this.terminationTask = terminationTask;
/*     */   }
/*     */   
/*     */   public ManagedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, Executor handoffExecutor, Runnable terminationTask) {
/*  57 */     super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, HANDLER);
/*  58 */     this.terminationTask = terminationTask;
/*  59 */     this.handoffExecutor = handoffExecutor;
/*     */   }
/*     */   
/*     */   public ManagedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, Executor handoffExecutor, Runnable terminationTask) {
/*  63 */     super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, HANDLER);
/*  64 */     this.terminationTask = terminationTask;
/*  65 */     this.handoffExecutor = handoffExecutor;
/*     */   }
/*     */   
/*     */   public StandardThreadPoolMXBean getThreadPoolMXBean() {
/*  69 */     return this.mxBean;
/*     */   }
/*     */   
/*     */   public Executor getHandoffExecutor() {
/*  73 */     return this.handoffExecutor;
/*     */   }
/*     */   
/*     */   public void setHandoffExecutor(Executor handoffExecutor) {
/*  77 */     Assert.checkNotNullParam("handoffExecutor", handoffExecutor);
/*  78 */     this.handoffExecutor = handoffExecutor;
/*  79 */     setRejectedExecutionHandler(HANDLER);
/*     */   }
/*     */   
/*     */   void reject(Runnable r) {
/*  83 */     this.handoffExecutor.execute(r);
/*     */   }
/*     */   
/*     */   protected void terminated() {
/*  87 */     this.terminationTask.run();
/*     */   }
/*     */   
/*     */   class MXBeanImpl implements StandardThreadPoolMXBean {
/*     */     public float getGrowthResistance() {
/*  92 */       return 1.0F;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setGrowthResistance(float value) {}
/*     */ 
/*     */     
/*     */     public boolean isGrowthResistanceSupported() {
/* 100 */       return false;
/*     */     }
/*     */     
/*     */     public int getCorePoolSize() {
/* 104 */       return ManagedThreadPoolExecutor.this.getCorePoolSize();
/*     */     }
/*     */     
/*     */     public void setCorePoolSize(int corePoolSize) {
/* 108 */       ManagedThreadPoolExecutor.this.setCorePoolSize(corePoolSize);
/*     */     }
/*     */     
/*     */     public boolean isCorePoolSizeSupported() {
/* 112 */       return true;
/*     */     }
/*     */     
/*     */     public boolean prestartCoreThread() {
/* 116 */       return ManagedThreadPoolExecutor.this.prestartCoreThread();
/*     */     }
/*     */     
/*     */     public int prestartAllCoreThreads() {
/* 120 */       return ManagedThreadPoolExecutor.this.prestartAllCoreThreads();
/*     */     }
/*     */     
/*     */     public boolean isCoreThreadPrestartSupported() {
/* 124 */       return true;
/*     */     }
/*     */     
/*     */     public int getMaximumPoolSize() {
/* 128 */       return ManagedThreadPoolExecutor.this.getMaximumPoolSize();
/*     */     }
/*     */     
/*     */     public void setMaximumPoolSize(int maxPoolSize) {
/* 132 */       ManagedThreadPoolExecutor.this.setMaximumPoolSize(maxPoolSize);
/*     */     }
/*     */     
/*     */     public int getPoolSize() {
/* 136 */       return ManagedThreadPoolExecutor.this.getPoolSize();
/*     */     }
/*     */     
/*     */     public int getLargestPoolSize() {
/* 140 */       return ManagedThreadPoolExecutor.this.getLargestPoolSize();
/*     */     }
/*     */     
/*     */     public int getActiveCount() {
/* 144 */       return ManagedThreadPoolExecutor.this.getActiveCount();
/*     */     }
/*     */     
/*     */     public boolean isAllowCoreThreadTimeOut() {
/* 148 */       return ManagedThreadPoolExecutor.this.allowsCoreThreadTimeOut();
/*     */     }
/*     */     
/*     */     public void setAllowCoreThreadTimeOut(boolean value) {
/* 152 */       ManagedThreadPoolExecutor.this.allowCoreThreadTimeOut(value);
/*     */     }
/*     */     
/*     */     public long getKeepAliveTimeSeconds() {
/* 156 */       return ManagedThreadPoolExecutor.this.getKeepAliveTime(TimeUnit.SECONDS);
/*     */     }
/*     */     
/*     */     public void setKeepAliveTimeSeconds(long seconds) {
/* 160 */       ManagedThreadPoolExecutor.this.setKeepAliveTime(seconds, TimeUnit.SECONDS);
/*     */     }
/*     */     
/*     */     public int getMaximumQueueSize() {
/* 164 */       return 0;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setMaximumQueueSize(int size) {}
/*     */     
/*     */     public int getQueueSize() {
/* 171 */       return ManagedThreadPoolExecutor.this.getQueue().size();
/*     */     }
/*     */     
/*     */     public int getLargestQueueSize() {
/* 175 */       return 0;
/*     */     }
/*     */     
/*     */     public boolean isQueueBounded() {
/* 179 */       return false;
/*     */     }
/*     */     
/*     */     public boolean isQueueSizeModifiable() {
/* 183 */       return false;
/*     */     }
/*     */     
/*     */     public boolean isShutdown() {
/* 187 */       return ManagedThreadPoolExecutor.this.isShutdown();
/*     */     }
/*     */     
/*     */     public boolean isTerminating() {
/* 191 */       return ManagedThreadPoolExecutor.this.isTerminating();
/*     */     }
/*     */     
/*     */     public boolean isTerminated() {
/* 195 */       return ManagedThreadPoolExecutor.this.isTerminated();
/*     */     }
/*     */     
/*     */     public long getSubmittedTaskCount() {
/* 199 */       return ManagedThreadPoolExecutor.this.getTaskCount();
/*     */     }
/*     */     
/*     */     public long getRejectedTaskCount() {
/* 203 */       return 0L;
/*     */     }
/*     */     
/*     */     public long getCompletedTaskCount() {
/* 207 */       return ManagedThreadPoolExecutor.this.getCompletedTaskCount();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\ManagedThreadPoolExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
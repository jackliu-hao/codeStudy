/*     */ package cn.hutool.core.thread;
/*     */ 
/*     */ import cn.hutool.core.builder.Builder;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.RejectedExecutionHandler;
/*     */ import java.util.concurrent.SynchronousQueue;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExecutorBuilder
/*     */   implements Builder<ThreadPoolExecutor>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final int DEFAULT_QUEUE_CAPACITY = 1024;
/*     */   private int corePoolSize;
/*  43 */   private int maxPoolSize = Integer.MAX_VALUE;
/*     */ 
/*     */ 
/*     */   
/*  47 */   private long keepAliveTime = TimeUnit.SECONDS.toNanos(60L);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private BlockingQueue<Runnable> workQueue;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ThreadFactory threadFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private RejectedExecutionHandler handler;
/*     */ 
/*     */ 
/*     */   
/*     */   private Boolean allowCoreThreadTimeOut;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExecutorBuilder setCorePoolSize(int corePoolSize) {
/*  72 */     this.corePoolSize = corePoolSize;
/*  73 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExecutorBuilder setMaxPoolSize(int maxPoolSize) {
/*  83 */     this.maxPoolSize = maxPoolSize;
/*  84 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExecutorBuilder setKeepAliveTime(long keepAliveTime, TimeUnit unit) {
/*  95 */     return setKeepAliveTime(unit.toNanos(keepAliveTime));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExecutorBuilder setKeepAliveTime(long keepAliveTime) {
/* 105 */     this.keepAliveTime = keepAliveTime;
/* 106 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExecutorBuilder setWorkQueue(BlockingQueue<Runnable> workQueue) {
/* 124 */     this.workQueue = workQueue;
/* 125 */     return this;
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
/*     */   public ExecutorBuilder useArrayBlockingQueue(int capacity) {
/* 137 */     return setWorkQueue(new ArrayBlockingQueue<>(capacity));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExecutorBuilder useSynchronousQueue() {
/* 148 */     return useSynchronousQueue(false);
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
/*     */   public ExecutorBuilder useSynchronousQueue(boolean fair) {
/* 160 */     return setWorkQueue(new SynchronousQueue<>(fair));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExecutorBuilder setThreadFactory(ThreadFactory threadFactory) {
/* 171 */     this.threadFactory = threadFactory;
/* 172 */     return this;
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
/*     */   
/*     */   public ExecutorBuilder setHandler(RejectedExecutionHandler handler) {
/* 185 */     this.handler = handler;
/* 186 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExecutorBuilder setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
/* 196 */     this.allowCoreThreadTimeOut = Boolean.valueOf(allowCoreThreadTimeOut);
/* 197 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExecutorBuilder create() {
/* 206 */     return new ExecutorBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ThreadPoolExecutor build() {
/* 214 */     return build(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExecutorService buildFinalizable() {
/* 224 */     return new FinalizableDelegatedExecutorService(build());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ThreadPoolExecutor build(ExecutorBuilder builder) {
/*     */     BlockingQueue<Runnable> workQueue;
/* 234 */     int corePoolSize = builder.corePoolSize;
/* 235 */     int maxPoolSize = builder.maxPoolSize;
/* 236 */     long keepAliveTime = builder.keepAliveTime;
/*     */     
/* 238 */     if (null != builder.workQueue) {
/* 239 */       workQueue = builder.workQueue;
/*     */     } else {
/*     */       
/* 242 */       workQueue = (corePoolSize <= 0) ? new SynchronousQueue<>() : new LinkedBlockingQueue<>(1024);
/*     */     } 
/* 244 */     ThreadFactory threadFactory = (null != builder.threadFactory) ? builder.threadFactory : Executors.defaultThreadFactory();
/* 245 */     RejectedExecutionHandler handler = (RejectedExecutionHandler)ObjectUtil.defaultIfNull(builder.handler, RejectPolicy.ABORT.getValue());
/*     */     
/* 247 */     ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.NANOSECONDS, workQueue, threadFactory, handler);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 255 */     if (null != builder.allowCoreThreadTimeOut) {
/* 256 */       threadPoolExecutor.allowCoreThreadTimeOut(builder.allowCoreThreadTimeOut.booleanValue());
/*     */     }
/* 258 */     return threadPoolExecutor;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\ExecutorBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
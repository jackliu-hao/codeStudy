/*     */ package cn.hutool.core.thread;
/*     */ 
/*     */ import cn.hutool.core.builder.Builder;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.atomic.AtomicLong;
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
/*     */ public class ThreadFactoryBuilder
/*     */   implements Builder<ThreadFactory>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private ThreadFactory backingThreadFactory;
/*     */   private String namePrefix;
/*     */   private Boolean daemon;
/*     */   private Integer priority;
/*     */   private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
/*     */   
/*     */   public static ThreadFactoryBuilder create() {
/*  48 */     return new ThreadFactoryBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ThreadFactoryBuilder setThreadFactory(ThreadFactory backingThreadFactory) {
/*  58 */     this.backingThreadFactory = backingThreadFactory;
/*  59 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ThreadFactoryBuilder setNamePrefix(String namePrefix) {
/*  69 */     this.namePrefix = namePrefix;
/*  70 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ThreadFactoryBuilder setDaemon(boolean daemon) {
/*  80 */     this.daemon = Boolean.valueOf(daemon);
/*  81 */     return this;
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
/*     */   public ThreadFactoryBuilder setPriority(int priority) {
/*  94 */     if (priority < 1) {
/*  95 */       throw new IllegalArgumentException(StrUtil.format("Thread priority ({}) must be >= {}", new Object[] { Integer.valueOf(priority), Integer.valueOf(1) }));
/*     */     }
/*  97 */     if (priority > 10) {
/*  98 */       throw new IllegalArgumentException(StrUtil.format("Thread priority ({}) must be <= {}", new Object[] { Integer.valueOf(priority), Integer.valueOf(10) }));
/*     */     }
/* 100 */     this.priority = Integer.valueOf(priority);
/* 101 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ThreadFactoryBuilder setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
/* 111 */     this.uncaughtExceptionHandler = uncaughtExceptionHandler;
/* 112 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ThreadFactory build() {
/* 122 */     return build(this);
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
/*     */   private static ThreadFactory build(ThreadFactoryBuilder builder) {
/* 134 */     ThreadFactory backingThreadFactory = (null != builder.backingThreadFactory) ? builder.backingThreadFactory : Executors.defaultThreadFactory();
/* 135 */     String namePrefix = builder.namePrefix;
/* 136 */     Boolean daemon = builder.daemon;
/* 137 */     Integer priority = builder.priority;
/* 138 */     Thread.UncaughtExceptionHandler handler = builder.uncaughtExceptionHandler;
/* 139 */     AtomicLong count = (null == namePrefix) ? null : new AtomicLong();
/* 140 */     return r -> {
/*     */         Thread thread = backingThreadFactory.newThread(r);
/*     */         if (null != namePrefix)
/*     */           thread.setName(namePrefix + count.getAndIncrement()); 
/*     */         if (null != daemon)
/*     */           thread.setDaemon(daemon.booleanValue()); 
/*     */         if (null != priority)
/*     */           thread.setPriority(priority.intValue()); 
/*     */         if (null != handler)
/*     */           thread.setUncaughtExceptionHandler(handler); 
/*     */         return thread;
/*     */       };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\ThreadFactoryBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
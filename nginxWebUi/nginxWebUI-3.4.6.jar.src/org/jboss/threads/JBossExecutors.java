/*     */ package org.jboss.threads;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.RejectedExecutionHandler;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.wildfly.common.Assert;
/*     */ import sun.misc.Unsafe;
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
/*     */ public final class JBossExecutors
/*     */ {
/*  40 */   private static final Logger THREAD_ERROR_LOGGER = Logger.getLogger("org.jboss.threads.errors");
/*     */ 
/*     */ 
/*     */   
/*  44 */   private static final RuntimePermission COPY_CONTEXT_CLASSLOADER_PERMISSION = new RuntimePermission("copyClassLoader");
/*     */   
/*  46 */   private static final ExecutorService REJECTING_EXECUTOR_SERVICE = new DelegatingExecutorService(RejectingExecutor.INSTANCE);
/*  47 */   private static final ExecutorService DISCARDING_EXECUTOR_SERVICE = new DelegatingExecutorService(DiscardingExecutor.INSTANCE);
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
/*     */   public static Executor directExecutor() {
/*  60 */     return SimpleDirectExecutor.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Executor rejectingExecutor() {
/*  69 */     return RejectingExecutor.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Executor rejectingExecutor(String message) {
/*  79 */     return new RejectingExecutor(message);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExecutorService rejectingExecutorService() {
/*  88 */     return REJECTING_EXECUTOR_SERVICE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExecutorService rejectingExecutorService(String message) {
/*  99 */     return protectedExecutorService(rejectingExecutor(message));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Executor discardingExecutor() {
/* 108 */     return DiscardingExecutor.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExecutorService discardingExecutorService() {
/* 118 */     return DISCARDING_EXECUTOR_SERVICE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Executor contextClassLoaderExecutor(Executor delegate, final ClassLoader taskClassLoader) {
/* 129 */     return new DelegatingExecutor(delegate) {
/*     */         public void execute(Runnable command) {
/* 131 */           super.execute(new ContextClassLoaderSavingRunnable(taskClassLoader, command));
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 140 */   private static final RejectedExecutionHandler ABORT_POLICY = new ThreadPoolExecutor.AbortPolicy();
/* 141 */   private static final RejectedExecutionHandler CALLER_RUNS_POLICY = new ThreadPoolExecutor.CallerRunsPolicy();
/* 142 */   private static final RejectedExecutionHandler DISCARD_OLDEST_POLICY = new ThreadPoolExecutor.DiscardOldestPolicy();
/* 143 */   private static final RejectedExecutionHandler DISCARD_POLICY = new ThreadPoolExecutor.DiscardPolicy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RejectedExecutionHandler abortPolicy() {
/* 152 */     return ABORT_POLICY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RejectedExecutionHandler callerRunsPolicy() {
/* 162 */     return CALLER_RUNS_POLICY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RejectedExecutionHandler discardOldestPolicy() {
/* 172 */     return DISCARD_OLDEST_POLICY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RejectedExecutionHandler discardPolicy() {
/* 182 */     return DISCARD_POLICY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RejectedExecutionHandler handoffPolicy(Executor target) {
/* 193 */     return new HandoffRejectedExecutionHandler(target);
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
/*     */   public static ExecutorService protectedExecutorService(Executor target) {
/* 208 */     return new DelegatingExecutorService(target);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ScheduledExecutorService protectedScheduledExecutorService(ScheduledExecutorService target) {
/* 219 */     return new DelegatingScheduledExecutorService(target);
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
/*     */   public static ThreadFactory resettingThreadFactory(final ThreadFactory delegate) throws SecurityException {
/* 236 */     return new ThreadFactory() {
/*     */         public Thread newThread(Runnable r) {
/* 238 */           return delegate.newThread(new ThreadLocalResettingRunnable(r));
/*     */         }
/*     */       };
/*     */   }
/*     */   
/* 243 */   private static final Runnable TCCL_RESETTER = new Runnable() {
/*     */       public void run() {
/* 245 */         Thread.currentThread().setContextClassLoader(null);
/*     */       }
/*     */       
/*     */       public String toString() {
/* 249 */         return "ContextClassLoader-resetting Runnable";
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 257 */   private static final Runnable NULL_RUNNABLE = NullRunnable.getInstance();
/*     */ 
/*     */   
/*     */   static final ClassLoader SAFE_CL;
/*     */ 
/*     */ 
/*     */   
/*     */   public static Runnable nullRunnable() {
/* 265 */     return NULL_RUNNABLE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Runnable contextClassLoaderResetter() {
/* 275 */     return TCCL_RESETTER;
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
/*     */   public static Runnable classLoaderPreservingTask(Runnable delegate) throws SecurityException {
/* 288 */     SecurityManager sm = System.getSecurityManager();
/* 289 */     if (sm != null) {
/* 290 */       sm.checkPermission(COPY_CONTEXT_CLASSLOADER_PERMISSION);
/*     */     }
/* 292 */     return classLoaderPreservingTaskUnchecked(delegate);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/* 298 */     ClassLoader safeClassLoader = JBossExecutors.class.getClassLoader();
/* 299 */     if (safeClassLoader == null) {
/* 300 */       safeClassLoader = ClassLoader.getSystemClassLoader();
/*     */     }
/* 302 */     if (safeClassLoader == null)
/* 303 */       safeClassLoader = new ClassLoader() {
/*     */         
/*     */         }; 
/* 306 */     SAFE_CL = safeClassLoader;
/*     */   }
/*     */   
/*     */   static Runnable classLoaderPreservingTaskUnchecked(Runnable delegate) {
/* 310 */     Assert.checkNotNullParam("delegate", delegate);
/* 311 */     return new ContextClassLoaderSavingRunnable(getContextClassLoader(Thread.currentThread()), delegate);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 319 */   static final Unsafe unsafe = AccessController.<Unsafe>doPrivileged(new PrivilegedAction<Unsafe>() {
/*     */         public Unsafe run() {
/*     */           try {
/* 322 */             Field field = Unsafe.class.getDeclaredField("theUnsafe");
/* 323 */             field.setAccessible(true);
/* 324 */             return (Unsafe)field.get(null);
/* 325 */           } catch (IllegalAccessException e) {
/* 326 */             throw new IllegalAccessError(e.getMessage());
/* 327 */           } catch (NoSuchFieldException e) {
/* 328 */             throw new NoSuchFieldError(e.getMessage());
/*     */           } 
/*     */         }
/*     */       }); static {
/*     */     try {
/* 333 */       contextClassLoaderOffs = unsafe.objectFieldOffset(Thread.class.getDeclaredField("contextClassLoader"));
/* 334 */     } catch (NoSuchFieldException e) {
/* 335 */       throw new NoSuchFieldError(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static final long contextClassLoaderOffs;
/*     */ 
/*     */ 
/*     */   
/*     */   static ClassLoader getContextClassLoader(Thread thread) {
/* 346 */     return (ClassLoader)unsafe.getObject(thread, contextClassLoaderOffs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static ClassLoader getAndSetContextClassLoader(Thread thread, ClassLoader newClassLoader) {
/*     */     try {
/* 358 */       return getContextClassLoader(thread);
/*     */     } finally {
/* 360 */       setContextClassLoader(thread, newClassLoader);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void setContextClassLoader(Thread thread, ClassLoader classLoader) {
/* 371 */     unsafe.putObject(thread, contextClassLoaderOffs, classLoader);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void clearContextClassLoader(Thread thread) {
/* 380 */     unsafe.putObject(thread, contextClassLoaderOffs, SAFE_CL);
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
/*     */   public static Thread.UncaughtExceptionHandler loggingExceptionHandler(Logger log) {
/* 394 */     return new LoggingUncaughtExceptionHandler(log);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Thread.UncaughtExceptionHandler loggingExceptionHandler(String categoryName) {
/* 404 */     return new LoggingUncaughtExceptionHandler(Logger.getLogger(categoryName));
/*     */   }
/*     */   
/* 407 */   private static final Thread.UncaughtExceptionHandler LOGGING_HANDLER = loggingExceptionHandler(THREAD_ERROR_LOGGER);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Thread.UncaughtExceptionHandler loggingExceptionHandler() {
/* 415 */     return LOGGING_HANDLER;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\JBossExecutors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
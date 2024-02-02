/*     */ package cn.hutool.core.thread;
/*     */ 
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.CompletionService;
/*     */ import java.util.concurrent.CountDownLatch;
/*     */ import java.util.concurrent.ExecutorCompletionService;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.RejectedExecutionHandler;
/*     */ import java.util.concurrent.ScheduledThreadPoolExecutor;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.function.Supplier;
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
/*     */ public class ThreadUtil
/*     */ {
/*     */   public static ExecutorService newExecutor(int corePoolSize) {
/*  37 */     ExecutorBuilder builder = ExecutorBuilder.create();
/*  38 */     if (corePoolSize > 0) {
/*  39 */       builder.setCorePoolSize(corePoolSize);
/*     */     }
/*  41 */     return builder.build();
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
/*     */   public static ExecutorService newExecutor() {
/*  56 */     return ExecutorBuilder.create().useSynchronousQueue().build();
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
/*     */   public static ExecutorService newSingleExecutor() {
/*  71 */     return ExecutorBuilder.create()
/*  72 */       .setCorePoolSize(1)
/*  73 */       .setMaxPoolSize(1)
/*  74 */       .setKeepAliveTime(0L)
/*  75 */       .buildFinalizable();
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
/*     */   public static ThreadPoolExecutor newExecutor(int corePoolSize, int maximumPoolSize) {
/*  87 */     return ExecutorBuilder.create()
/*  88 */       .setCorePoolSize(corePoolSize)
/*  89 */       .setMaxPoolSize(maximumPoolSize)
/*  90 */       .build();
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
/*     */   public static ExecutorService newExecutor(int corePoolSize, int maximumPoolSize, int maximumQueueSize) {
/* 104 */     return ExecutorBuilder.create()
/* 105 */       .setCorePoolSize(corePoolSize)
/* 106 */       .setMaxPoolSize(maximumPoolSize)
/* 107 */       .setWorkQueue(new LinkedBlockingQueue<>(maximumQueueSize))
/* 108 */       .build();
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
/*     */   public static ThreadPoolExecutor newExecutorByBlockingCoefficient(float blockingCoefficient) {
/* 124 */     if (blockingCoefficient >= 1.0F || blockingCoefficient < 0.0F) {
/* 125 */       throw new IllegalArgumentException("[blockingCoefficient] must between 0 and 1, or equals 0.");
/*     */     }
/*     */ 
/*     */     
/* 129 */     int poolSize = (int)(Runtime.getRuntime().availableProcessors() / (1.0F - blockingCoefficient));
/* 130 */     return ExecutorBuilder.create().setCorePoolSize(poolSize).setMaxPoolSize(poolSize).setKeepAliveTime(0L).build();
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
/*     */   
/*     */   public static ExecutorService newFixedExecutor(int nThreads, String threadNamePrefix, boolean isBlocked) {
/* 149 */     return newFixedExecutor(nThreads, 1024, threadNamePrefix, isBlocked);
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
/*     */ 
/*     */   
/*     */   public static ExecutorService newFixedExecutor(int nThreads, int maximumQueueSize, String threadNamePrefix, boolean isBlocked) {
/* 169 */     return newFixedExecutor(nThreads, maximumQueueSize, threadNamePrefix, (isBlocked ? RejectPolicy.BLOCK : RejectPolicy.ABORT)
/* 170 */         .getValue());
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExecutorService newFixedExecutor(int nThreads, int maximumQueueSize, String threadNamePrefix, RejectedExecutionHandler handler) {
/* 192 */     return ExecutorBuilder.create()
/* 193 */       .setCorePoolSize(nThreads).setMaxPoolSize(nThreads)
/* 194 */       .setWorkQueue(new LinkedBlockingQueue<>(maximumQueueSize))
/* 195 */       .setThreadFactory(createThreadFactory(threadNamePrefix))
/* 196 */       .setHandler(handler)
/* 197 */       .build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void execute(Runnable runnable) {
/* 206 */     GlobalThreadPool.execute(runnable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Runnable execAsync(Runnable runnable, boolean isDaemon) {
/* 217 */     Thread thread = new Thread(runnable);
/* 218 */     thread.setDaemon(isDaemon);
/* 219 */     thread.start();
/*     */     
/* 221 */     return runnable;
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
/*     */   public static <T> Future<T> execAsync(Callable<T> task) {
/* 233 */     return GlobalThreadPool.submit(task);
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
/*     */   public static Future<?> execAsync(Runnable runnable) {
/* 245 */     return GlobalThreadPool.submit(runnable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> CompletionService<T> newCompletionService() {
/* 256 */     return new ExecutorCompletionService<>(GlobalThreadPool.getExecutor());
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
/*     */   public static <T> CompletionService<T> newCompletionService(ExecutorService executor) {
/* 268 */     return new ExecutorCompletionService<>(executor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CountDownLatch newCountDownLatch(int threadCount) {
/* 278 */     return new CountDownLatch(threadCount);
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
/*     */   public static Thread newThread(Runnable runnable, String name) {
/* 290 */     Thread t = newThread(runnable, name, false);
/* 291 */     if (t.getPriority() != 5) {
/* 292 */       t.setPriority(5);
/*     */     }
/* 294 */     return t;
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
/*     */   public static Thread newThread(Runnable runnable, String name, boolean isDaemon) {
/* 307 */     Thread t = new Thread(null, runnable, name);
/* 308 */     t.setDaemon(isDaemon);
/* 309 */     return t;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean sleep(Number timeout, TimeUnit timeUnit) {
/*     */     try {
/* 321 */       timeUnit.sleep(timeout.longValue());
/* 322 */     } catch (InterruptedException e) {
/* 323 */       return false;
/*     */     } 
/* 325 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean sleep(Number millis) {
/* 335 */     if (millis == null) {
/* 336 */       return true;
/*     */     }
/* 338 */     return sleep(millis.longValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean sleep(long millis) {
/* 349 */     if (millis > 0L) {
/*     */       try {
/* 351 */         Thread.sleep(millis);
/* 352 */       } catch (InterruptedException e) {
/* 353 */         return false;
/*     */       } 
/*     */     }
/* 356 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean safeSleep(Number millis) {
/* 367 */     if (millis == null) {
/* 368 */       return true;
/*     */     }
/*     */     
/* 371 */     return safeSleep(millis.longValue());
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
/*     */   public static boolean safeSleep(long millis) {
/* 383 */     long done = 0L;
/*     */ 
/*     */     
/* 386 */     while (done >= 0L && done < millis) {
/* 387 */       long before = System.currentTimeMillis();
/* 388 */       if (false == sleep(millis - done)) {
/* 389 */         return false;
/*     */       }
/* 391 */       long spendTime = System.currentTimeMillis() - before;
/* 392 */       if (spendTime <= 0L) {
/*     */         break;
/*     */       }
/*     */       
/* 396 */       done += spendTime;
/*     */     } 
/* 398 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StackTraceElement[] getStackTrace() {
/* 405 */     return Thread.currentThread().getStackTrace();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StackTraceElement getStackTraceElement(int i) {
/* 415 */     StackTraceElement[] stackTrace = getStackTrace();
/* 416 */     if (i < 0) {
/* 417 */       i += stackTrace.length;
/*     */     }
/* 419 */     return stackTrace[i];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> ThreadLocal<T> createThreadLocal(boolean isInheritable) {
/* 430 */     if (isInheritable) {
/* 431 */       return new InheritableThreadLocal<>();
/*     */     }
/* 433 */     return new ThreadLocal<>();
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
/*     */   public static <T> ThreadLocal<T> createThreadLocal(Supplier<? extends T> supplier) {
/* 447 */     return ThreadLocal.withInitial(supplier);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ThreadFactoryBuilder createThreadFactoryBuilder() {
/* 458 */     return ThreadFactoryBuilder.create();
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
/*     */   public static ThreadFactory createThreadFactory(String threadNamePrefix) {
/* 470 */     return ThreadFactoryBuilder.create().setNamePrefix(threadNamePrefix).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void interrupt(Thread thread, boolean isJoin) {
/* 480 */     if (null != thread && false == thread.isInterrupted()) {
/* 481 */       thread.interrupt();
/* 482 */       if (isJoin) {
/* 483 */         waitForDie(thread);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void waitForDie() {
/* 492 */     waitForDie(Thread.currentThread());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void waitForDie(Thread thread) {
/* 501 */     if (null == thread) {
/*     */       return;
/*     */     }
/*     */     
/* 505 */     boolean dead = false;
/*     */     do {
/*     */       try {
/* 508 */         thread.join();
/* 509 */         dead = true;
/* 510 */       } catch (InterruptedException interruptedException) {}
/*     */     
/*     */     }
/* 513 */     while (false == dead);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Thread[] getThreads() {
/* 522 */     return getThreads(Thread.currentThread().getThreadGroup().getParent());
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
/*     */   public static Thread[] getThreads(ThreadGroup group) {
/* 534 */     Thread[] slackList = new Thread[group.activeCount() * 2];
/* 535 */     int actualSize = group.enumerate(slackList);
/* 536 */     Thread[] result = new Thread[actualSize];
/* 537 */     System.arraycopy(slackList, 0, result, 0, actualSize);
/* 538 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Thread getMainThread() {
/* 548 */     for (Thread thread : getThreads()) {
/* 549 */       if (thread.getId() == 1L) {
/* 550 */         return thread;
/*     */       }
/*     */     } 
/* 553 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ThreadGroup currentThreadGroup() {
/* 563 */     SecurityManager s = System.getSecurityManager();
/* 564 */     return (null != s) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
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
/*     */   public static ThreadFactory newNamedThreadFactory(String prefix, boolean isDaemon) {
/* 576 */     return new NamedThreadFactory(prefix, isDaemon);
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
/*     */   public static ThreadFactory newNamedThreadFactory(String prefix, ThreadGroup threadGroup, boolean isDaemon) {
/* 589 */     return new NamedThreadFactory(prefix, threadGroup, isDaemon);
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
/*     */   public static ThreadFactory newNamedThreadFactory(String prefix, ThreadGroup threadGroup, boolean isDaemon, Thread.UncaughtExceptionHandler handler) {
/* 603 */     return new NamedThreadFactory(prefix, threadGroup, isDaemon, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void sync(Object obj) {
/* 614 */     synchronized (obj) {
/*     */       try {
/* 616 */         obj.wait();
/* 617 */       } catch (InterruptedException interruptedException) {}
/*     */     } 
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
/*     */   public static ConcurrencyTester concurrencyTest(int threadSize, Runnable runnable) {
/* 635 */     return (new ConcurrencyTester(threadSize)).test(runnable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ScheduledThreadPoolExecutor createScheduledExecutor(int corePoolSize) {
/* 646 */     return new ScheduledThreadPoolExecutor(corePoolSize);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ScheduledThreadPoolExecutor schedule(ScheduledThreadPoolExecutor executor, Runnable command, long initialDelay, long period, boolean fixedRateOrFixedDelay) {
/* 671 */     return schedule(executor, command, initialDelay, period, TimeUnit.MILLISECONDS, fixedRateOrFixedDelay);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ScheduledThreadPoolExecutor schedule(ScheduledThreadPoolExecutor executor, Runnable command, long initialDelay, long period, TimeUnit timeUnit, boolean fixedRateOrFixedDelay) {
/* 697 */     if (null == executor) {
/* 698 */       executor = createScheduledExecutor(2);
/*     */     }
/* 700 */     if (fixedRateOrFixedDelay) {
/* 701 */       executor.scheduleAtFixedRate(command, initialDelay, period, timeUnit);
/*     */     } else {
/* 703 */       executor.scheduleWithFixedDelay(command, initialDelay, period, timeUnit);
/*     */     } 
/*     */     
/* 706 */     return executor;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\ThreadUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
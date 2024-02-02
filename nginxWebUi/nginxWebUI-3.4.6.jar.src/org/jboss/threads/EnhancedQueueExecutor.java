/*      */ package org.jboss.threads;
/*      */ 
/*      */ import java.lang.management.ManagementFactory;
/*      */ import java.security.AccessControlContext;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.time.Duration;
/*      */ import java.time.temporal.ChronoUnit;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Hashtable;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.concurrent.Executors;
/*      */ import java.util.concurrent.RejectedExecutionException;
/*      */ import java.util.concurrent.ThreadFactory;
/*      */ import java.util.concurrent.ThreadLocalRandom;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.atomic.LongAdder;
/*      */ import java.util.concurrent.locks.LockSupport;
/*      */ import javax.management.ObjectInstance;
/*      */ import javax.management.ObjectName;
/*      */ import org.jboss.threads.management.ManageableThreadPoolExecutorService;
/*      */ import org.jboss.threads.management.StandardThreadPoolMXBean;
/*      */ import org.wildfly.common.Assert;
/*      */ import org.wildfly.common.cpu.ProcessorInfo;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class EnhancedQueueExecutor
/*      */   extends EnhancedQueueExecutorBase6
/*      */   implements ManageableThreadPoolExecutorService
/*      */ {
/*   71 */   private static final Thread[] NO_THREADS = new Thread[0];
/*      */   
/*      */   static {
/*   74 */     Version.getVersionString();
/*   75 */     MBeanUnregisterAction.forceInit();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  129 */   public static final boolean DISABLE_HINT = readBooleanPropertyPrefixed("disable", false);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  134 */   static final boolean UPDATE_TAIL = readBooleanPropertyPrefixed("update-tail", false);
/*      */ 
/*      */ 
/*      */   
/*  138 */   static final boolean UPDATE_STATISTICS = readBooleanPropertyPrefixed("statistics", false);
/*      */ 
/*      */ 
/*      */   
/*  142 */   static final boolean UPDATE_ACTIVE_COUNT = (UPDATE_STATISTICS || 
/*  143 */     readBooleanPropertyPrefixed("statistics.active-count", false));
/*      */ 
/*      */ 
/*      */   
/*  147 */   static final boolean NO_QUEUE_LIMIT = readBooleanPropertyPrefixed("unlimited-queue", false);
/*      */ 
/*      */ 
/*      */   
/*  151 */   static final boolean REGISTER_MBEAN = readBooleanPropertyPrefixed("register-mbean", true);
/*      */ 
/*      */ 
/*      */   
/*  155 */   static final boolean DISABLE_MBEAN = readBooleanPropertyPrefixed("disable-mbean", (readProperty("org.graalvm.nativeimage.imagecode", null) != null));
/*      */ 
/*      */ 
/*      */   
/*  159 */   static final int PARK_SPINS = readIntPropertyPrefixed("park-spins", (ProcessorInfo.availableProcessors() == 1) ? 0 : 128);
/*      */ 
/*      */ 
/*      */   
/*  163 */   static final int YIELD_FACTOR = Math.max(Math.min(readIntPropertyPrefixed("park-yields", 1), PARK_SPINS), 0);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  169 */   static final Executor DEFAULT_HANDLER = JBossExecutors.rejectingExecutor();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final ThreadFactory threadFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  182 */   private final Set<Thread> runningThreads = Collections.newSetFromMap(new ConcurrentHashMap<>());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final MXBeanImpl mxBean;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Object handle;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final AccessControlContext acc;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   volatile Waiter terminationWaiters;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   volatile long queueSize;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   volatile long timeoutNanos;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   volatile float growthResistance;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   volatile Executor handoffExecutor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   volatile Thread.UncaughtExceptionHandler exceptionHandler;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   volatile Runnable terminationTask;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   volatile int peakThreadCount;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   volatile int peakQueueSize;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  259 */   private final LongAdder submittedTaskCounter = new LongAdder();
/*  260 */   private final LongAdder completedTaskCounter = new LongAdder();
/*  261 */   private final LongAdder rejectedTaskCounter = new LongAdder();
/*  262 */   private final LongAdder spinMisses = new LongAdder(); volatile int activeCount; private static final long terminationWaitersOffset;
/*      */   private static final long queueSizeOffset;
/*      */   private static final long peakThreadCountOffset;
/*      */   private static final long activeCountOffset;
/*      */   private static final long peakQueueSizeOffset;
/*      */   private static final Object sequenceBase;
/*      */   private static final long sequenceOffset;
/*      */   private static final long TS_THREAD_CNT_MASK = 1048575L;
/*      */   private static final long TS_CURRENT_SHIFT = 0L;
/*      */   private static final long TS_CORE_SHIFT = 20L;
/*      */   private static final long TS_MAX_SHIFT = 40L;
/*      */   private static final long TS_ALLOW_CORE_TIMEOUT = 1152921504606846976L;
/*      */   private static final long TS_SHUTDOWN_REQUESTED = 2305843009213693952L;
/*      */   private static final long TS_SHUTDOWN_INTERRUPT = 4611686018427387904L;
/*      */   private static final long TS_SHUTDOWN_COMPLETE = -9223372036854775808L;
/*      */   private static final int EXE_OK = 0;
/*      */   private static final int EXE_REJECT_QUEUE_FULL = 1;
/*      */   private static final int EXE_REJECT_SHUTDOWN = 2;
/*      */   private static final int EXE_CREATE_THREAD = 3;
/*      */   private static final int AT_YES = 0;
/*      */   private static final int AT_NO = 1;
/*      */   private static final int AT_SHUTDOWN = 2;
/*      */   
/*      */   static {
/*      */     try {
/*  287 */       terminationWaitersOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutor.class.getDeclaredField("terminationWaiters"));
/*      */       
/*  289 */       queueSizeOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutor.class.getDeclaredField("queueSize"));
/*      */       
/*  291 */       peakThreadCountOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutor.class.getDeclaredField("peakThreadCount"));
/*  292 */       activeCountOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutor.class.getDeclaredField("activeCount"));
/*  293 */       peakQueueSizeOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutor.class.getDeclaredField("peakQueueSize"));
/*      */       
/*  295 */       sequenceBase = JBossExecutors.unsafe.staticFieldBase(EnhancedQueueExecutor.class.getDeclaredField("sequence"));
/*  296 */       sequenceOffset = JBossExecutors.unsafe.staticFieldOffset(EnhancedQueueExecutor.class.getDeclaredField("sequence"));
/*  297 */     } catch (NoSuchFieldException e) {
/*  298 */       throw new NoSuchFieldError(e.getMessage());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  332 */   static final QNode TERMINATE_REQUESTED = new TerminateWaiterNode();
/*  333 */   static final QNode TERMINATE_COMPLETE = new TerminateWaiterNode();
/*      */   
/*  335 */   static final Waiter TERMINATE_COMPLETE_WAITER = new Waiter(null);
/*      */   
/*  337 */   static final Runnable WAITING = new NullRunnable();
/*  338 */   static final Runnable GAVE_UP = new NullRunnable();
/*  339 */   static final Runnable ACCEPTED = new NullRunnable();
/*  340 */   static final Runnable EXIT = new NullRunnable();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  346 */   static volatile int sequence = 1;
/*      */ 
/*      */   
/*      */   EnhancedQueueExecutor(Builder builder) {
/*  350 */     this.acc = AccessController.getContext();
/*  351 */     int maxSize = builder.getMaximumPoolSize();
/*  352 */     int coreSize = Math.min(builder.getCorePoolSize(), maxSize);
/*  353 */     this.handoffExecutor = builder.getHandoffExecutor();
/*  354 */     this.exceptionHandler = builder.getExceptionHandler();
/*  355 */     this.threadFactory = builder.getThreadFactory();
/*  356 */     this.terminationTask = builder.getTerminationTask();
/*  357 */     this.growthResistance = builder.getGrowthResistance();
/*  358 */     Duration keepAliveTime = builder.getKeepAliveTime();
/*      */ 
/*      */     
/*  361 */     this.threadStatus = withCoreSize(withMaxSize(withAllowCoreTimeout(0L, builder.allowsCoreThreadTimeOut()), maxSize), coreSize);
/*  362 */     this.timeoutNanos = TimeUtil.clampedPositiveNanos(keepAliveTime);
/*  363 */     this.queueSize = withMaxQueueSize(withCurrentQueueSize(0L, 0), builder.getMaximumQueueSize());
/*  364 */     this.mxBean = new MXBeanImpl();
/*  365 */     if (!DISABLE_MBEAN && builder.isRegisterMBean()) {
/*  366 */       String configuredName = builder.getMBeanName();
/*  367 */       String finalName = (configuredName != null) ? configuredName : ("threadpool-" + JBossExecutors.unsafe.getAndAddInt(sequenceBase, sequenceOffset, 1));
/*  368 */       this.handle = AccessController.doPrivileged(new MBeanRegisterAction(finalName, this.mxBean), this.acc);
/*      */     } else {
/*  370 */       this.handle = null;
/*      */     } 
/*      */   }
/*      */   
/*      */   static final class MBeanRegisterAction implements PrivilegedAction<ObjectInstance> {
/*      */     private final String finalName;
/*      */     private final EnhancedQueueExecutor.MXBeanImpl mxBean;
/*      */     
/*      */     MBeanRegisterAction(String finalName, EnhancedQueueExecutor.MXBeanImpl mxBean) {
/*  379 */       this.finalName = finalName;
/*  380 */       this.mxBean = mxBean;
/*      */     }
/*      */     
/*      */     public ObjectInstance run() {
/*      */       try {
/*  385 */         Hashtable<String, String> table = new Hashtable<>();
/*  386 */         table.put("name", ObjectName.quote(this.finalName));
/*  387 */         table.put("type", "thread-pool");
/*  388 */         return ManagementFactory.getPlatformMBeanServer().registerMBean(this.mxBean, new ObjectName("jboss.threads", table));
/*  389 */       } catch (Throwable throwable) {
/*      */         
/*  391 */         return null;
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Builder
/*      */   {
/*  404 */     private ThreadFactory threadFactory = Executors.defaultThreadFactory();
/*  405 */     private Runnable terminationTask = NullRunnable.getInstance();
/*  406 */     private Executor handoffExecutor = EnhancedQueueExecutor.DEFAULT_HANDLER;
/*  407 */     private Thread.UncaughtExceptionHandler exceptionHandler = JBossExecutors.loggingExceptionHandler();
/*  408 */     private int coreSize = 16;
/*  409 */     private int maxSize = 64;
/*  410 */     private Duration keepAliveTime = Duration.ofSeconds(30L);
/*      */     private float growthResistance;
/*      */     private boolean allowCoreTimeOut;
/*  413 */     private int maxQueueSize = Integer.MAX_VALUE;
/*  414 */     private boolean registerMBean = EnhancedQueueExecutor.REGISTER_MBEAN;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private String mBeanName;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ThreadFactory getThreadFactory() {
/*  428 */       return this.threadFactory;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder setThreadFactory(ThreadFactory threadFactory) {
/*  438 */       Assert.checkNotNullParam("threadFactory", threadFactory);
/*  439 */       this.threadFactory = threadFactory;
/*  440 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Runnable getTerminationTask() {
/*  449 */       return this.terminationTask;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder setTerminationTask(Runnable terminationTask) {
/*  459 */       Assert.checkNotNullParam("terminationTask", terminationTask);
/*  460 */       this.terminationTask = terminationTask;
/*  461 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getCorePoolSize() {
/*  474 */       return this.coreSize;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder setCorePoolSize(int coreSize) {
/*  486 */       Assert.checkMinimumParameter("coreSize", 0, coreSize);
/*  487 */       Assert.checkMaximumParameter("coreSize", 1048575L, coreSize);
/*  488 */       this.coreSize = coreSize;
/*  489 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getMaximumPoolSize() {
/*  499 */       return this.maxSize;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder setMaximumPoolSize(int maxSize) {
/*  511 */       Assert.checkMinimumParameter("maxSize", 0, maxSize);
/*  512 */       Assert.checkMaximumParameter("maxSize", 1048575L, maxSize);
/*  513 */       this.maxSize = maxSize;
/*  514 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Duration getKeepAliveTime() {
/*  524 */       return this.keepAliveTime;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public long getKeepAliveTime(TimeUnit keepAliveUnits) {
/*  538 */       Assert.checkNotNullParam("keepAliveUnits", keepAliveUnits);
/*  539 */       long secondsPart = keepAliveUnits.convert(this.keepAliveTime.getSeconds(), TimeUnit.SECONDS);
/*  540 */       long nanoPart = keepAliveUnits.convert(this.keepAliveTime.getNano(), TimeUnit.NANOSECONDS);
/*  541 */       long sum = secondsPart + nanoPart;
/*  542 */       return (sum < 0L) ? Long.MAX_VALUE : sum;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder setKeepAliveTime(Duration keepAliveTime) {
/*  551 */       Assert.checkNotNullParam("keepAliveTime", keepAliveTime);
/*  552 */       this.keepAliveTime = keepAliveTime;
/*  553 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public Builder setKeepAliveTime(long keepAliveTime, TimeUnit keepAliveUnits) {
/*  567 */       Assert.checkMinimumParameter("keepAliveTime", 1L, keepAliveTime);
/*  568 */       Assert.checkNotNullParam("keepAliveUnits", keepAliveUnits);
/*  569 */       this.keepAliveTime = Duration.of(keepAliveTime, JDKSpecific.timeToTemporal(keepAliveUnits));
/*  570 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public float getGrowthResistance() {
/*  584 */       return this.growthResistance;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder setGrowthResistance(float growthResistance) {
/*  596 */       Assert.checkMinimumParameter("growthResistance", 0.0F, growthResistance);
/*  597 */       Assert.checkMaximumParameter("growthResistance", 1.0F, growthResistance);
/*  598 */       this.growthResistance = growthResistance;
/*  599 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean allowsCoreThreadTimeOut() {
/*  610 */       return this.allowCoreTimeOut;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder allowCoreThreadTimeOut(boolean allowCoreTimeOut) {
/*  622 */       this.allowCoreTimeOut = allowCoreTimeOut;
/*  623 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getMaximumQueueSize() {
/*  633 */       return this.maxQueueSize;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder setMaximumQueueSize(int maxQueueSize) {
/*  645 */       Assert.checkMinimumParameter("maxQueueSize", 0, maxQueueSize);
/*  646 */       Assert.checkMaximumParameter("maxQueueSize", 2147483647, maxQueueSize);
/*  647 */       this.maxQueueSize = maxQueueSize;
/*  648 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Executor getHandoffExecutor() {
/*  657 */       return this.handoffExecutor;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder setHandoffExecutor(Executor handoffExecutor) {
/*  667 */       Assert.checkNotNullParam("handoffExecutor", handoffExecutor);
/*  668 */       this.handoffExecutor = handoffExecutor;
/*  669 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Thread.UncaughtExceptionHandler getExceptionHandler() {
/*  678 */       return this.exceptionHandler;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder setExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
/*  688 */       this.exceptionHandler = exceptionHandler;
/*  689 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public EnhancedQueueExecutor build() {
/*  698 */       return new EnhancedQueueExecutor(this);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean isRegisterMBean() {
/*  707 */       return this.registerMBean;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder setRegisterMBean(boolean registerMBean) {
/*  717 */       this.registerMBean = registerMBean;
/*  718 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getMBeanName() {
/*  727 */       return this.mBeanName;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder setMBeanName(String mBeanName) {
/*  737 */       this.mBeanName = mBeanName;
/*  738 */       return this;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void execute(Runnable runnable) {
/*  752 */     Assert.checkNotNullParam("runnable", runnable);
/*  753 */     Runnable realRunnable = JBossExecutors.classLoaderPreservingTaskUnchecked(runnable);
/*      */     
/*  755 */     int result = tryExecute(realRunnable);
/*  756 */     boolean ok = false;
/*  757 */     if (result == 0) {
/*      */       
/*  759 */       if (currentSizeOf(this.threadStatus) == 0 && tryAllocateThread(0.0F) == 0 && !doStartThread((Runnable)null)) {
/*  760 */         deallocateThread();
/*      */       }
/*  762 */       if (UPDATE_STATISTICS) this.submittedTaskCounter.increment();  return;
/*      */     } 
/*  764 */     if (result == 3) { try {
/*  765 */         ok = doStartThread(realRunnable);
/*      */       } finally {
/*  767 */         if (!ok) deallocateThread(); 
/*      */       }  }
/*  769 */     else { if (UPDATE_STATISTICS) this.rejectedTaskCounter.increment(); 
/*  770 */       if (result == 2) {
/*  771 */         rejectShutdown(realRunnable);
/*      */       } else {
/*  773 */         assert result == 1;
/*  774 */         rejectQueueFull(realRunnable);
/*      */       }  }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void shutdown() {
/*  784 */     shutdown(false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Runnable> shutdownNow() {
/*  795 */     shutdown(true);
/*  796 */     ArrayList<Runnable> list = new ArrayList<>();
/*  797 */     TaskNode head = this.head;
/*      */     
/*      */     while (true) {
/*  800 */       QNode headNext = head.getNext();
/*  801 */       if (headNext instanceof TaskNode) {
/*  802 */         TaskNode taskNode = (TaskNode)headNext;
/*  803 */         if (compareAndSetHead(head, taskNode)) {
/*  804 */           if (!NO_QUEUE_LIMIT) decreaseQueueSize(); 
/*  805 */           head = taskNode;
/*  806 */           list.add(taskNode.task);
/*      */         }  continue;
/*      */       } 
/*      */       break;
/*      */     } 
/*  811 */     return list;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isShutdown() {
/*  822 */     return isShutdownRequested(this.threadStatus);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isTerminated() {
/*  831 */     return isShutdownComplete(this.threadStatus);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
/*  845 */     Assert.checkMinimumParameter("timeout", 0L, timeout);
/*  846 */     Assert.checkNotNullParam("unit", unit);
/*  847 */     if (timeout > 0L) {
/*  848 */       Thread thread = Thread.currentThread();
/*  849 */       if (this.runningThreads.contains(thread)) {
/*  850 */         throw Messages.msg.cannotAwaitWithin();
/*      */       }
/*  852 */       Waiter waiters = this.terminationWaiters;
/*  853 */       if (waiters == TERMINATE_COMPLETE_WAITER) {
/*  854 */         return true;
/*      */       }
/*  856 */       Waiter waiter = new Waiter(waiters);
/*  857 */       waiter.setThread(Thread.currentThread());
/*  858 */       while (!compareAndSetTerminationWaiters(waiters, waiter)) {
/*  859 */         waiters = this.terminationWaiters;
/*  860 */         if (waiters == TERMINATE_COMPLETE_WAITER) {
/*  861 */           return true;
/*      */         }
/*  863 */         waiter.setNext(waiters);
/*      */       } 
/*      */       try {
/*  866 */         LockSupport.parkNanos(this, unit.toNanos(timeout));
/*      */       } finally {
/*      */         
/*  869 */         waiter.setThread(null);
/*      */       } 
/*      */     } 
/*  872 */     if (Thread.interrupted()) throw new InterruptedException(); 
/*  873 */     return isTerminated();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public StandardThreadPoolMXBean getThreadPoolMXBean() {
/*  881 */     return this.mxBean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void shutdown(boolean interrupt) {
/*      */     label50: while (true) {
/*  914 */       long oldStatus = this.threadStatus;
/*  915 */       long newStatus = withShutdownRequested(oldStatus);
/*  916 */       if (interrupt) newStatus = withShutdownInterrupt(newStatus); 
/*  917 */       if (currentSizeOf(oldStatus) == 0) newStatus = withShutdownComplete(newStatus); 
/*  918 */       if (newStatus == oldStatus)
/*  919 */         return;  if (compareAndSetThreadStatus(oldStatus, newStatus)) {
/*  920 */         assert oldStatus != newStatus;
/*  921 */         if (isShutdownRequested(newStatus) != isShutdownRequested(oldStatus)) {
/*  922 */           QNode tailNext; assert !isShutdownRequested(oldStatus);
/*      */ 
/*      */           
/*  925 */           TaskNode tail = this.tail;
/*      */ 
/*      */           
/*      */           while (true) {
/*  929 */             tailNext = tail.getNext();
/*  930 */             if (tailNext instanceof TaskNode) {
/*  931 */               tail = (TaskNode)tailNext; continue;
/*  932 */             }  if (tailNext instanceof PoolThreadNode || tailNext == null) {
/*      */               
/*  934 */               PoolThreadNode node = (PoolThreadNode)tailNext;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*  944 */               if (tail.compareAndSetNext(node, TERMINATE_REQUESTED)) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  954 */                 while (node != null) {
/*  955 */                   node.compareAndSetTask(WAITING, EXIT);
/*  956 */                   node.unpark();
/*  957 */                   node = node.getNext();
/*      */                 }  continue label50;
/*      */               }  continue;
/*      */             } 
/*      */             break;
/*      */           } 
/*  963 */           if (!(tailNext instanceof TerminateWaiterNode))
/*      */           {
/*      */ 
/*      */             
/*  967 */             throw Assert.unreachableCode();
/*      */           }
/*      */         } 
/*      */         
/*  971 */         if (isShutdownInterrupt(newStatus) != isShutdownInterrupt(oldStatus)) {
/*  972 */           assert !isShutdownInterrupt(oldStatus);
/*      */           
/*  974 */           for (Thread thread : this.runningThreads) {
/*  975 */             thread.interrupt();
/*      */           }
/*      */         } 
/*  978 */         if (isShutdownComplete(newStatus) != isShutdownComplete(oldStatus)) {
/*  979 */           assert !isShutdownComplete(oldStatus);
/*  980 */           completeTermination();
/*      */         } 
/*      */         return;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isTerminating() {
/*  990 */     long threadStatus = this.threadStatus;
/*  991 */     return (isShutdownRequested(threadStatus) && !isShutdownComplete(threadStatus));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean prestartCoreThread() {
/* 1002 */     if (tryAllocateThread(1.0F) != 0) return false; 
/* 1003 */     if (doStartThread((Runnable)null)) return true; 
/* 1004 */     deallocateThread();
/* 1005 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int prestartAllCoreThreads() {
/* 1014 */     int cnt = 0;
/* 1015 */     for (; prestartCoreThread(); cnt++);
/* 1016 */     return cnt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public float getGrowthResistance() {
/* 1034 */     return this.growthResistance;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setGrowthResistance(float growthResistance) {
/* 1044 */     Assert.checkMinimumParameter("growthResistance", 0.0F, growthResistance);
/* 1045 */     Assert.checkMaximumParameter("growthResistance", 1.0F, growthResistance);
/* 1046 */     this.growthResistance = growthResistance;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getCorePoolSize() {
/* 1059 */     return coreSizeOf(this.threadStatus);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCorePoolSize(int corePoolSize) {
/* 1070 */     Assert.checkMinimumParameter("corePoolSize", 0, corePoolSize);
/* 1071 */     Assert.checkMaximumParameter("corePoolSize", 1048575L, corePoolSize);
/*      */     
/*      */     while (true) {
/* 1074 */       long newVal, oldVal = this.threadStatus;
/* 1075 */       if (corePoolSize > maxSizeOf(oldVal)) {
/*      */         
/* 1077 */         newVal = withCoreSize(withMaxSize(oldVal, corePoolSize), corePoolSize);
/*      */       } else {
/* 1079 */         newVal = withCoreSize(oldVal, corePoolSize);
/*      */       } 
/* 1081 */       if (compareAndSetThreadStatus(oldVal, newVal)) {
/* 1082 */         if (maxSizeOf(newVal) < maxSizeOf(oldVal) || coreSizeOf(newVal) < coreSizeOf(oldVal))
/*      */         {
/* 1084 */           for (Thread activeThread : this.runningThreads) {
/* 1085 */             LockSupport.unpark(activeThread);
/*      */           }
/*      */         }
/*      */         return;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaximumPoolSize() {
/* 1097 */     return maxSizeOf(this.threadStatus);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMaximumPoolSize(int maxPoolSize) {
/* 1108 */     Assert.checkMinimumParameter("maxPoolSize", 0, maxPoolSize);
/* 1109 */     Assert.checkMaximumParameter("maxPoolSize", 1048575L, maxPoolSize);
/*      */     
/*      */     while (true) {
/* 1112 */       long newVal, oldVal = this.threadStatus;
/* 1113 */       if (maxPoolSize < coreSizeOf(oldVal)) {
/*      */         
/* 1115 */         newVal = withCoreSize(withMaxSize(oldVal, maxPoolSize), maxPoolSize);
/*      */       } else {
/* 1117 */         newVal = withMaxSize(oldVal, maxPoolSize);
/*      */       } 
/* 1119 */       if (compareAndSetThreadStatus(oldVal, newVal)) {
/* 1120 */         if (maxSizeOf(newVal) < maxSizeOf(oldVal) || coreSizeOf(newVal) < coreSizeOf(oldVal))
/*      */         {
/* 1122 */           for (Thread activeThread : this.runningThreads) {
/* 1123 */             LockSupport.unpark(activeThread);
/*      */           }
/*      */         }
/*      */         return;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean allowsCoreThreadTimeOut() {
/* 1136 */     return isAllowCoreTimeout(this.threadStatus);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void allowCoreThreadTimeOut(boolean value) {
/*      */     while (true) {
/* 1149 */       long oldVal = this.threadStatus;
/* 1150 */       long newVal = withAllowCoreTimeout(oldVal, value);
/* 1151 */       if (oldVal == newVal)
/* 1152 */         return;  if (compareAndSetThreadStatus(oldVal, newVal)) {
/* 1153 */         if (value)
/*      */         {
/* 1155 */           for (Thread activeThread : this.runningThreads) {
/* 1156 */             LockSupport.unpark(activeThread);
/*      */           }
/*      */         }
/*      */         return;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public long getKeepAliveTime(TimeUnit keepAliveUnits) {
/* 1173 */     Assert.checkNotNullParam("keepAliveUnits", keepAliveUnits);
/* 1174 */     return keepAliveUnits.convert(this.timeoutNanos, TimeUnit.NANOSECONDS);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Duration getKeepAliveTime() {
/* 1186 */     return Duration.of(this.timeoutNanos, ChronoUnit.NANOS);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void setKeepAliveTime(long keepAliveTime, TimeUnit keepAliveUnits) {
/* 1201 */     Assert.checkMinimumParameter("keepAliveTime", 1L, keepAliveTime);
/* 1202 */     Assert.checkNotNullParam("keepAliveUnits", keepAliveUnits);
/* 1203 */     this.timeoutNanos = Math.max(1L, keepAliveUnits.toNanos(keepAliveTime));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setKeepAliveTime(Duration keepAliveTime) {
/* 1215 */     Assert.checkNotNullParam("keepAliveTime", keepAliveTime);
/* 1216 */     this.timeoutNanos = TimeUtil.clampedPositiveNanos(keepAliveTime);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaximumQueueSize() {
/* 1226 */     return maxQueueSizeOf(this.queueSize);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMaximumQueueSize(int maxQueueSize) {
/*      */     long oldVal;
/* 1237 */     Assert.checkMinimumParameter("maxQueueSize", 0, maxQueueSize);
/* 1238 */     Assert.checkMaximumParameter("maxQueueSize", 2147483647, maxQueueSize);
/* 1239 */     if (NO_QUEUE_LIMIT)
/*      */       return; 
/*      */     do {
/* 1242 */       oldVal = this.queueSize;
/* 1243 */     } while (!compareAndSetQueueSize(oldVal, withMaxQueueSize(oldVal, maxQueueSize)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Executor getHandoffExecutor() {
/* 1252 */     return this.handoffExecutor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setHandoffExecutor(Executor handoffExecutor) {
/* 1261 */     Assert.checkNotNullParam("handoffExecutor", handoffExecutor);
/* 1262 */     this.handoffExecutor = handoffExecutor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Thread.UncaughtExceptionHandler getExceptionHandler() {
/* 1271 */     return this.exceptionHandler;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
/* 1280 */     Assert.checkNotNullParam("exceptionHandler", exceptionHandler);
/* 1281 */     this.exceptionHandler = exceptionHandler;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTerminationTask(Runnable terminationTask) {
/* 1290 */     this.terminationTask = terminationTask;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getQueueSize() {
/* 1303 */     return NO_QUEUE_LIMIT ? -1 : currentQueueSizeOf(this.queueSize);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getLargestPoolSize() {
/* 1312 */     return UPDATE_STATISTICS ? this.peakThreadCount : -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getActiveCount() {
/* 1321 */     return UPDATE_ACTIVE_COUNT ? this.activeCount : -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getLargestQueueSize() {
/* 1331 */     return (UPDATE_STATISTICS && !NO_QUEUE_LIMIT) ? this.peakQueueSize : -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getSubmittedTaskCount() {
/* 1341 */     return UPDATE_STATISTICS ? this.submittedTaskCounter.longValue() : -1L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getRejectedTaskCount() {
/* 1351 */     return UPDATE_STATISTICS ? this.rejectedTaskCounter.longValue() : -1L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getCompletedTaskCount() {
/* 1361 */     return UPDATE_STATISTICS ? this.completedTaskCounter.longValue() : -1L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getPoolSize() {
/* 1370 */     return currentSizeOf(this.threadStatus);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Thread[] getRunningThreads() {
/* 1380 */     return this.runningThreads.<Thread>toArray(NO_THREADS);
/*      */   }
/*      */   
/*      */   static class MBeanUnregisterAction
/*      */     implements PrivilegedAction<Void> {
/*      */     private final Object handle;
/*      */     
/*      */     static void forceInit() {}
/*      */     
/*      */     MBeanUnregisterAction(Object handle) {
/* 1390 */       this.handle = handle;
/*      */     }
/*      */     
/*      */     public Void run() {
/*      */       try {
/* 1395 */         ManagementFactory.getPlatformMBeanServer().unregisterMBean(((ObjectInstance)this.handle).getObjectName());
/* 1396 */       } catch (Throwable throwable) {}
/*      */       
/* 1398 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   final class ThreadBody
/*      */     implements Runnable
/*      */   {
/*      */     private Runnable initialTask;
/*      */ 
/*      */     
/*      */     ThreadBody(Runnable initialTask) {
/* 1410 */       this.initialTask = initialTask;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void run() {
/*      */       EnhancedQueueExecutor.QNode node;
/* 1418 */       Thread currentThread = Thread.currentThread();
/* 1419 */       LongAdder spinMisses = EnhancedQueueExecutor.this.spinMisses;
/* 1420 */       EnhancedQueueExecutor.this.runningThreads.add(currentThread);
/*      */ 
/*      */       
/* 1423 */       doRunTask(getAndClearInitialTask());
/*      */ 
/*      */       
/* 1426 */       EnhancedQueueExecutor.PoolThreadNode nextPoolThreadNode = new EnhancedQueueExecutor.PoolThreadNode(currentThread);
/*      */ 
/*      */       
/*      */       label56: while (true) {
/* 1430 */         node = getOrAddNode(nextPoolThreadNode);
/* 1431 */         if (node instanceof EnhancedQueueExecutor.TaskNode) {
/*      */           
/* 1433 */           doRunTask(((EnhancedQueueExecutor.TaskNode)node).getAndClearTask()); continue;
/*      */         } 
/* 1435 */         if (node == nextPoolThreadNode) {
/*      */           
/* 1437 */           EnhancedQueueExecutor.PoolThreadNode newNode = nextPoolThreadNode;
/*      */           
/* 1439 */           nextPoolThreadNode = new EnhancedQueueExecutor.PoolThreadNode(currentThread);
/*      */           
/* 1441 */           long start = System.nanoTime();
/* 1442 */           long elapsed = 0L;
/*      */           while (true) {
/* 1444 */             Runnable task = newNode.getTask();
/* 1445 */             assert task != EnhancedQueueExecutor.ACCEPTED && task != EnhancedQueueExecutor.GAVE_UP;
/* 1446 */             if (task != EnhancedQueueExecutor.WAITING && task != EnhancedQueueExecutor.EXIT) {
/* 1447 */               if (newNode.compareAndSetTask(task, EnhancedQueueExecutor.ACCEPTED)) {
/*      */                 
/* 1449 */                 doRunTask(task);
/*      */                 
/*      */                 continue label56;
/*      */               } 
/*      */               
/* 1454 */               if (EnhancedQueueExecutor.UPDATE_STATISTICS) spinMisses.increment(); 
/*      */               continue;
/*      */             } 
/* 1457 */             long timeoutNanos = EnhancedQueueExecutor.this.timeoutNanos;
/* 1458 */             long oldVal = EnhancedQueueExecutor.this.threadStatus;
/* 1459 */             if (elapsed >= timeoutNanos || task == EnhancedQueueExecutor.EXIT || EnhancedQueueExecutor.currentSizeOf(oldVal) > EnhancedQueueExecutor.maxSizeOf(oldVal)) {
/*      */               
/* 1461 */               if (task == EnhancedQueueExecutor.EXIT || 
/* 1462 */                 EnhancedQueueExecutor.isShutdownRequested(oldVal) || 
/* 1463 */                 EnhancedQueueExecutor.isAllowCoreTimeout(oldVal) || 
/* 1464 */                 EnhancedQueueExecutor.currentSizeOf(oldVal) > EnhancedQueueExecutor.coreSizeOf(oldVal)) {
/*      */                 
/* 1466 */                 if (newNode.compareAndSetTask(task, EnhancedQueueExecutor.GAVE_UP)) {
/*      */                   while (true) {
/* 1468 */                     if (EnhancedQueueExecutor.this.tryDeallocateThread(oldVal)) {
/*      */                       
/* 1470 */                       EnhancedQueueExecutor.this.runningThreads.remove(currentThread);
/*      */                       return;
/*      */                     } 
/* 1473 */                     if (EnhancedQueueExecutor.UPDATE_STATISTICS) spinMisses.increment(); 
/* 1474 */                     oldVal = EnhancedQueueExecutor.this.threadStatus;
/*      */                   } 
/*      */                   break;
/*      */                 } 
/*      */                 continue;
/*      */               } 
/* 1480 */               if (elapsed >= timeoutNanos) {
/* 1481 */                 newNode.park(EnhancedQueueExecutor.this);
/*      */               } else {
/* 1483 */                 newNode.park(EnhancedQueueExecutor.this, timeoutNanos - elapsed);
/*      */               } 
/* 1485 */               Thread.interrupted();
/* 1486 */               elapsed = System.nanoTime() - start;
/*      */ 
/*      */               
/*      */               continue;
/*      */             } 
/*      */             
/* 1492 */             assert task == EnhancedQueueExecutor.WAITING;
/* 1493 */             newNode.park(EnhancedQueueExecutor.this, timeoutNanos - elapsed);
/* 1494 */             Thread.interrupted();
/* 1495 */             elapsed = System.nanoTime() - start;
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*      */         break;
/*      */       } 
/*      */ 
/*      */       
/* 1505 */       assert node instanceof EnhancedQueueExecutor.TerminateWaiterNode;
/*      */       
/* 1507 */       EnhancedQueueExecutor.this.runningThreads.remove(currentThread);
/* 1508 */       EnhancedQueueExecutor.this.deallocateThread();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private EnhancedQueueExecutor.QNode getOrAddNode(EnhancedQueueExecutor.PoolThreadNode nextPoolThreadNode) {
/*      */       while (true) {
/* 1520 */         EnhancedQueueExecutor.TaskNode head = EnhancedQueueExecutor.this.head;
/* 1521 */         EnhancedQueueExecutor.QNode headNext = head.getNext();
/* 1522 */         if (headNext instanceof EnhancedQueueExecutor.TaskNode) {
/* 1523 */           EnhancedQueueExecutor.TaskNode taskNode = (EnhancedQueueExecutor.TaskNode)headNext;
/* 1524 */           if (EnhancedQueueExecutor.this.compareAndSetHead(head, taskNode)) {
/* 1525 */             if (!EnhancedQueueExecutor.NO_QUEUE_LIMIT) EnhancedQueueExecutor.this.decreaseQueueSize(); 
/* 1526 */             return taskNode;
/*      */           } 
/* 1528 */         } else if (headNext instanceof EnhancedQueueExecutor.PoolThreadNode || headNext == null) {
/* 1529 */           nextPoolThreadNode.setNextRelaxed(headNext);
/* 1530 */           if (head.compareAndSetNext(headNext, nextPoolThreadNode)) {
/* 1531 */             return nextPoolThreadNode;
/*      */           }
/*      */         } else {
/* 1534 */           assert headNext instanceof EnhancedQueueExecutor.TerminateWaiterNode;
/* 1535 */           return headNext;
/*      */         } 
/* 1537 */         if (EnhancedQueueExecutor.UPDATE_STATISTICS) EnhancedQueueExecutor.this.spinMisses.increment(); 
/* 1538 */         JDKSpecific.onSpinWait();
/*      */       } 
/*      */     }
/*      */     
/*      */     private Runnable getAndClearInitialTask() {
/*      */       try {
/* 1544 */         return this.initialTask;
/*      */       } finally {
/* 1546 */         this.initialTask = null;
/*      */       } 
/*      */     }
/*      */     
/*      */     void doRunTask(Runnable task) {
/* 1551 */       if (task != null) {
/* 1552 */         if (EnhancedQueueExecutor.isShutdownInterrupt(EnhancedQueueExecutor.this.threadStatus)) {
/* 1553 */           Thread.currentThread().interrupt();
/*      */         } else {
/* 1555 */           Thread.interrupted();
/*      */         } 
/* 1557 */         if (EnhancedQueueExecutor.UPDATE_ACTIVE_COUNT) EnhancedQueueExecutor.this.incrementActiveCount(); 
/* 1558 */         EnhancedQueueExecutor.this.safeRun(task);
/* 1559 */         if (EnhancedQueueExecutor.UPDATE_ACTIVE_COUNT) {
/* 1560 */           EnhancedQueueExecutor.this.decrementActiveCount();
/* 1561 */           if (EnhancedQueueExecutor.UPDATE_STATISTICS) {
/* 1562 */             EnhancedQueueExecutor.this.completedTaskCounter.increment();
/*      */           }
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int tryAllocateThread(float growthResistance) {
/*      */     while (true) {
/* 1583 */       long oldStat = this.threadStatus;
/* 1584 */       if (isShutdownRequested(oldStat)) {
/* 1585 */         return 2;
/*      */       }
/* 1587 */       int oldSize = currentSizeOf(oldStat);
/* 1588 */       if (oldSize >= maxSizeOf(oldStat))
/*      */       {
/* 1590 */         return 1;
/*      */       }
/* 1592 */       if (oldSize >= coreSizeOf(oldStat) && oldSize > 0)
/*      */       {
/* 1594 */         if (growthResistance != 0.0F && (growthResistance == 1.0F || ThreadLocalRandom.current().nextFloat() < growthResistance))
/*      */         {
/* 1596 */           return 1;
/*      */         }
/*      */       }
/*      */       
/* 1600 */       int newSize = oldSize + 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1611 */       if (compareAndSetThreadStatus(oldStat, withCurrentSize(oldStat, newSize))) {
/*      */         
/* 1613 */         if (UPDATE_STATISTICS) {
/*      */           int oldVal;
/*      */           do {
/* 1616 */             oldVal = this.peakThreadCount;
/* 1617 */           } while (oldVal < newSize && 
/* 1618 */             !compareAndSetPeakThreadCount(oldVal, newSize));
/*      */         } 
/* 1620 */         return 0;
/*      */       } 
/* 1622 */       if (UPDATE_STATISTICS) this.spinMisses.increment();
/*      */     
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   void deallocateThread() {
/*      */     long oldStat;
/*      */     do {
/* 1632 */       oldStat = this.threadStatus;
/* 1633 */     } while (!tryDeallocateThread(oldStat));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean tryDeallocateThread(long oldStat) {
/* 1650 */     long newStat = withCurrentSize(oldStat, currentSizeOf(oldStat) - 1);
/* 1651 */     if (currentSizeOf(newStat) == 0 && isShutdownRequested(oldStat)) {
/* 1652 */       newStat = withShutdownComplete(newStat);
/*      */     }
/* 1654 */     if (!compareAndSetThreadStatus(oldStat, newStat)) return false; 
/* 1655 */     if (isShutdownComplete(newStat)) {
/* 1656 */       completeTermination();
/*      */     }
/* 1658 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean doStartThread(Runnable runnable) throws RejectedExecutionException {
/*      */     Thread thread;
/*      */     try {
/* 1671 */       thread = this.threadFactory.newThread(new ThreadBody(runnable));
/* 1672 */     } catch (Throwable t) {
/* 1673 */       if (runnable != null) {
/* 1674 */         if (UPDATE_STATISTICS) this.rejectedTaskCounter.increment(); 
/* 1675 */         rejectException(runnable, t);
/*      */       } 
/* 1677 */       return false;
/*      */     } 
/* 1679 */     if (thread == null) {
/* 1680 */       if (runnable != null) {
/* 1681 */         if (UPDATE_STATISTICS) this.rejectedTaskCounter.increment(); 
/* 1682 */         rejectNoThread(runnable);
/*      */       } 
/* 1684 */       return false;
/*      */     } 
/*      */     try {
/* 1687 */       thread.start();
/* 1688 */     } catch (Throwable t) {
/* 1689 */       if (runnable != null) {
/* 1690 */         if (UPDATE_STATISTICS) this.rejectedTaskCounter.increment(); 
/* 1691 */         rejectException(runnable, t);
/*      */       } 
/* 1693 */       return false;
/*      */     } 
/* 1695 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int tryExecute(Runnable runnable) {
/*      */     QNode tailNext;
/* 1704 */     if (TAIL_LOCK) lockTail(); 
/* 1705 */     TaskNode tail = this.tail;
/* 1706 */     TaskNode node = null;
/*      */     while (true) {
/* 1708 */       tailNext = tail.getNext();
/* 1709 */       if (tailNext instanceof TaskNode) {
/*      */         TaskNode tailNextTaskNode;
/*      */         do {
/* 1712 */           if (UPDATE_STATISTICS) this.spinMisses.increment(); 
/* 1713 */           tailNextTaskNode = (TaskNode)tailNext;
/*      */           
/* 1715 */           tail = tailNextTaskNode;
/* 1716 */           tailNext = tail.getNext();
/* 1717 */         } while (tailNext instanceof TaskNode);
/*      */         
/* 1719 */         if (UPDATE_TAIL) compareAndSetTail(tail, tailNextTaskNode);
/*      */       
/*      */       } 
/* 1722 */       assert !(tailNext instanceof TaskNode);
/* 1723 */       if (tailNext instanceof PoolThreadNode) {
/* 1724 */         QNode tailNextNext = tailNext.getNext();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1739 */         if (tail.compareAndSetNext(tailNext, tailNextNext)) {
/* 1740 */           assert tail instanceof TaskNode && tail.task == null;
/* 1741 */           PoolThreadNode consumerNode = (PoolThreadNode)tailNext;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1751 */           if (consumerNode.compareAndSetTask(WAITING, runnable)) {
/* 1752 */             if (TAIL_LOCK) unlockTail(); 
/* 1753 */             consumerNode.unpark();
/* 1754 */             return 0;
/*      */           } 
/*      */         } 
/*      */         
/* 1758 */         if (UPDATE_STATISTICS) this.spinMisses.increment();
/*      */         
/* 1760 */         tail = this.tail; continue;
/* 1761 */       }  if (tailNext == null) {
/*      */         
/* 1763 */         int tr = tryAllocateThread(this.growthResistance);
/* 1764 */         if (tr == 0) {
/* 1765 */           if (TAIL_LOCK) unlockTail(); 
/* 1766 */           return 3;
/*      */         } 
/* 1768 */         if (tr == 2) {
/* 1769 */           if (TAIL_LOCK) unlockTail(); 
/* 1770 */           return 2;
/*      */         } 
/* 1772 */         assert tr == 1;
/*      */         
/* 1774 */         if (!NO_QUEUE_LIMIT && !increaseQueueSize()) {
/*      */ 
/*      */           
/* 1777 */           tr = tryAllocateThread(0.0F);
/* 1778 */           if (TAIL_LOCK) unlockTail(); 
/* 1779 */           if (tr == 0) {
/* 1780 */             return 3;
/*      */           }
/* 1782 */           if (tr == 2) {
/* 1783 */             return 2;
/*      */           }
/* 1785 */           assert tr == 1;
/* 1786 */           return 1;
/*      */         } 
/*      */         
/* 1789 */         if (node == null)
/*      */         {
/* 1791 */           node = new TaskNode(runnable);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1802 */         if (tail.compareAndSetNext(null, node)) {
/*      */ 
/*      */           
/* 1805 */           compareAndSetTail(tail, node);
/* 1806 */           if (TAIL_LOCK) unlockTail(); 
/* 1807 */           return 0;
/*      */         } 
/*      */         
/* 1810 */         if (!NO_QUEUE_LIMIT) decreaseQueueSize(); 
/* 1811 */         if (UPDATE_STATISTICS) this.spinMisses.increment();
/*      */         
/* 1813 */         tail = this.tail; continue;
/*      */       }  break;
/* 1815 */     }  if (TAIL_LOCK) unlockTail();
/*      */     
/* 1817 */     assert tailNext instanceof TerminateWaiterNode;
/*      */     
/* 1819 */     return 2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void completeTermination() {
/* 1831 */     Thread.interrupted();
/* 1832 */     Runnable terminationTask = this.terminationTask;
/* 1833 */     this.terminationTask = null;
/* 1834 */     safeRun(terminationTask);
/*      */     
/* 1836 */     Waiter waiters = getAndSetTerminationWaiters(TERMINATE_COMPLETE_WAITER);
/* 1837 */     while (waiters != null) {
/* 1838 */       LockSupport.unpark(waiters.getThread());
/* 1839 */       waiters = waiters.getNext();
/*      */     } 
/* 1841 */     this.tail.setNext(TERMINATE_COMPLETE);
/* 1842 */     if (!DISABLE_MBEAN) {
/* 1843 */       Object handle = this.handle;
/* 1844 */       if (handle != null) {
/* 1845 */         AccessController.doPrivileged(new MBeanUnregisterAction(handle), this.acc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void incrementActiveCount() {
/* 1855 */     JBossExecutors.unsafe.getAndAddInt(this, activeCountOffset, 1);
/*      */   }
/*      */   
/*      */   void decrementActiveCount() {
/* 1859 */     JBossExecutors.unsafe.getAndAddInt(this, activeCountOffset, -1);
/*      */   }
/*      */   
/*      */   boolean compareAndSetPeakThreadCount(int expect, int update) {
/* 1863 */     return JBossExecutors.unsafe.compareAndSwapInt(this, peakThreadCountOffset, expect, update);
/*      */   }
/*      */   
/*      */   boolean compareAndSetPeakQueueSize(int expect, int update) {
/* 1867 */     return JBossExecutors.unsafe.compareAndSwapInt(this, peakQueueSizeOffset, expect, update);
/*      */   }
/*      */   
/*      */   boolean compareAndSetQueueSize(long expect, long update) {
/* 1871 */     return JBossExecutors.unsafe.compareAndSwapLong(this, queueSizeOffset, expect, update);
/*      */   }
/*      */   
/*      */   boolean compareAndSetTerminationWaiters(Waiter expect, Waiter update) {
/* 1875 */     return JBossExecutors.unsafe.compareAndSwapObject(this, terminationWaitersOffset, expect, update);
/*      */   }
/*      */   
/*      */   Waiter getAndSetTerminationWaiters(Waiter update) {
/* 1879 */     return (Waiter)JBossExecutors.unsafe.getAndSetObject(this, terminationWaitersOffset, update);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean increaseQueueSize() {
/* 1887 */     long oldVal = this.queueSize;
/* 1888 */     int oldSize = currentQueueSizeOf(oldVal);
/* 1889 */     if (oldSize >= maxQueueSizeOf(oldVal))
/*      */     {
/* 1891 */       return false;
/*      */     }
/* 1893 */     int newSize = oldSize + 1;
/* 1894 */     while (!compareAndSetQueueSize(oldVal, withCurrentQueueSize(oldVal, newSize))) {
/* 1895 */       if (UPDATE_STATISTICS) this.spinMisses.increment(); 
/* 1896 */       oldVal = this.queueSize;
/* 1897 */       oldSize = currentQueueSizeOf(oldVal);
/* 1898 */       if (oldSize >= maxQueueSizeOf(oldVal))
/*      */       {
/* 1900 */         return false;
/*      */       }
/* 1902 */       newSize = oldSize + 1;
/*      */     } 
/* 1904 */     if (UPDATE_STATISTICS) {
/*      */       
/*      */       do {
/* 1907 */         oldSize = this.peakQueueSize;
/* 1908 */       } while (newSize > oldSize && 
/* 1909 */         !compareAndSetPeakQueueSize(oldSize, newSize));
/*      */     }
/* 1911 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   void decreaseQueueSize() {
/* 1916 */     long oldVal = this.queueSize;
/* 1917 */     assert currentQueueSizeOf(oldVal) > 0;
/* 1918 */     while (!compareAndSetQueueSize(oldVal, withCurrentQueueSize(oldVal, currentQueueSizeOf(oldVal) - 1))) {
/* 1919 */       if (UPDATE_STATISTICS) this.spinMisses.increment(); 
/* 1920 */       oldVal = this.queueSize;
/* 1921 */       assert currentQueueSizeOf(oldVal) > 0;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int currentQueueSizeOf(long queueSize) {
/* 1930 */     return (int)(queueSize & 0x7FFFFFFFL);
/*      */   }
/*      */   
/*      */   static long withCurrentQueueSize(long queueSize, int current) {
/* 1934 */     assert current >= 0;
/* 1935 */     return queueSize & 0xFFFFFFFF00000000L | current;
/*      */   }
/*      */   
/*      */   static int maxQueueSizeOf(long queueSize) {
/* 1939 */     return (int)(queueSize >>> 32L & 0x7FFFFFFFL);
/*      */   }
/*      */   
/*      */   static long withMaxQueueSize(long queueSize, int max) {
/* 1943 */     assert max >= 0;
/* 1944 */     return queueSize & 0xFFFFFFFFL | max << 32L;
/*      */   }
/*      */   
/*      */   static int coreSizeOf(long status) {
/* 1948 */     return (int)(status >>> 20L & 0xFFFFFL);
/*      */   }
/*      */   
/*      */   static int maxSizeOf(long status) {
/* 1952 */     return (int)(status >>> 40L & 0xFFFFFL);
/*      */   }
/*      */   
/*      */   static int currentSizeOf(long status) {
/* 1956 */     return (int)(status >>> 0L & 0xFFFFFL);
/*      */   }
/*      */   
/*      */   static long withCoreSize(long status, int newCoreSize) {
/* 1960 */     assert 0 <= newCoreSize && newCoreSize <= 1048575L;
/* 1961 */     return status & 0xFFFFFF00000FFFFFL | newCoreSize << 20L;
/*      */   }
/*      */   
/*      */   static long withCurrentSize(long status, int newCurrentSize) {
/* 1965 */     assert 0 <= newCurrentSize && newCurrentSize <= 1048575L;
/* 1966 */     return status & 0xFFFFFFFFFFF00000L | newCurrentSize << 0L;
/*      */   }
/*      */   
/*      */   static long withMaxSize(long status, int newMaxSize) {
/* 1970 */     assert 0 <= newMaxSize && newMaxSize <= 1048575L;
/* 1971 */     return status & 0xF00000FFFFFFFFFFL | newMaxSize << 40L;
/*      */   }
/*      */   
/*      */   static long withShutdownRequested(long status) {
/* 1975 */     return status | 0x2000000000000000L;
/*      */   }
/*      */   
/*      */   static long withShutdownComplete(long status) {
/* 1979 */     return status | Long.MIN_VALUE;
/*      */   }
/*      */   
/*      */   static long withShutdownInterrupt(long status) {
/* 1983 */     return status | 0x4000000000000000L;
/*      */   }
/*      */   
/*      */   static long withAllowCoreTimeout(long status, boolean allowed) {
/* 1987 */     return allowed ? (status | 0x1000000000000000L) : (status & 0xEFFFFFFFFFFFFFFFL);
/*      */   }
/*      */   
/*      */   static boolean isShutdownRequested(long status) {
/* 1991 */     return ((status & 0x2000000000000000L) != 0L);
/*      */   }
/*      */   
/*      */   static boolean isShutdownComplete(long status) {
/* 1995 */     return ((status & Long.MIN_VALUE) != 0L);
/*      */   }
/*      */   
/*      */   static boolean isShutdownInterrupt(long threadStatus) {
/* 1999 */     return ((threadStatus & 0x4000000000000000L) != 0L);
/*      */   }
/*      */   
/*      */   static boolean isAllowCoreTimeout(long oldVal) {
/* 2003 */     return ((oldVal & 0x1000000000000000L) != 0L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void safeRun(Runnable task) {
/* 2015 */     if (task == null)
/* 2016 */       return;  Thread currentThread = Thread.currentThread();
/* 2017 */     JBossExecutors.clearContextClassLoader(currentThread);
/*      */     try {
/* 2019 */       task.run();
/* 2020 */     } catch (Throwable t) {
/*      */       try {
/* 2022 */         this.exceptionHandler.uncaughtException(Thread.currentThread(), t);
/* 2023 */       } catch (Throwable throwable) {}
/*      */     }
/*      */     finally {
/*      */       
/* 2027 */       JBossExecutors.clearContextClassLoader(currentThread);
/*      */       
/* 2029 */       Thread.interrupted();
/*      */     } 
/*      */   }
/*      */   
/*      */   void rejectException(Runnable task, Throwable cause) {
/*      */     try {
/* 2035 */       this.handoffExecutor.execute(task);
/* 2036 */     } catch (Throwable t) {
/* 2037 */       t.addSuppressed(cause);
/* 2038 */       throw t;
/*      */     } 
/*      */   }
/*      */   
/*      */   void rejectNoThread(Runnable task) {
/*      */     try {
/* 2044 */       this.handoffExecutor.execute(task);
/* 2045 */     } catch (Throwable t) {
/* 2046 */       t.addSuppressed(new RejectedExecutionException("No threads available"));
/* 2047 */       throw t;
/*      */     } 
/*      */   }
/*      */   
/*      */   void rejectQueueFull(Runnable task) {
/*      */     try {
/* 2053 */       this.handoffExecutor.execute(task);
/* 2054 */     } catch (Throwable t) {
/* 2055 */       t.addSuppressed(new RejectedExecutionException("Queue is full"));
/* 2056 */       throw t;
/*      */     } 
/*      */   }
/*      */   
/*      */   void rejectShutdown(Runnable task) {
/*      */     try {
/* 2062 */       this.handoffExecutor.execute(task);
/* 2063 */     } catch (Throwable t) {
/* 2064 */       t.addSuppressed(new RejectedExecutionException("Executor is being shut down"));
/* 2065 */       throw t;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   static abstract class QNode
/*      */   {
/*      */     private static final long nextOffset;
/*      */     
/*      */     private volatile QNode next;
/*      */     
/*      */     static {
/*      */       try {
/* 2078 */         nextOffset = JBossExecutors.unsafe.objectFieldOffset(QNode.class.getDeclaredField("next"));
/* 2079 */       } catch (NoSuchFieldException e) {
/* 2080 */         throw new NoSuchFieldError(e.getMessage());
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean compareAndSetNext(QNode expect, QNode update) {
/* 2088 */       return JBossExecutors.unsafe.compareAndSwapObject(this, nextOffset, expect, update);
/*      */     }
/*      */     
/*      */     QNode getNext() {
/* 2092 */       return this.next;
/*      */     }
/*      */     
/*      */     void setNext(QNode node) {
/* 2096 */       this.next = node;
/*      */     }
/*      */     
/*      */     void setNextRelaxed(QNode node) {
/* 2100 */       JBossExecutors.unsafe.putObject(this, nextOffset, node);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static abstract class PoolThreadNodeBase
/*      */     extends QNode
/*      */   {
/*      */     int p00;
/*      */     
/*      */     int p01;
/*      */     
/*      */     int p02;
/*      */     int p03;
/*      */     int p04;
/*      */     int p05;
/*      */     int p06;
/*      */     int p07;
/*      */     int p08;
/*      */     int p09;
/*      */     int p0A;
/*      */     int p0B;
/*      */     int p0C;
/*      */     int p0D;
/*      */     int p0E;
/*      */     int p0F;
/*      */   }
/*      */   
/*      */   static final class PoolThreadNode
/*      */     extends PoolThreadNodeBase
/*      */   {
/*      */     private static final int STATE_NORMAL = 0;
/*      */     private static final int STATE_PARKED = 1;
/*      */     private static final int STATE_UNPARKED = 2;
/*      */     private static final long taskOffset;
/*      */     private static final long parkedOffset;
/*      */     private final Thread thread;
/*      */     private volatile Runnable task;
/*      */     private volatile int parked;
/*      */     
/*      */     static {
/*      */       try {
/* 2142 */         taskOffset = JBossExecutors.unsafe.objectFieldOffset(PoolThreadNode.class.getDeclaredField("task"));
/* 2143 */         parkedOffset = JBossExecutors.unsafe.objectFieldOffset(PoolThreadNode.class.getDeclaredField("parked"));
/* 2144 */       } catch (NoSuchFieldException e) {
/* 2145 */         throw new NoSuchFieldError(e.getMessage());
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     PoolThreadNode(Thread thread) {
/* 2161 */       this.thread = thread;
/* 2162 */       this.task = EnhancedQueueExecutor.WAITING;
/*      */     }
/*      */     
/*      */     boolean compareAndSetTask(Runnable expect, Runnable update) {
/* 2166 */       return (this.task == expect && JBossExecutors.unsafe.compareAndSwapObject(this, taskOffset, expect, update));
/*      */     }
/*      */     
/*      */     Runnable getTask() {
/* 2170 */       return this.task;
/*      */     }
/*      */     
/*      */     PoolThreadNode getNext() {
/* 2174 */       return (PoolThreadNode)super.getNext();
/*      */     }
/*      */     
/*      */     void park(EnhancedQueueExecutor enhancedQueueExecutor) {
/* 2178 */       int spins = EnhancedQueueExecutor.PARK_SPINS;
/* 2179 */       if (this.parked == 2 && JBossExecutors.unsafe.compareAndSwapInt(this, parkedOffset, 2, 0)) {
/*      */         return;
/*      */       }
/* 2182 */       while (spins > 0) {
/* 2183 */         if (spins < EnhancedQueueExecutor.YIELD_FACTOR) {
/* 2184 */           Thread.yield();
/*      */         } else {
/* 2186 */           JDKSpecific.onSpinWait();
/*      */         } 
/* 2188 */         spins--;
/* 2189 */         if (this.parked == 2 && JBossExecutors.unsafe.compareAndSwapInt(this, parkedOffset, 2, 0)) {
/*      */           return;
/*      */         }
/*      */       } 
/* 2193 */       if (this.parked == 0 && JBossExecutors.unsafe.compareAndSwapInt(this, parkedOffset, 0, 1))
/* 2194 */         try { LockSupport.park(enhancedQueueExecutor);
/*      */            }
/*      */         
/*      */         finally
/*      */         
/* 2199 */         { this.parked = 0; }
/*      */          
/*      */     }
/*      */     
/*      */     void park(EnhancedQueueExecutor enhancedQueueExecutor, long nanos) {
/*      */       long remaining;
/* 2205 */       int spins = EnhancedQueueExecutor.PARK_SPINS;
/* 2206 */       if (spins > 0) {
/* 2207 */         long start = System.nanoTime();
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2212 */         if (this.parked == 2 && JBossExecutors.unsafe.compareAndSwapInt(this, parkedOffset, 2, 0)) {
/*      */           return;
/*      */         }
/* 2215 */         while (spins > 0) {
/* 2216 */           if (spins < EnhancedQueueExecutor.YIELD_FACTOR) {
/* 2217 */             Thread.yield();
/*      */           } else {
/* 2219 */             JDKSpecific.onSpinWait();
/*      */           } 
/* 2221 */           if (this.parked == 2 && JBossExecutors.unsafe.compareAndSwapInt(this, parkedOffset, 2, 0)) {
/*      */             return;
/*      */           }
/* 2224 */           spins--;
/*      */         } 
/* 2226 */         remaining = nanos - System.nanoTime() - start;
/* 2227 */         if (remaining < 0L) {
/*      */           return;
/*      */         }
/*      */       } else {
/* 2231 */         remaining = nanos;
/*      */       } 
/* 2233 */       if (this.parked == 0 && JBossExecutors.unsafe.compareAndSwapInt(this, parkedOffset, 0, 1))
/* 2234 */         try { LockSupport.parkNanos(enhancedQueueExecutor, remaining);
/*      */            }
/*      */         
/*      */         finally
/*      */         
/* 2239 */         { this.parked = 0; }
/*      */          
/*      */     }
/*      */     
/*      */     void unpark() {
/* 2244 */       if (this.parked == 0 && JBossExecutors.unsafe.compareAndSwapInt(this, parkedOffset, 0, 2)) {
/*      */         return;
/*      */       }
/* 2247 */       LockSupport.unpark(this.thread);
/*      */     }
/*      */   }
/*      */   
/*      */   static final class TerminateWaiterNode
/*      */     extends QNode {}
/*      */   
/*      */   static final class TaskNode
/*      */     extends QNode {
/*      */     volatile Runnable task;
/*      */     
/*      */     TaskNode(Runnable task) {
/* 2259 */       this.task = task;
/*      */     }
/*      */     
/*      */     Runnable getAndClearTask() {
/*      */       try {
/* 2264 */         return this.task;
/*      */       } finally {
/* 2266 */         this.task = null;
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final class MXBeanImpl
/*      */     implements StandardThreadPoolMXBean
/*      */   {
/*      */     public float getGrowthResistance() {
/* 2280 */       return EnhancedQueueExecutor.this.getGrowthResistance();
/*      */     }
/*      */     
/*      */     public void setGrowthResistance(float value) {
/* 2284 */       EnhancedQueueExecutor.this.setGrowthResistance(value);
/*      */     }
/*      */     
/*      */     public boolean isGrowthResistanceSupported() {
/* 2288 */       return true;
/*      */     }
/*      */     
/*      */     public int getCorePoolSize() {
/* 2292 */       return EnhancedQueueExecutor.this.getCorePoolSize();
/*      */     }
/*      */     
/*      */     public void setCorePoolSize(int corePoolSize) {
/* 2296 */       EnhancedQueueExecutor.this.setCorePoolSize(corePoolSize);
/*      */     }
/*      */     
/*      */     public boolean isCorePoolSizeSupported() {
/* 2300 */       return true;
/*      */     }
/*      */     
/*      */     public boolean prestartCoreThread() {
/* 2304 */       return EnhancedQueueExecutor.this.prestartCoreThread();
/*      */     }
/*      */     
/*      */     public int prestartAllCoreThreads() {
/* 2308 */       return EnhancedQueueExecutor.this.prestartAllCoreThreads();
/*      */     }
/*      */     
/*      */     public boolean isCoreThreadPrestartSupported() {
/* 2312 */       return true;
/*      */     }
/*      */     
/*      */     public int getMaximumPoolSize() {
/* 2316 */       return EnhancedQueueExecutor.this.getMaximumPoolSize();
/*      */     }
/*      */     
/*      */     public void setMaximumPoolSize(int maxPoolSize) {
/* 2320 */       EnhancedQueueExecutor.this.setMaximumPoolSize(maxPoolSize);
/*      */     }
/*      */     
/*      */     public int getPoolSize() {
/* 2324 */       return EnhancedQueueExecutor.this.getPoolSize();
/*      */     }
/*      */     
/*      */     public int getLargestPoolSize() {
/* 2328 */       return EnhancedQueueExecutor.this.getLargestPoolSize();
/*      */     }
/*      */     
/*      */     public int getActiveCount() {
/* 2332 */       return EnhancedQueueExecutor.this.getActiveCount();
/*      */     }
/*      */     
/*      */     public boolean isAllowCoreThreadTimeOut() {
/* 2336 */       return EnhancedQueueExecutor.this.allowsCoreThreadTimeOut();
/*      */     }
/*      */     
/*      */     public void setAllowCoreThreadTimeOut(boolean value) {
/* 2340 */       EnhancedQueueExecutor.this.allowCoreThreadTimeOut(value);
/*      */     }
/*      */     
/*      */     public long getKeepAliveTimeSeconds() {
/* 2344 */       return EnhancedQueueExecutor.this.getKeepAliveTime().getSeconds();
/*      */     }
/*      */     
/*      */     public void setKeepAliveTimeSeconds(long seconds) {
/* 2348 */       EnhancedQueueExecutor.this.setKeepAliveTime(Duration.of(seconds, ChronoUnit.SECONDS));
/*      */     }
/*      */     
/*      */     public int getMaximumQueueSize() {
/* 2352 */       return EnhancedQueueExecutor.this.getMaximumQueueSize();
/*      */     }
/*      */     
/*      */     public void setMaximumQueueSize(int size) {
/* 2356 */       EnhancedQueueExecutor.this.setMaximumQueueSize(size);
/*      */     }
/*      */     
/*      */     public int getQueueSize() {
/* 2360 */       return EnhancedQueueExecutor.this.getQueueSize();
/*      */     }
/*      */     
/*      */     public int getLargestQueueSize() {
/* 2364 */       return EnhancedQueueExecutor.this.getLargestQueueSize();
/*      */     }
/*      */     
/*      */     public boolean isQueueBounded() {
/* 2368 */       return !EnhancedQueueExecutor.NO_QUEUE_LIMIT;
/*      */     }
/*      */     
/*      */     public boolean isQueueSizeModifiable() {
/* 2372 */       return !EnhancedQueueExecutor.NO_QUEUE_LIMIT;
/*      */     }
/*      */     
/*      */     public boolean isShutdown() {
/* 2376 */       return EnhancedQueueExecutor.this.isShutdown();
/*      */     }
/*      */     
/*      */     public boolean isTerminating() {
/* 2380 */       return EnhancedQueueExecutor.this.isTerminating();
/*      */     }
/*      */     
/*      */     public boolean isTerminated() {
/* 2384 */       return EnhancedQueueExecutor.this.isTerminated();
/*      */     }
/*      */     
/*      */     public long getSubmittedTaskCount() {
/* 2388 */       return EnhancedQueueExecutor.this.getSubmittedTaskCount();
/*      */     }
/*      */     
/*      */     public long getRejectedTaskCount() {
/* 2392 */       return EnhancedQueueExecutor.this.getRejectedTaskCount();
/*      */     }
/*      */     
/*      */     public long getCompletedTaskCount() {
/* 2396 */       return EnhancedQueueExecutor.this.getCompletedTaskCount();
/*      */     }
/*      */     
/*      */     public long getSpinMissCount() {
/* 2400 */       return EnhancedQueueExecutor.this.spinMisses.longValue();
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\EnhancedQueueExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
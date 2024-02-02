package org.jboss.threads;

import java.lang.management.ManagementFactory;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.LockSupport;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.jboss.threads.management.ManageableThreadPoolExecutorService;
import org.jboss.threads.management.StandardThreadPoolMXBean;
import org.wildfly.common.Assert;
import org.wildfly.common.cpu.ProcessorInfo;

public final class EnhancedQueueExecutor extends EnhancedQueueExecutorBase6 implements ManageableThreadPoolExecutorService {
   private static final Thread[] NO_THREADS = new Thread[0];
   public static final boolean DISABLE_HINT;
   static final boolean UPDATE_TAIL;
   static final boolean UPDATE_STATISTICS;
   static final boolean UPDATE_ACTIVE_COUNT;
   static final boolean NO_QUEUE_LIMIT;
   static final boolean REGISTER_MBEAN;
   static final boolean DISABLE_MBEAN;
   static final int PARK_SPINS;
   static final int YIELD_FACTOR;
   static final Executor DEFAULT_HANDLER;
   private final ThreadFactory threadFactory;
   private final Set<Thread> runningThreads = Collections.newSetFromMap(new ConcurrentHashMap());
   private final MXBeanImpl mxBean;
   private final Object handle;
   private final AccessControlContext acc = AccessController.getContext();
   volatile Waiter terminationWaiters;
   volatile long queueSize;
   volatile long timeoutNanos;
   volatile float growthResistance;
   volatile Executor handoffExecutor;
   volatile Thread.UncaughtExceptionHandler exceptionHandler;
   volatile Runnable terminationTask;
   volatile int peakThreadCount;
   volatile int peakQueueSize;
   private final LongAdder submittedTaskCounter = new LongAdder();
   private final LongAdder completedTaskCounter = new LongAdder();
   private final LongAdder rejectedTaskCounter = new LongAdder();
   private final LongAdder spinMisses = new LongAdder();
   volatile int activeCount;
   private static final long terminationWaitersOffset;
   private static final long queueSizeOffset;
   private static final long peakThreadCountOffset;
   private static final long activeCountOffset;
   private static final long peakQueueSizeOffset;
   private static final Object sequenceBase;
   private static final long sequenceOffset;
   private static final long TS_THREAD_CNT_MASK = 1048575L;
   private static final long TS_CURRENT_SHIFT = 0L;
   private static final long TS_CORE_SHIFT = 20L;
   private static final long TS_MAX_SHIFT = 40L;
   private static final long TS_ALLOW_CORE_TIMEOUT = 1152921504606846976L;
   private static final long TS_SHUTDOWN_REQUESTED = 2305843009213693952L;
   private static final long TS_SHUTDOWN_INTERRUPT = 4611686018427387904L;
   private static final long TS_SHUTDOWN_COMPLETE = Long.MIN_VALUE;
   private static final int EXE_OK = 0;
   private static final int EXE_REJECT_QUEUE_FULL = 1;
   private static final int EXE_REJECT_SHUTDOWN = 2;
   private static final int EXE_CREATE_THREAD = 3;
   private static final int AT_YES = 0;
   private static final int AT_NO = 1;
   private static final int AT_SHUTDOWN = 2;
   static final QNode TERMINATE_REQUESTED;
   static final QNode TERMINATE_COMPLETE;
   static final Waiter TERMINATE_COMPLETE_WAITER;
   static final Runnable WAITING;
   static final Runnable GAVE_UP;
   static final Runnable ACCEPTED;
   static final Runnable EXIT;
   static volatile int sequence;

   EnhancedQueueExecutor(Builder builder) {
      int maxSize = builder.getMaximumPoolSize();
      int coreSize = Math.min(builder.getCorePoolSize(), maxSize);
      this.handoffExecutor = builder.getHandoffExecutor();
      this.exceptionHandler = builder.getExceptionHandler();
      this.threadFactory = builder.getThreadFactory();
      this.terminationTask = builder.getTerminationTask();
      this.growthResistance = builder.getGrowthResistance();
      Duration keepAliveTime = builder.getKeepAliveTime();
      this.threadStatus = withCoreSize(withMaxSize(withAllowCoreTimeout(0L, builder.allowsCoreThreadTimeOut()), maxSize), coreSize);
      this.timeoutNanos = TimeUtil.clampedPositiveNanos(keepAliveTime);
      this.queueSize = withMaxQueueSize(withCurrentQueueSize(0L, 0), builder.getMaximumQueueSize());
      this.mxBean = new MXBeanImpl();
      if (!DISABLE_MBEAN && builder.isRegisterMBean()) {
         String configuredName = builder.getMBeanName();
         String finalName = configuredName != null ? configuredName : "threadpool-" + JBossExecutors.unsafe.getAndAddInt(sequenceBase, sequenceOffset, 1);
         this.handle = AccessController.doPrivileged(new MBeanRegisterAction(finalName, this.mxBean), this.acc);
      } else {
         this.handle = null;
      }

   }

   public void execute(Runnable runnable) {
      Assert.checkNotNullParam("runnable", runnable);
      Runnable realRunnable = JBossExecutors.classLoaderPreservingTaskUnchecked(runnable);
      int result = this.tryExecute(realRunnable);
      boolean ok = false;
      if (result == 0) {
         if (currentSizeOf(this.threadStatus) == 0 && this.tryAllocateThread(0.0F) == 0 && !this.doStartThread((Runnable)null)) {
            this.deallocateThread();
         }

         if (UPDATE_STATISTICS) {
            this.submittedTaskCounter.increment();
         }

      } else {
         if (result == 3) {
            try {
               ok = this.doStartThread(realRunnable);
            } finally {
               if (!ok) {
                  this.deallocateThread();
               }

            }
         } else {
            if (UPDATE_STATISTICS) {
               this.rejectedTaskCounter.increment();
            }

            if (result == 2) {
               this.rejectShutdown(realRunnable);
            } else {
               assert result == 1;

               this.rejectQueueFull(realRunnable);
            }
         }

      }
   }

   public void shutdown() {
      this.shutdown(false);
   }

   public List<Runnable> shutdownNow() {
      this.shutdown(true);
      ArrayList<Runnable> list = new ArrayList();
      TaskNode head = this.head;

      while(true) {
         QNode headNext = head.getNext();
         if (!(headNext instanceof TaskNode)) {
            return list;
         }

         TaskNode taskNode = (TaskNode)headNext;
         if (this.compareAndSetHead(head, taskNode)) {
            if (!NO_QUEUE_LIMIT) {
               this.decreaseQueueSize();
            }

            head = taskNode;
            list.add(taskNode.task);
         }
      }
   }

   public boolean isShutdown() {
      return isShutdownRequested(this.threadStatus);
   }

   public boolean isTerminated() {
      return isShutdownComplete(this.threadStatus);
   }

   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      Assert.checkMinimumParameter("timeout", 0L, timeout);
      Assert.checkNotNullParam("unit", unit);
      if (timeout > 0L) {
         Thread thread = Thread.currentThread();
         if (this.runningThreads.contains(thread)) {
            throw Messages.msg.cannotAwaitWithin();
         }

         Waiter waiters = this.terminationWaiters;
         if (waiters == TERMINATE_COMPLETE_WAITER) {
            return true;
         }

         Waiter waiter = new Waiter(waiters);
         waiter.setThread(Thread.currentThread());

         while(!this.compareAndSetTerminationWaiters(waiters, waiter)) {
            waiters = this.terminationWaiters;
            if (waiters == TERMINATE_COMPLETE_WAITER) {
               return true;
            }

            waiter.setNext(waiters);
         }

         try {
            LockSupport.parkNanos(this, unit.toNanos(timeout));
         } finally {
            waiter.setThread((Thread)null);
         }
      }

      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         return this.isTerminated();
      }
   }

   public StandardThreadPoolMXBean getThreadPoolMXBean() {
      return this.mxBean;
   }

   public void shutdown(boolean interrupt) {
      long oldStatus;
      long newStatus;
      do {
         oldStatus = this.threadStatus;
         newStatus = withShutdownRequested(oldStatus);
         if (interrupt) {
            newStatus = withShutdownInterrupt(newStatus);
         }

         if (currentSizeOf(oldStatus) == 0) {
            newStatus = withShutdownComplete(newStatus);
         }

         if (newStatus == oldStatus) {
            return;
         }
      } while(!this.compareAndSetThreadStatus(oldStatus, newStatus));

      assert oldStatus != newStatus;

      if (isShutdownRequested(newStatus) != isShutdownRequested(oldStatus)) {
         assert !isShutdownRequested(oldStatus);

         TaskNode tail = this.tail;

         label80:
         while(true) {
            while(true) {
               QNode tailNext = tail.getNext();
               if (tailNext instanceof TaskNode) {
                  tail = (TaskNode)tailNext;
               } else {
                  if (!(tailNext instanceof PoolThreadNode) && tailNext != null) {
                     if (!(tailNext instanceof TerminateWaiterNode)) {
                        throw Assert.unreachableCode();
                     }
                     break label80;
                  }

                  PoolThreadNode node = (PoolThreadNode)tailNext;
                  if (tail.compareAndSetNext(node, TERMINATE_REQUESTED)) {
                     while(true) {
                        if (node == null) {
                           break label80;
                        }

                        node.compareAndSetTask(WAITING, EXIT);
                        node.unpark();
                        node = node.getNext();
                     }
                  }
               }
            }
         }
      }

      if (isShutdownInterrupt(newStatus) != isShutdownInterrupt(oldStatus)) {
         assert !isShutdownInterrupt(oldStatus);

         Iterator var9 = this.runningThreads.iterator();

         while(var9.hasNext()) {
            Thread thread = (Thread)var9.next();
            thread.interrupt();
         }
      }

      if (isShutdownComplete(newStatus) != isShutdownComplete(oldStatus)) {
         assert !isShutdownComplete(oldStatus);

         this.completeTermination();
      }

   }

   public boolean isTerminating() {
      long threadStatus = this.threadStatus;
      return isShutdownRequested(threadStatus) && !isShutdownComplete(threadStatus);
   }

   public boolean prestartCoreThread() {
      if (this.tryAllocateThread(1.0F) != 0) {
         return false;
      } else if (this.doStartThread((Runnable)null)) {
         return true;
      } else {
         this.deallocateThread();
         return false;
      }
   }

   public int prestartAllCoreThreads() {
      int cnt;
      for(cnt = 0; this.prestartCoreThread(); ++cnt) {
      }

      return cnt;
   }

   public float getGrowthResistance() {
      return this.growthResistance;
   }

   public void setGrowthResistance(float growthResistance) {
      Assert.checkMinimumParameter("growthResistance", 0.0F, growthResistance);
      Assert.checkMaximumParameter("growthResistance", 1.0F, growthResistance);
      this.growthResistance = growthResistance;
   }

   public int getCorePoolSize() {
      return coreSizeOf(this.threadStatus);
   }

   public void setCorePoolSize(int corePoolSize) {
      Assert.checkMinimumParameter("corePoolSize", 0, corePoolSize);
      Assert.checkMaximumParameter("corePoolSize", 1048575L, (long)corePoolSize);

      long oldVal;
      long newVal;
      do {
         oldVal = this.threadStatus;
         if (corePoolSize > maxSizeOf(oldVal)) {
            newVal = withCoreSize(withMaxSize(oldVal, corePoolSize), corePoolSize);
         } else {
            newVal = withCoreSize(oldVal, corePoolSize);
         }
      } while(!this.compareAndSetThreadStatus(oldVal, newVal));

      if (maxSizeOf(newVal) < maxSizeOf(oldVal) || coreSizeOf(newVal) < coreSizeOf(oldVal)) {
         Iterator var6 = this.runningThreads.iterator();

         while(var6.hasNext()) {
            Thread activeThread = (Thread)var6.next();
            LockSupport.unpark(activeThread);
         }
      }

   }

   public int getMaximumPoolSize() {
      return maxSizeOf(this.threadStatus);
   }

   public void setMaximumPoolSize(int maxPoolSize) {
      Assert.checkMinimumParameter("maxPoolSize", 0, maxPoolSize);
      Assert.checkMaximumParameter("maxPoolSize", 1048575L, (long)maxPoolSize);

      long oldVal;
      long newVal;
      do {
         oldVal = this.threadStatus;
         if (maxPoolSize < coreSizeOf(oldVal)) {
            newVal = withCoreSize(withMaxSize(oldVal, maxPoolSize), maxPoolSize);
         } else {
            newVal = withMaxSize(oldVal, maxPoolSize);
         }
      } while(!this.compareAndSetThreadStatus(oldVal, newVal));

      if (maxSizeOf(newVal) < maxSizeOf(oldVal) || coreSizeOf(newVal) < coreSizeOf(oldVal)) {
         Iterator var6 = this.runningThreads.iterator();

         while(var6.hasNext()) {
            Thread activeThread = (Thread)var6.next();
            LockSupport.unpark(activeThread);
         }
      }

   }

   public boolean allowsCoreThreadTimeOut() {
      return isAllowCoreTimeout(this.threadStatus);
   }

   public void allowCoreThreadTimeOut(boolean value) {
      long oldVal;
      long newVal;
      do {
         oldVal = this.threadStatus;
         newVal = withAllowCoreTimeout(oldVal, value);
         if (oldVal == newVal) {
            return;
         }
      } while(!this.compareAndSetThreadStatus(oldVal, newVal));

      if (value) {
         Iterator var6 = this.runningThreads.iterator();

         while(var6.hasNext()) {
            Thread activeThread = (Thread)var6.next();
            LockSupport.unpark(activeThread);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public long getKeepAliveTime(TimeUnit keepAliveUnits) {
      Assert.checkNotNullParam("keepAliveUnits", keepAliveUnits);
      return keepAliveUnits.convert(this.timeoutNanos, TimeUnit.NANOSECONDS);
   }

   public Duration getKeepAliveTime() {
      return Duration.of(this.timeoutNanos, ChronoUnit.NANOS);
   }

   /** @deprecated */
   @Deprecated
   public void setKeepAliveTime(long keepAliveTime, TimeUnit keepAliveUnits) {
      Assert.checkMinimumParameter("keepAliveTime", 1L, keepAliveTime);
      Assert.checkNotNullParam("keepAliveUnits", keepAliveUnits);
      this.timeoutNanos = Math.max(1L, keepAliveUnits.toNanos(keepAliveTime));
   }

   public void setKeepAliveTime(Duration keepAliveTime) {
      Assert.checkNotNullParam("keepAliveTime", keepAliveTime);
      this.timeoutNanos = TimeUtil.clampedPositiveNanos(keepAliveTime);
   }

   public int getMaximumQueueSize() {
      return maxQueueSizeOf(this.queueSize);
   }

   public void setMaximumQueueSize(int maxQueueSize) {
      Assert.checkMinimumParameter("maxQueueSize", 0, maxQueueSize);
      Assert.checkMaximumParameter("maxQueueSize", Integer.MAX_VALUE, maxQueueSize);
      if (!NO_QUEUE_LIMIT) {
         long oldVal;
         do {
            oldVal = this.queueSize;
         } while(!this.compareAndSetQueueSize(oldVal, withMaxQueueSize(oldVal, maxQueueSize)));

      }
   }

   public Executor getHandoffExecutor() {
      return this.handoffExecutor;
   }

   public void setHandoffExecutor(Executor handoffExecutor) {
      Assert.checkNotNullParam("handoffExecutor", handoffExecutor);
      this.handoffExecutor = handoffExecutor;
   }

   public Thread.UncaughtExceptionHandler getExceptionHandler() {
      return this.exceptionHandler;
   }

   public void setExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
      Assert.checkNotNullParam("exceptionHandler", exceptionHandler);
      this.exceptionHandler = exceptionHandler;
   }

   public void setTerminationTask(Runnable terminationTask) {
      this.terminationTask = terminationTask;
   }

   public int getQueueSize() {
      return NO_QUEUE_LIMIT ? -1 : currentQueueSizeOf(this.queueSize);
   }

   public int getLargestPoolSize() {
      return UPDATE_STATISTICS ? this.peakThreadCount : -1;
   }

   public int getActiveCount() {
      return UPDATE_ACTIVE_COUNT ? this.activeCount : -1;
   }

   public int getLargestQueueSize() {
      return UPDATE_STATISTICS && !NO_QUEUE_LIMIT ? this.peakQueueSize : -1;
   }

   public long getSubmittedTaskCount() {
      return UPDATE_STATISTICS ? this.submittedTaskCounter.longValue() : -1L;
   }

   public long getRejectedTaskCount() {
      return UPDATE_STATISTICS ? this.rejectedTaskCounter.longValue() : -1L;
   }

   public long getCompletedTaskCount() {
      return UPDATE_STATISTICS ? this.completedTaskCounter.longValue() : -1L;
   }

   public int getPoolSize() {
      return currentSizeOf(this.threadStatus);
   }

   public Thread[] getRunningThreads() {
      return (Thread[])this.runningThreads.toArray(NO_THREADS);
   }

   int tryAllocateThread(float growthResistance) {
      while(true) {
         long oldStat = this.threadStatus;
         if (isShutdownRequested(oldStat)) {
            return 2;
         }

         int oldSize = currentSizeOf(oldStat);
         if (oldSize >= maxSizeOf(oldStat)) {
            return 1;
         }

         if (oldSize >= coreSizeOf(oldStat) && oldSize > 0 && growthResistance != 0.0F && (growthResistance == 1.0F || ThreadLocalRandom.current().nextFloat() < growthResistance)) {
            return 1;
         }

         int newSize = oldSize + 1;
         if (this.compareAndSetThreadStatus(oldStat, withCurrentSize(oldStat, newSize))) {
            int oldVal;
            if (UPDATE_STATISTICS) {
               do {
                  oldVal = this.peakThreadCount;
               } while(oldVal < newSize && !this.compareAndSetPeakThreadCount(oldVal, newSize));
            }

            return 0;
         }

         if (UPDATE_STATISTICS) {
            this.spinMisses.increment();
         }
      }
   }

   void deallocateThread() {
      long oldStat;
      do {
         oldStat = this.threadStatus;
      } while(!this.tryDeallocateThread(oldStat));

   }

   boolean tryDeallocateThread(long oldStat) {
      long newStat = withCurrentSize(oldStat, currentSizeOf(oldStat) - 1);
      if (currentSizeOf(newStat) == 0 && isShutdownRequested(oldStat)) {
         newStat = withShutdownComplete(newStat);
      }

      if (!this.compareAndSetThreadStatus(oldStat, newStat)) {
         return false;
      } else {
         if (isShutdownComplete(newStat)) {
            this.completeTermination();
         }

         return true;
      }
   }

   boolean doStartThread(Runnable runnable) throws RejectedExecutionException {
      Thread thread;
      try {
         thread = this.threadFactory.newThread(new ThreadBody(runnable));
      } catch (Throwable var5) {
         if (runnable != null) {
            if (UPDATE_STATISTICS) {
               this.rejectedTaskCounter.increment();
            }

            this.rejectException(runnable, var5);
         }

         return false;
      }

      if (thread == null) {
         if (runnable != null) {
            if (UPDATE_STATISTICS) {
               this.rejectedTaskCounter.increment();
            }

            this.rejectNoThread(runnable);
         }

         return false;
      } else {
         try {
            thread.start();
            return true;
         } catch (Throwable var4) {
            if (runnable != null) {
               if (UPDATE_STATISTICS) {
                  this.rejectedTaskCounter.increment();
               }

               this.rejectException(runnable, var4);
            }

            return false;
         }
      }
   }

   private int tryExecute(Runnable runnable) {
      if (TAIL_LOCK) {
         this.lockTail();
      }

      TaskNode tail = this.tail;
      TaskNode node = null;

      while(true) {
         while(true) {
            QNode tailNext = tail.getNext();
            if (tailNext instanceof TaskNode) {
               while(true) {
                  if (UPDATE_STATISTICS) {
                     this.spinMisses.increment();
                  }

                  TaskNode tailNextTaskNode = (TaskNode)tailNext;
                  tail = tailNextTaskNode;
                  tailNext = tailNextTaskNode.getNext();
                  if (!(tailNext instanceof TaskNode)) {
                     if (UPDATE_TAIL) {
                        this.compareAndSetTail(tailNextTaskNode, tailNextTaskNode);
                     }
                     break;
                  }
               }
            }

            assert !(tailNext instanceof TaskNode);

            if (tailNext instanceof PoolThreadNode) {
               QNode tailNextNext = tailNext.getNext();
               if (tail.compareAndSetNext(tailNext, tailNextNext)) {
                  assert tail instanceof TaskNode && tail.task == null;

                  PoolThreadNode consumerNode = (PoolThreadNode)tailNext;
                  if (consumerNode.compareAndSetTask(WAITING, runnable)) {
                     if (TAIL_LOCK) {
                        this.unlockTail();
                     }

                     consumerNode.unpark();
                     return 0;
                  }
               }

               if (UPDATE_STATISTICS) {
                  this.spinMisses.increment();
               }

               tail = this.tail;
            } else {
               if (tailNext != null) {
                  if (TAIL_LOCK) {
                     this.unlockTail();
                  }

                  assert tailNext instanceof TerminateWaiterNode;

                  return 2;
               }

               int tr = this.tryAllocateThread(this.growthResistance);
               if (tr == 0) {
                  if (TAIL_LOCK) {
                     this.unlockTail();
                  }

                  return 3;
               }

               if (tr == 2) {
                  if (TAIL_LOCK) {
                     this.unlockTail();
                  }

                  return 2;
               }

               assert tr == 1;

               if (!NO_QUEUE_LIMIT && !this.increaseQueueSize()) {
                  tr = this.tryAllocateThread(0.0F);
                  if (TAIL_LOCK) {
                     this.unlockTail();
                  }

                  if (tr == 0) {
                     return 3;
                  }

                  if (tr == 2) {
                     return 2;
                  }

                  assert tr == 1;

                  return 1;
               }

               if (node == null) {
                  node = new TaskNode(runnable);
               }

               if (tail.compareAndSetNext((QNode)null, node)) {
                  this.compareAndSetTail(tail, node);
                  if (TAIL_LOCK) {
                     this.unlockTail();
                  }

                  return 0;
               }

               if (!NO_QUEUE_LIMIT) {
                  this.decreaseQueueSize();
               }

               if (UPDATE_STATISTICS) {
                  this.spinMisses.increment();
               }

               tail = this.tail;
            }
         }
      }
   }

   void completeTermination() {
      Thread.interrupted();
      Runnable terminationTask = this.terminationTask;
      this.terminationTask = null;
      this.safeRun(terminationTask);

      for(Waiter waiters = this.getAndSetTerminationWaiters(TERMINATE_COMPLETE_WAITER); waiters != null; waiters = waiters.getNext()) {
         LockSupport.unpark(waiters.getThread());
      }

      this.tail.setNext(TERMINATE_COMPLETE);
      if (!DISABLE_MBEAN) {
         Object handle = this.handle;
         if (handle != null) {
            AccessController.doPrivileged(new MBeanUnregisterAction(handle), this.acc);
         }
      }

   }

   void incrementActiveCount() {
      JBossExecutors.unsafe.getAndAddInt(this, activeCountOffset, 1);
   }

   void decrementActiveCount() {
      JBossExecutors.unsafe.getAndAddInt(this, activeCountOffset, -1);
   }

   boolean compareAndSetPeakThreadCount(int expect, int update) {
      return JBossExecutors.unsafe.compareAndSwapInt(this, peakThreadCountOffset, expect, update);
   }

   boolean compareAndSetPeakQueueSize(int expect, int update) {
      return JBossExecutors.unsafe.compareAndSwapInt(this, peakQueueSizeOffset, expect, update);
   }

   boolean compareAndSetQueueSize(long expect, long update) {
      return JBossExecutors.unsafe.compareAndSwapLong(this, queueSizeOffset, expect, update);
   }

   boolean compareAndSetTerminationWaiters(Waiter expect, Waiter update) {
      return JBossExecutors.unsafe.compareAndSwapObject(this, terminationWaitersOffset, expect, update);
   }

   Waiter getAndSetTerminationWaiters(Waiter update) {
      return (Waiter)JBossExecutors.unsafe.getAndSetObject(this, terminationWaitersOffset, update);
   }

   boolean increaseQueueSize() {
      long oldVal = this.queueSize;
      int oldSize = currentQueueSizeOf(oldVal);
      if (oldSize >= maxQueueSizeOf(oldVal)) {
         return false;
      } else {
         int newSize;
         for(newSize = oldSize + 1; !this.compareAndSetQueueSize(oldVal, withCurrentQueueSize(oldVal, newSize)); newSize = oldSize + 1) {
            if (UPDATE_STATISTICS) {
               this.spinMisses.increment();
            }

            oldVal = this.queueSize;
            oldSize = currentQueueSizeOf(oldVal);
            if (oldSize >= maxQueueSizeOf(oldVal)) {
               return false;
            }
         }

         if (UPDATE_STATISTICS) {
            do {
               oldSize = this.peakQueueSize;
            } while(newSize > oldSize && !this.compareAndSetPeakQueueSize(oldSize, newSize));
         }

         return true;
      }
   }

   void decreaseQueueSize() {
      long oldVal = this.queueSize;

      assert currentQueueSizeOf(oldVal) > 0;

      do {
         if (this.compareAndSetQueueSize(oldVal, withCurrentQueueSize(oldVal, currentQueueSizeOf(oldVal) - 1))) {
            return;
         }

         if (UPDATE_STATISTICS) {
            this.spinMisses.increment();
         }

         oldVal = this.queueSize;
      } while($assertionsDisabled || currentQueueSizeOf(oldVal) > 0);

      throw new AssertionError();
   }

   static int currentQueueSizeOf(long queueSize) {
      return (int)(queueSize & 2147483647L);
   }

   static long withCurrentQueueSize(long queueSize, int current) {
      assert current >= 0;

      return queueSize & -4294967296L | (long)current;
   }

   static int maxQueueSizeOf(long queueSize) {
      return (int)(queueSize >>> 32 & 2147483647L);
   }

   static long withMaxQueueSize(long queueSize, int max) {
      assert max >= 0;

      return queueSize & 4294967295L | (long)max << 32;
   }

   static int coreSizeOf(long status) {
      return (int)(status >>> 20 & 1048575L);
   }

   static int maxSizeOf(long status) {
      return (int)(status >>> 40 & 1048575L);
   }

   static int currentSizeOf(long status) {
      return (int)(status >>> 0 & 1048575L);
   }

   static long withCoreSize(long status, int newCoreSize) {
      assert 0 <= newCoreSize && (long)newCoreSize <= 1048575L;

      return status & -1099510579201L | (long)newCoreSize << 20;
   }

   static long withCurrentSize(long status, int newCurrentSize) {
      assert 0 <= newCurrentSize && (long)newCurrentSize <= 1048575L;

      return status & -1048576L | (long)newCurrentSize << 0;
   }

   static long withMaxSize(long status, int newMaxSize) {
      assert 0 <= newMaxSize && (long)newMaxSize <= 1048575L;

      return status & -1152920405095219201L | (long)newMaxSize << 40;
   }

   static long withShutdownRequested(long status) {
      return status | 2305843009213693952L;
   }

   static long withShutdownComplete(long status) {
      return status | Long.MIN_VALUE;
   }

   static long withShutdownInterrupt(long status) {
      return status | 4611686018427387904L;
   }

   static long withAllowCoreTimeout(long status, boolean allowed) {
      return allowed ? status | 1152921504606846976L : status & -1152921504606846977L;
   }

   static boolean isShutdownRequested(long status) {
      return (status & 2305843009213693952L) != 0L;
   }

   static boolean isShutdownComplete(long status) {
      return (status & Long.MIN_VALUE) != 0L;
   }

   static boolean isShutdownInterrupt(long threadStatus) {
      return (threadStatus & 4611686018427387904L) != 0L;
   }

   static boolean isAllowCoreTimeout(long oldVal) {
      return (oldVal & 1152921504606846976L) != 0L;
   }

   void safeRun(Runnable task) {
      if (task != null) {
         Thread currentThread = Thread.currentThread();
         JBossExecutors.clearContextClassLoader(currentThread);

         try {
            task.run();
         } catch (Throwable var10) {
            Throwable t = var10;

            try {
               this.exceptionHandler.uncaughtException(Thread.currentThread(), t);
            } catch (Throwable var9) {
            }
         } finally {
            JBossExecutors.clearContextClassLoader(currentThread);
            Thread.interrupted();
         }

      }
   }

   void rejectException(Runnable task, Throwable cause) {
      try {
         this.handoffExecutor.execute(task);
      } catch (Throwable var4) {
         var4.addSuppressed(cause);
         throw var4;
      }
   }

   void rejectNoThread(Runnable task) {
      try {
         this.handoffExecutor.execute(task);
      } catch (Throwable var3) {
         var3.addSuppressed(new RejectedExecutionException("No threads available"));
         throw var3;
      }
   }

   void rejectQueueFull(Runnable task) {
      try {
         this.handoffExecutor.execute(task);
      } catch (Throwable var3) {
         var3.addSuppressed(new RejectedExecutionException("Queue is full"));
         throw var3;
      }
   }

   void rejectShutdown(Runnable task) {
      try {
         this.handoffExecutor.execute(task);
      } catch (Throwable var3) {
         var3.addSuppressed(new RejectedExecutionException("Executor is being shut down"));
         throw var3;
      }
   }

   static {
      Version.getVersionString();
      EnhancedQueueExecutor.MBeanUnregisterAction.forceInit();
      DISABLE_HINT = readBooleanPropertyPrefixed("disable", false);
      UPDATE_TAIL = readBooleanPropertyPrefixed("update-tail", false);
      UPDATE_STATISTICS = readBooleanPropertyPrefixed("statistics", false);
      UPDATE_ACTIVE_COUNT = UPDATE_STATISTICS || readBooleanPropertyPrefixed("statistics.active-count", false);
      NO_QUEUE_LIMIT = readBooleanPropertyPrefixed("unlimited-queue", false);
      REGISTER_MBEAN = readBooleanPropertyPrefixed("register-mbean", true);
      DISABLE_MBEAN = readBooleanPropertyPrefixed("disable-mbean", readProperty("org.graalvm.nativeimage.imagecode", (String)null) != null);
      PARK_SPINS = readIntPropertyPrefixed("park-spins", ProcessorInfo.availableProcessors() == 1 ? 0 : 128);
      YIELD_FACTOR = Math.max(Math.min(readIntPropertyPrefixed("park-yields", 1), PARK_SPINS), 0);
      DEFAULT_HANDLER = JBossExecutors.rejectingExecutor();

      try {
         terminationWaitersOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutor.class.getDeclaredField("terminationWaiters"));
         queueSizeOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutor.class.getDeclaredField("queueSize"));
         peakThreadCountOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutor.class.getDeclaredField("peakThreadCount"));
         activeCountOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutor.class.getDeclaredField("activeCount"));
         peakQueueSizeOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutor.class.getDeclaredField("peakQueueSize"));
         sequenceBase = JBossExecutors.unsafe.staticFieldBase(EnhancedQueueExecutor.class.getDeclaredField("sequence"));
         sequenceOffset = JBossExecutors.unsafe.staticFieldOffset(EnhancedQueueExecutor.class.getDeclaredField("sequence"));
      } catch (NoSuchFieldException var1) {
         throw new NoSuchFieldError(var1.getMessage());
      }

      TERMINATE_REQUESTED = new TerminateWaiterNode();
      TERMINATE_COMPLETE = new TerminateWaiterNode();
      TERMINATE_COMPLETE_WAITER = new Waiter((Waiter)null);
      WAITING = new NullRunnable();
      GAVE_UP = new NullRunnable();
      ACCEPTED = new NullRunnable();
      EXIT = new NullRunnable();
      sequence = 1;
   }

   final class MXBeanImpl implements StandardThreadPoolMXBean {
      public float getGrowthResistance() {
         return EnhancedQueueExecutor.this.getGrowthResistance();
      }

      public void setGrowthResistance(float value) {
         EnhancedQueueExecutor.this.setGrowthResistance(value);
      }

      public boolean isGrowthResistanceSupported() {
         return true;
      }

      public int getCorePoolSize() {
         return EnhancedQueueExecutor.this.getCorePoolSize();
      }

      public void setCorePoolSize(int corePoolSize) {
         EnhancedQueueExecutor.this.setCorePoolSize(corePoolSize);
      }

      public boolean isCorePoolSizeSupported() {
         return true;
      }

      public boolean prestartCoreThread() {
         return EnhancedQueueExecutor.this.prestartCoreThread();
      }

      public int prestartAllCoreThreads() {
         return EnhancedQueueExecutor.this.prestartAllCoreThreads();
      }

      public boolean isCoreThreadPrestartSupported() {
         return true;
      }

      public int getMaximumPoolSize() {
         return EnhancedQueueExecutor.this.getMaximumPoolSize();
      }

      public void setMaximumPoolSize(int maxPoolSize) {
         EnhancedQueueExecutor.this.setMaximumPoolSize(maxPoolSize);
      }

      public int getPoolSize() {
         return EnhancedQueueExecutor.this.getPoolSize();
      }

      public int getLargestPoolSize() {
         return EnhancedQueueExecutor.this.getLargestPoolSize();
      }

      public int getActiveCount() {
         return EnhancedQueueExecutor.this.getActiveCount();
      }

      public boolean isAllowCoreThreadTimeOut() {
         return EnhancedQueueExecutor.this.allowsCoreThreadTimeOut();
      }

      public void setAllowCoreThreadTimeOut(boolean value) {
         EnhancedQueueExecutor.this.allowCoreThreadTimeOut(value);
      }

      public long getKeepAliveTimeSeconds() {
         return EnhancedQueueExecutor.this.getKeepAliveTime().getSeconds();
      }

      public void setKeepAliveTimeSeconds(long seconds) {
         EnhancedQueueExecutor.this.setKeepAliveTime(Duration.of(seconds, ChronoUnit.SECONDS));
      }

      public int getMaximumQueueSize() {
         return EnhancedQueueExecutor.this.getMaximumQueueSize();
      }

      public void setMaximumQueueSize(int size) {
         EnhancedQueueExecutor.this.setMaximumQueueSize(size);
      }

      public int getQueueSize() {
         return EnhancedQueueExecutor.this.getQueueSize();
      }

      public int getLargestQueueSize() {
         return EnhancedQueueExecutor.this.getLargestQueueSize();
      }

      public boolean isQueueBounded() {
         return !EnhancedQueueExecutor.NO_QUEUE_LIMIT;
      }

      public boolean isQueueSizeModifiable() {
         return !EnhancedQueueExecutor.NO_QUEUE_LIMIT;
      }

      public boolean isShutdown() {
         return EnhancedQueueExecutor.this.isShutdown();
      }

      public boolean isTerminating() {
         return EnhancedQueueExecutor.this.isTerminating();
      }

      public boolean isTerminated() {
         return EnhancedQueueExecutor.this.isTerminated();
      }

      public long getSubmittedTaskCount() {
         return EnhancedQueueExecutor.this.getSubmittedTaskCount();
      }

      public long getRejectedTaskCount() {
         return EnhancedQueueExecutor.this.getRejectedTaskCount();
      }

      public long getCompletedTaskCount() {
         return EnhancedQueueExecutor.this.getCompletedTaskCount();
      }

      public long getSpinMissCount() {
         return EnhancedQueueExecutor.this.spinMisses.longValue();
      }
   }

   static final class TaskNode extends QNode {
      volatile Runnable task;

      TaskNode(Runnable task) {
         this.task = task;
      }

      Runnable getAndClearTask() {
         Runnable var1;
         try {
            var1 = this.task;
         } finally {
            this.task = null;
         }

         return var1;
      }
   }

   static final class TerminateWaiterNode extends QNode {
   }

   static final class PoolThreadNode extends PoolThreadNodeBase {
      private static final int STATE_NORMAL = 0;
      private static final int STATE_PARKED = 1;
      private static final int STATE_UNPARKED = 2;
      private static final long taskOffset;
      private static final long parkedOffset;
      private final Thread thread;
      private volatile Runnable task;
      private volatile int parked;

      PoolThreadNode(Thread thread) {
         this.thread = thread;
         this.task = EnhancedQueueExecutor.WAITING;
      }

      boolean compareAndSetTask(Runnable expect, Runnable update) {
         return this.task == expect && JBossExecutors.unsafe.compareAndSwapObject(this, taskOffset, expect, update);
      }

      Runnable getTask() {
         return this.task;
      }

      PoolThreadNode getNext() {
         return (PoolThreadNode)super.getNext();
      }

      void park(EnhancedQueueExecutor enhancedQueueExecutor) {
         int spins = EnhancedQueueExecutor.PARK_SPINS;
         if (this.parked != 2 || !JBossExecutors.unsafe.compareAndSwapInt(this, parkedOffset, 2, 0)) {
            do {
               if (spins <= 0) {
                  if (this.parked == 0 && JBossExecutors.unsafe.compareAndSwapInt(this, parkedOffset, 0, 1)) {
                     try {
                        LockSupport.park(enhancedQueueExecutor);
                     } finally {
                        this.parked = 0;
                     }
                  }

                  return;
               }

               if (spins < EnhancedQueueExecutor.YIELD_FACTOR) {
                  Thread.yield();
               } else {
                  JDKSpecific.onSpinWait();
               }

               --spins;
            } while(this.parked != 2 || !JBossExecutors.unsafe.compareAndSwapInt(this, parkedOffset, 2, 0));

         }
      }

      void park(EnhancedQueueExecutor enhancedQueueExecutor, long nanos) {
         int spins = EnhancedQueueExecutor.PARK_SPINS;
         long remaining;
         if (spins > 0) {
            long start = System.nanoTime();
            if (this.parked == 2 && JBossExecutors.unsafe.compareAndSwapInt(this, parkedOffset, 2, 0)) {
               return;
            }

            while(spins > 0) {
               if (spins < EnhancedQueueExecutor.YIELD_FACTOR) {
                  Thread.yield();
               } else {
                  JDKSpecific.onSpinWait();
               }

               if (this.parked == 2 && JBossExecutors.unsafe.compareAndSwapInt(this, parkedOffset, 2, 0)) {
                  return;
               }

               --spins;
            }

            remaining = nanos - (System.nanoTime() - start);
            if (remaining < 0L) {
               return;
            }
         } else {
            remaining = nanos;
         }

         if (this.parked == 0 && JBossExecutors.unsafe.compareAndSwapInt(this, parkedOffset, 0, 1)) {
            try {
               LockSupport.parkNanos(enhancedQueueExecutor, remaining);
            } finally {
               this.parked = 0;
            }
         }

      }

      void unpark() {
         if (this.parked != 0 || !JBossExecutors.unsafe.compareAndSwapInt(this, parkedOffset, 0, 2)) {
            LockSupport.unpark(this.thread);
         }
      }

      static {
         try {
            taskOffset = JBossExecutors.unsafe.objectFieldOffset(PoolThreadNode.class.getDeclaredField("task"));
            parkedOffset = JBossExecutors.unsafe.objectFieldOffset(PoolThreadNode.class.getDeclaredField("parked"));
         } catch (NoSuchFieldException var1) {
            throw new NoSuchFieldError(var1.getMessage());
         }
      }
   }

   abstract static class PoolThreadNodeBase extends QNode {
      int p00;
      int p01;
      int p02;
      int p03;
      int p04;
      int p05;
      int p06;
      int p07;
      int p08;
      int p09;
      int p0A;
      int p0B;
      int p0C;
      int p0D;
      int p0E;
      int p0F;
   }

   abstract static class QNode {
      private static final long nextOffset;
      private volatile QNode next;

      boolean compareAndSetNext(QNode expect, QNode update) {
         return JBossExecutors.unsafe.compareAndSwapObject(this, nextOffset, expect, update);
      }

      QNode getNext() {
         return this.next;
      }

      void setNext(QNode node) {
         this.next = node;
      }

      void setNextRelaxed(QNode node) {
         JBossExecutors.unsafe.putObject(this, nextOffset, node);
      }

      static {
         try {
            nextOffset = JBossExecutors.unsafe.objectFieldOffset(QNode.class.getDeclaredField("next"));
         } catch (NoSuchFieldException var1) {
            throw new NoSuchFieldError(var1.getMessage());
         }
      }
   }

   final class ThreadBody implements Runnable {
      private Runnable initialTask;

      ThreadBody(Runnable initialTask) {
         this.initialTask = initialTask;
      }

      public void run() {
         Thread currentThread = Thread.currentThread();
         LongAdder spinMisses = EnhancedQueueExecutor.this.spinMisses;
         EnhancedQueueExecutor.this.runningThreads.add(currentThread);
         this.doRunTask(this.getAndClearInitialTask());
         PoolThreadNode nextPoolThreadNode = new PoolThreadNode(currentThread);

         while(true) {
            label64:
            while(true) {
               QNode node = this.getOrAddNode(nextPoolThreadNode);
               if (!(node instanceof TaskNode)) {
                  if (node != nextPoolThreadNode) {
                     assert node instanceof TerminateWaiterNode;

                     EnhancedQueueExecutor.this.runningThreads.remove(currentThread);
                     EnhancedQueueExecutor.this.deallocateThread();
                     return;
                  }

                  PoolThreadNode newNode = nextPoolThreadNode;
                  nextPoolThreadNode = new PoolThreadNode(currentThread);
                  long start = System.nanoTime();
                  long elapsed = 0L;

                  while(true) {
                     while(true) {
                        Runnable task = newNode.getTask();

                        assert task != EnhancedQueueExecutor.ACCEPTED && task != EnhancedQueueExecutor.GAVE_UP;

                        if (task != EnhancedQueueExecutor.WAITING && task != EnhancedQueueExecutor.EXIT) {
                           if (newNode.compareAndSetTask(task, EnhancedQueueExecutor.ACCEPTED)) {
                              this.doRunTask(task);
                              continue label64;
                           }

                           if (EnhancedQueueExecutor.UPDATE_STATISTICS) {
                              spinMisses.increment();
                           }
                        } else {
                           long timeoutNanos = EnhancedQueueExecutor.this.timeoutNanos;
                           long oldVal = EnhancedQueueExecutor.this.threadStatus;
                           if (elapsed < timeoutNanos && task != EnhancedQueueExecutor.EXIT && EnhancedQueueExecutor.currentSizeOf(oldVal) <= EnhancedQueueExecutor.maxSizeOf(oldVal)) {
                              assert task == EnhancedQueueExecutor.WAITING;

                              newNode.park(EnhancedQueueExecutor.this, timeoutNanos - elapsed);
                              Thread.interrupted();
                              elapsed = System.nanoTime() - start;
                           } else if (task != EnhancedQueueExecutor.EXIT && !EnhancedQueueExecutor.isShutdownRequested(oldVal) && !EnhancedQueueExecutor.isAllowCoreTimeout(oldVal) && EnhancedQueueExecutor.currentSizeOf(oldVal) <= EnhancedQueueExecutor.coreSizeOf(oldVal)) {
                              if (elapsed >= timeoutNanos) {
                                 newNode.park(EnhancedQueueExecutor.this);
                              } else {
                                 newNode.park(EnhancedQueueExecutor.this, timeoutNanos - elapsed);
                              }

                              Thread.interrupted();
                              elapsed = System.nanoTime() - start;
                           } else if (newNode.compareAndSetTask(task, EnhancedQueueExecutor.GAVE_UP)) {
                              for(; !EnhancedQueueExecutor.this.tryDeallocateThread(oldVal); oldVal = EnhancedQueueExecutor.this.threadStatus) {
                                 if (EnhancedQueueExecutor.UPDATE_STATISTICS) {
                                    spinMisses.increment();
                                 }
                              }

                              EnhancedQueueExecutor.this.runningThreads.remove(currentThread);
                              return;
                           }
                        }
                     }
                  }
               } else {
                  this.doRunTask(((TaskNode)node).getAndClearTask());
               }
            }
         }
      }

      private QNode getOrAddNode(PoolThreadNode nextPoolThreadNode) {
         while(true) {
            TaskNode head = EnhancedQueueExecutor.this.head;
            QNode headNext = head.getNext();
            if (headNext instanceof TaskNode) {
               TaskNode taskNode = (TaskNode)headNext;
               if (EnhancedQueueExecutor.this.compareAndSetHead(head, taskNode)) {
                  if (!EnhancedQueueExecutor.NO_QUEUE_LIMIT) {
                     EnhancedQueueExecutor.this.decreaseQueueSize();
                  }

                  return taskNode;
               }
            } else {
               if (!(headNext instanceof PoolThreadNode) && headNext != null) {
                  assert headNext instanceof TerminateWaiterNode;

                  return headNext;
               }

               nextPoolThreadNode.setNextRelaxed(headNext);
               if (head.compareAndSetNext(headNext, nextPoolThreadNode)) {
                  return nextPoolThreadNode;
               }
            }

            if (EnhancedQueueExecutor.UPDATE_STATISTICS) {
               EnhancedQueueExecutor.this.spinMisses.increment();
            }

            JDKSpecific.onSpinWait();
         }
      }

      private Runnable getAndClearInitialTask() {
         Runnable var1;
         try {
            var1 = this.initialTask;
         } finally {
            this.initialTask = null;
         }

         return var1;
      }

      void doRunTask(Runnable task) {
         if (task != null) {
            if (EnhancedQueueExecutor.isShutdownInterrupt(EnhancedQueueExecutor.this.threadStatus)) {
               Thread.currentThread().interrupt();
            } else {
               Thread.interrupted();
            }

            if (EnhancedQueueExecutor.UPDATE_ACTIVE_COUNT) {
               EnhancedQueueExecutor.this.incrementActiveCount();
            }

            EnhancedQueueExecutor.this.safeRun(task);
            if (EnhancedQueueExecutor.UPDATE_ACTIVE_COUNT) {
               EnhancedQueueExecutor.this.decrementActiveCount();
               if (EnhancedQueueExecutor.UPDATE_STATISTICS) {
                  EnhancedQueueExecutor.this.completedTaskCounter.increment();
               }
            }
         }

      }
   }

   static class MBeanUnregisterAction implements PrivilegedAction<Void> {
      private final Object handle;

      static void forceInit() {
      }

      MBeanUnregisterAction(Object handle) {
         this.handle = handle;
      }

      public Void run() {
         try {
            ManagementFactory.getPlatformMBeanServer().unregisterMBean(((ObjectInstance)this.handle).getObjectName());
         } catch (Throwable var2) {
         }

         return null;
      }
   }

   public static final class Builder {
      private ThreadFactory threadFactory = Executors.defaultThreadFactory();
      private Runnable terminationTask = NullRunnable.getInstance();
      private Executor handoffExecutor;
      private Thread.UncaughtExceptionHandler exceptionHandler;
      private int coreSize;
      private int maxSize;
      private Duration keepAliveTime;
      private float growthResistance;
      private boolean allowCoreTimeOut;
      private int maxQueueSize;
      private boolean registerMBean;
      private String mBeanName;

      public Builder() {
         this.handoffExecutor = EnhancedQueueExecutor.DEFAULT_HANDLER;
         this.exceptionHandler = JBossExecutors.loggingExceptionHandler();
         this.coreSize = 16;
         this.maxSize = 64;
         this.keepAliveTime = Duration.ofSeconds(30L);
         this.maxQueueSize = Integer.MAX_VALUE;
         this.registerMBean = EnhancedQueueExecutor.REGISTER_MBEAN;
      }

      public ThreadFactory getThreadFactory() {
         return this.threadFactory;
      }

      public Builder setThreadFactory(ThreadFactory threadFactory) {
         Assert.checkNotNullParam("threadFactory", threadFactory);
         this.threadFactory = threadFactory;
         return this;
      }

      public Runnable getTerminationTask() {
         return this.terminationTask;
      }

      public Builder setTerminationTask(Runnable terminationTask) {
         Assert.checkNotNullParam("terminationTask", terminationTask);
         this.terminationTask = terminationTask;
         return this;
      }

      public int getCorePoolSize() {
         return this.coreSize;
      }

      public Builder setCorePoolSize(int coreSize) {
         Assert.checkMinimumParameter("coreSize", 0, coreSize);
         Assert.checkMaximumParameter("coreSize", 1048575L, (long)coreSize);
         this.coreSize = coreSize;
         return this;
      }

      public int getMaximumPoolSize() {
         return this.maxSize;
      }

      public Builder setMaximumPoolSize(int maxSize) {
         Assert.checkMinimumParameter("maxSize", 0, maxSize);
         Assert.checkMaximumParameter("maxSize", 1048575L, (long)maxSize);
         this.maxSize = maxSize;
         return this;
      }

      public Duration getKeepAliveTime() {
         return this.keepAliveTime;
      }

      /** @deprecated */
      @Deprecated
      public long getKeepAliveTime(TimeUnit keepAliveUnits) {
         Assert.checkNotNullParam("keepAliveUnits", keepAliveUnits);
         long secondsPart = keepAliveUnits.convert(this.keepAliveTime.getSeconds(), TimeUnit.SECONDS);
         long nanoPart = keepAliveUnits.convert((long)this.keepAliveTime.getNano(), TimeUnit.NANOSECONDS);
         long sum = secondsPart + nanoPart;
         return sum < 0L ? Long.MAX_VALUE : sum;
      }

      public Builder setKeepAliveTime(Duration keepAliveTime) {
         Assert.checkNotNullParam("keepAliveTime", keepAliveTime);
         this.keepAliveTime = keepAliveTime;
         return this;
      }

      /** @deprecated */
      @Deprecated
      public Builder setKeepAliveTime(long keepAliveTime, TimeUnit keepAliveUnits) {
         Assert.checkMinimumParameter("keepAliveTime", 1L, keepAliveTime);
         Assert.checkNotNullParam("keepAliveUnits", keepAliveUnits);
         this.keepAliveTime = Duration.of(keepAliveTime, JDKSpecific.timeToTemporal(keepAliveUnits));
         return this;
      }

      public float getGrowthResistance() {
         return this.growthResistance;
      }

      public Builder setGrowthResistance(float growthResistance) {
         Assert.checkMinimumParameter("growthResistance", 0.0F, growthResistance);
         Assert.checkMaximumParameter("growthResistance", 1.0F, growthResistance);
         this.growthResistance = growthResistance;
         return this;
      }

      public boolean allowsCoreThreadTimeOut() {
         return this.allowCoreTimeOut;
      }

      public Builder allowCoreThreadTimeOut(boolean allowCoreTimeOut) {
         this.allowCoreTimeOut = allowCoreTimeOut;
         return this;
      }

      public int getMaximumQueueSize() {
         return this.maxQueueSize;
      }

      public Builder setMaximumQueueSize(int maxQueueSize) {
         Assert.checkMinimumParameter("maxQueueSize", 0, maxQueueSize);
         Assert.checkMaximumParameter("maxQueueSize", Integer.MAX_VALUE, maxQueueSize);
         this.maxQueueSize = maxQueueSize;
         return this;
      }

      public Executor getHandoffExecutor() {
         return this.handoffExecutor;
      }

      public Builder setHandoffExecutor(Executor handoffExecutor) {
         Assert.checkNotNullParam("handoffExecutor", handoffExecutor);
         this.handoffExecutor = handoffExecutor;
         return this;
      }

      public Thread.UncaughtExceptionHandler getExceptionHandler() {
         return this.exceptionHandler;
      }

      public Builder setExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
         this.exceptionHandler = exceptionHandler;
         return this;
      }

      public EnhancedQueueExecutor build() {
         return new EnhancedQueueExecutor(this);
      }

      public boolean isRegisterMBean() {
         return this.registerMBean;
      }

      public Builder setRegisterMBean(boolean registerMBean) {
         this.registerMBean = registerMBean;
         return this;
      }

      public String getMBeanName() {
         return this.mBeanName;
      }

      public Builder setMBeanName(String mBeanName) {
         this.mBeanName = mBeanName;
         return this;
      }
   }

   static final class MBeanRegisterAction implements PrivilegedAction<ObjectInstance> {
      private final String finalName;
      private final MXBeanImpl mxBean;

      MBeanRegisterAction(String finalName, MXBeanImpl mxBean) {
         this.finalName = finalName;
         this.mxBean = mxBean;
      }

      public ObjectInstance run() {
         try {
            Hashtable<String, String> table = new Hashtable();
            table.put("name", ObjectName.quote(this.finalName));
            table.put("type", "thread-pool");
            return ManagementFactory.getPlatformMBeanServer().registerMBean(this.mxBean, new ObjectName("jboss.threads", table));
         } catch (Throwable var2) {
            return null;
         }
      }
   }
}

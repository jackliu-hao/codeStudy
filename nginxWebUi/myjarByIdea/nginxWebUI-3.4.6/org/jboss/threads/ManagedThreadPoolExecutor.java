package org.jboss.threads;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.jboss.threads.management.ManageableThreadPoolExecutorService;
import org.jboss.threads.management.StandardThreadPoolMXBean;
import org.wildfly.common.Assert;

public final class ManagedThreadPoolExecutor extends ThreadPoolExecutor implements ManageableThreadPoolExecutorService {
   private final Runnable terminationTask;
   private final StandardThreadPoolMXBean mxBean = new MXBeanImpl();
   private volatile Executor handoffExecutor = JBossExecutors.rejectingExecutor();
   private static final RejectedExecutionHandler HANDLER = new RejectedExecutionHandler() {
      public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
         ((ManagedThreadPoolExecutor)executor).reject(r);
      }
   };

   public ManagedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, Runnable terminationTask) {
      super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, HANDLER);
      this.terminationTask = terminationTask;
   }

   public ManagedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, Runnable terminationTask) {
      super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, HANDLER);
      this.terminationTask = terminationTask;
   }

   public ManagedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, Executor handoffExecutor, Runnable terminationTask) {
      super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, HANDLER);
      this.terminationTask = terminationTask;
      this.handoffExecutor = handoffExecutor;
   }

   public ManagedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, Executor handoffExecutor, Runnable terminationTask) {
      super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, HANDLER);
      this.terminationTask = terminationTask;
      this.handoffExecutor = handoffExecutor;
   }

   public StandardThreadPoolMXBean getThreadPoolMXBean() {
      return this.mxBean;
   }

   public Executor getHandoffExecutor() {
      return this.handoffExecutor;
   }

   public void setHandoffExecutor(Executor handoffExecutor) {
      Assert.checkNotNullParam("handoffExecutor", handoffExecutor);
      this.handoffExecutor = handoffExecutor;
      super.setRejectedExecutionHandler(HANDLER);
   }

   void reject(Runnable r) {
      this.handoffExecutor.execute(r);
   }

   protected void terminated() {
      this.terminationTask.run();
   }

   class MXBeanImpl implements StandardThreadPoolMXBean {
      public float getGrowthResistance() {
         return 1.0F;
      }

      public void setGrowthResistance(float value) {
      }

      public boolean isGrowthResistanceSupported() {
         return false;
      }

      public int getCorePoolSize() {
         return ManagedThreadPoolExecutor.this.getCorePoolSize();
      }

      public void setCorePoolSize(int corePoolSize) {
         ManagedThreadPoolExecutor.this.setCorePoolSize(corePoolSize);
      }

      public boolean isCorePoolSizeSupported() {
         return true;
      }

      public boolean prestartCoreThread() {
         return ManagedThreadPoolExecutor.this.prestartCoreThread();
      }

      public int prestartAllCoreThreads() {
         return ManagedThreadPoolExecutor.this.prestartAllCoreThreads();
      }

      public boolean isCoreThreadPrestartSupported() {
         return true;
      }

      public int getMaximumPoolSize() {
         return ManagedThreadPoolExecutor.this.getMaximumPoolSize();
      }

      public void setMaximumPoolSize(int maxPoolSize) {
         ManagedThreadPoolExecutor.this.setMaximumPoolSize(maxPoolSize);
      }

      public int getPoolSize() {
         return ManagedThreadPoolExecutor.this.getPoolSize();
      }

      public int getLargestPoolSize() {
         return ManagedThreadPoolExecutor.this.getLargestPoolSize();
      }

      public int getActiveCount() {
         return ManagedThreadPoolExecutor.this.getActiveCount();
      }

      public boolean isAllowCoreThreadTimeOut() {
         return ManagedThreadPoolExecutor.this.allowsCoreThreadTimeOut();
      }

      public void setAllowCoreThreadTimeOut(boolean value) {
         ManagedThreadPoolExecutor.this.allowCoreThreadTimeOut(value);
      }

      public long getKeepAliveTimeSeconds() {
         return ManagedThreadPoolExecutor.this.getKeepAliveTime(TimeUnit.SECONDS);
      }

      public void setKeepAliveTimeSeconds(long seconds) {
         ManagedThreadPoolExecutor.this.setKeepAliveTime(seconds, TimeUnit.SECONDS);
      }

      public int getMaximumQueueSize() {
         return 0;
      }

      public void setMaximumQueueSize(int size) {
      }

      public int getQueueSize() {
         return ManagedThreadPoolExecutor.this.getQueue().size();
      }

      public int getLargestQueueSize() {
         return 0;
      }

      public boolean isQueueBounded() {
         return false;
      }

      public boolean isQueueSizeModifiable() {
         return false;
      }

      public boolean isShutdown() {
         return ManagedThreadPoolExecutor.this.isShutdown();
      }

      public boolean isTerminating() {
         return ManagedThreadPoolExecutor.this.isTerminating();
      }

      public boolean isTerminated() {
         return ManagedThreadPoolExecutor.this.isTerminated();
      }

      public long getSubmittedTaskCount() {
         return ManagedThreadPoolExecutor.this.getTaskCount();
      }

      public long getRejectedTaskCount() {
         return 0L;
      }

      public long getCompletedTaskCount() {
         return ManagedThreadPoolExecutor.this.getCompletedTaskCount();
      }
   }
}

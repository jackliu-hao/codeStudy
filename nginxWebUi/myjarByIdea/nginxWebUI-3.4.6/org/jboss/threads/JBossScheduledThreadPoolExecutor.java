package org.jboss.threads;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class JBossScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {
   private final AtomicInteger rejectCount = new AtomicInteger();
   private final Runnable terminationTask;

   public JBossScheduledThreadPoolExecutor(int corePoolSize, Runnable terminationTask) {
      super(corePoolSize);
      this.terminationTask = terminationTask;
      this.setRejectedExecutionHandler(super.getRejectedExecutionHandler());
   }

   public JBossScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, Runnable terminationTask) {
      super(corePoolSize, threadFactory);
      this.terminationTask = terminationTask;
      this.setRejectedExecutionHandler(super.getRejectedExecutionHandler());
   }

   public JBossScheduledThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler, Runnable terminationTask) {
      super(corePoolSize);
      this.terminationTask = terminationTask;
      this.setRejectedExecutionHandler(handler);
   }

   public JBossScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler, Runnable terminationTask) {
      super(corePoolSize, threadFactory);
      this.terminationTask = terminationTask;
      this.setRejectedExecutionHandler(handler);
   }

   public long getKeepAliveTime() {
      return this.getKeepAliveTime(TimeUnit.MILLISECONDS);
   }

   public void setKeepAliveTime(long milliseconds) {
      super.setKeepAliveTime(milliseconds, TimeUnit.MILLISECONDS);
      super.allowCoreThreadTimeOut(milliseconds < Long.MAX_VALUE);
   }

   public void setKeepAliveTime(long time, TimeUnit unit) {
      super.setKeepAliveTime(time, unit);
      super.allowCoreThreadTimeOut(time < Long.MAX_VALUE);
   }

   public int getRejectedCount() {
      return this.rejectCount.get();
   }

   public int getCurrentThreadCount() {
      return this.getActiveCount();
   }

   public int getLargestThreadCount() {
      return this.getLargestPoolSize();
   }

   public int getMaxThreads() {
      return this.getCorePoolSize();
   }

   public void setMaxThreads(int newSize) {
      this.setCorePoolSize(newSize);
   }

   public RejectedExecutionHandler getRejectedExecutionHandler() {
      return ((CountingRejectHandler)super.getRejectedExecutionHandler()).getDelegate();
   }

   public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
      super.setRejectedExecutionHandler(new CountingRejectHandler(handler));
   }

   public int getQueueSize() {
      return this.getQueue().size();
   }

   protected void terminated() {
      this.terminationTask.run();
   }

   private final class CountingRejectHandler implements RejectedExecutionHandler {
      private final RejectedExecutionHandler delegate;

      public CountingRejectHandler(RejectedExecutionHandler delegate) {
         this.delegate = delegate;
      }

      public RejectedExecutionHandler getDelegate() {
         return this.delegate;
      }

      public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
         JBossScheduledThreadPoolExecutor.this.rejectCount.incrementAndGet();
         if (JBossScheduledThreadPoolExecutor.this.isShutdown()) {
            throw Messages.msg.shutDownInitiated();
         } else {
            this.delegate.rejectedExecution(r, executor);
         }
      }
   }
}

package cn.hutool.core.thread;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.util.ObjectUtil;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorBuilder implements Builder<ThreadPoolExecutor> {
   private static final long serialVersionUID = 1L;
   public static final int DEFAULT_QUEUE_CAPACITY = 1024;
   private int corePoolSize;
   private int maxPoolSize = Integer.MAX_VALUE;
   private long keepAliveTime;
   private BlockingQueue<Runnable> workQueue;
   private ThreadFactory threadFactory;
   private RejectedExecutionHandler handler;
   private Boolean allowCoreThreadTimeOut;

   public ExecutorBuilder() {
      this.keepAliveTime = TimeUnit.SECONDS.toNanos(60L);
   }

   public ExecutorBuilder setCorePoolSize(int corePoolSize) {
      this.corePoolSize = corePoolSize;
      return this;
   }

   public ExecutorBuilder setMaxPoolSize(int maxPoolSize) {
      this.maxPoolSize = maxPoolSize;
      return this;
   }

   public ExecutorBuilder setKeepAliveTime(long keepAliveTime, TimeUnit unit) {
      return this.setKeepAliveTime(unit.toNanos(keepAliveTime));
   }

   public ExecutorBuilder setKeepAliveTime(long keepAliveTime) {
      this.keepAliveTime = keepAliveTime;
      return this;
   }

   public ExecutorBuilder setWorkQueue(BlockingQueue<Runnable> workQueue) {
      this.workQueue = workQueue;
      return this;
   }

   public ExecutorBuilder useArrayBlockingQueue(int capacity) {
      return this.setWorkQueue(new ArrayBlockingQueue(capacity));
   }

   public ExecutorBuilder useSynchronousQueue() {
      return this.useSynchronousQueue(false);
   }

   public ExecutorBuilder useSynchronousQueue(boolean fair) {
      return this.setWorkQueue(new SynchronousQueue(fair));
   }

   public ExecutorBuilder setThreadFactory(ThreadFactory threadFactory) {
      this.threadFactory = threadFactory;
      return this;
   }

   public ExecutorBuilder setHandler(RejectedExecutionHandler handler) {
      this.handler = handler;
      return this;
   }

   public ExecutorBuilder setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
      this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
      return this;
   }

   public static ExecutorBuilder create() {
      return new ExecutorBuilder();
   }

   public ThreadPoolExecutor build() {
      return build(this);
   }

   public ExecutorService buildFinalizable() {
      return new FinalizableDelegatedExecutorService(this.build());
   }

   private static ThreadPoolExecutor build(ExecutorBuilder builder) {
      int corePoolSize = builder.corePoolSize;
      int maxPoolSize = builder.maxPoolSize;
      long keepAliveTime = builder.keepAliveTime;
      Object workQueue;
      if (null != builder.workQueue) {
         workQueue = builder.workQueue;
      } else {
         workQueue = corePoolSize <= 0 ? new SynchronousQueue() : new LinkedBlockingQueue(1024);
      }

      ThreadFactory threadFactory = null != builder.threadFactory ? builder.threadFactory : Executors.defaultThreadFactory();
      RejectedExecutionHandler handler = (RejectedExecutionHandler)ObjectUtil.defaultIfNull(builder.handler, (Object)RejectPolicy.ABORT.getValue());
      ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.NANOSECONDS, (BlockingQueue)workQueue, threadFactory, handler);
      if (null != builder.allowCoreThreadTimeOut) {
         threadPoolExecutor.allowCoreThreadTimeOut(builder.allowCoreThreadTimeOut);
      }

      return threadPoolExecutor;
   }
}

package cn.hutool.core.thread;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.util.StrUtil;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadFactoryBuilder implements Builder<ThreadFactory> {
   private static final long serialVersionUID = 1L;
   private ThreadFactory backingThreadFactory;
   private String namePrefix;
   private Boolean daemon;
   private Integer priority;
   private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

   public static ThreadFactoryBuilder create() {
      return new ThreadFactoryBuilder();
   }

   public ThreadFactoryBuilder setThreadFactory(ThreadFactory backingThreadFactory) {
      this.backingThreadFactory = backingThreadFactory;
      return this;
   }

   public ThreadFactoryBuilder setNamePrefix(String namePrefix) {
      this.namePrefix = namePrefix;
      return this;
   }

   public ThreadFactoryBuilder setDaemon(boolean daemon) {
      this.daemon = daemon;
      return this;
   }

   public ThreadFactoryBuilder setPriority(int priority) {
      if (priority < 1) {
         throw new IllegalArgumentException(StrUtil.format("Thread priority ({}) must be >= {}", new Object[]{priority, 1}));
      } else if (priority > 10) {
         throw new IllegalArgumentException(StrUtil.format("Thread priority ({}) must be <= {}", new Object[]{priority, 10}));
      } else {
         this.priority = priority;
         return this;
      }
   }

   public ThreadFactoryBuilder setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
      this.uncaughtExceptionHandler = uncaughtExceptionHandler;
      return this;
   }

   public ThreadFactory build() {
      return build(this);
   }

   private static ThreadFactory build(ThreadFactoryBuilder builder) {
      ThreadFactory backingThreadFactory = null != builder.backingThreadFactory ? builder.backingThreadFactory : Executors.defaultThreadFactory();
      String namePrefix = builder.namePrefix;
      Boolean daemon = builder.daemon;
      Integer priority = builder.priority;
      Thread.UncaughtExceptionHandler handler = builder.uncaughtExceptionHandler;
      AtomicLong count = null == namePrefix ? null : new AtomicLong();
      return (r) -> {
         Thread thread = backingThreadFactory.newThread(r);
         if (null != namePrefix) {
            thread.setName(namePrefix + count.getAndIncrement());
         }

         if (null != daemon) {
            thread.setDaemon(daemon);
         }

         if (null != priority) {
            thread.setPriority(priority);
         }

         if (null != handler) {
            thread.setUncaughtExceptionHandler(handler);
         }

         return thread;
      };
   }
}

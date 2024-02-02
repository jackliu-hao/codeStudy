package io.undertow.server.protocol.framed;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

final class ShutdownFallbackExecutor {
   private static volatile Executor EXECUTOR = null;

   private ShutdownFallbackExecutor() {
   }

   static void execute(Runnable runnable) {
      if (EXECUTOR == null) {
         Class var1 = ShutdownFallbackExecutor.class;
         synchronized(ShutdownFallbackExecutor.class) {
            if (EXECUTOR == null) {
               EXECUTOR = new ThreadPoolExecutor(0, 1, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), new ShutdownFallbackThreadFactory());
            }
         }
      }

      EXECUTOR.execute(runnable);
   }

   static final class ShutdownFallbackThreadFactory implements ThreadFactory {
      private final AtomicLong count = new AtomicLong();
      private final ThreadFactory threadFactory = Executors.defaultThreadFactory();

      public Thread newThread(Runnable r) {
         Thread thread = this.threadFactory.newThread(r);
         thread.setName("undertow-shutdown-" + this.count.getAndIncrement());
         thread.setDaemon(true);
         return thread;
      }
   }
}

package cn.hutool.cache;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public enum GlobalPruneTimer {
   INSTANCE;

   private final AtomicInteger cacheTaskNumber = new AtomicInteger(1);
   private ScheduledExecutorService pruneTimer;

   private GlobalPruneTimer() {
      this.create();
   }

   public ScheduledFuture<?> schedule(Runnable task, long delay) {
      return this.pruneTimer.scheduleAtFixedRate(task, delay, delay, TimeUnit.MILLISECONDS);
   }

   public void create() {
      if (null != this.pruneTimer) {
         this.shutdownNow();
      }

      this.pruneTimer = new ScheduledThreadPoolExecutor(1, (r) -> {
         return ThreadUtil.newThread(r, StrUtil.format("Pure-Timer-{}", new Object[]{this.cacheTaskNumber.getAndIncrement()}));
      });
   }

   public void shutdown() {
      if (null != this.pruneTimer) {
         this.pruneTimer.shutdown();
      }

   }

   public List<Runnable> shutdownNow() {
      return null != this.pruneTimer ? this.pruneTimer.shutdownNow() : null;
   }
}

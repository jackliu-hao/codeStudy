package cn.hutool.core.thread;

import cn.hutool.core.exceptions.UtilException;
import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class SyncFinisher implements Closeable {
   private final Set<Worker> workers;
   private final int threadSize;
   private ExecutorService executorService;
   private boolean isBeginAtSameTime;
   private final CountDownLatch beginLatch = new CountDownLatch(1);
   private CountDownLatch endLatch;

   public SyncFinisher(int threadSize) {
      this.threadSize = threadSize;
      this.workers = new LinkedHashSet();
   }

   public SyncFinisher setBeginAtSameTime(boolean isBeginAtSameTime) {
      this.isBeginAtSameTime = isBeginAtSameTime;
      return this;
   }

   public SyncFinisher addRepeatWorker(final Runnable runnable) {
      for(int i = 0; i < this.threadSize; ++i) {
         this.addWorker(new Worker() {
            public void work() {
               runnable.run();
            }
         });
      }

      return this;
   }

   public SyncFinisher addWorker(final Runnable runnable) {
      return this.addWorker(new Worker() {
         public void work() {
            runnable.run();
         }
      });
   }

   public synchronized SyncFinisher addWorker(Worker worker) {
      this.workers.add(worker);
      return this;
   }

   public void start() {
      this.start(true);
   }

   public void start(boolean sync) {
      this.endLatch = new CountDownLatch(this.workers.size());
      if (null == this.executorService || this.executorService.isShutdown()) {
         this.executorService = ThreadUtil.newExecutor(this.threadSize);
      }

      Iterator var2 = this.workers.iterator();

      while(var2.hasNext()) {
         Worker worker = (Worker)var2.next();
         this.executorService.submit(worker);
      }

      this.beginLatch.countDown();
      if (sync) {
         try {
            this.endLatch.await();
         } catch (InterruptedException var4) {
            throw new UtilException(var4);
         }
      }

   }

   public void stop() {
      if (null != this.executorService) {
         this.executorService.shutdown();
      }

      this.executorService = null;
      this.clearWorker();
   }

   public void clearWorker() {
      this.workers.clear();
   }

   public long count() {
      return this.endLatch.getCount();
   }

   public void close() throws IOException {
      this.stop();
   }

   public abstract class Worker implements Runnable {
      public void run() {
         if (SyncFinisher.this.isBeginAtSameTime) {
            try {
               SyncFinisher.this.beginLatch.await();
            } catch (InterruptedException var6) {
               throw new UtilException(var6);
            }
         }

         try {
            this.work();
         } finally {
            SyncFinisher.this.endLatch.countDown();
         }

      }

      public abstract void work();
   }
}

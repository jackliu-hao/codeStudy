package cn.hutool.core.thread;

import java.util.concurrent.Semaphore;

public class SemaphoreRunnable implements Runnable {
   private final Runnable runnable;
   private final Semaphore semaphore;

   public SemaphoreRunnable(Runnable runnable, Semaphore semaphore) {
      this.runnable = runnable;
      this.semaphore = semaphore;
   }

   public Semaphore getSemaphore() {
      return this.semaphore;
   }

   public void run() {
      if (null != this.semaphore) {
         try {
            this.semaphore.acquire();

            try {
               this.runnable.run();
            } finally {
               this.semaphore.release();
            }
         } catch (InterruptedException var5) {
            Thread.currentThread().interrupt();
         }
      }

   }
}

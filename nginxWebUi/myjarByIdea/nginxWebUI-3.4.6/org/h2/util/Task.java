package org.h2.util;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Task implements Runnable {
   private static final AtomicInteger counter = new AtomicInteger();
   public volatile boolean stop;
   private volatile Object result;
   private volatile boolean finished;
   private Thread thread;
   private volatile Exception ex;

   public abstract void call() throws Exception;

   public void run() {
      try {
         this.call();
      } catch (Exception var2) {
         this.ex = var2;
      }

      this.finished = true;
   }

   public Task execute() {
      return this.execute(this.getClass().getName() + ":" + counter.getAndIncrement());
   }

   public Task execute(String var1) {
      this.thread = new Thread(this, var1);
      this.thread.setDaemon(true);
      this.thread.start();
      return this;
   }

   public Object get() {
      Exception var1 = this.getException();
      if (var1 != null) {
         throw new RuntimeException(var1);
      } else {
         return this.result;
      }
   }

   public boolean isFinished() {
      return this.finished;
   }

   public Exception getException() {
      this.join();
      return this.ex != null ? this.ex : null;
   }

   public void join() {
      this.stop = true;
      if (this.thread == null) {
         throw new IllegalStateException("Thread not started");
      } else {
         try {
            this.thread.join();
         } catch (InterruptedException var2) {
         }

      }
   }
}

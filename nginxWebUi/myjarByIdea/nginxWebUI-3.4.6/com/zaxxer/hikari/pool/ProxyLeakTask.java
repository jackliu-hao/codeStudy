package com.zaxxer.hikari.pool;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ProxyLeakTask implements Runnable {
   private static final Logger LOGGER = LoggerFactory.getLogger(ProxyLeakTask.class);
   static final ProxyLeakTask NO_LEAK = new ProxyLeakTask() {
      void schedule(ScheduledExecutorService executorService, long leakDetectionThreshold) {
      }

      public void run() {
      }

      public void cancel() {
      }
   };
   private ScheduledFuture<?> scheduledFuture;
   private String connectionName;
   private Exception exception;
   private String threadName;
   private boolean isLeaked;

   ProxyLeakTask(PoolEntry poolEntry) {
      this.exception = new Exception("Apparent connection leak detected");
      this.threadName = Thread.currentThread().getName();
      this.connectionName = poolEntry.connection.toString();
   }

   private ProxyLeakTask() {
   }

   void schedule(ScheduledExecutorService executorService, long leakDetectionThreshold) {
      this.scheduledFuture = executorService.schedule(this, leakDetectionThreshold, TimeUnit.MILLISECONDS);
   }

   public void run() {
      this.isLeaked = true;
      StackTraceElement[] stackTrace = this.exception.getStackTrace();
      StackTraceElement[] trace = new StackTraceElement[stackTrace.length - 5];
      System.arraycopy(stackTrace, 5, trace, 0, trace.length);
      this.exception.setStackTrace(trace);
      LOGGER.warn("Connection leak detection triggered for {} on thread {}, stack trace follows", this.connectionName, this.threadName, this.exception);
   }

   void cancel() {
      this.scheduledFuture.cancel(false);
      if (this.isLeaked) {
         LOGGER.info((String)"Previously reported leaked connection {} on thread {} was returned to the pool (unleaked)", (Object)this.connectionName, (Object)this.threadName);
      }

   }

   // $FF: synthetic method
   ProxyLeakTask(Object x0) {
      this();
   }
}

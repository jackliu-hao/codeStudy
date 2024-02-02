package org.jboss.threads;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import org.jboss.logging.Logger;
import org.wildfly.common.Assert;
import org.wildfly.common.lock.ExtendedLock;
import org.wildfly.common.lock.Locks;

public final class ViewExecutor extends AbstractExecutorService {
   private static final Logger log = Logger.getLogger("org.jboss.threads.view-executor");
   private static final Runnable[] NO_RUNNABLES = new Runnable[0];
   private final Executor delegate;
   private final ExtendedLock lock;
   private final Condition shutDownCondition;
   private final ArrayDeque<Runnable> queue;
   private final Set<TaskWrapper> allWrappers = Collections.newSetFromMap(new ConcurrentHashMap());
   private int queueLimit;
   private short submittedCount;
   private short maxCount;
   private short runningCount;
   private int state = 0;
   private volatile Thread.UncaughtExceptionHandler handler;
   private volatile Runnable terminationTask;
   private static final int ST_RUNNING = 0;
   private static final int ST_SHUTDOWN_REQ = 1;
   private static final int ST_SHUTDOWN_INT_REQ = 2;
   private static final int ST_STOPPED = 3;

   ViewExecutor(Builder builder) {
      this.delegate = builder.getDelegate();
      this.maxCount = (short)builder.getMaxSize();
      int queueLimit = builder.getQueueLimit();
      this.queueLimit = queueLimit;
      this.handler = builder.getUncaughtHandler();
      this.queue = new ArrayDeque(Math.min(queueLimit, builder.getQueueInitialSize()));
      this.lock = Locks.reentrantLock();
      this.shutDownCondition = this.lock.newCondition();
   }

   public void execute(Runnable command) {
      this.lock.lock();

      try {
         if (this.state != 0) {
            throw new RejectedExecutionException("Executor has been shut down");
         }

         short submittedCount = this.submittedCount;
         if (this.runningCount + submittedCount < this.maxCount) {
            this.submittedCount = (short)(submittedCount + 1);
            TaskWrapper tw = new TaskWrapper(JBossExecutors.classLoaderPreservingTask(command));
            this.allWrappers.add(tw);

            try {
               this.delegate.execute(tw);
            } catch (Throwable var8) {
               --this.submittedCount;
               this.allWrappers.remove(tw);
               throw var8;
            }
         } else {
            if (this.queue.size() >= this.queueLimit) {
               throw new RejectedExecutionException("No executor queue space remaining");
            }

            this.queue.add(command);
         }
      } finally {
         this.lock.unlock();
      }

   }

   public void shutdown() {
      this.shutdown(false);
   }

   public void shutdown(boolean interrupt) {
      this.lock.lock();
      int oldState = this.state;
      if (oldState < 1) {
         boolean emptyQueue;
         try {
            emptyQueue = this.queue.isEmpty();
         } catch (Throwable var9) {
            this.lock.unlock();
            throw var9;
         }

         if (this.runningCount == 0 && this.submittedCount == 0 && emptyQueue) {
            this.state = 3;

            try {
               this.shutDownCondition.signalAll();
            } finally {
               this.lock.unlock();
            }

            this.runTermination();
            return;
         }
      }

      this.state = interrupt ? 2 : 1;
      this.lock.unlock();
      if (interrupt && oldState < 2) {
         Iterator var10 = this.allWrappers.iterator();

         while(var10.hasNext()) {
            TaskWrapper wrapper = (TaskWrapper)var10.next();
            wrapper.interrupt();
         }
      }

   }

   public List<Runnable> shutdownNow() {
      this.lock.lock();
      int oldState = this.state;

      Runnable[] tasks;
      try {
         tasks = (Runnable[])this.queue.toArray(NO_RUNNABLES);
         this.queue.clear();
      } catch (Throwable var8) {
         this.lock.unlock();
         throw var8;
      }

      if (oldState < 2) {
         if (this.runningCount == 0 && this.submittedCount == 0) {
            this.state = 3;

            try {
               this.shutDownCondition.signalAll();
            } finally {
               this.lock.unlock();
            }

            this.runTermination();
         } else {
            this.lock.unlock();
            this.state = 2;
            Iterator var3 = this.allWrappers.iterator();

            while(var3.hasNext()) {
               TaskWrapper wrapper = (TaskWrapper)var3.next();
               wrapper.interrupt();
            }
         }
      } else {
         this.lock.unlock();
      }

      return Arrays.asList(tasks);
   }

   public boolean isShutdown() {
      this.lock.lock();

      boolean var1;
      try {
         var1 = this.state >= 1;
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   public boolean isTerminated() {
      this.lock.lock();

      boolean var1;
      try {
         var1 = this.state == 3;
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      this.lock.lock();

      boolean var10;
      try {
         if (this.state == 3) {
            boolean var14 = true;
            return var14;
         }

         long nanos = unit.toNanos(timeout);
         if (nanos <= 0L) {
            boolean var15 = false;
            return var15;
         }

         long elapsed = 0L;
         long start = System.nanoTime();

         do {
            this.shutDownCondition.awaitNanos(nanos - elapsed);
            if (this.state == 3) {
               var10 = true;
               return var10;
            }

            elapsed = System.nanoTime() - start;
         } while(elapsed < nanos);

         var10 = false;
      } finally {
         this.lock.unlock();
      }

      return var10;
   }

   public Thread.UncaughtExceptionHandler getExceptionHandler() {
      return this.handler;
   }

   public void setExceptionHandler(Thread.UncaughtExceptionHandler handler) {
      Assert.checkNotNullParam("handler", handler);
      this.handler = handler;
   }

   public Runnable getTerminationTask() {
      return this.terminationTask;
   }

   public void setTerminationTask(Runnable terminationTask) {
      this.terminationTask = terminationTask;
   }

   public String toString() {
      return "view of " + this.delegate;
   }

   public static Builder builder(Executor delegate) {
      Assert.checkNotNullParam("delegate", delegate);
      return new Builder(delegate);
   }

   private void runTermination() {
      Runnable task = this.terminationTask;
      this.terminationTask = null;
      if (task != null) {
         try {
            task.run();
         } catch (Throwable var5) {
            Throwable t = var5;

            try {
               this.handler.uncaughtException(Thread.currentThread(), t);
            } catch (Throwable var4) {
            }
         }
      }

   }

   // $FF: synthetic method
   static short access$110(ViewExecutor x0) {
      short var10002 = x0.submittedCount;
      x0.submittedCount = (short)(var10002 - 1);
      return var10002;
   }

   // $FF: synthetic method
   static short access$208(ViewExecutor x0) {
      short var10002 = x0.runningCount;
      x0.runningCount = (short)(var10002 + 1);
      return var10002;
   }

   // $FF: synthetic method
   static short access$210(ViewExecutor x0) {
      short var10002 = x0.runningCount;
      x0.runningCount = (short)(var10002 - 1);
      return var10002;
   }

   // $FF: synthetic method
   static short access$108(ViewExecutor x0) {
      short var10002 = x0.submittedCount;
      x0.submittedCount = (short)(var10002 + 1);
      return var10002;
   }

   class TaskWrapper implements Runnable {
      private volatile Thread thread;
      private Runnable command;

      TaskWrapper(Runnable command) {
         this.command = command;
      }

      void interrupt() {
         Thread thread = this.thread;
         if (thread != null) {
            thread.interrupt();
         }

      }

      public void run() {
         this.thread = Thread.currentThread();

         while(true) {
            try {
               ViewExecutor.this.lock.lock();

               try {
                  ViewExecutor.access$110(ViewExecutor.this);
                  ViewExecutor.access$208(ViewExecutor.this);
               } finally {
                  ViewExecutor.this.lock.unlock();
               }

               try {
                  this.command.run();
               } catch (Throwable var29) {
                  Throwable t = var29;

                  try {
                     ViewExecutor.this.handler.uncaughtException(Thread.currentThread(), t);
                  } catch (Throwable var28) {
                  }
               }

               ViewExecutor.this.lock.lock();
               ViewExecutor.access$210(ViewExecutor.this);

               try {
                  this.command = (Runnable)ViewExecutor.this.queue.pollFirst();
               } catch (Throwable var27) {
                  ViewExecutor.this.lock.unlock();
                  throw var27;
               }

               if (ViewExecutor.this.runningCount + ViewExecutor.this.submittedCount >= ViewExecutor.this.maxCount || this.command == null) {
                  if (this.command == null && ViewExecutor.this.runningCount == 0 && ViewExecutor.this.submittedCount == 0 && ViewExecutor.this.state != 0) {
                     ViewExecutor.this.state = 3;

                     try {
                        ViewExecutor.this.shutDownCondition.signalAll();
                     } finally {
                        ViewExecutor.this.lock.unlock();
                     }

                     ViewExecutor.this.runTermination();
                     return;
                  }

                  ViewExecutor.this.lock.unlock();
                  return;
               }

               ViewExecutor.access$108(ViewExecutor.this);
               ViewExecutor.this.lock.unlock();

               try {
                  ViewExecutor.this.delegate.execute(this);
               } catch (Throwable var31) {
                  ViewExecutor.log.warn("Failed to resubmit executor task to delegate executor (executing task immediately instead)", (Throwable)var31);
                  continue;
               }
            } finally {
               this.thread = null;
            }

            return;
         }
      }
   }

   public static final class Builder {
      private final Executor delegate;
      private short maxSize = 1;
      private int queueLimit = Integer.MAX_VALUE;
      private int queueInitialSize = 256;
      private Thread.UncaughtExceptionHandler handler = JBossExecutors.loggingExceptionHandler();

      Builder(Executor delegate) {
         this.delegate = delegate;
      }

      public int getMaxSize() {
         return this.maxSize;
      }

      public Builder setMaxSize(int maxSize) {
         Assert.checkMinimumParameter("maxSize", 1, maxSize);
         Assert.checkMaximumParameter("maxSize", 32767, maxSize);
         this.maxSize = (short)maxSize;
         return this;
      }

      public int getQueueLimit() {
         return this.queueLimit;
      }

      public Builder setQueueLimit(int queueLimit) {
         Assert.checkMinimumParameter("queueLimit", 0, queueLimit);
         this.queueLimit = queueLimit;
         return this;
      }

      public Executor getDelegate() {
         return this.delegate;
      }

      public Thread.UncaughtExceptionHandler getUncaughtHandler() {
         return this.handler;
      }

      public Builder setUncaughtHandler(Thread.UncaughtExceptionHandler handler) {
         this.handler = handler;
         return this;
      }

      public int getQueueInitialSize() {
         return this.queueInitialSize;
      }

      public Builder setQueueInitialSize(int queueInitialSize) {
         this.queueInitialSize = queueInitialSize;
         return this;
      }

      public ViewExecutor build() {
         return new ViewExecutor(this);
      }
   }
}

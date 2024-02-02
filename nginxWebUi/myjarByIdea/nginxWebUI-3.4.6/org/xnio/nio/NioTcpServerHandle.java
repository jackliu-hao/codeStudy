package org.xnio.nio;

import java.io.Closeable;
import java.nio.channels.SelectionKey;
import java.util.concurrent.TimeUnit;
import org.jboss.logging.Logger;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;

final class NioTcpServerHandle extends NioHandle implements ChannelClosed {
   private static final String FQCN = NioTcpServerHandle.class.getName();
   private final Runnable freeTask;
   private final NioTcpServer server;
   private int count;
   private int low;
   private int high;
   private int tokenCount = -1;
   private boolean stopped;
   private boolean backOff;
   private int backOffTime = 0;

   NioTcpServerHandle(NioTcpServer server, SelectionKey key, WorkerThread thread, int low, int high) {
      super(thread, key);
      this.server = server;
      this.low = low;
      this.high = high;
      this.freeTask = new Runnable() {
         public void run() {
            NioTcpServerHandle.this.freeConnection();
         }
      };
   }

   void handleReady(int ops) {
      ChannelListeners.invokeChannelListener(this.server, this.server.getAcceptListener());
   }

   void forceTermination() {
      IoUtils.safeClose((Closeable)this.server);
   }

   void terminated() {
      this.server.invokeCloseHandler();
   }

   Runnable getFreeTask() {
      return this.freeTask;
   }

   void resume() {
      WorkerThread thread = this.getWorkerThread();
      if (thread == Thread.currentThread()) {
         if (!this.stopped && !this.backOff && this.server.resumed) {
            super.resume(16);
         }
      } else {
         thread.execute(new Runnable() {
            public void run() {
               NioTcpServerHandle.this.resume();
            }
         });
      }

   }

   void suspend() {
      WorkerThread thread = this.getWorkerThread();
      if (thread == Thread.currentThread()) {
         if (this.stopped || this.backOff || !this.server.resumed) {
            super.suspend(16);
         }
      } else {
         thread.execute(new Runnable() {
            public void run() {
               NioTcpServerHandle.this.suspend();
            }
         });
      }

   }

   public void channelClosed() {
      WorkerThread thread = this.getWorkerThread();
      if (thread == Thread.currentThread()) {
         this.freeConnection();
      } else {
         thread.execute(this.freeTask);
      }

   }

   void freeConnection() {
      assert Thread.currentThread() == this.getWorkerThread();

      if (this.count-- <= this.low && this.tokenCount != 0 && this.stopped) {
         Log.tcpServerConnectionLimitLog.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, "Connection freed, resuming accept connections", new Object[0]);
         this.stopped = false;
         if (this.server.resumed) {
            this.backOff = false;
            super.resume(16);
         }
      }

   }

   void setTokenCount(int newCount) {
      WorkerThread workerThread = this.getWorkerThread();
      if (workerThread == Thread.currentThread()) {
         if (this.tokenCount == 0) {
            this.tokenCount = newCount;
            if (this.count <= this.low && this.stopped) {
               this.stopped = false;
               if (this.server.resumed && !this.backOff) {
                  super.resume(16);
               }
            }

            return;
         }

         workerThread = workerThread.getNextThread();
      }

      this.setThreadNewCount(workerThread, newCount);
   }

   void startBackOff() {
      this.backOff = true;
      this.backOffTime = Math.max(250, Math.min(30000, this.backOffTime << 2));
      this.suspend();
      this.getWorkerThread().executeAfter(this::endBackOff, (long)this.backOffTime, TimeUnit.MILLISECONDS);
   }

   void endBackOff() {
      this.backOff = false;
      this.resume();
   }

   void resetBackOff() {
      this.backOffTime = 0;
   }

   private void setThreadNewCount(WorkerThread workerThread, final int newCount) {
      final int number = workerThread.getNumber();
      workerThread.execute(new Runnable() {
         public void run() {
            NioTcpServerHandle.this.server.getHandle(number).setTokenCount(newCount);
         }
      });
   }

   void initializeTokenCount(final int newCount) {
      WorkerThread workerThread = this.getWorkerThread();
      final int number = workerThread.getNumber();
      if (workerThread == Thread.currentThread()) {
         this.tokenCount = newCount;
         if (newCount == 0) {
            this.stopped = true;
            super.suspend(16);
         }
      } else {
         workerThread.execute(new Runnable() {
            public void run() {
               NioTcpServerHandle.this.server.getHandle(number).initializeTokenCount(newCount);
            }
         });
      }

   }

   boolean getConnection() {
      assert Thread.currentThread() == this.getWorkerThread();

      if (!this.stopped && !this.backOff) {
         if (this.tokenCount != -1 && --this.tokenCount == 0) {
            this.setThreadNewCount(this.getWorkerThread().getNextThread(), this.server.getTokenConnectionCount());
         }

         if (++this.count >= this.high || this.tokenCount == 0) {
            if (Log.tcpServerLog.isDebugEnabled() && this.count >= this.high) {
               Log.tcpServerConnectionLimitLog.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, "Total open connections reach high water limit (%s) by this new accepting request. Temporarily stopping accept connections", this.high);
            }

            this.stopped = true;
            super.suspend(16);
         }

         return true;
      } else {
         Log.tcpServerConnectionLimitLog.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, "Refusing accepting request (temporarily stopped: %s, backed off: %s)", this.stopped, this.backOff);
         return false;
      }
   }

   public void executeSetTask(final int high, final int low) {
      WorkerThread thread = this.getWorkerThread();
      if (thread == Thread.currentThread()) {
         this.high = high;
         this.low = low;
         if (this.count >= high && !this.stopped) {
            this.stopped = true;
            this.suspend();
         } else if (this.count <= low && this.stopped) {
            this.stopped = false;
            if (this.server.resumed && !this.backOff) {
               this.resume();
            }
         }
      } else {
         thread.execute(new Runnable() {
            public void run() {
               NioTcpServerHandle.this.executeSetTask(high, low);
            }
         });
      }

   }

   int getConnectionCount() {
      assert Thread.currentThread() == this.getWorkerThread();

      return this.count;
   }

   int getBackOffTime() {
      return this.backOffTime;
   }
}

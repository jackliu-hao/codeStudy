package org.xnio.nio;

import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import org.xnio.Bits;

abstract class NioHandle {
   private final WorkerThread workerThread;
   private final SelectionKey selectionKey;

   protected NioHandle(WorkerThread workerThread, SelectionKey selectionKey) {
      this.workerThread = workerThread;
      this.selectionKey = selectionKey;
   }

   void resume(int ops) {
      try {
         if (!Bits.allAreSet(this.selectionKey.interestOps(), ops)) {
            this.workerThread.setOps(this.selectionKey, ops);
         }
      } catch (CancelledKeyException var3) {
      }

   }

   void wakeup(final int ops) {
      this.workerThread.queueTask(new Runnable() {
         public void run() {
            NioHandle.this.handleReady(ops);
         }
      });

      try {
         if (!Bits.allAreSet(this.selectionKey.interestOps(), ops)) {
            this.workerThread.setOps(this.selectionKey, ops);
         }
      } catch (CancelledKeyException var3) {
      }

   }

   void suspend(int ops) {
      try {
         if (!Bits.allAreClear(this.selectionKey.interestOps(), ops)) {
            this.workerThread.clearOps(this.selectionKey, ops);
         }
      } catch (CancelledKeyException var3) {
      }

   }

   boolean isResumed(int ops) {
      try {
         return Bits.allAreSet(this.selectionKey.interestOps(), ops);
      } catch (CancelledKeyException var3) {
         return false;
      }
   }

   abstract void handleReady(int var1);

   abstract void forceTermination();

   abstract void terminated();

   WorkerThread getWorkerThread() {
      return this.workerThread;
   }

   SelectionKey getSelectionKey() {
      return this.selectionKey;
   }

   void cancelKey(boolean block) {
      this.workerThread.cancelKey(this.selectionKey, block);
   }
}

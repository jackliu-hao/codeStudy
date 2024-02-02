package org.jboss.threads;

import org.wildfly.common.annotation.NotNull;

abstract class EnhancedQueueExecutorBase1 extends EnhancedQueueExecutorBase0 {
   static final long tailLockOffset;
   static final long tailOffset;
   static final boolean COMBINED_LOCK;
   static final boolean TAIL_SPIN;
   static final boolean TAIL_LOCK;
   volatile int tailLock;
   @NotNull
   volatile EnhancedQueueExecutor.TaskNode tail;

   void compareAndSetTail(EnhancedQueueExecutor.TaskNode expect, EnhancedQueueExecutor.TaskNode update) {
      if (this.tail == expect) {
         JBossExecutors.unsafe.compareAndSwapObject(this, tailOffset, expect, update);
      }

   }

   final void lockTail() {
      int spins = 0;

      while(this.tailLock != 0 || !JBossExecutors.unsafe.compareAndSwapInt(this, tailLockOffset, 0, 1)) {
         if (spins == EnhancedQueueExecutorBase3.YIELD_SPINS) {
            spins = 0;
            Thread.yield();
         } else {
            ++spins;
            JDKSpecific.onSpinWait();
         }
      }

   }

   final void unlockTail() {
      assert this.tailLock == 1;

      this.tailLock = 0;
   }

   static {
      try {
         tailLockOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutorBase1.class.getDeclaredField("tailLock"));
         tailOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutorBase1.class.getDeclaredField("tail"));
      } catch (NoSuchFieldException var1) {
         throw new NoSuchFieldError(var1.getMessage());
      }

      COMBINED_LOCK = readBooleanPropertyPrefixed("combined-lock", false);
      TAIL_SPIN = readBooleanPropertyPrefixed("tail-spin", false);
      TAIL_LOCK = readBooleanPropertyPrefixed("tail-lock", true);
   }
}

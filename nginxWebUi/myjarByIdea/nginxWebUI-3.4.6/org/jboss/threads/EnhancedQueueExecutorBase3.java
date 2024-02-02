package org.jboss.threads;

import org.wildfly.common.annotation.NotNull;
import org.wildfly.common.cpu.ProcessorInfo;

abstract class EnhancedQueueExecutorBase3 extends EnhancedQueueExecutorBase2 {
   static final long headOffset;
   static final boolean HEAD_LOCK;
   static final boolean HEAD_SPIN;
   static final int YIELD_SPINS;
   @NotNull
   volatile EnhancedQueueExecutor.TaskNode head;

   EnhancedQueueExecutorBase3() {
      this.head = this.tail = new EnhancedQueueExecutor.TaskNode((Runnable)null);
   }

   boolean compareAndSetHead(EnhancedQueueExecutor.TaskNode expect, EnhancedQueueExecutor.TaskNode update) {
      return JBossExecutors.unsafe.compareAndSwapObject(this, headOffset, expect, update);
   }

   static {
      try {
         headOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutorBase3.class.getDeclaredField("head"));
      } catch (NoSuchFieldException var1) {
         throw new NoSuchFieldError(var1.getMessage());
      }

      HEAD_LOCK = readBooleanPropertyPrefixed("head-lock", false);
      HEAD_SPIN = readBooleanPropertyPrefixed("head-spin", true);
      YIELD_SPINS = readIntPropertyPrefixed("lock-yield-spins", ProcessorInfo.availableProcessors() == 1 ? 0 : 128);
   }
}

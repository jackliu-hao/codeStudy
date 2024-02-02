package org.jboss.threads;

abstract class EnhancedQueueExecutorBase5 extends EnhancedQueueExecutorBase4 {
   static final long threadStatusOffset;
   volatile long threadStatus;

   boolean compareAndSetThreadStatus(long expect, long update) {
      return JBossExecutors.unsafe.compareAndSwapLong(this, threadStatusOffset, expect, update);
   }

   static {
      try {
         threadStatusOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutorBase5.class.getDeclaredField("threadStatus"));
      } catch (NoSuchFieldException var1) {
         throw new NoSuchFieldError(var1.getMessage());
      }
   }
}

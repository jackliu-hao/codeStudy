package org.wildfly.common.lock;

import java.security.AccessController;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import org.wildfly.common.Assert;
import org.wildfly.common.cpu.ProcessorInfo;

public class SpinLock implements ExtendedLock {
   private static final long ownerOffset;
   private static final int spinLimit;
   private volatile Thread owner;
   private int level;

   public boolean isLocked() {
      return this.owner != null;
   }

   public boolean isHeldByCurrentThread() {
      return this.owner == Thread.currentThread();
   }

   public boolean isFair() {
      return true;
   }

   public void lock() {
      int spins = 0;

      while(true) {
         Thread owner = this.owner;
         if (owner == Thread.currentThread()) {
            ++this.level;
            return;
         }

         if (owner == null && JDKSpecific.unsafe.compareAndSwapObject(this, ownerOffset, (Object)null, Thread.currentThread())) {
            this.level = 1;
            return;
         }

         if (spins >= spinLimit) {
            Thread.yield();
         } else {
            JDKSpecific.onSpinWait();
            ++spins;
         }
      }
   }

   public void lockInterruptibly() throws InterruptedException {
      int spins = 0;

      while(!Thread.interrupted()) {
         Thread owner = this.owner;
         if (owner == Thread.currentThread()) {
            ++this.level;
            return;
         }

         if (owner == null && JDKSpecific.unsafe.compareAndSwapObject(this, ownerOffset, (Object)null, Thread.currentThread())) {
            this.level = 1;
            return;
         }

         if (spins >= spinLimit) {
            Thread.yield();
         } else {
            JDKSpecific.onSpinWait();
            ++spins;
         }
      }

      throw new InterruptedException();
   }

   public boolean tryLock() {
      Thread owner = this.owner;
      if (owner == Thread.currentThread()) {
         ++this.level;
         return true;
      } else if (owner == null && JDKSpecific.unsafe.compareAndSwapObject(this, ownerOffset, (Object)null, Thread.currentThread())) {
         this.level = 1;
         return true;
      } else {
         return false;
      }
   }

   public void unlock() {
      Thread owner = this.owner;
      if (owner == Thread.currentThread()) {
         if (--this.level == 0) {
            this.owner = null;
         }

      } else {
         throw new IllegalMonitorStateException();
      }
   }

   public boolean tryLock(long time, TimeUnit unit) throws UnsupportedOperationException {
      throw Assert.unsupported();
   }

   public Condition newCondition() throws UnsupportedOperationException {
      throw Assert.unsupported();
   }

   static {
      try {
         ownerOffset = JDKSpecific.unsafe.objectFieldOffset(SpinLock.class.getDeclaredField("owner"));
      } catch (NoSuchFieldException var1) {
         throw new NoSuchFieldError(var1.getMessage());
      }

      spinLimit = (Integer)AccessController.doPrivileged(() -> {
         return Integer.valueOf(System.getProperty("jboss.spin-lock.limit", ProcessorInfo.availableProcessors() == 1 ? "0" : "5000"));
      });
   }
}

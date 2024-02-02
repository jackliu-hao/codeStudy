package org.wildfly.common.lock;

import org.wildfly.common.annotation.NotNull;

public final class Locks {
   private Locks() {
   }

   @NotNull
   public static ExtendedLock reentrantLock() {
      return new ExtendedReentrantLock();
   }

   @NotNull
   public static ExtendedLock reentrantLock(boolean fair) {
      return new ExtendedReentrantLock(fair);
   }

   @NotNull
   public static ExtendedLock spinLock() {
      return new SpinLock();
   }
}

package com.zaxxer.hikari.util;

import java.sql.SQLException;
import java.sql.SQLTransientException;
import java.util.concurrent.Semaphore;

public class SuspendResumeLock {
   public static final SuspendResumeLock FAUX_LOCK = new SuspendResumeLock(false) {
      public void acquire() {
      }

      public void release() {
      }

      public void suspend() {
      }

      public void resume() {
      }
   };
   private static final int MAX_PERMITS = 10000;
   private final Semaphore acquisitionSemaphore;

   public SuspendResumeLock() {
      this(true);
   }

   private SuspendResumeLock(boolean createSemaphore) {
      this.acquisitionSemaphore = createSemaphore ? new Semaphore(10000, true) : null;
   }

   public void acquire() throws SQLException {
      if (!this.acquisitionSemaphore.tryAcquire()) {
         if (Boolean.getBoolean("com.zaxxer.hikari.throwIfSuspended")) {
            throw new SQLTransientException("The pool is currently suspended and configured to throw exceptions upon acquisition");
         } else {
            this.acquisitionSemaphore.acquireUninterruptibly();
         }
      }
   }

   public void release() {
      this.acquisitionSemaphore.release();
   }

   public void suspend() {
      this.acquisitionSemaphore.acquireUninterruptibly(10000);
   }

   public void resume() {
      this.acquisitionSemaphore.release(10000);
   }

   // $FF: synthetic method
   SuspendResumeLock(boolean x0, Object x1) {
      this(x0);
   }
}

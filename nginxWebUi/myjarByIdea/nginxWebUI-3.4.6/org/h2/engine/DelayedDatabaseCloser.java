package org.h2.engine;

import java.lang.ref.WeakReference;
import org.h2.message.Trace;

class DelayedDatabaseCloser extends Thread {
   private final Trace trace;
   private volatile WeakReference<Database> databaseRef;
   private int delayInMillis;

   DelayedDatabaseCloser(Database var1, int var2) {
      this.databaseRef = new WeakReference(var1);
      this.delayInMillis = var2;
      this.trace = var1.getTrace(2);
      this.setName("H2 Close Delay " + var1.getShortName());
      this.setDaemon(true);
      this.start();
   }

   void reset() {
      this.databaseRef = null;
   }

   public void run() {
      while(true) {
         if (this.delayInMillis > 0) {
            try {
               byte var8 = 100;
               Thread.sleep((long)var8);
               this.delayInMillis -= var8;
            } catch (Exception var5) {
            }

            WeakReference var9 = this.databaseRef;
            if (var9 != null && var9.get() != null) {
               continue;
            }

            return;
         }

         WeakReference var2 = this.databaseRef;
         Database var1;
         if (var2 != null && (var1 = (Database)var2.get()) != null) {
            try {
               var1.close(false);
            } catch (RuntimeException var7) {
               RuntimeException var3 = var7;

               try {
                  this.trace.error(var3, "could not close the database");
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
                  throw var7;
               }
            }
         }

         return;
      }
   }
}

package org.h2.engine;

import java.util.Iterator;
import java.util.WeakHashMap;

class OnExitDatabaseCloser extends Thread {
   private static final WeakHashMap<Database, Void> DATABASES = new WeakHashMap();
   private static final Thread INSTANCE = new OnExitDatabaseCloser();
   private static boolean registered;
   private static boolean terminated;

   static synchronized void register(Database var0) {
      if (!terminated) {
         DATABASES.put(var0, (Object)null);
         if (!registered) {
            registered = true;

            try {
               Runtime.getRuntime().addShutdownHook(INSTANCE);
            } catch (IllegalStateException var2) {
            } catch (SecurityException var3) {
            }
         }

      }
   }

   static synchronized void unregister(Database var0) {
      if (!terminated) {
         DATABASES.remove(var0);
         if (DATABASES.isEmpty() && registered) {
            try {
               Runtime.getRuntime().removeShutdownHook(INSTANCE);
            } catch (IllegalStateException var2) {
            } catch (SecurityException var3) {
            }

            registered = false;
         }

      }
   }

   private static void onShutdown() {
      Class var0 = OnExitDatabaseCloser.class;
      synchronized(OnExitDatabaseCloser.class) {
         terminated = true;
      }

      RuntimeException var8 = null;
      Iterator var1 = DATABASES.keySet().iterator();

      while(var1.hasNext()) {
         Database var2 = (Database)var1.next();

         try {
            var2.close(true);
         } catch (RuntimeException var7) {
            RuntimeException var3 = var7;

            try {
               var2.getTrace(2).error(var3, "could not close the database");
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
               if (var8 == null) {
                  var8 = var7;
               } else {
                  var8.addSuppressed(var7);
               }
            }
         }
      }

      if (var8 != null) {
         throw var8;
      }
   }

   private OnExitDatabaseCloser() {
   }

   public void run() {
      onShutdown();
   }
}

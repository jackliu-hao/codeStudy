package org.h2.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class AbbaLockingDetector implements Runnable {
   private final int tickIntervalMs = 2;
   private volatile boolean stop;
   private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
   private Thread thread;
   private final Map<String, Map<String, String>> lockOrdering = new WeakHashMap();
   private final Set<String> knownDeadlocks = new HashSet();

   public AbbaLockingDetector startCollecting() {
      this.thread = new Thread(this, "AbbaLockingDetector");
      this.thread.setDaemon(true);
      this.thread.start();
      return this;
   }

   public synchronized void reset() {
      this.lockOrdering.clear();
      this.knownDeadlocks.clear();
   }

   public AbbaLockingDetector stopCollecting() {
      this.stop = true;
      if (this.thread != null) {
         try {
            this.thread.join();
         } catch (InterruptedException var2) {
         }

         this.thread = null;
      }

      return this;
   }

   public void run() {
      while(true) {
         if (!this.stop) {
            try {
               this.tick();
               continue;
            } catch (Throwable var2) {
            }
         }

         return;
      }
   }

   private void tick() {
      try {
         Thread.sleep(2L);
      } catch (InterruptedException var2) {
      }

      ThreadInfo[] var1 = this.threadMXBean.dumpAllThreads(true, false);
      this.processThreadList(var1);
   }

   private void processThreadList(ThreadInfo[] var1) {
      ArrayList var2 = new ArrayList();
      ThreadInfo[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ThreadInfo var6 = var3[var5];
         var2.clear();
         generateOrdering(var2, var6);
         if (var2.size() > 1) {
            this.markHigher(var2, var6);
         }
      }

   }

   private static void generateOrdering(List<String> var0, ThreadInfo var1) {
      MonitorInfo[] var2 = var1.getLockedMonitors();
      Arrays.sort(var2, (var0x, var1x) -> {
         return var1x.getLockedStackDepth() - var0x.getLockedStackDepth();
      });
      MonitorInfo[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         MonitorInfo var6 = var3[var5];
         String var7 = getObjectName(var6);
         if (!var7.equals("sun.misc.Launcher$AppClassLoader") && !var0.contains(var7)) {
            var0.add(var7);
         }
      }

   }

   private synchronized void markHigher(List<String> var1, ThreadInfo var2) {
      String var3 = (String)var1.get(var1.size() - 1);
      Object var4 = (Map)this.lockOrdering.get(var3);
      if (var4 == null) {
         var4 = new WeakHashMap();
         this.lockOrdering.put(var3, var4);
      }

      String var5 = null;

      for(int var6 = 0; var6 < var1.size() - 1; ++var6) {
         String var7 = (String)var1.get(var6);
         Map var8 = (Map)this.lockOrdering.get(var7);
         boolean var9 = false;
         if (var8 != null) {
            String var10 = (String)var8.get(var3);
            if (var10 != null) {
               var9 = true;
               String var11 = var3 + " " + var7;
               if (!this.knownDeadlocks.contains(var11)) {
                  System.out.println(var3 + " synchronized after \n " + var7 + ", but in the past before\nAFTER\n" + getStackTraceForThread(var2) + "BEFORE\n" + var10);
                  this.knownDeadlocks.add(var11);
               }
            }
         }

         if (!var9 && !((Map)var4).containsKey(var7)) {
            if (var5 == null) {
               var5 = getStackTraceForThread(var2);
            }

            ((Map)var4).put(var7, var5);
         }
      }

   }

   private static String getStackTraceForThread(ThreadInfo var0) {
      StringBuilder var1 = (new StringBuilder()).append('"').append(var0.getThreadName()).append("\" Id=").append(var0.getThreadId()).append(' ').append(var0.getThreadState());
      if (var0.getLockName() != null) {
         var1.append(" on ").append(var0.getLockName());
      }

      if (var0.getLockOwnerName() != null) {
         var1.append(" owned by \"").append(var0.getLockOwnerName()).append("\" Id=").append(var0.getLockOwnerId());
      }

      if (var0.isSuspended()) {
         var1.append(" (suspended)");
      }

      if (var0.isInNative()) {
         var1.append(" (in native)");
      }

      var1.append('\n');
      StackTraceElement[] var2 = var0.getStackTrace();
      MonitorInfo[] var3 = var0.getLockedMonitors();
      boolean var4 = false;

      for(int var5 = 0; var5 < var2.length; ++var5) {
         StackTraceElement var6 = var2[var5];
         if (var4) {
            dumpStackTraceElement(var0, var1, var5, var6);
         }

         MonitorInfo[] var7 = var3;
         int var8 = var3.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            MonitorInfo var10 = var7[var9];
            if (var10.getLockedStackDepth() == var5) {
               if (!var4) {
                  dumpStackTraceElement(var0, var1, var5, var6);
                  var4 = true;
               }

               var1.append("\t-  locked ").append(var10);
               var1.append('\n');
            }
         }
      }

      return var1.toString();
   }

   private static void dumpStackTraceElement(ThreadInfo var0, StringBuilder var1, int var2, StackTraceElement var3) {
      var1.append('\t').append("at ").append(var3).append('\n');
      if (var2 == 0 && var0.getLockInfo() != null) {
         Thread.State var4 = var0.getThreadState();
         switch (var4) {
            case BLOCKED:
               var1.append("\t-  blocked on ").append(var0.getLockInfo()).append('\n');
               break;
            case WAITING:
               var1.append("\t-  waiting on ").append(var0.getLockInfo()).append('\n');
               break;
            case TIMED_WAITING:
               var1.append("\t-  waiting on ").append(var0.getLockInfo()).append('\n');
         }
      }

   }

   private static String getObjectName(MonitorInfo var0) {
      return var0.getClassName() + "@" + Integer.toHexString(var0.getIdentityHashCode());
   }
}

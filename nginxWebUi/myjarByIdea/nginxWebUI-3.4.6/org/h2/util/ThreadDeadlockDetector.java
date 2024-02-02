package org.h2.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.h2.engine.SysProperties;
import org.h2.mvstore.db.MVTable;

public class ThreadDeadlockDetector {
   private static final String INDENT = "    ";
   private static ThreadDeadlockDetector detector;
   private final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

   private ThreadDeadlockDetector() {
      Timer var1 = new Timer("ThreadDeadlockDetector", true);
      var1.schedule(new TimerTask() {
         public void run() {
            ThreadDeadlockDetector.this.checkForDeadlocks();
         }
      }, 10L, 10000L);
   }

   public static synchronized void init() {
      if (detector == null) {
         detector = new ThreadDeadlockDetector();
      }

   }

   void checkForDeadlocks() {
      long[] var1 = this.threadBean.findDeadlockedThreads();
      if (var1 != null) {
         dumpThreadsAndLocks("ThreadDeadlockDetector - deadlock found :", this.threadBean, var1, System.out);
      }
   }

   public static void dumpAllThreadsAndLocks(String var0) {
      dumpAllThreadsAndLocks(var0, System.out);
   }

   public static void dumpAllThreadsAndLocks(String var0, PrintStream var1) {
      ThreadMXBean var2 = ManagementFactory.getThreadMXBean();
      long[] var3 = var2.getAllThreadIds();
      dumpThreadsAndLocks(var0, var2, var3, var1);
   }

   private static void dumpThreadsAndLocks(String var0, ThreadMXBean var1, long[] var2, PrintStream var3) {
      StringWriter var4 = new StringWriter();
      PrintWriter var5 = new PrintWriter(var4);
      var5.println(var0);
      HashMap var6;
      HashMap var7;
      HashMap var8;
      if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
         var6 = MVTable.WAITING_FOR_LOCK.getSnapshotOfAllThreads();
         var7 = MVTable.EXCLUSIVE_LOCKS.getSnapshotOfAllThreads();
         var8 = MVTable.SHARED_LOCKS.getSnapshotOfAllThreads();
      } else {
         var6 = new HashMap();
         var7 = new HashMap();
         var8 = new HashMap();
      }

      ThreadInfo[] var9 = var1.getThreadInfo(var2, true, true);
      ThreadInfo[] var10 = var9;
      int var11 = var9.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         ThreadInfo var13 = var10[var12];
         printThreadInfo(var5, var13);
         printLockInfo(var5, var13.getLockedSynchronizers(), (String)var6.get(var13.getThreadId()), (ArrayList)var7.get(var13.getThreadId()), (ArrayList)var8.get(var13.getThreadId()));
      }

      var5.flush();
      var3.println(var4.getBuffer());
      var3.flush();
   }

   private static void printThreadInfo(PrintWriter var0, ThreadInfo var1) {
      printThread(var0, var1);
      StackTraceElement[] var2 = var1.getStackTrace();
      MonitorInfo[] var3 = var1.getLockedMonitors();

      for(int var4 = 0; var4 < var2.length; ++var4) {
         StackTraceElement var5 = var2[var4];
         var0.println("    at " + var5.toString());
         MonitorInfo[] var6 = var3;
         int var7 = var3.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            MonitorInfo var9 = var6[var8];
            if (var9.getLockedStackDepth() == var4) {
               var0.println("      - locked " + var9);
            }
         }
      }

      var0.println();
   }

   private static void printThread(PrintWriter var0, ThreadInfo var1) {
      var0.print("\"" + var1.getThreadName() + "\" Id=" + var1.getThreadId() + " in " + var1.getThreadState());
      if (var1.getLockName() != null) {
         var0.append(" on lock=").append(var1.getLockName());
      }

      if (var1.isSuspended()) {
         var0.append(" (suspended)");
      }

      if (var1.isInNative()) {
         var0.append(" (running in native)");
      }

      var0.println();
      if (var1.getLockOwnerName() != null) {
         var0.println("     owned by " + var1.getLockOwnerName() + " Id=" + var1.getLockOwnerId());
      }

   }

   private static void printLockInfo(PrintWriter var0, LockInfo[] var1, String var2, ArrayList<String> var3, ArrayList<String> var4) {
      var0.println("    Locked synchronizers: count = " + var1.length);
      LockInfo[] var5 = var1;
      int var6 = var1.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         LockInfo var8 = var5[var7];
         var0.println("      - " + var8);
      }

      if (var2 != null) {
         var0.println("    Waiting for table: " + var2);
      }

      Iterator var9;
      String var10;
      if (var3 != null) {
         var0.println("    Exclusive table locks: count = " + var3.size());
         var9 = var3.iterator();

         while(var9.hasNext()) {
            var10 = (String)var9.next();
            var0.println("      - " + var10);
         }
      }

      if (var4 != null) {
         var0.println("    Shared table locks: count = " + var4.size());
         var9 = var4.iterator();

         while(var9.hasNext()) {
            var10 = (String)var9.next();
            var0.println("      - " + var10);
         }
      }

      var0.println();
   }
}

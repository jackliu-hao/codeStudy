/*     */ package org.h2.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.management.LockInfo;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.MonitorInfo;
/*     */ import java.lang.management.ThreadInfo;
/*     */ import java.lang.management.ThreadMXBean;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.mvstore.db.MVTable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ThreadDeadlockDetector
/*     */ {
/*     */   private static final String INDENT = "    ";
/*     */   private static ThreadDeadlockDetector detector;
/*  36 */   private final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
/*     */ 
/*     */ 
/*     */   
/*     */   private ThreadDeadlockDetector() {
/*  41 */     Timer timer = new Timer("ThreadDeadlockDetector", true);
/*  42 */     timer.schedule(new TimerTask()
/*     */         {
/*     */           public void run() {
/*  45 */             ThreadDeadlockDetector.this.checkForDeadlocks();
/*     */           }
/*     */         },  10L, 10000L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void init() {
/*  54 */     if (detector == null) {
/*  55 */       detector = new ThreadDeadlockDetector();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void checkForDeadlocks() {
/*  64 */     long[] arrayOfLong = this.threadBean.findDeadlockedThreads();
/*  65 */     if (arrayOfLong == null) {
/*     */       return;
/*     */     }
/*  68 */     dumpThreadsAndLocks("ThreadDeadlockDetector - deadlock found :", this.threadBean, arrayOfLong, System.out);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void dumpAllThreadsAndLocks(String paramString) {
/*  78 */     dumpAllThreadsAndLocks(paramString, System.out);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void dumpAllThreadsAndLocks(String paramString, PrintStream paramPrintStream) {
/*  88 */     ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
/*  89 */     long[] arrayOfLong = threadMXBean.getAllThreadIds();
/*  90 */     dumpThreadsAndLocks(paramString, threadMXBean, arrayOfLong, paramPrintStream);
/*     */   }
/*     */   
/*     */   private static void dumpThreadsAndLocks(String paramString, ThreadMXBean paramThreadMXBean, long[] paramArrayOflong, PrintStream paramPrintStream) {
/*     */     HashMap<Object, Object> hashMap1, hashMap2, hashMap3;
/*  95 */     StringWriter stringWriter = new StringWriter();
/*  96 */     PrintWriter printWriter = new PrintWriter(stringWriter);
/*     */     
/*  98 */     printWriter.println(paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 103 */     if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
/*     */       
/* 105 */       hashMap1 = MVTable.WAITING_FOR_LOCK.getSnapshotOfAllThreads();
/*     */       
/* 107 */       hashMap2 = MVTable.EXCLUSIVE_LOCKS.getSnapshotOfAllThreads();
/*     */       
/* 109 */       hashMap3 = MVTable.SHARED_LOCKS.getSnapshotOfAllThreads();
/*     */     } else {
/* 111 */       hashMap1 = new HashMap<>();
/* 112 */       hashMap2 = new HashMap<>();
/* 113 */       hashMap3 = new HashMap<>();
/*     */     } 
/*     */     
/* 116 */     ThreadInfo[] arrayOfThreadInfo = paramThreadMXBean.getThreadInfo(paramArrayOflong, true, true);
/*     */     
/* 118 */     for (ThreadInfo threadInfo : arrayOfThreadInfo) {
/* 119 */       printThreadInfo(printWriter, threadInfo);
/* 120 */       printLockInfo(printWriter, threadInfo.getLockedSynchronizers(), (String)hashMap1
/* 121 */           .get(Long.valueOf(threadInfo.getThreadId())), (ArrayList<String>)hashMap2
/* 122 */           .get(Long.valueOf(threadInfo.getThreadId())), (ArrayList<String>)hashMap3
/* 123 */           .get(Long.valueOf(threadInfo.getThreadId())));
/*     */     } 
/*     */     
/* 126 */     printWriter.flush();
/*     */ 
/*     */     
/* 129 */     paramPrintStream.println(stringWriter.getBuffer());
/* 130 */     paramPrintStream.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   private static void printThreadInfo(PrintWriter paramPrintWriter, ThreadInfo paramThreadInfo) {
/* 135 */     printThread(paramPrintWriter, paramThreadInfo);
/*     */ 
/*     */     
/* 138 */     StackTraceElement[] arrayOfStackTraceElement = paramThreadInfo.getStackTrace();
/* 139 */     MonitorInfo[] arrayOfMonitorInfo = paramThreadInfo.getLockedMonitors();
/* 140 */     for (byte b = 0; b < arrayOfStackTraceElement.length; b++) {
/* 141 */       StackTraceElement stackTraceElement = arrayOfStackTraceElement[b];
/* 142 */       paramPrintWriter.println("    at " + stackTraceElement.toString());
/* 143 */       for (MonitorInfo monitorInfo : arrayOfMonitorInfo) {
/* 144 */         if (monitorInfo.getLockedStackDepth() == b) {
/* 145 */           paramPrintWriter.println("      - locked " + monitorInfo);
/*     */         }
/*     */       } 
/*     */     } 
/* 149 */     paramPrintWriter.println();
/*     */   }
/*     */   
/*     */   private static void printThread(PrintWriter paramPrintWriter, ThreadInfo paramThreadInfo) {
/* 153 */     paramPrintWriter.print("\"" + paramThreadInfo.getThreadName() + "\" Id=" + paramThreadInfo
/* 154 */         .getThreadId() + " in " + paramThreadInfo.getThreadState());
/* 155 */     if (paramThreadInfo.getLockName() != null) {
/* 156 */       paramPrintWriter.append(" on lock=").append(paramThreadInfo.getLockName());
/*     */     }
/* 158 */     if (paramThreadInfo.isSuspended()) {
/* 159 */       paramPrintWriter.append(" (suspended)");
/*     */     }
/* 161 */     if (paramThreadInfo.isInNative()) {
/* 162 */       paramPrintWriter.append(" (running in native)");
/*     */     }
/* 164 */     paramPrintWriter.println();
/* 165 */     if (paramThreadInfo.getLockOwnerName() != null) {
/* 166 */       paramPrintWriter.println("     owned by " + paramThreadInfo.getLockOwnerName() + " Id=" + paramThreadInfo
/* 167 */           .getLockOwnerId());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void printLockInfo(PrintWriter paramPrintWriter, LockInfo[] paramArrayOfLockInfo, String paramString, ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2) {
/* 175 */     paramPrintWriter.println("    Locked synchronizers: count = " + paramArrayOfLockInfo.length);
/* 176 */     for (LockInfo lockInfo : paramArrayOfLockInfo) {
/* 177 */       paramPrintWriter.println("      - " + lockInfo);
/*     */     }
/* 179 */     if (paramString != null) {
/* 180 */       paramPrintWriter.println("    Waiting for table: " + paramString);
/*     */     }
/* 182 */     if (paramArrayList1 != null) {
/* 183 */       paramPrintWriter.println("    Exclusive table locks: count = " + paramArrayList1.size());
/* 184 */       for (String str : paramArrayList1) {
/* 185 */         paramPrintWriter.println("      - " + str);
/*     */       }
/*     */     } 
/* 188 */     if (paramArrayList2 != null) {
/* 189 */       paramPrintWriter.println("    Shared table locks: count = " + paramArrayList2.size());
/* 190 */       for (String str : paramArrayList2) {
/* 191 */         paramPrintWriter.println("      - " + str);
/*     */       }
/*     */     } 
/* 194 */     paramPrintWriter.println();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\ThreadDeadlockDetector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
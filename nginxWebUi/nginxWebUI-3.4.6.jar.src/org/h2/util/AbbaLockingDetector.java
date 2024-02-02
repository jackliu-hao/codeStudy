/*     */ package org.h2.util;
/*     */ 
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.MonitorInfo;
/*     */ import java.lang.management.ThreadInfo;
/*     */ import java.lang.management.ThreadMXBean;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AbbaLockingDetector
/*     */   implements Runnable
/*     */ {
/*  25 */   private final int tickIntervalMs = 2;
/*     */   
/*     */   private volatile boolean stop;
/*     */   
/*  29 */   private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
/*     */ 
/*     */ 
/*     */   
/*     */   private Thread thread;
/*     */ 
/*     */   
/*  36 */   private final Map<String, Map<String, String>> lockOrdering = new WeakHashMap<>();
/*     */   
/*  38 */   private final Set<String> knownDeadlocks = new HashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbbaLockingDetector startCollecting() {
/*  46 */     this.thread = new Thread(this, "AbbaLockingDetector");
/*  47 */     this.thread.setDaemon(true);
/*  48 */     this.thread.start();
/*  49 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void reset() {
/*  56 */     this.lockOrdering.clear();
/*  57 */     this.knownDeadlocks.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbbaLockingDetector stopCollecting() {
/*  66 */     this.stop = true;
/*  67 */     if (this.thread != null) {
/*     */       try {
/*  69 */         this.thread.join();
/*  70 */       } catch (InterruptedException interruptedException) {}
/*     */ 
/*     */       
/*  73 */       this.thread = null;
/*     */     } 
/*  75 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/*  80 */     while (!this.stop) {
/*     */       try {
/*  82 */         tick();
/*  83 */       } catch (Throwable throwable) {
/*     */         break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void tick() {
/*     */     try {
/*  92 */       Thread.sleep(2L);
/*  93 */     } catch (InterruptedException interruptedException) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  98 */     ThreadInfo[] arrayOfThreadInfo = this.threadMXBean.dumpAllThreads(true, false);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 103 */     processThreadList(arrayOfThreadInfo);
/*     */   }
/*     */   
/*     */   private void processThreadList(ThreadInfo[] paramArrayOfThreadInfo) {
/* 107 */     ArrayList<String> arrayList = new ArrayList();
/* 108 */     for (ThreadInfo threadInfo : paramArrayOfThreadInfo) {
/* 109 */       arrayList.clear();
/* 110 */       generateOrdering(arrayList, threadInfo);
/* 111 */       if (arrayList.size() > 1) {
/* 112 */         markHigher(arrayList, threadInfo);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void generateOrdering(List<String> paramList, ThreadInfo paramThreadInfo) {
/* 122 */     MonitorInfo[] arrayOfMonitorInfo = paramThreadInfo.getLockedMonitors();
/* 123 */     Arrays.sort(arrayOfMonitorInfo, (paramMonitorInfo1, paramMonitorInfo2) -> paramMonitorInfo2.getLockedStackDepth() - paramMonitorInfo1.getLockedStackDepth());
/* 124 */     for (MonitorInfo monitorInfo : arrayOfMonitorInfo) {
/* 125 */       String str = getObjectName(monitorInfo);
/* 126 */       if (!str.equals("sun.misc.Launcher$AppClassLoader"))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 132 */         if (!paramList.contains(str)) {
/* 133 */           paramList.add(str);
/*     */         }
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private synchronized void markHigher(List<String> paramList, ThreadInfo paramThreadInfo) {
/* 140 */     String str1 = paramList.get(paramList.size() - 1);
/* 141 */     Map<Object, Object> map = (Map)this.lockOrdering.get(str1);
/* 142 */     if (map == null) {
/* 143 */       map = new WeakHashMap<>();
/* 144 */       this.lockOrdering.put(str1, map);
/*     */     } 
/* 146 */     String str2 = null;
/* 147 */     for (byte b = 0; b < paramList.size() - 1; b++) {
/* 148 */       String str = paramList.get(b);
/* 149 */       Map map1 = this.lockOrdering.get(str);
/* 150 */       boolean bool = false;
/* 151 */       if (map1 != null) {
/* 152 */         String str3 = (String)map1.get(str1);
/* 153 */         if (str3 != null) {
/* 154 */           bool = true;
/* 155 */           String str4 = str1 + " " + str;
/* 156 */           if (!this.knownDeadlocks.contains(str4)) {
/* 157 */             System.out.println(str1 + " synchronized after \n " + str + ", but in the past before\nAFTER\n" + 
/*     */                 
/* 159 */                 getStackTraceForThread(paramThreadInfo) + "BEFORE\n" + str3);
/*     */             
/* 161 */             this.knownDeadlocks.add(str4);
/*     */           } 
/*     */         } 
/*     */       } 
/* 165 */       if (!bool && !map.containsKey(str)) {
/* 166 */         if (str2 == null) {
/* 167 */           str2 = getStackTraceForThread(paramThreadInfo);
/*     */         }
/* 169 */         map.put(str, str2);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getStackTraceForThread(ThreadInfo paramThreadInfo) {
/* 182 */     StringBuilder stringBuilder = (new StringBuilder()).append('"').append(paramThreadInfo.getThreadName()).append("\" Id=").append(paramThreadInfo.getThreadId()).append(' ').append(paramThreadInfo.getThreadState());
/* 183 */     if (paramThreadInfo.getLockName() != null) {
/* 184 */       stringBuilder.append(" on ").append(paramThreadInfo.getLockName());
/*     */     }
/* 186 */     if (paramThreadInfo.getLockOwnerName() != null) {
/* 187 */       stringBuilder.append(" owned by \"").append(paramThreadInfo.getLockOwnerName())
/* 188 */         .append("\" Id=").append(paramThreadInfo.getLockOwnerId());
/*     */     }
/* 190 */     if (paramThreadInfo.isSuspended()) {
/* 191 */       stringBuilder.append(" (suspended)");
/*     */     }
/* 193 */     if (paramThreadInfo.isInNative()) {
/* 194 */       stringBuilder.append(" (in native)");
/*     */     }
/* 196 */     stringBuilder.append('\n');
/* 197 */     StackTraceElement[] arrayOfStackTraceElement = paramThreadInfo.getStackTrace();
/* 198 */     MonitorInfo[] arrayOfMonitorInfo = paramThreadInfo.getLockedMonitors();
/* 199 */     boolean bool = false;
/* 200 */     for (byte b = 0; b < arrayOfStackTraceElement.length; b++) {
/* 201 */       StackTraceElement stackTraceElement = arrayOfStackTraceElement[b];
/* 202 */       if (bool) {
/* 203 */         dumpStackTraceElement(paramThreadInfo, stringBuilder, b, stackTraceElement);
/*     */       }
/*     */       
/* 206 */       for (MonitorInfo monitorInfo : arrayOfMonitorInfo) {
/* 207 */         if (monitorInfo.getLockedStackDepth() == b) {
/*     */ 
/*     */ 
/*     */           
/* 211 */           if (!bool) {
/* 212 */             dumpStackTraceElement(paramThreadInfo, stringBuilder, b, stackTraceElement);
/* 213 */             bool = true;
/*     */           } 
/* 215 */           stringBuilder.append("\t-  locked ").append(monitorInfo);
/* 216 */           stringBuilder.append('\n');
/*     */         } 
/*     */       } 
/*     */     } 
/* 220 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   private static void dumpStackTraceElement(ThreadInfo paramThreadInfo, StringBuilder paramStringBuilder, int paramInt, StackTraceElement paramStackTraceElement) {
/* 225 */     paramStringBuilder.append('\t').append("at ").append(paramStackTraceElement)
/* 226 */       .append('\n');
/* 227 */     if (paramInt == 0 && paramThreadInfo.getLockInfo() != null) {
/* 228 */       Thread.State state = paramThreadInfo.getThreadState();
/* 229 */       switch (state) {
/*     */         case BLOCKED:
/* 231 */           paramStringBuilder.append("\t-  blocked on ")
/* 232 */             .append(paramThreadInfo.getLockInfo())
/* 233 */             .append('\n');
/*     */           break;
/*     */         case WAITING:
/* 236 */           paramStringBuilder.append("\t-  waiting on ")
/* 237 */             .append(paramThreadInfo.getLockInfo())
/* 238 */             .append('\n');
/*     */           break;
/*     */         case TIMED_WAITING:
/* 241 */           paramStringBuilder.append("\t-  waiting on ")
/* 242 */             .append(paramThreadInfo.getLockInfo())
/* 243 */             .append('\n');
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static String getObjectName(MonitorInfo paramMonitorInfo) {
/* 251 */     return paramMonitorInfo.getClassName() + "@" + 
/* 252 */       Integer.toHexString(paramMonitorInfo.getIdentityHashCode());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\AbbaLockingDetector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
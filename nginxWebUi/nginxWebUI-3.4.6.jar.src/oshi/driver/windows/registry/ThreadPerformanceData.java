/*     */ package oshi.driver.windows.registry;
/*     */ 
/*     */ import com.sun.jna.platform.win32.WinBase;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.windows.perfmon.ThreadInformation;
/*     */ import oshi.util.tuples.Pair;
/*     */ import oshi.util.tuples.Triplet;
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
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class ThreadPerformanceData
/*     */ {
/*     */   private static final String THREAD = "Thread";
/*     */   
/*     */   public static Map<Integer, PerfCounterBlock> buildThreadMapFromRegistry(Collection<Integer> pids) {
/*  65 */     Triplet<List<Map<ThreadInformation.ThreadPerformanceProperty, Object>>, Long, Long> threadData = HkeyPerformanceDataUtil.readPerfDataFromRegistry("Thread", ThreadInformation.ThreadPerformanceProperty.class);
/*  66 */     if (threadData == null) {
/*  67 */       return null;
/*     */     }
/*  69 */     List<Map<ThreadInformation.ThreadPerformanceProperty, Object>> threadInstanceMaps = (List<Map<ThreadInformation.ThreadPerformanceProperty, Object>>)threadData.getA();
/*  70 */     long perfTime100nSec = ((Long)threadData.getB()).longValue();
/*  71 */     long now = ((Long)threadData.getC()).longValue();
/*     */ 
/*     */     
/*  74 */     Map<Integer, PerfCounterBlock> threadMap = new HashMap<>();
/*     */     
/*  76 */     for (Map<ThreadInformation.ThreadPerformanceProperty, Object> threadInstanceMap : threadInstanceMaps) {
/*  77 */       int pid = ((Integer)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.IDPROCESS)).intValue();
/*  78 */       if ((pids == null || pids.contains(Integer.valueOf(pid))) && pid > 0) {
/*  79 */         int tid = ((Integer)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.IDTHREAD)).intValue();
/*  80 */         String name = (String)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.NAME);
/*  81 */         long upTime = (perfTime100nSec - ((Long)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.ELAPSEDTIME)).longValue()) / 10000L;
/*     */         
/*  83 */         if (upTime < 1L) {
/*  84 */           upTime = 1L;
/*     */         }
/*  86 */         long user = ((Long)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.PERCENTUSERTIME)).longValue() / 10000L;
/*     */ 
/*     */         
/*  89 */         long kernel = ((Long)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.PERCENTPRIVILEGEDTIME)).longValue() / 10000L;
/*  90 */         int priority = ((Integer)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.PRIORITYCURRENT)).intValue();
/*  91 */         int threadState = ((Integer)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.THREADSTATE)).intValue();
/*  92 */         long startAddr = ((Long)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.STARTADDRESS)).longValue();
/*     */         
/*  94 */         int contextSwitches = ((Integer)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.CONTEXTSWITCHESPERSEC)).intValue();
/*  95 */         threadMap.put(Integer.valueOf(tid), new PerfCounterBlock(name, tid, pid, now - upTime, user, kernel, priority, threadState, startAddr, contextSwitches));
/*     */       } 
/*     */     } 
/*     */     
/*  99 */     return threadMap;
/*     */   }
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
/*     */   public static Map<Integer, PerfCounterBlock> buildThreadMapFromPerfCounters(Collection<Integer> pids) {
/* 112 */     Map<Integer, PerfCounterBlock> threadMap = new HashMap<>();
/*     */     
/* 114 */     Pair<List<String>, Map<ThreadInformation.ThreadPerformanceProperty, List<Long>>> instanceValues = ThreadInformation.queryThreadCounters();
/* 115 */     long now = System.currentTimeMillis();
/* 116 */     List<String> instances = (List<String>)instanceValues.getA();
/* 117 */     Map<ThreadInformation.ThreadPerformanceProperty, List<Long>> valueMap = (Map<ThreadInformation.ThreadPerformanceProperty, List<Long>>)instanceValues.getB();
/* 118 */     List<Long> tidList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.IDTHREAD);
/* 119 */     List<Long> pidList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.IDPROCESS);
/* 120 */     List<Long> userList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.PERCENTUSERTIME);
/* 121 */     List<Long> kernelList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.PERCENTPRIVILEGEDTIME);
/* 122 */     List<Long> startTimeList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.ELAPSEDTIME);
/* 123 */     List<Long> priorityList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.PRIORITYCURRENT);
/* 124 */     List<Long> stateList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.THREADSTATE);
/* 125 */     List<Long> startAddrList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.STARTADDRESS);
/* 126 */     List<Long> contextSwitchesList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.CONTEXTSWITCHESPERSEC);
/*     */     
/* 128 */     int nameIndex = 0;
/* 129 */     for (int inst = 0; inst < instances.size(); inst++) {
/* 130 */       int pid = ((Long)pidList.get(inst)).intValue();
/* 131 */       if (pids == null || pids.contains(Integer.valueOf(pid))) {
/* 132 */         int tid = ((Long)tidList.get(inst)).intValue();
/* 133 */         String name = Integer.toString(nameIndex++);
/* 134 */         long startTime = ((Long)startTimeList.get(inst)).longValue();
/* 135 */         startTime = WinBase.FILETIME.filetimeToDate((int)(startTime >> 32L), (int)(startTime & 0xFFFFFFFFL)).getTime();
/* 136 */         if (startTime > now) {
/* 137 */           startTime = now - 1L;
/*     */         }
/* 139 */         long user = ((Long)userList.get(inst)).longValue() / 10000L;
/* 140 */         long kernel = ((Long)kernelList.get(inst)).longValue() / 10000L;
/* 141 */         int priority = ((Long)priorityList.get(inst)).intValue();
/* 142 */         int threadState = ((Long)stateList.get(inst)).intValue();
/* 143 */         long startAddr = ((Long)startAddrList.get(inst)).longValue();
/* 144 */         int contextSwitches = ((Long)contextSwitchesList.get(inst)).intValue();
/*     */ 
/*     */ 
/*     */         
/* 148 */         threadMap.put(Integer.valueOf(tid), new PerfCounterBlock(name, tid, pid, startTime, user, kernel, priority, threadState, startAddr, contextSwitches));
/*     */       } 
/*     */     } 
/*     */     
/* 152 */     return threadMap;
/*     */   }
/*     */ 
/*     */   
/*     */   @Immutable
/*     */   public static class PerfCounterBlock
/*     */   {
/*     */     private final String name;
/*     */     
/*     */     private final int threadID;
/*     */     
/*     */     private final int owningProcessID;
/*     */     private final long startTime;
/*     */     private final long userTime;
/*     */     private final long kernelTime;
/*     */     private final int priority;
/*     */     private final int threadState;
/*     */     private final long startAddress;
/*     */     private final int contextSwitches;
/*     */     
/*     */     public PerfCounterBlock(String name, int threadID, int owningProcessID, long startTime, long userTime, long kernelTime, int priority, int threadState, long startAddress, int contextSwitches) {
/* 173 */       this.name = name;
/* 174 */       this.threadID = threadID;
/* 175 */       this.owningProcessID = owningProcessID;
/* 176 */       this.startTime = startTime;
/* 177 */       this.userTime = userTime;
/* 178 */       this.kernelTime = kernelTime;
/* 179 */       this.priority = priority;
/* 180 */       this.threadState = threadState;
/* 181 */       this.startAddress = startAddress;
/* 182 */       this.contextSwitches = contextSwitches;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getName() {
/* 189 */       return this.name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getThreadID() {
/* 196 */       return this.threadID;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getOwningProcessID() {
/* 203 */       return this.owningProcessID;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getStartTime() {
/* 210 */       return this.startTime;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getUserTime() {
/* 217 */       return this.userTime;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getKernelTime() {
/* 224 */       return this.kernelTime;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getPriority() {
/* 231 */       return this.priority;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getThreadState() {
/* 238 */       return this.threadState;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getStartAddress() {
/* 245 */       return this.startAddress;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getContextSwitches() {
/* 252 */       return this.contextSwitches;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\registry\ThreadPerformanceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package oshi.driver.windows.registry;
/*     */ 
/*     */ import com.sun.jna.platform.win32.WinBase;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.windows.perfmon.ProcessInformation;
/*     */ import oshi.util.GlobalConfig;
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
/*     */ @ThreadSafe
/*     */ public final class ProcessPerformanceData
/*     */ {
/*     */   private static final String PROCESS = "Process";
/*     */   public static final String WIN_HKEY_PERFDATA = "oshi.os.windows.hkeyperfdata";
/*  50 */   private static final boolean PERFDATA = GlobalConfig.get("oshi.os.windows.hkeyperfdata", true);
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
/*     */   public static Map<Integer, PerfCounterBlock> buildProcessMapFromRegistry(Collection<Integer> pids) {
/*  67 */     Triplet<List<Map<ProcessInformation.ProcessPerformanceProperty, Object>>, Long, Long> processData = null;
/*  68 */     if (PERFDATA) {
/*  69 */       processData = HkeyPerformanceDataUtil.readPerfDataFromRegistry("Process", ProcessInformation.ProcessPerformanceProperty.class);
/*     */     }
/*  71 */     if (processData == null) {
/*  72 */       return null;
/*     */     }
/*  74 */     List<Map<ProcessInformation.ProcessPerformanceProperty, Object>> processInstanceMaps = (List<Map<ProcessInformation.ProcessPerformanceProperty, Object>>)processData.getA();
/*  75 */     long now = ((Long)processData.getC()).longValue();
/*     */ 
/*     */     
/*  78 */     Map<Integer, PerfCounterBlock> processMap = new HashMap<>();
/*     */     
/*  80 */     for (Map<ProcessInformation.ProcessPerformanceProperty, Object> processInstanceMap : processInstanceMaps) {
/*  81 */       int pid = ((Integer)processInstanceMap.get(ProcessInformation.ProcessPerformanceProperty.PROCESSID)).intValue();
/*  82 */       String name = (String)processInstanceMap.get(ProcessInformation.ProcessPerformanceProperty.NAME);
/*  83 */       if ((pids == null || pids.contains(Integer.valueOf(pid))) && !"_Total".equals(name)) {
/*     */ 
/*     */         
/*  86 */         long ctime = ((Long)processInstanceMap.get(ProcessInformation.ProcessPerformanceProperty.CREATIONDATE)).longValue();
/*  87 */         if (ctime > now) {
/*  88 */           ctime = WinBase.FILETIME.filetimeToDate((int)(ctime >> 32L), (int)(ctime & 0xFFFFFFFFL)).getTime();
/*     */         }
/*  90 */         long upTime = now - ctime;
/*  91 */         if (upTime < 1L) {
/*  92 */           upTime = 1L;
/*     */         }
/*  94 */         processMap.put(Integer.valueOf(pid), new PerfCounterBlock(name, ((Integer)processInstanceMap
/*     */               
/*  96 */               .get(ProcessInformation.ProcessPerformanceProperty.PARENTPROCESSID)).intValue(), ((Integer)processInstanceMap
/*  97 */               .get(ProcessInformation.ProcessPerformanceProperty.PRIORITY)).intValue(), ((Long)processInstanceMap
/*  98 */               .get(ProcessInformation.ProcessPerformanceProperty.PRIVATEPAGECOUNT)).longValue(), ctime, upTime, ((Long)processInstanceMap
/*  99 */               .get(ProcessInformation.ProcessPerformanceProperty.READTRANSFERCOUNT)).longValue(), ((Long)processInstanceMap
/* 100 */               .get(ProcessInformation.ProcessPerformanceProperty.WRITETRANSFERCOUNT)).longValue(), ((Integer)processInstanceMap
/* 101 */               .get(ProcessInformation.ProcessPerformanceProperty.PAGEFAULTSPERSEC)).intValue()));
/*     */       } 
/*     */     } 
/* 104 */     return processMap;
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
/*     */   public static Map<Integer, PerfCounterBlock> buildProcessMapFromPerfCounters(Collection<Integer> pids) {
/* 117 */     Map<Integer, PerfCounterBlock> processMap = new HashMap<>();
/*     */     
/* 119 */     Pair<List<String>, Map<ProcessInformation.ProcessPerformanceProperty, List<Long>>> instanceValues = ProcessInformation.queryProcessCounters();
/* 120 */     long now = System.currentTimeMillis();
/* 121 */     List<String> instances = (List<String>)instanceValues.getA();
/* 122 */     Map<ProcessInformation.ProcessPerformanceProperty, List<Long>> valueMap = (Map<ProcessInformation.ProcessPerformanceProperty, List<Long>>)instanceValues.getB();
/* 123 */     List<Long> pidList = valueMap.get(ProcessInformation.ProcessPerformanceProperty.PROCESSID);
/* 124 */     List<Long> ppidList = valueMap.get(ProcessInformation.ProcessPerformanceProperty.PARENTPROCESSID);
/* 125 */     List<Long> priorityList = valueMap.get(ProcessInformation.ProcessPerformanceProperty.PRIORITY);
/* 126 */     List<Long> ioReadList = valueMap.get(ProcessInformation.ProcessPerformanceProperty.READTRANSFERCOUNT);
/* 127 */     List<Long> ioWriteList = valueMap.get(ProcessInformation.ProcessPerformanceProperty.WRITETRANSFERCOUNT);
/* 128 */     List<Long> workingSetSizeList = valueMap.get(ProcessInformation.ProcessPerformanceProperty.PRIVATEPAGECOUNT);
/* 129 */     List<Long> creationTimeList = valueMap.get(ProcessInformation.ProcessPerformanceProperty.CREATIONDATE);
/* 130 */     List<Long> pageFaultsList = valueMap.get(ProcessInformation.ProcessPerformanceProperty.PAGEFAULTSPERSEC);
/*     */     
/* 132 */     for (int inst = 0; inst < instances.size(); inst++) {
/* 133 */       int pid = ((Long)pidList.get(inst)).intValue();
/* 134 */       if (pids == null || pids.contains(Integer.valueOf(pid))) {
/*     */ 
/*     */         
/* 137 */         long ctime = ((Long)creationTimeList.get(inst)).longValue();
/* 138 */         if (ctime > now) {
/* 139 */           ctime = WinBase.FILETIME.filetimeToDate((int)(ctime >> 32L), (int)(ctime & 0xFFFFFFFFL)).getTime();
/*     */         }
/* 141 */         long upTime = now - ctime;
/* 142 */         if (upTime < 1L) {
/* 143 */           upTime = 1L;
/*     */         }
/* 145 */         processMap.put(Integer.valueOf(pid), new PerfCounterBlock(instances
/* 146 */               .get(inst), ((Long)ppidList.get(inst)).intValue(), ((Long)priorityList
/* 147 */               .get(inst)).intValue(), ((Long)workingSetSizeList.get(inst)).longValue(), ctime, upTime, ((Long)ioReadList
/* 148 */               .get(inst)).longValue(), ((Long)ioWriteList.get(inst)).longValue(), ((Long)pageFaultsList.get(inst)).intValue()));
/*     */       } 
/*     */     } 
/* 151 */     return processMap;
/*     */   }
/*     */ 
/*     */   
/*     */   @Immutable
/*     */   public static class PerfCounterBlock
/*     */   {
/*     */     private final String name;
/*     */     
/*     */     private final int parentProcessID;
/*     */     
/*     */     private final int priority;
/*     */     private final long residentSetSize;
/*     */     private final long startTime;
/*     */     private final long upTime;
/*     */     private final long bytesRead;
/*     */     private final long bytesWritten;
/*     */     private final int pageFaults;
/*     */     
/*     */     public PerfCounterBlock(String name, int parentProcessID, int priority, long residentSetSize, long startTime, long upTime, long bytesRead, long bytesWritten, int pageFaults) {
/* 171 */       this.name = name;
/* 172 */       this.parentProcessID = parentProcessID;
/* 173 */       this.priority = priority;
/* 174 */       this.residentSetSize = residentSetSize;
/* 175 */       this.startTime = startTime;
/* 176 */       this.upTime = upTime;
/* 177 */       this.bytesRead = bytesRead;
/* 178 */       this.bytesWritten = bytesWritten;
/* 179 */       this.pageFaults = pageFaults;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getName() {
/* 186 */       return this.name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getParentProcessID() {
/* 193 */       return this.parentProcessID;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getPriority() {
/* 200 */       return this.priority;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getResidentSetSize() {
/* 207 */       return this.residentSetSize;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getStartTime() {
/* 214 */       return this.startTime;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getUpTime() {
/* 221 */       return this.upTime;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getBytesRead() {
/* 228 */       return this.bytesRead;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getBytesWritten() {
/* 235 */       return this.bytesWritten;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getPageFaults() {
/* 242 */       return this.pageFaults;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\registry\ProcessPerformanceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
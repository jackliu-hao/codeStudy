/*     */ package oshi.driver.windows.registry;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import com.sun.jna.platform.win32.Kernel32;
/*     */ import com.sun.jna.platform.win32.VersionHelpers;
/*     */ import com.sun.jna.platform.win32.Wtsapi32;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.windows.wmi.Win32Process;
/*     */ import oshi.util.platform.windows.WmiUtil;
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
/*     */ public final class ProcessWtsData
/*     */ {
/*  55 */   private static final Logger LOG = LoggerFactory.getLogger(ProcessWtsData.class);
/*     */   
/*  57 */   private static final boolean IS_WINDOWS7_OR_GREATER = VersionHelpers.IsWindows7OrGreater();
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
/*     */   public static Map<Integer, WtsInfo> queryProcessWtsMap(Collection<Integer> pids) {
/*  72 */     if (IS_WINDOWS7_OR_GREATER)
/*     */     {
/*  74 */       return queryProcessWtsMapFromWTS(pids);
/*     */     }
/*     */ 
/*     */     
/*  78 */     return queryProcessWtsMapFromPerfMon(pids);
/*     */   }
/*     */   
/*     */   private static Map<Integer, WtsInfo> queryProcessWtsMapFromWTS(Collection<Integer> pids) {
/*  82 */     Map<Integer, WtsInfo> wtsMap = new HashMap<>();
/*  83 */     IntByReference pCount = new IntByReference(0);
/*  84 */     PointerByReference ppProcessInfo = new PointerByReference();
/*  85 */     if (!Wtsapi32.INSTANCE.WTSEnumerateProcessesEx(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, new IntByReference(1), -2, ppProcessInfo, pCount)) {
/*     */ 
/*     */       
/*  88 */       LOG.error("Failed to enumerate Processes. Error code: {}", Integer.valueOf(Kernel32.INSTANCE.GetLastError()));
/*  89 */       return wtsMap;
/*     */     } 
/*     */     
/*  92 */     Pointer pProcessInfo = ppProcessInfo.getValue();
/*  93 */     Wtsapi32.WTS_PROCESS_INFO_EX processInfoRef = new Wtsapi32.WTS_PROCESS_INFO_EX(pProcessInfo);
/*  94 */     Wtsapi32.WTS_PROCESS_INFO_EX[] processInfo = (Wtsapi32.WTS_PROCESS_INFO_EX[])processInfoRef.toArray(pCount.getValue());
/*  95 */     for (int i = 0; i < processInfo.length; i++) {
/*  96 */       if (pids == null || pids.contains(Integer.valueOf((processInfo[i]).ProcessId))) {
/*  97 */         wtsMap.put(Integer.valueOf((processInfo[i]).ProcessId), new WtsInfo((processInfo[i]).pProcessName, "", (processInfo[i]).NumberOfThreads, (processInfo[i]).PagefileUsage & 0xFFFFFFFFL, (processInfo[i]).KernelTime
/*     */ 
/*     */               
/* 100 */               .getValue() / 10000L, (processInfo[i]).UserTime
/* 101 */               .getValue() / 10000L, (processInfo[i]).HandleCount));
/*     */       }
/*     */     } 
/*     */     
/* 105 */     if (!Wtsapi32.INSTANCE.WTSFreeMemoryEx(1, pProcessInfo, pCount.getValue())) {
/* 106 */       LOG.warn("Failed to Free Memory for Processes. Error code: {}", Integer.valueOf(Kernel32.INSTANCE.GetLastError()));
/*     */     }
/* 108 */     return wtsMap;
/*     */   }
/*     */   
/*     */   private static Map<Integer, WtsInfo> queryProcessWtsMapFromPerfMon(Collection<Integer> pids) {
/* 112 */     Map<Integer, WtsInfo> wtsMap = new HashMap<>();
/* 113 */     WbemcliUtil.WmiResult<Win32Process.ProcessXPProperty> processWmiResult = Win32Process.queryProcesses(pids);
/* 114 */     for (int i = 0; i < processWmiResult.getResultCount(); i++) {
/* 115 */       wtsMap.put(Integer.valueOf(WmiUtil.getUint32(processWmiResult, (Enum)Win32Process.ProcessXPProperty.PROCESSID, i)), new WtsInfo(
/* 116 */             WmiUtil.getString(processWmiResult, (Enum)Win32Process.ProcessXPProperty.NAME, i), 
/* 117 */             WmiUtil.getString(processWmiResult, (Enum)Win32Process.ProcessXPProperty.EXECUTABLEPATH, i), 
/* 118 */             WmiUtil.getUint32(processWmiResult, (Enum)Win32Process.ProcessXPProperty.THREADCOUNT, i), 1024L * (
/*     */             
/* 120 */             WmiUtil.getUint32(processWmiResult, (Enum)Win32Process.ProcessXPProperty.PAGEFILEUSAGE, i) & 0xFFFFFFFFL), 
/* 121 */             WmiUtil.getUint64(processWmiResult, (Enum)Win32Process.ProcessXPProperty.KERNELMODETIME, i) / 10000L, 
/* 122 */             WmiUtil.getUint64(processWmiResult, (Enum)Win32Process.ProcessXPProperty.USERMODETIME, i) / 10000L, 
/* 123 */             WmiUtil.getUint32(processWmiResult, (Enum)Win32Process.ProcessXPProperty.HANDLECOUNT, i)));
/*     */     }
/* 125 */     return wtsMap;
/*     */   }
/*     */ 
/*     */   
/*     */   @Immutable
/*     */   public static class WtsInfo
/*     */   {
/*     */     private final String name;
/*     */     
/*     */     private final String path;
/*     */     
/*     */     private final int threadCount;
/*     */     private final long virtualSize;
/*     */     private final long kernelTime;
/*     */     private final long userTime;
/*     */     private final long openFiles;
/*     */     
/*     */     public WtsInfo(String name, String path, int threadCount, long virtualSize, long kernelTime, long userTime, long openFiles) {
/* 143 */       this.name = name;
/* 144 */       this.path = path;
/* 145 */       this.threadCount = threadCount;
/* 146 */       this.virtualSize = virtualSize;
/* 147 */       this.kernelTime = kernelTime;
/* 148 */       this.userTime = userTime;
/* 149 */       this.openFiles = openFiles;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getName() {
/* 156 */       return this.name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getPath() {
/* 163 */       return this.path;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getThreadCount() {
/* 170 */       return this.threadCount;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getVirtualSize() {
/* 177 */       return this.virtualSize;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getKernelTime() {
/* 184 */       return this.kernelTime;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getUserTime() {
/* 191 */       return this.userTime;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getOpenFiles() {
/* 198 */       return this.openFiles;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\registry\ProcessWtsData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
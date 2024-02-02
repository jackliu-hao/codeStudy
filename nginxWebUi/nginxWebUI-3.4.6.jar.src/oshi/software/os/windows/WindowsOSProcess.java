/*     */ package oshi.software.os.windows;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.platform.win32.Advapi32;
/*     */ import com.sun.jna.platform.win32.Advapi32Util;
/*     */ import com.sun.jna.platform.win32.BaseTSD;
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import com.sun.jna.platform.win32.Kernel32;
/*     */ import com.sun.jna.platform.win32.Kernel32Util;
/*     */ import com.sun.jna.platform.win32.VersionHelpers;
/*     */ import com.sun.jna.platform.win32.Win32Exception;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.io.File;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.windows.registry.ProcessPerformanceData;
/*     */ import oshi.driver.windows.registry.ProcessWtsData;
/*     */ import oshi.driver.windows.registry.ThreadPerformanceData;
/*     */ import oshi.driver.windows.wmi.Win32Process;
/*     */ import oshi.driver.windows.wmi.Win32ProcessCached;
/*     */ import oshi.software.common.AbstractOSProcess;
/*     */ import oshi.software.os.OSProcess;
/*     */ import oshi.software.os.OSThread;
/*     */ import oshi.util.GlobalConfig;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.platform.windows.WmiUtil;
/*     */ import oshi.util.tuples.Pair;
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
/*     */ public class WindowsOSProcess
/*     */   extends AbstractOSProcess
/*     */ {
/*  76 */   private static final Logger LOG = LoggerFactory.getLogger(WindowsOSProcess.class);
/*     */   
/*     */   public static final String OSHI_OS_WINDOWS_COMMANDLINE_BATCH = "oshi.os.windows.commandline.batch";
/*     */   
/*  80 */   private static final boolean USE_BATCH_COMMANDLINE = GlobalConfig.get("oshi.os.windows.commandline.batch", false);
/*     */   
/*  82 */   private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
/*  83 */   private static final boolean IS_WINDOWS7_OR_GREATER = VersionHelpers.IsWindows7OrGreater();
/*     */   
/*  85 */   private Supplier<Pair<String, String>> userInfo = Memoizer.memoize(this::queryUserInfo);
/*  86 */   private Supplier<Pair<String, String>> groupInfo = Memoizer.memoize(this::queryGroupInfo);
/*  87 */   private Supplier<String> commandLine = Memoizer.memoize(this::queryCommandLine);
/*     */   
/*     */   private String name;
/*     */   private String path;
/*     */   private String currentWorkingDirectory;
/*  92 */   private OSProcess.State state = OSProcess.State.INVALID;
/*     */   
/*     */   private int parentProcessID;
/*     */   private int threadCount;
/*     */   private int priority;
/*     */   private long virtualSize;
/*     */   private long residentSetSize;
/*     */   private long kernelTime;
/*     */   private long userTime;
/*     */   private long startTime;
/*     */   private long upTime;
/*     */   private long bytesRead;
/*     */   private long bytesWritten;
/*     */   private long openFiles;
/*     */   private int bitness;
/*     */   private long pageFaults;
/*     */   
/*     */   public WindowsOSProcess(int pid, WindowsOperatingSystem os, Map<Integer, ProcessPerformanceData.PerfCounterBlock> processMap, Map<Integer, ProcessWtsData.WtsInfo> processWtsMap) {
/* 110 */     super(pid);
/*     */     
/* 112 */     if (pid == os.getProcessId()) {
/* 113 */       String cwd = (new File(".")).getAbsolutePath();
/*     */       
/* 115 */       this.currentWorkingDirectory = cwd.isEmpty() ? "" : cwd.substring(0, cwd.length() - 1);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 120 */     this.state = OSProcess.State.RUNNING;
/*     */     
/* 122 */     this.bitness = os.getBitness();
/* 123 */     updateAttributes(processMap.get(Integer.valueOf(pid)), processWtsMap.get(Integer.valueOf(pid)));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 128 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPath() {
/* 133 */     return this.path;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCommandLine() {
/* 138 */     return this.commandLine.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCurrentWorkingDirectory() {
/* 143 */     return this.currentWorkingDirectory;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUser() {
/* 148 */     return (String)((Pair)this.userInfo.get()).getA();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUserID() {
/* 153 */     return (String)((Pair)this.userInfo.get()).getB();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getGroup() {
/* 158 */     return (String)((Pair)this.groupInfo.get()).getA();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getGroupID() {
/* 163 */     return (String)((Pair)this.groupInfo.get()).getB();
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess.State getState() {
/* 168 */     return this.state;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getParentProcessID() {
/* 173 */     return this.parentProcessID;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getThreadCount() {
/* 178 */     return this.threadCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPriority() {
/* 183 */     return this.priority;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getVirtualSize() {
/* 188 */     return this.virtualSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getResidentSetSize() {
/* 193 */     return this.residentSetSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getKernelTime() {
/* 198 */     return this.kernelTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUserTime() {
/* 203 */     return this.userTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUpTime() {
/* 208 */     return this.upTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartTime() {
/* 213 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesRead() {
/* 218 */     return this.bytesRead;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesWritten() {
/* 223 */     return this.bytesWritten;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOpenFiles() {
/* 228 */     return this.openFiles;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBitness() {
/* 233 */     return this.bitness;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getAffinityMask() {
/* 238 */     WinNT.HANDLE pHandle = Kernel32.INSTANCE.OpenProcess(1024, false, getProcessID());
/* 239 */     if (pHandle != null) {
/*     */       try {
/* 241 */         BaseTSD.ULONG_PTRByReference processAffinity = new BaseTSD.ULONG_PTRByReference();
/* 242 */         BaseTSD.ULONG_PTRByReference systemAffinity = new BaseTSD.ULONG_PTRByReference();
/* 243 */         if (Kernel32.INSTANCE.GetProcessAffinityMask(pHandle, processAffinity, systemAffinity)) {
/* 244 */           return Pointer.nativeValue(processAffinity.getValue().toPointer());
/*     */         }
/*     */       } finally {
/* 247 */         Kernel32.INSTANCE.CloseHandle(pHandle);
/*     */       } 
/* 249 */       Kernel32.INSTANCE.CloseHandle(pHandle);
/*     */     } 
/* 251 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMinorFaults() {
/* 256 */     return this.pageFaults;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<OSThread> getThreadDetails() {
/* 263 */     Map<Integer, ThreadPerformanceData.PerfCounterBlock> threads = ThreadPerformanceData.buildThreadMapFromRegistry(Collections.singleton(Integer.valueOf(getProcessID())));
/*     */     
/* 265 */     if (threads != null) {
/* 266 */       threads = ThreadPerformanceData.buildThreadMapFromPerfCounters(Collections.singleton(Integer.valueOf(getProcessID())));
/*     */     }
/* 268 */     if (threads == null) {
/* 269 */       return Collections.emptyList();
/*     */     }
/* 271 */     return Collections.unmodifiableList((List<? extends OSThread>)threads.entrySet().stream()
/* 272 */         .map(entry -> new WindowsOSThread(getProcessID(), ((Integer)entry.getKey()).intValue(), this.name, (ThreadPerformanceData.PerfCounterBlock)entry.getValue()))
/* 273 */         .collect(Collectors.toList()));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 278 */     Set<Integer> pids = Collections.singleton(Integer.valueOf(getProcessID()));
/*     */     
/* 280 */     Map<Integer, ProcessPerformanceData.PerfCounterBlock> pcb = ProcessPerformanceData.buildProcessMapFromRegistry(null);
/*     */     
/* 282 */     if (pcb == null) {
/* 283 */       pcb = ProcessPerformanceData.buildProcessMapFromPerfCounters(pids);
/*     */     }
/* 285 */     Map<Integer, ProcessWtsData.WtsInfo> wts = ProcessWtsData.queryProcessWtsMap(pids);
/* 286 */     return updateAttributes(pcb.get(Integer.valueOf(getProcessID())), wts.get(Integer.valueOf(getProcessID())));
/*     */   }
/*     */   
/*     */   private boolean updateAttributes(ProcessPerformanceData.PerfCounterBlock pcb, ProcessWtsData.WtsInfo wts) {
/* 290 */     this.name = pcb.getName();
/* 291 */     this.path = wts.getPath();
/* 292 */     this.parentProcessID = pcb.getParentProcessID();
/* 293 */     this.threadCount = wts.getThreadCount();
/* 294 */     this.priority = pcb.getPriority();
/* 295 */     this.virtualSize = wts.getVirtualSize();
/* 296 */     this.residentSetSize = pcb.getResidentSetSize();
/* 297 */     this.kernelTime = wts.getKernelTime();
/* 298 */     this.userTime = wts.getUserTime();
/* 299 */     this.startTime = pcb.getStartTime();
/* 300 */     this.upTime = pcb.getUpTime();
/* 301 */     this.bytesRead = pcb.getBytesRead();
/* 302 */     this.bytesWritten = pcb.getBytesWritten();
/* 303 */     this.openFiles = wts.getOpenFiles();
/* 304 */     this.pageFaults = pcb.getPageFaults();
/*     */ 
/*     */ 
/*     */     
/* 308 */     WinNT.HANDLE pHandle = Kernel32.INSTANCE.OpenProcess(1024, false, getProcessID());
/* 309 */     if (pHandle != null) {
/*     */       
/*     */       try {
/* 312 */         if (IS_VISTA_OR_GREATER && this.bitness == 64) {
/* 313 */           IntByReference wow64 = new IntByReference(0);
/* 314 */           if (Kernel32.INSTANCE.IsWow64Process(pHandle, wow64) && wow64.getValue() > 0) {
/* 315 */             this.bitness = 32;
/*     */           }
/*     */         } 
/*     */         
/* 319 */         WinNT.HANDLEByReference phToken = new WinNT.HANDLEByReference();
/*     */         try {
/* 321 */           if (IS_WINDOWS7_OR_GREATER) {
/* 322 */             this.path = Kernel32Util.QueryFullProcessImageName(pHandle, 0);
/*     */           }
/* 324 */         } catch (Win32Exception e) {
/* 325 */           this.state = OSProcess.State.INVALID;
/*     */         } finally {
/* 327 */           WinNT.HANDLE token = phToken.getValue();
/* 328 */           if (token != null) {
/* 329 */             Kernel32.INSTANCE.CloseHandle(token);
/*     */           }
/*     */         } 
/*     */       } finally {
/* 333 */         Kernel32.INSTANCE.CloseHandle(pHandle);
/*     */       } 
/*     */     }
/*     */     
/* 337 */     return !this.state.equals(OSProcess.State.INVALID);
/*     */   }
/*     */ 
/*     */   
/*     */   private String queryCommandLine() {
/* 342 */     if (USE_BATCH_COMMANDLINE) {
/* 343 */       return Win32ProcessCached.getInstance().getCommandLine(getProcessID(), getStartTime());
/*     */     }
/*     */ 
/*     */     
/* 347 */     WbemcliUtil.WmiResult<Win32Process.CommandLineProperty> commandLineProcs = Win32Process.queryCommandLines(Collections.singleton(Integer.valueOf(getProcessID())));
/* 348 */     if (commandLineProcs.getResultCount() > 0) {
/* 349 */       return WmiUtil.getString(commandLineProcs, (Enum)Win32Process.CommandLineProperty.COMMANDLINE, 0);
/*     */     }
/* 351 */     return "unknown";
/*     */   }
/*     */   
/*     */   private Pair<String, String> queryUserInfo() {
/* 355 */     Pair<String, String> pair = null;
/* 356 */     WinNT.HANDLE pHandle = Kernel32.INSTANCE.OpenProcess(1024, false, getProcessID());
/* 357 */     if (pHandle != null) {
/* 358 */       WinNT.HANDLEByReference phToken = new WinNT.HANDLEByReference();
/* 359 */       if (Advapi32.INSTANCE.OpenProcessToken(pHandle, 10, phToken)) {
/* 360 */         Advapi32Util.Account account = Advapi32Util.getTokenAccount(phToken.getValue());
/* 361 */         pair = new Pair(account.name, account.sidString);
/*     */       } else {
/* 363 */         int error = Kernel32.INSTANCE.GetLastError();
/*     */         
/* 365 */         if (error != 5) {
/* 366 */           LOG.error("Failed to get process token for process {}: {}", Integer.valueOf(getProcessID()), 
/* 367 */               Integer.valueOf(Kernel32.INSTANCE.GetLastError()));
/*     */         }
/*     */       } 
/* 370 */       WinNT.HANDLE token = phToken.getValue();
/* 371 */       if (token != null) {
/* 372 */         Kernel32.INSTANCE.CloseHandle(token);
/*     */       }
/* 374 */       Kernel32.INSTANCE.CloseHandle(pHandle);
/*     */     } 
/* 376 */     if (pair == null) {
/* 377 */       return new Pair("unknown", "unknown");
/*     */     }
/* 379 */     return pair;
/*     */   }
/*     */   
/*     */   private Pair<String, String> queryGroupInfo() {
/* 383 */     Pair<String, String> pair = null;
/* 384 */     WinNT.HANDLE pHandle = Kernel32.INSTANCE.OpenProcess(1024, false, getProcessID());
/* 385 */     if (pHandle != null) {
/* 386 */       WinNT.HANDLEByReference phToken = new WinNT.HANDLEByReference();
/* 387 */       if (Advapi32.INSTANCE.OpenProcessToken(pHandle, 10, phToken)) {
/* 388 */         Advapi32Util.Account account = Advapi32Util.getTokenPrimaryGroup(phToken.getValue());
/* 389 */         pair = new Pair(account.name, account.sidString);
/*     */       } else {
/* 391 */         int error = Kernel32.INSTANCE.GetLastError();
/*     */         
/* 393 */         if (error != 5) {
/* 394 */           LOG.error("Failed to get process token for process {}: {}", Integer.valueOf(getProcessID()), 
/* 395 */               Integer.valueOf(Kernel32.INSTANCE.GetLastError()));
/*     */         }
/*     */       } 
/* 398 */       WinNT.HANDLE token = phToken.getValue();
/* 399 */       if (token != null) {
/* 400 */         Kernel32.INSTANCE.CloseHandle(token);
/*     */       }
/* 402 */       Kernel32.INSTANCE.CloseHandle(pHandle);
/*     */     } 
/* 404 */     if (pair == null) {
/* 405 */       return new Pair("unknown", "unknown");
/*     */     }
/* 407 */     return pair;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\windows\WindowsOSProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
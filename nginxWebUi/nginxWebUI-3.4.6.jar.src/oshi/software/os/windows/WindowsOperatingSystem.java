/*     */ package oshi.software.os.windows;
/*     */ 
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.platform.win32.Advapi32;
/*     */ import com.sun.jna.platform.win32.Advapi32Util;
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import com.sun.jna.platform.win32.Kernel32;
/*     */ import com.sun.jna.platform.win32.Psapi;
/*     */ import com.sun.jna.platform.win32.Tlhelp32;
/*     */ import com.sun.jna.platform.win32.User32;
/*     */ import com.sun.jna.platform.win32.VersionHelpers;
/*     */ import com.sun.jna.platform.win32.W32ServiceManager;
/*     */ import com.sun.jna.platform.win32.Win32Exception;
/*     */ import com.sun.jna.platform.win32.WinDef;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.platform.win32.Winsvc;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.function.Supplier;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.windows.registry.HkeyUserData;
/*     */ import oshi.driver.windows.registry.NetSessionData;
/*     */ import oshi.driver.windows.registry.ProcessPerformanceData;
/*     */ import oshi.driver.windows.registry.ProcessWtsData;
/*     */ import oshi.driver.windows.registry.SessionWtsData;
/*     */ import oshi.driver.windows.wmi.Win32OperatingSystem;
/*     */ import oshi.driver.windows.wmi.Win32Processor;
/*     */ import oshi.software.common.AbstractOperatingSystem;
/*     */ import oshi.software.os.FileSystem;
/*     */ import oshi.software.os.InternetProtocolStats;
/*     */ import oshi.software.os.NetworkParams;
/*     */ import oshi.software.os.OSProcess;
/*     */ import oshi.software.os.OSService;
/*     */ import oshi.software.os.OSSession;
/*     */ import oshi.software.os.OperatingSystem;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.GlobalConfig;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
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
/*     */ public class WindowsOperatingSystem
/*     */   extends AbstractOperatingSystem
/*     */ {
/* 102 */   private static final Logger LOG = LoggerFactory.getLogger(WindowsOperatingSystem.class);
/*     */   
/*     */   private static final String WIN_VERSION_PROPERTIES = "oshi.windows.versions.properties";
/* 105 */   private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 110 */   private static Supplier<String> systemLog = Memoizer.memoize(WindowsOperatingSystem::querySystemLog, TimeUnit.HOURS
/* 111 */       .toNanos(1L));
/*     */   
/* 113 */   private static final long BOOTTIME = querySystemBootTime();
/*     */   
/*     */   static {
/* 116 */     enableDebugPrivilege();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 123 */   private Supplier<Map<Integer, ProcessPerformanceData.PerfCounterBlock>> processMapFromRegistry = Memoizer.memoize(WindowsOperatingSystem::queryProcessMapFromRegistry, 
/* 124 */       Memoizer.defaultExpiration());
/* 125 */   private Supplier<Map<Integer, ProcessPerformanceData.PerfCounterBlock>> processMapFromPerfCounters = Memoizer.memoize(WindowsOperatingSystem::queryProcessMapFromPerfCounters, 
/* 126 */       Memoizer.defaultExpiration());
/*     */ 
/*     */   
/*     */   public String queryManufacturer() {
/* 130 */     return "Microsoft";
/*     */   }
/*     */ 
/*     */   
/*     */   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
/* 135 */     WbemcliUtil.WmiResult<Win32OperatingSystem.OSVersionProperty> versionInfo = Win32OperatingSystem.queryOsVersion();
/* 136 */     if (versionInfo.getResultCount() < 1) {
/* 137 */       return new AbstractOperatingSystem.FamilyVersionInfo("Windows", new OperatingSystem.OSVersionInfo(System.getProperty("os.version"), null, null));
/*     */     }
/*     */ 
/*     */     
/* 141 */     int suiteMask = WmiUtil.getUint32(versionInfo, (Enum)Win32OperatingSystem.OSVersionProperty.SUITEMASK, 0);
/* 142 */     String buildNumber = WmiUtil.getString(versionInfo, (Enum)Win32OperatingSystem.OSVersionProperty.BUILDNUMBER, 0);
/* 143 */     String version = parseVersion(versionInfo, suiteMask, buildNumber);
/* 144 */     String codeName = parseCodeName(suiteMask);
/* 145 */     return new AbstractOperatingSystem.FamilyVersionInfo("Windows", new OperatingSystem.OSVersionInfo(version, codeName, buildNumber));
/*     */   }
/*     */ 
/*     */   
/*     */   private String parseVersion(WbemcliUtil.WmiResult<Win32OperatingSystem.OSVersionProperty> versionInfo, int suiteMask, String buildNumber) {
/* 150 */     String version = System.getProperty("os.version");
/*     */ 
/*     */ 
/*     */     
/* 154 */     String[] verSplit = WmiUtil.getString(versionInfo, (Enum)Win32OperatingSystem.OSVersionProperty.VERSION, 0).split("\\D");
/* 155 */     int major = (verSplit.length > 0) ? ParseUtil.parseIntOrDefault(verSplit[0], 0) : 0;
/* 156 */     int minor = (verSplit.length > 1) ? ParseUtil.parseIntOrDefault(verSplit[1], 0) : 0;
/*     */ 
/*     */ 
/*     */     
/* 160 */     boolean ntWorkstation = (WmiUtil.getUint32(versionInfo, (Enum)Win32OperatingSystem.OSVersionProperty.PRODUCTTYPE, 0) == 1);
/*     */ 
/*     */     
/* 163 */     StringBuilder verLookup = (new StringBuilder(major)).append('.').append(minor);
/*     */     
/* 165 */     if (IS_VISTA_OR_GREATER && ntWorkstation) {
/* 166 */       verLookup.append(".nt");
/* 167 */     } else if (major == 10 && ParseUtil.parseLongOrDefault(buildNumber, 0L) > 17762L) {
/* 168 */       verLookup.append(".17763+");
/* 169 */     } else if (major == 5 && minor == 2) {
/* 170 */       if (ntWorkstation && getBitness() == 64) {
/* 171 */         verLookup.append(".nt.x64");
/* 172 */       } else if ((suiteMask & 0x8000) != 0) {
/* 173 */         verLookup.append(".HS");
/* 174 */       } else if (User32.INSTANCE.GetSystemMetrics(89) != 0) {
/* 175 */         verLookup.append(".R2");
/*     */       } 
/*     */     } 
/*     */     
/* 179 */     Properties verProps = FileUtil.readPropertiesFromFilename("oshi.windows.versions.properties");
/*     */     
/* 181 */     version = (verProps.getProperty(verLookup.toString()) != null) ? verProps.getProperty(verLookup.toString()) : version;
/*     */     
/* 183 */     String sp = WmiUtil.getString(versionInfo, (Enum)Win32OperatingSystem.OSVersionProperty.CSDVERSION, 0);
/* 184 */     if (!sp.isEmpty() && !"unknown".equals(sp)) {
/* 185 */       version = version + " " + sp.replace("Service Pack ", "SP");
/*     */     }
/*     */     
/* 188 */     return version;
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
/*     */   private static String parseCodeName(int suiteMask) {
/* 200 */     List<String> suites = new ArrayList<>();
/* 201 */     if ((suiteMask & 0x2) != 0) {
/* 202 */       suites.add("Enterprise");
/*     */     }
/* 204 */     if ((suiteMask & 0x4) != 0) {
/* 205 */       suites.add("BackOffice");
/*     */     }
/* 207 */     if ((suiteMask & 0x8) != 0) {
/* 208 */       suites.add("Communication Server");
/*     */     }
/* 210 */     if ((suiteMask & 0x80) != 0) {
/* 211 */       suites.add("Datacenter");
/*     */     }
/* 213 */     if ((suiteMask & 0x200) != 0) {
/* 214 */       suites.add("Home");
/*     */     }
/* 216 */     if ((suiteMask & 0x400) != 0) {
/* 217 */       suites.add("Web Server");
/*     */     }
/* 219 */     if ((suiteMask & 0x2000) != 0) {
/* 220 */       suites.add("Storage Server");
/*     */     }
/* 222 */     if ((suiteMask & 0x4000) != 0) {
/* 223 */       suites.add("Compute Cluster");
/*     */     }
/*     */     
/* 226 */     return String.join(",", (Iterable)suites);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int queryBitness(int jvmBitness) {
/* 231 */     if (jvmBitness < 64 && System.getenv("ProgramFiles(x86)") != null && IS_VISTA_OR_GREATER) {
/* 232 */       WbemcliUtil.WmiResult<Win32Processor.BitnessProperty> bitnessMap = Win32Processor.queryBitness();
/* 233 */       if (bitnessMap.getResultCount() > 0) {
/* 234 */         return WmiUtil.getUint16(bitnessMap, (Enum)Win32Processor.BitnessProperty.ADDRESSWIDTH, 0);
/*     */       }
/*     */     } 
/* 237 */     return jvmBitness;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean queryElevated() {
/*     */     try {
/* 243 */       File dir = new File(System.getenv("windir") + "\\system32\\config\\systemprofile");
/* 244 */       return dir.isDirectory();
/* 245 */     } catch (SecurityException e) {
/* 246 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public FileSystem getFileSystem() {
/* 252 */     return (FileSystem)new WindowsFileSystem();
/*     */   }
/*     */ 
/*     */   
/*     */   public InternetProtocolStats getInternetProtocolStats() {
/* 257 */     return new WindowsInternetProtocolStats();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSSession> getSessions() {
/* 262 */     List<OSSession> whoList = HkeyUserData.queryUserSessions();
/* 263 */     whoList.addAll(SessionWtsData.queryUserSessions());
/* 264 */     whoList.addAll(NetSessionData.queryUserSessions());
/* 265 */     return Collections.unmodifiableList(whoList);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSProcess> getProcesses(int limit, OperatingSystem.ProcessSort sort) {
/* 270 */     List<OSProcess> procList = processMapToList((Collection<Integer>)null);
/* 271 */     List<OSProcess> sorted = processSort(procList, limit, sort);
/* 272 */     return Collections.unmodifiableList(sorted);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSProcess> getProcesses(Collection<Integer> pids) {
/* 277 */     return Collections.unmodifiableList(processMapToList(pids));
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSProcess> getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sort) {
/* 282 */     Set<Integer> childPids = new HashSet<>();
/*     */     
/* 284 */     Tlhelp32.PROCESSENTRY32.ByReference processEntry = new Tlhelp32.PROCESSENTRY32.ByReference();
/* 285 */     WinNT.HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0L));
/*     */     try {
/* 287 */       while (Kernel32.INSTANCE.Process32Next(snapshot, (Tlhelp32.PROCESSENTRY32)processEntry)) {
/* 288 */         if (processEntry.th32ParentProcessID.intValue() == parentPid) {
/* 289 */           childPids.add(Integer.valueOf(processEntry.th32ProcessID.intValue()));
/*     */         }
/*     */       } 
/*     */     } finally {
/* 293 */       Kernel32.INSTANCE.CloseHandle(snapshot);
/*     */     } 
/* 295 */     List<OSProcess> procList = getProcesses(childPids);
/* 296 */     List<OSProcess> sorted = processSort(procList, limit, sort);
/* 297 */     return Collections.unmodifiableList(sorted);
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess getProcess(int pid) {
/* 302 */     List<OSProcess> procList = processMapToList(Arrays.asList(new Integer[] { Integer.valueOf(pid) }));
/* 303 */     return procList.isEmpty() ? null : procList.get(0);
/*     */   }
/*     */ 
/*     */   
/*     */   private List<OSProcess> processMapToList(Collection<Integer> pids) {
/* 308 */     Map<Integer, ProcessPerformanceData.PerfCounterBlock> processMap = this.processMapFromRegistry.get();
/*     */     
/* 310 */     if (processMap == null)
/*     */     {
/* 312 */       processMap = (pids == null) ? this.processMapFromPerfCounters.get() : ProcessPerformanceData.buildProcessMapFromPerfCounters(pids);
/*     */     }
/*     */     
/* 315 */     Map<Integer, ProcessWtsData.WtsInfo> processWtsMap = ProcessWtsData.queryProcessWtsMap(pids);
/*     */     
/* 317 */     Set<Integer> mapKeys = new HashSet<>(processWtsMap.keySet());
/* 318 */     mapKeys.retainAll(processMap.keySet());
/*     */     
/* 320 */     List<OSProcess> processList = new ArrayList<>();
/* 321 */     for (Integer pid : mapKeys) {
/* 322 */       processList.add(new WindowsOSProcess(pid.intValue(), this, processMap, processWtsMap));
/*     */     }
/* 324 */     return processList;
/*     */   }
/*     */   
/*     */   private static Map<Integer, ProcessPerformanceData.PerfCounterBlock> queryProcessMapFromRegistry() {
/* 328 */     return ProcessPerformanceData.buildProcessMapFromRegistry(null);
/*     */   }
/*     */   
/*     */   private static Map<Integer, ProcessPerformanceData.PerfCounterBlock> queryProcessMapFromPerfCounters() {
/* 332 */     return ProcessPerformanceData.buildProcessMapFromPerfCounters(null);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getProcessId() {
/* 337 */     return Kernel32.INSTANCE.GetCurrentProcessId();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getProcessCount() {
/* 342 */     Psapi.PERFORMANCE_INFORMATION perfInfo = new Psapi.PERFORMANCE_INFORMATION();
/* 343 */     if (!Psapi.INSTANCE.GetPerformanceInfo(perfInfo, perfInfo.size())) {
/* 344 */       LOG.error("Failed to get Performance Info. Error code: {}", Integer.valueOf(Kernel32.INSTANCE.GetLastError()));
/* 345 */       return 0;
/*     */     } 
/* 347 */     return perfInfo.ProcessCount.intValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getThreadCount() {
/* 352 */     Psapi.PERFORMANCE_INFORMATION perfInfo = new Psapi.PERFORMANCE_INFORMATION();
/* 353 */     if (!Psapi.INSTANCE.GetPerformanceInfo(perfInfo, perfInfo.size())) {
/* 354 */       LOG.error("Failed to get Performance Info. Error code: {}", Integer.valueOf(Kernel32.INSTANCE.GetLastError()));
/* 355 */       return 0;
/*     */     } 
/* 357 */     return perfInfo.ThreadCount.intValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSystemUptime() {
/* 362 */     return querySystemUptime();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static long querySystemUptime() {
/* 368 */     if (IS_VISTA_OR_GREATER) {
/* 369 */       return Kernel32.INSTANCE.GetTickCount64() / 1000L;
/*     */     }
/*     */     
/* 372 */     return Kernel32.INSTANCE.GetTickCount() / 1000L;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getSystemBootTime() {
/* 378 */     return BOOTTIME;
/*     */   }
/*     */   
/*     */   private static long querySystemBootTime() {
/* 382 */     String eventLog = systemLog.get();
/* 383 */     if (eventLog != null) {
/*     */       try {
/* 385 */         Advapi32Util.EventLogIterator iter = new Advapi32Util.EventLogIterator(null, eventLog, 8);
/*     */ 
/*     */ 
/*     */         
/* 389 */         long event6005Time = 0L;
/* 390 */         while (iter.hasNext()) {
/* 391 */           Advapi32Util.EventLogRecord record = iter.next();
/* 392 */           if (record.getStatusCode() == 12)
/*     */           {
/*     */             
/* 395 */             return (record.getRecord()).TimeGenerated.longValue(); } 
/* 396 */           if (record.getStatusCode() == 6005) {
/*     */ 
/*     */             
/* 399 */             if (event6005Time > 0L) {
/* 400 */               return event6005Time;
/*     */             }
/*     */             
/* 403 */             event6005Time = (record.getRecord()).TimeGenerated.longValue();
/*     */           } 
/*     */         } 
/*     */         
/* 407 */         if (event6005Time > 0L) {
/* 408 */           return event6005Time;
/*     */         }
/* 410 */       } catch (Win32Exception e) {
/* 411 */         LOG.warn("Can't open event log \"{}\".", eventLog);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 416 */     return System.currentTimeMillis() / 1000L - querySystemUptime();
/*     */   }
/*     */ 
/*     */   
/*     */   public NetworkParams getNetworkParams() {
/* 421 */     return (NetworkParams)new WindowsNetworkParams();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean enableDebugPrivilege() {
/* 431 */     WinNT.HANDLEByReference hToken = new WinNT.HANDLEByReference();
/* 432 */     boolean success = Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), 40, hToken);
/*     */     
/* 434 */     if (!success) {
/* 435 */       LOG.error("OpenProcessToken failed. Error: {}", Integer.valueOf(Native.getLastError()));
/* 436 */       return false;
/*     */     } 
/*     */     try {
/* 439 */       WinNT.LUID luid = new WinNT.LUID();
/* 440 */       success = Advapi32.INSTANCE.LookupPrivilegeValue(null, "SeDebugPrivilege", luid);
/* 441 */       if (!success) {
/* 442 */         LOG.error("LookupPrivilegeValue failed. Error: {}", Integer.valueOf(Native.getLastError()));
/* 443 */         return false;
/*     */       } 
/* 445 */       WinNT.TOKEN_PRIVILEGES tkp = new WinNT.TOKEN_PRIVILEGES(1);
/* 446 */       tkp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(luid, new WinDef.DWORD(2L));
/* 447 */       success = Advapi32.INSTANCE.AdjustTokenPrivileges(hToken.getValue(), false, tkp, 0, null, null);
/*     */       
/* 449 */       int err = Native.getLastError();
/* 450 */       if (err != 0) {
/* 451 */         LOG.error("AdjustTokenPrivileges failed. Error: {}", Integer.valueOf(err));
/* 452 */         return false;
/*     */       } 
/*     */     } finally {
/* 455 */       Kernel32.INSTANCE.CloseHandle(hToken.getValue());
/*     */     } 
/* 457 */     return true;
/*     */   }
/*     */   
/*     */   public OSService[] getServices() {
/*     */     
/* 462 */     try { W32ServiceManager sm = new W32ServiceManager(); 
/* 463 */       try { sm.open(4);
/* 464 */         Winsvc.ENUM_SERVICE_STATUS_PROCESS[] services = sm.enumServicesStatusExProcess(48, 3, null);
/*     */         
/* 466 */         OSService[] svcArray = new OSService[services.length];
/* 467 */         for (int i = 0; i < services.length; i++) {
/*     */           OSService.State state;
/* 469 */           switch ((services[i]).ServiceStatusProcess.dwCurrentState) {
/*     */             case 1:
/* 471 */               state = OSService.State.STOPPED;
/*     */               break;
/*     */             case 4:
/* 474 */               state = OSService.State.RUNNING;
/*     */               break;
/*     */             default:
/* 477 */               state = OSService.State.OTHER;
/*     */               break;
/*     */           } 
/* 480 */           svcArray[i] = new OSService((services[i]).lpDisplayName, (services[i]).ServiceStatusProcess.dwProcessId, state);
/*     */         } 
/*     */         
/* 483 */         OSService[] arrayOfOSService1 = svcArray;
/* 484 */         sm.close(); return arrayOfOSService1; } catch (Throwable throwable) { try { sm.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (Win32Exception ex)
/* 485 */     { LOG.error("Win32Exception: {}", ex.getMessage());
/* 486 */       return new OSService[0]; }
/*     */   
/*     */   }
/*     */   
/*     */   private static String querySystemLog() {
/* 491 */     String systemLog = GlobalConfig.get("oshi.os.windows.eventlog", "System");
/* 492 */     if (systemLog.isEmpty())
/*     */     {
/* 494 */       return null;
/*     */     }
/*     */     
/* 497 */     WinNT.HANDLE h = Advapi32.INSTANCE.OpenEventLog(null, systemLog);
/* 498 */     if (h == null) {
/* 499 */       LOG.warn("Unable to open configured system Event log \"{}\". Calculating boot time from uptime.", systemLog);
/*     */       
/* 501 */       return null;
/*     */     } 
/* 503 */     return systemLog;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\windows\WindowsOperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package oshi.software.os.windows;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.W32ServiceManager;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Winsvc;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.ptr.IntByReference;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.registry.HkeyUserData;
import oshi.driver.windows.registry.NetSessionData;
import oshi.driver.windows.registry.ProcessPerformanceData;
import oshi.driver.windows.registry.ProcessWtsData;
import oshi.driver.windows.registry.SessionWtsData;
import oshi.driver.windows.wmi.Win32OperatingSystem;
import oshi.driver.windows.wmi.Win32Processor;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OSSession;
import oshi.software.os.OperatingSystem;
import oshi.util.FileUtil;
import oshi.util.GlobalConfig;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiUtil;

@ThreadSafe
public class WindowsOperatingSystem extends AbstractOperatingSystem {
   private static final Logger LOG = LoggerFactory.getLogger(WindowsOperatingSystem.class);
   private static final String WIN_VERSION_PROPERTIES = "oshi.windows.versions.properties";
   private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
   private static Supplier<String> systemLog;
   private static final long BOOTTIME;
   private Supplier<Map<Integer, ProcessPerformanceData.PerfCounterBlock>> processMapFromRegistry = Memoizer.memoize(WindowsOperatingSystem::queryProcessMapFromRegistry, Memoizer.defaultExpiration());
   private Supplier<Map<Integer, ProcessPerformanceData.PerfCounterBlock>> processMapFromPerfCounters = Memoizer.memoize(WindowsOperatingSystem::queryProcessMapFromPerfCounters, Memoizer.defaultExpiration());

   public String queryManufacturer() {
      return "Microsoft";
   }

   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
      WbemcliUtil.WmiResult<Win32OperatingSystem.OSVersionProperty> versionInfo = Win32OperatingSystem.queryOsVersion();
      if (versionInfo.getResultCount() < 1) {
         return new AbstractOperatingSystem.FamilyVersionInfo("Windows", new OperatingSystem.OSVersionInfo(System.getProperty("os.version"), (String)null, (String)null));
      } else {
         int suiteMask = WmiUtil.getUint32(versionInfo, Win32OperatingSystem.OSVersionProperty.SUITEMASK, 0);
         String buildNumber = WmiUtil.getString(versionInfo, Win32OperatingSystem.OSVersionProperty.BUILDNUMBER, 0);
         String version = this.parseVersion(versionInfo, suiteMask, buildNumber);
         String codeName = parseCodeName(suiteMask);
         return new AbstractOperatingSystem.FamilyVersionInfo("Windows", new OperatingSystem.OSVersionInfo(version, codeName, buildNumber));
      }
   }

   private String parseVersion(WbemcliUtil.WmiResult<Win32OperatingSystem.OSVersionProperty> versionInfo, int suiteMask, String buildNumber) {
      String version = System.getProperty("os.version");
      String[] verSplit = WmiUtil.getString(versionInfo, Win32OperatingSystem.OSVersionProperty.VERSION, 0).split("\\D");
      int major = verSplit.length > 0 ? ParseUtil.parseIntOrDefault(verSplit[0], 0) : 0;
      int minor = verSplit.length > 1 ? ParseUtil.parseIntOrDefault(verSplit[1], 0) : 0;
      boolean ntWorkstation = WmiUtil.getUint32(versionInfo, Win32OperatingSystem.OSVersionProperty.PRODUCTTYPE, 0) == 1;
      StringBuilder verLookup = (new StringBuilder(major)).append('.').append(minor);
      if (IS_VISTA_OR_GREATER && ntWorkstation) {
         verLookup.append(".nt");
      } else if (major == 10 && ParseUtil.parseLongOrDefault(buildNumber, 0L) > 17762L) {
         verLookup.append(".17763+");
      } else if (major == 5 && minor == 2) {
         if (ntWorkstation && this.getBitness() == 64) {
            verLookup.append(".nt.x64");
         } else if ((suiteMask & '耀') != 0) {
            verLookup.append(".HS");
         } else if (User32.INSTANCE.GetSystemMetrics(89) != 0) {
            verLookup.append(".R2");
         }
      }

      Properties verProps = FileUtil.readPropertiesFromFilename("oshi.windows.versions.properties");
      version = verProps.getProperty(verLookup.toString()) != null ? verProps.getProperty(verLookup.toString()) : version;
      String sp = WmiUtil.getString(versionInfo, Win32OperatingSystem.OSVersionProperty.CSDVERSION, 0);
      if (!sp.isEmpty() && !"unknown".equals(sp)) {
         version = version + " " + sp.replace("Service Pack ", "SP");
      }

      return version;
   }

   private static String parseCodeName(int suiteMask) {
      List<String> suites = new ArrayList();
      if ((suiteMask & 2) != 0) {
         suites.add("Enterprise");
      }

      if ((suiteMask & 4) != 0) {
         suites.add("BackOffice");
      }

      if ((suiteMask & 8) != 0) {
         suites.add("Communication Server");
      }

      if ((suiteMask & 128) != 0) {
         suites.add("Datacenter");
      }

      if ((suiteMask & 512) != 0) {
         suites.add("Home");
      }

      if ((suiteMask & 1024) != 0) {
         suites.add("Web Server");
      }

      if ((suiteMask & 8192) != 0) {
         suites.add("Storage Server");
      }

      if ((suiteMask & 16384) != 0) {
         suites.add("Compute Cluster");
      }

      return String.join(",", suites);
   }

   protected int queryBitness(int jvmBitness) {
      if (jvmBitness < 64 && System.getenv("ProgramFiles(x86)") != null && IS_VISTA_OR_GREATER) {
         WbemcliUtil.WmiResult<Win32Processor.BitnessProperty> bitnessMap = Win32Processor.queryBitness();
         if (bitnessMap.getResultCount() > 0) {
            return WmiUtil.getUint16(bitnessMap, Win32Processor.BitnessProperty.ADDRESSWIDTH, 0);
         }
      }

      return jvmBitness;
   }

   public boolean queryElevated() {
      try {
         File dir = new File(System.getenv("windir") + "\\system32\\config\\systemprofile");
         return dir.isDirectory();
      } catch (SecurityException var2) {
         return false;
      }
   }

   public FileSystem getFileSystem() {
      return new WindowsFileSystem();
   }

   public InternetProtocolStats getInternetProtocolStats() {
      return new WindowsInternetProtocolStats();
   }

   public List<OSSession> getSessions() {
      List<OSSession> whoList = HkeyUserData.queryUserSessions();
      whoList.addAll(SessionWtsData.queryUserSessions());
      whoList.addAll(NetSessionData.queryUserSessions());
      return Collections.unmodifiableList(whoList);
   }

   public List<OSProcess> getProcesses(int limit, OperatingSystem.ProcessSort sort) {
      List<OSProcess> procList = this.processMapToList((Collection)null);
      List<OSProcess> sorted = this.processSort(procList, limit, sort);
      return Collections.unmodifiableList(sorted);
   }

   public List<OSProcess> getProcesses(Collection<Integer> pids) {
      return Collections.unmodifiableList(this.processMapToList(pids));
   }

   public List<OSProcess> getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sort) {
      Set<Integer> childPids = new HashSet();
      Tlhelp32.PROCESSENTRY32.ByReference processEntry = new Tlhelp32.PROCESSENTRY32.ByReference();
      WinNT.HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0L));

      try {
         while(Kernel32.INSTANCE.Process32Next(snapshot, processEntry)) {
            if (processEntry.th32ParentProcessID.intValue() == parentPid) {
               childPids.add(processEntry.th32ProcessID.intValue());
            }
         }
      } finally {
         Kernel32.INSTANCE.CloseHandle(snapshot);
      }

      List procList = this.getProcesses(childPids);
      List var8 = this.processSort(procList, limit, sort);
      return Collections.unmodifiableList(var8);
   }

   public OSProcess getProcess(int pid) {
      List<OSProcess> procList = this.processMapToList(Arrays.asList(pid));
      return procList.isEmpty() ? null : (OSProcess)procList.get(0);
   }

   private List<OSProcess> processMapToList(Collection<Integer> pids) {
      Map<Integer, ProcessPerformanceData.PerfCounterBlock> processMap = (Map)this.processMapFromRegistry.get();
      if (processMap == null) {
         processMap = pids == null ? (Map)this.processMapFromPerfCounters.get() : ProcessPerformanceData.buildProcessMapFromPerfCounters(pids);
      }

      Map<Integer, ProcessWtsData.WtsInfo> processWtsMap = ProcessWtsData.queryProcessWtsMap(pids);
      Set<Integer> mapKeys = new HashSet(processWtsMap.keySet());
      mapKeys.retainAll(processMap.keySet());
      List<OSProcess> processList = new ArrayList();
      Iterator var6 = mapKeys.iterator();

      while(var6.hasNext()) {
         Integer pid = (Integer)var6.next();
         processList.add(new WindowsOSProcess(pid, this, processMap, processWtsMap));
      }

      return processList;
   }

   private static Map<Integer, ProcessPerformanceData.PerfCounterBlock> queryProcessMapFromRegistry() {
      return ProcessPerformanceData.buildProcessMapFromRegistry((Collection)null);
   }

   private static Map<Integer, ProcessPerformanceData.PerfCounterBlock> queryProcessMapFromPerfCounters() {
      return ProcessPerformanceData.buildProcessMapFromPerfCounters((Collection)null);
   }

   public int getProcessId() {
      return Kernel32.INSTANCE.GetCurrentProcessId();
   }

   public int getProcessCount() {
      Psapi.PERFORMANCE_INFORMATION perfInfo = new Psapi.PERFORMANCE_INFORMATION();
      if (!Psapi.INSTANCE.GetPerformanceInfo(perfInfo, perfInfo.size())) {
         LOG.error((String)"Failed to get Performance Info. Error code: {}", (Object)Kernel32.INSTANCE.GetLastError());
         return 0;
      } else {
         return perfInfo.ProcessCount.intValue();
      }
   }

   public int getThreadCount() {
      Psapi.PERFORMANCE_INFORMATION perfInfo = new Psapi.PERFORMANCE_INFORMATION();
      if (!Psapi.INSTANCE.GetPerformanceInfo(perfInfo, perfInfo.size())) {
         LOG.error((String)"Failed to get Performance Info. Error code: {}", (Object)Kernel32.INSTANCE.GetLastError());
         return 0;
      } else {
         return perfInfo.ThreadCount.intValue();
      }
   }

   public long getSystemUptime() {
      return querySystemUptime();
   }

   private static long querySystemUptime() {
      return IS_VISTA_OR_GREATER ? Kernel32.INSTANCE.GetTickCount64() / 1000L : (long)Kernel32.INSTANCE.GetTickCount() / 1000L;
   }

   public long getSystemBootTime() {
      return BOOTTIME;
   }

   private static long querySystemBootTime() {
      String eventLog = (String)systemLog.get();
      if (eventLog != null) {
         try {
            Advapi32Util.EventLogIterator iter = new Advapi32Util.EventLogIterator((String)null, eventLog, 8);
            long event6005Time = 0L;

            while(iter.hasNext()) {
               Advapi32Util.EventLogRecord record = iter.next();
               if (record.getStatusCode() == 12) {
                  return record.getRecord().TimeGenerated.longValue();
               }

               if (record.getStatusCode() == 6005) {
                  if (event6005Time > 0L) {
                     return event6005Time;
                  }

                  event6005Time = record.getRecord().TimeGenerated.longValue();
               }
            }

            if (event6005Time > 0L) {
               return event6005Time;
            }
         } catch (Win32Exception var5) {
            LOG.warn((String)"Can't open event log \"{}\".", (Object)eventLog);
         }
      }

      return System.currentTimeMillis() / 1000L - querySystemUptime();
   }

   public NetworkParams getNetworkParams() {
      return new WindowsNetworkParams();
   }

   private static boolean enableDebugPrivilege() {
      WinNT.HANDLEByReference hToken = new WinNT.HANDLEByReference();
      boolean success = Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), 40, hToken);
      if (!success) {
         LOG.error((String)"OpenProcessToken failed. Error: {}", (Object)Native.getLastError());
         return false;
      } else {
         try {
            WinNT.LUID luid = new WinNT.LUID();
            success = Advapi32.INSTANCE.LookupPrivilegeValue((String)null, "SeDebugPrivilege", luid);
            if (!success) {
               LOG.error((String)"LookupPrivilegeValue failed. Error: {}", (Object)Native.getLastError());
               boolean var9 = false;
               return var9;
            }

            WinNT.TOKEN_PRIVILEGES tkp = new WinNT.TOKEN_PRIVILEGES(1);
            tkp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(luid, new WinDef.DWORD(2L));
            success = Advapi32.INSTANCE.AdjustTokenPrivileges(hToken.getValue(), false, tkp, 0, (WinNT.TOKEN_PRIVILEGES)null, (IntByReference)null);
            int err = Native.getLastError();
            if (err != 0) {
               LOG.error((String)"AdjustTokenPrivileges failed. Error: {}", (Object)err);
               boolean var5 = false;
               return var5;
            }
         } finally {
            Kernel32.INSTANCE.CloseHandle(hToken.getValue());
         }

         return true;
      }
   }

   public OSService[] getServices() {
      try {
         W32ServiceManager sm = new W32ServiceManager();

         OSService[] var9;
         try {
            sm.open(4);
            Winsvc.ENUM_SERVICE_STATUS_PROCESS[] services = sm.enumServicesStatusExProcess(48, 3, (String)null);
            OSService[] svcArray = new OSService[services.length];

            for(int i = 0; i < services.length; ++i) {
               OSService.State state;
               switch (services[i].ServiceStatusProcess.dwCurrentState) {
                  case 1:
                     state = OSService.State.STOPPED;
                     break;
                  case 4:
                     state = OSService.State.RUNNING;
                     break;
                  default:
                     state = OSService.State.OTHER;
               }

               svcArray[i] = new OSService(services[i].lpDisplayName, services[i].ServiceStatusProcess.dwProcessId, state);
            }

            var9 = svcArray;
         } catch (Throwable var7) {
            try {
               sm.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         sm.close();
         return var9;
      } catch (Win32Exception var8) {
         LOG.error((String)"Win32Exception: {}", (Object)var8.getMessage());
         return new OSService[0];
      }
   }

   private static String querySystemLog() {
      String systemLog = GlobalConfig.get("oshi.os.windows.eventlog", "System");
      if (systemLog.isEmpty()) {
         return null;
      } else {
         WinNT.HANDLE h = Advapi32.INSTANCE.OpenEventLog((String)null, systemLog);
         if (h == null) {
            LOG.warn((String)"Unable to open configured system Event log \"{}\". Calculating boot time from uptime.", (Object)systemLog);
            return null;
         } else {
            return systemLog;
         }
      }
   }

   static {
      systemLog = Memoizer.memoize(WindowsOperatingSystem::querySystemLog, TimeUnit.HOURS.toNanos(1L));
      BOOTTIME = querySystemBootTime();
      enableDebugPrivilege();
   }
}

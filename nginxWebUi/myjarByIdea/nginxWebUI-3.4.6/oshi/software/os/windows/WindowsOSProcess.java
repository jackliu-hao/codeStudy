package oshi.software.os.windows;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.ptr.IntByReference;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.registry.ProcessPerformanceData;
import oshi.driver.windows.registry.ProcessWtsData;
import oshi.driver.windows.registry.ThreadPerformanceData;
import oshi.driver.windows.wmi.Win32Process;
import oshi.driver.windows.wmi.Win32ProcessCached;
import oshi.software.common.AbstractOSProcess;
import oshi.software.os.OSProcess;
import oshi.software.os.OSThread;
import oshi.util.GlobalConfig;
import oshi.util.Memoizer;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public class WindowsOSProcess extends AbstractOSProcess {
   private static final Logger LOG = LoggerFactory.getLogger(WindowsOSProcess.class);
   public static final String OSHI_OS_WINDOWS_COMMANDLINE_BATCH = "oshi.os.windows.commandline.batch";
   private static final boolean USE_BATCH_COMMANDLINE = GlobalConfig.get("oshi.os.windows.commandline.batch", false);
   private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
   private static final boolean IS_WINDOWS7_OR_GREATER = VersionHelpers.IsWindows7OrGreater();
   private Supplier<Pair<String, String>> userInfo = Memoizer.memoize(this::queryUserInfo);
   private Supplier<Pair<String, String>> groupInfo = Memoizer.memoize(this::queryGroupInfo);
   private Supplier<String> commandLine = Memoizer.memoize(this::queryCommandLine);
   private String name;
   private String path;
   private String currentWorkingDirectory;
   private OSProcess.State state;
   private int parentProcessID;
   private int threadCount;
   private int priority;
   private long virtualSize;
   private long residentSetSize;
   private long kernelTime;
   private long userTime;
   private long startTime;
   private long upTime;
   private long bytesRead;
   private long bytesWritten;
   private long openFiles;
   private int bitness;
   private long pageFaults;

   public WindowsOSProcess(int pid, WindowsOperatingSystem os, Map<Integer, ProcessPerformanceData.PerfCounterBlock> processMap, Map<Integer, ProcessWtsData.WtsInfo> processWtsMap) {
      super(pid);
      this.state = OSProcess.State.INVALID;
      if (pid == os.getProcessId()) {
         String cwd = (new File(".")).getAbsolutePath();
         this.currentWorkingDirectory = cwd.isEmpty() ? "" : cwd.substring(0, cwd.length() - 1);
      }

      this.state = OSProcess.State.RUNNING;
      this.bitness = os.getBitness();
      this.updateAttributes((ProcessPerformanceData.PerfCounterBlock)processMap.get(pid), (ProcessWtsData.WtsInfo)processWtsMap.get(pid));
   }

   public String getName() {
      return this.name;
   }

   public String getPath() {
      return this.path;
   }

   public String getCommandLine() {
      return (String)this.commandLine.get();
   }

   public String getCurrentWorkingDirectory() {
      return this.currentWorkingDirectory;
   }

   public String getUser() {
      return (String)((Pair)this.userInfo.get()).getA();
   }

   public String getUserID() {
      return (String)((Pair)this.userInfo.get()).getB();
   }

   public String getGroup() {
      return (String)((Pair)this.groupInfo.get()).getA();
   }

   public String getGroupID() {
      return (String)((Pair)this.groupInfo.get()).getB();
   }

   public OSProcess.State getState() {
      return this.state;
   }

   public int getParentProcessID() {
      return this.parentProcessID;
   }

   public int getThreadCount() {
      return this.threadCount;
   }

   public int getPriority() {
      return this.priority;
   }

   public long getVirtualSize() {
      return this.virtualSize;
   }

   public long getResidentSetSize() {
      return this.residentSetSize;
   }

   public long getKernelTime() {
      return this.kernelTime;
   }

   public long getUserTime() {
      return this.userTime;
   }

   public long getUpTime() {
      return this.upTime;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public long getBytesRead() {
      return this.bytesRead;
   }

   public long getBytesWritten() {
      return this.bytesWritten;
   }

   public long getOpenFiles() {
      return this.openFiles;
   }

   public int getBitness() {
      return this.bitness;
   }

   public long getAffinityMask() {
      WinNT.HANDLE pHandle = Kernel32.INSTANCE.OpenProcess(1024, false, this.getProcessID());
      if (pHandle != null) {
         try {
            BaseTSD.ULONG_PTRByReference processAffinity = new BaseTSD.ULONG_PTRByReference();
            BaseTSD.ULONG_PTRByReference systemAffinity = new BaseTSD.ULONG_PTRByReference();
            if (Kernel32.INSTANCE.GetProcessAffinityMask(pHandle, processAffinity, systemAffinity)) {
               long var4 = Pointer.nativeValue(processAffinity.getValue().toPointer());
               return var4;
            }
         } finally {
            Kernel32.INSTANCE.CloseHandle(pHandle);
         }

         Kernel32.INSTANCE.CloseHandle(pHandle);
         return 0L;
      } else {
         return 0L;
      }
   }

   public long getMinorFaults() {
      return this.pageFaults;
   }

   public List<OSThread> getThreadDetails() {
      Map<Integer, ThreadPerformanceData.PerfCounterBlock> threads = ThreadPerformanceData.buildThreadMapFromRegistry(Collections.singleton(this.getProcessID()));
      if (threads != null) {
         threads = ThreadPerformanceData.buildThreadMapFromPerfCounters(Collections.singleton(this.getProcessID()));
      }

      return threads == null ? Collections.emptyList() : Collections.unmodifiableList((List)threads.entrySet().stream().map((entry) -> {
         return new WindowsOSThread(this.getProcessID(), (Integer)entry.getKey(), this.name, (ThreadPerformanceData.PerfCounterBlock)entry.getValue());
      }).collect(Collectors.toList()));
   }

   public boolean updateAttributes() {
      Set<Integer> pids = Collections.singleton(this.getProcessID());
      Map<Integer, ProcessPerformanceData.PerfCounterBlock> pcb = ProcessPerformanceData.buildProcessMapFromRegistry((Collection)null);
      if (pcb == null) {
         pcb = ProcessPerformanceData.buildProcessMapFromPerfCounters(pids);
      }

      Map<Integer, ProcessWtsData.WtsInfo> wts = ProcessWtsData.queryProcessWtsMap(pids);
      return this.updateAttributes((ProcessPerformanceData.PerfCounterBlock)pcb.get(this.getProcessID()), (ProcessWtsData.WtsInfo)wts.get(this.getProcessID()));
   }

   private boolean updateAttributes(ProcessPerformanceData.PerfCounterBlock pcb, ProcessWtsData.WtsInfo wts) {
      this.name = pcb.getName();
      this.path = wts.getPath();
      this.parentProcessID = pcb.getParentProcessID();
      this.threadCount = wts.getThreadCount();
      this.priority = pcb.getPriority();
      this.virtualSize = wts.getVirtualSize();
      this.residentSetSize = pcb.getResidentSetSize();
      this.kernelTime = wts.getKernelTime();
      this.userTime = wts.getUserTime();
      this.startTime = pcb.getStartTime();
      this.upTime = pcb.getUpTime();
      this.bytesRead = pcb.getBytesRead();
      this.bytesWritten = pcb.getBytesWritten();
      this.openFiles = wts.getOpenFiles();
      this.pageFaults = pcb.getPageFaults();
      WinNT.HANDLE pHandle = Kernel32.INSTANCE.OpenProcess(1024, false, this.getProcessID());
      if (pHandle != null) {
         try {
            if (IS_VISTA_OR_GREATER && this.bitness == 64) {
               IntByReference wow64 = new IntByReference(0);
               if (Kernel32.INSTANCE.IsWow64Process(pHandle, wow64) && wow64.getValue() > 0) {
                  this.bitness = 32;
               }
            }

            WinNT.HANDLEByReference phToken = new WinNT.HANDLEByReference();
            boolean var15 = false;

            WinNT.HANDLE token;
            label181: {
               try {
                  var15 = true;
                  if (IS_WINDOWS7_OR_GREATER) {
                     this.path = Kernel32Util.QueryFullProcessImageName(pHandle, 0);
                     var15 = false;
                  } else {
                     var15 = false;
                  }
                  break label181;
               } catch (Win32Exception var16) {
                  this.state = OSProcess.State.INVALID;
                  var15 = false;
               } finally {
                  if (var15) {
                     WinNT.HANDLE token = phToken.getValue();
                     if (token != null) {
                        Kernel32.INSTANCE.CloseHandle(token);
                     }

                  }
               }

               token = phToken.getValue();
               if (token != null) {
                  Kernel32.INSTANCE.CloseHandle(token);
               }

               return !this.state.equals(OSProcess.State.INVALID);
            }

            token = phToken.getValue();
            if (token != null) {
               Kernel32.INSTANCE.CloseHandle(token);
            }
         } finally {
            Kernel32.INSTANCE.CloseHandle(pHandle);
         }
      }

      return !this.state.equals(OSProcess.State.INVALID);
   }

   private String queryCommandLine() {
      if (USE_BATCH_COMMANDLINE) {
         return Win32ProcessCached.getInstance().getCommandLine(this.getProcessID(), this.getStartTime());
      } else {
         WbemcliUtil.WmiResult<Win32Process.CommandLineProperty> commandLineProcs = Win32Process.queryCommandLines(Collections.singleton(this.getProcessID()));
         return commandLineProcs.getResultCount() > 0 ? WmiUtil.getString(commandLineProcs, Win32Process.CommandLineProperty.COMMANDLINE, 0) : "unknown";
      }
   }

   private Pair<String, String> queryUserInfo() {
      Pair<String, String> pair = null;
      WinNT.HANDLE pHandle = Kernel32.INSTANCE.OpenProcess(1024, false, this.getProcessID());
      if (pHandle != null) {
         WinNT.HANDLEByReference phToken = new WinNT.HANDLEByReference();
         if (Advapi32.INSTANCE.OpenProcessToken(pHandle, 10, phToken)) {
            Advapi32Util.Account account = Advapi32Util.getTokenAccount(phToken.getValue());
            pair = new Pair(account.name, account.sidString);
         } else {
            int error = Kernel32.INSTANCE.GetLastError();
            if (error != 5) {
               LOG.error((String)"Failed to get process token for process {}: {}", (Object)this.getProcessID(), (Object)Kernel32.INSTANCE.GetLastError());
            }
         }

         WinNT.HANDLE token = phToken.getValue();
         if (token != null) {
            Kernel32.INSTANCE.CloseHandle(token);
         }

         Kernel32.INSTANCE.CloseHandle(pHandle);
      }

      return pair == null ? new Pair("unknown", "unknown") : pair;
   }

   private Pair<String, String> queryGroupInfo() {
      Pair<String, String> pair = null;
      WinNT.HANDLE pHandle = Kernel32.INSTANCE.OpenProcess(1024, false, this.getProcessID());
      if (pHandle != null) {
         WinNT.HANDLEByReference phToken = new WinNT.HANDLEByReference();
         if (Advapi32.INSTANCE.OpenProcessToken(pHandle, 10, phToken)) {
            Advapi32Util.Account account = Advapi32Util.getTokenPrimaryGroup(phToken.getValue());
            pair = new Pair(account.name, account.sidString);
         } else {
            int error = Kernel32.INSTANCE.GetLastError();
            if (error != 5) {
               LOG.error((String)"Failed to get process token for process {}: {}", (Object)this.getProcessID(), (Object)Kernel32.INSTANCE.GetLastError());
            }
         }

         WinNT.HANDLE token = phToken.getValue();
         if (token != null) {
            Kernel32.INSTANCE.CloseHandle(token);
         }

         Kernel32.INSTANCE.CloseHandle(pHandle);
      }

      return pair == null ? new Pair("unknown", "unknown") : pair;
   }
}

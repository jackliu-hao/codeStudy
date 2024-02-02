package oshi.driver.windows.registry;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.Wtsapi32;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.wmi.Win32Process;
import oshi.util.platform.windows.WmiUtil;

@ThreadSafe
public final class ProcessWtsData {
   private static final Logger LOG = LoggerFactory.getLogger(ProcessWtsData.class);
   private static final boolean IS_WINDOWS7_OR_GREATER = VersionHelpers.IsWindows7OrGreater();

   private ProcessWtsData() {
   }

   public static Map<Integer, WtsInfo> queryProcessWtsMap(Collection<Integer> pids) {
      return IS_WINDOWS7_OR_GREATER ? queryProcessWtsMapFromWTS(pids) : queryProcessWtsMapFromPerfMon(pids);
   }

   private static Map<Integer, WtsInfo> queryProcessWtsMapFromWTS(Collection<Integer> pids) {
      Map<Integer, WtsInfo> wtsMap = new HashMap();
      IntByReference pCount = new IntByReference(0);
      PointerByReference ppProcessInfo = new PointerByReference();
      if (!Wtsapi32.INSTANCE.WTSEnumerateProcessesEx(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, new IntByReference(1), -2, ppProcessInfo, pCount)) {
         LOG.error((String)"Failed to enumerate Processes. Error code: {}", (Object)Kernel32.INSTANCE.GetLastError());
         return wtsMap;
      } else {
         Pointer pProcessInfo = ppProcessInfo.getValue();
         Wtsapi32.WTS_PROCESS_INFO_EX processInfoRef = new Wtsapi32.WTS_PROCESS_INFO_EX(pProcessInfo);
         Wtsapi32.WTS_PROCESS_INFO_EX[] processInfo = (Wtsapi32.WTS_PROCESS_INFO_EX[])processInfoRef.toArray(pCount.getValue());

         for(int i = 0; i < processInfo.length; ++i) {
            if (pids == null || pids.contains(processInfo[i].ProcessId)) {
               wtsMap.put(processInfo[i].ProcessId, new WtsInfo(processInfo[i].pProcessName, "", processInfo[i].NumberOfThreads, (long)processInfo[i].PagefileUsage & 4294967295L, processInfo[i].KernelTime.getValue() / 10000L, processInfo[i].UserTime.getValue() / 10000L, (long)processInfo[i].HandleCount));
            }
         }

         if (!Wtsapi32.INSTANCE.WTSFreeMemoryEx(1, pProcessInfo, pCount.getValue())) {
            LOG.warn((String)"Failed to Free Memory for Processes. Error code: {}", (Object)Kernel32.INSTANCE.GetLastError());
         }

         return wtsMap;
      }
   }

   private static Map<Integer, WtsInfo> queryProcessWtsMapFromPerfMon(Collection<Integer> pids) {
      Map<Integer, WtsInfo> wtsMap = new HashMap();
      WbemcliUtil.WmiResult<Win32Process.ProcessXPProperty> processWmiResult = Win32Process.queryProcesses(pids);

      for(int i = 0; i < processWmiResult.getResultCount(); ++i) {
         wtsMap.put(WmiUtil.getUint32(processWmiResult, Win32Process.ProcessXPProperty.PROCESSID, i), new WtsInfo(WmiUtil.getString(processWmiResult, Win32Process.ProcessXPProperty.NAME, i), WmiUtil.getString(processWmiResult, Win32Process.ProcessXPProperty.EXECUTABLEPATH, i), WmiUtil.getUint32(processWmiResult, Win32Process.ProcessXPProperty.THREADCOUNT, i), 1024L * ((long)WmiUtil.getUint32(processWmiResult, Win32Process.ProcessXPProperty.PAGEFILEUSAGE, i) & 4294967295L), WmiUtil.getUint64(processWmiResult, Win32Process.ProcessXPProperty.KERNELMODETIME, i) / 10000L, WmiUtil.getUint64(processWmiResult, Win32Process.ProcessXPProperty.USERMODETIME, i) / 10000L, (long)WmiUtil.getUint32(processWmiResult, Win32Process.ProcessXPProperty.HANDLECOUNT, i)));
      }

      return wtsMap;
   }

   @Immutable
   public static class WtsInfo {
      private final String name;
      private final String path;
      private final int threadCount;
      private final long virtualSize;
      private final long kernelTime;
      private final long userTime;
      private final long openFiles;

      public WtsInfo(String name, String path, int threadCount, long virtualSize, long kernelTime, long userTime, long openFiles) {
         this.name = name;
         this.path = path;
         this.threadCount = threadCount;
         this.virtualSize = virtualSize;
         this.kernelTime = kernelTime;
         this.userTime = userTime;
         this.openFiles = openFiles;
      }

      public String getName() {
         return this.name;
      }

      public String getPath() {
         return this.path;
      }

      public int getThreadCount() {
         return this.threadCount;
      }

      public long getVirtualSize() {
         return this.virtualSize;
      }

      public long getKernelTime() {
         return this.kernelTime;
      }

      public long getUserTime() {
         return this.userTime;
      }

      public long getOpenFiles() {
         return this.openFiles;
      }
   }
}

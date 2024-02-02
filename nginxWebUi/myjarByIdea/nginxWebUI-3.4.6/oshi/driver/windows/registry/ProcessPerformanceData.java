package oshi.driver.windows.registry;

import com.sun.jna.platform.win32.WinBase;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.ProcessInformation;
import oshi.util.GlobalConfig;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class ProcessPerformanceData {
   private static final String PROCESS = "Process";
   public static final String WIN_HKEY_PERFDATA = "oshi.os.windows.hkeyperfdata";
   private static final boolean PERFDATA = GlobalConfig.get("oshi.os.windows.hkeyperfdata", true);

   private ProcessPerformanceData() {
   }

   public static Map<Integer, PerfCounterBlock> buildProcessMapFromRegistry(Collection<Integer> pids) {
      Triplet<List<Map<ProcessInformation.ProcessPerformanceProperty, Object>>, Long, Long> processData = null;
      if (PERFDATA) {
         processData = HkeyPerformanceDataUtil.readPerfDataFromRegistry("Process", ProcessInformation.ProcessPerformanceProperty.class);
      }

      if (processData == null) {
         return null;
      } else {
         List<Map<ProcessInformation.ProcessPerformanceProperty, Object>> processInstanceMaps = (List)processData.getA();
         long now = (Long)processData.getC();
         Map<Integer, PerfCounterBlock> processMap = new HashMap();
         Iterator var6 = processInstanceMaps.iterator();

         while(true) {
            Map processInstanceMap;
            int pid;
            String name;
            do {
               if (!var6.hasNext()) {
                  return processMap;
               }

               processInstanceMap = (Map)var6.next();
               pid = (Integer)processInstanceMap.get(ProcessInformation.ProcessPerformanceProperty.PROCESSID);
               name = (String)processInstanceMap.get(ProcessInformation.ProcessPerformanceProperty.NAME);
            } while(pids != null && !pids.contains(pid));

            if (!"_Total".equals(name)) {
               long ctime = (Long)processInstanceMap.get(ProcessInformation.ProcessPerformanceProperty.CREATIONDATE);
               if (ctime > now) {
                  ctime = WinBase.FILETIME.filetimeToDate((int)(ctime >> 32), (int)(ctime & 4294967295L)).getTime();
               }

               long upTime = now - ctime;
               if (upTime < 1L) {
                  upTime = 1L;
               }

               processMap.put(pid, new PerfCounterBlock(name, (Integer)processInstanceMap.get(ProcessInformation.ProcessPerformanceProperty.PARENTPROCESSID), (Integer)processInstanceMap.get(ProcessInformation.ProcessPerformanceProperty.PRIORITY), (Long)processInstanceMap.get(ProcessInformation.ProcessPerformanceProperty.PRIVATEPAGECOUNT), ctime, upTime, (Long)processInstanceMap.get(ProcessInformation.ProcessPerformanceProperty.READTRANSFERCOUNT), (Long)processInstanceMap.get(ProcessInformation.ProcessPerformanceProperty.WRITETRANSFERCOUNT), (Integer)processInstanceMap.get(ProcessInformation.ProcessPerformanceProperty.PAGEFAULTSPERSEC)));
            }
         }
      }
   }

   public static Map<Integer, PerfCounterBlock> buildProcessMapFromPerfCounters(Collection<Integer> pids) {
      Map<Integer, PerfCounterBlock> processMap = new HashMap();
      Pair<List<String>, Map<ProcessInformation.ProcessPerformanceProperty, List<Long>>> instanceValues = ProcessInformation.queryProcessCounters();
      long now = System.currentTimeMillis();
      List<String> instances = (List)instanceValues.getA();
      Map<ProcessInformation.ProcessPerformanceProperty, List<Long>> valueMap = (Map)instanceValues.getB();
      List<Long> pidList = (List)valueMap.get(ProcessInformation.ProcessPerformanceProperty.PROCESSID);
      List<Long> ppidList = (List)valueMap.get(ProcessInformation.ProcessPerformanceProperty.PARENTPROCESSID);
      List<Long> priorityList = (List)valueMap.get(ProcessInformation.ProcessPerformanceProperty.PRIORITY);
      List<Long> ioReadList = (List)valueMap.get(ProcessInformation.ProcessPerformanceProperty.READTRANSFERCOUNT);
      List<Long> ioWriteList = (List)valueMap.get(ProcessInformation.ProcessPerformanceProperty.WRITETRANSFERCOUNT);
      List<Long> workingSetSizeList = (List)valueMap.get(ProcessInformation.ProcessPerformanceProperty.PRIVATEPAGECOUNT);
      List<Long> creationTimeList = (List)valueMap.get(ProcessInformation.ProcessPerformanceProperty.CREATIONDATE);
      List<Long> pageFaultsList = (List)valueMap.get(ProcessInformation.ProcessPerformanceProperty.PAGEFAULTSPERSEC);

      for(int inst = 0; inst < instances.size(); ++inst) {
         int pid = ((Long)pidList.get(inst)).intValue();
         if (pids == null || pids.contains(pid)) {
            long ctime = (Long)creationTimeList.get(inst);
            if (ctime > now) {
               ctime = WinBase.FILETIME.filetimeToDate((int)(ctime >> 32), (int)(ctime & 4294967295L)).getTime();
            }

            long upTime = now - ctime;
            if (upTime < 1L) {
               upTime = 1L;
            }

            processMap.put(pid, new PerfCounterBlock((String)instances.get(inst), ((Long)ppidList.get(inst)).intValue(), ((Long)priorityList.get(inst)).intValue(), (Long)workingSetSizeList.get(inst), ctime, upTime, (Long)ioReadList.get(inst), (Long)ioWriteList.get(inst), ((Long)pageFaultsList.get(inst)).intValue()));
         }
      }

      return processMap;
   }

   @Immutable
   public static class PerfCounterBlock {
      private final String name;
      private final int parentProcessID;
      private final int priority;
      private final long residentSetSize;
      private final long startTime;
      private final long upTime;
      private final long bytesRead;
      private final long bytesWritten;
      private final int pageFaults;

      public PerfCounterBlock(String name, int parentProcessID, int priority, long residentSetSize, long startTime, long upTime, long bytesRead, long bytesWritten, int pageFaults) {
         this.name = name;
         this.parentProcessID = parentProcessID;
         this.priority = priority;
         this.residentSetSize = residentSetSize;
         this.startTime = startTime;
         this.upTime = upTime;
         this.bytesRead = bytesRead;
         this.bytesWritten = bytesWritten;
         this.pageFaults = pageFaults;
      }

      public String getName() {
         return this.name;
      }

      public int getParentProcessID() {
         return this.parentProcessID;
      }

      public int getPriority() {
         return this.priority;
      }

      public long getResidentSetSize() {
         return this.residentSetSize;
      }

      public long getStartTime() {
         return this.startTime;
      }

      public long getUpTime() {
         return this.upTime;
      }

      public long getBytesRead() {
         return this.bytesRead;
      }

      public long getBytesWritten() {
         return this.bytesWritten;
      }

      public long getPageFaults() {
         return (long)this.pageFaults;
      }
   }
}

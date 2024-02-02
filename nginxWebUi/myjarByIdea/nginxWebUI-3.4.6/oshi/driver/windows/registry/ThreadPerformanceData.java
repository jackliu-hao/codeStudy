package oshi.driver.windows.registry;

import com.sun.jna.platform.win32.WinBase;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.ThreadInformation;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class ThreadPerformanceData {
   private static final String THREAD = "Thread";

   private ThreadPerformanceData() {
   }

   public static Map<Integer, PerfCounterBlock> buildThreadMapFromRegistry(Collection<Integer> pids) {
      Triplet<List<Map<ThreadInformation.ThreadPerformanceProperty, Object>>, Long, Long> threadData = HkeyPerformanceDataUtil.readPerfDataFromRegistry("Thread", ThreadInformation.ThreadPerformanceProperty.class);
      if (threadData == null) {
         return null;
      } else {
         List<Map<ThreadInformation.ThreadPerformanceProperty, Object>> threadInstanceMaps = (List)threadData.getA();
         long perfTime100nSec = (Long)threadData.getB();
         long now = (Long)threadData.getC();
         Map<Integer, PerfCounterBlock> threadMap = new HashMap();
         Iterator var8 = threadInstanceMaps.iterator();

         while(true) {
            Map threadInstanceMap;
            int pid;
            do {
               if (!var8.hasNext()) {
                  return threadMap;
               }

               threadInstanceMap = (Map)var8.next();
               pid = (Integer)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.IDPROCESS);
            } while(pids != null && !pids.contains(pid));

            if (pid > 0) {
               int tid = (Integer)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.IDTHREAD);
               String name = (String)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.NAME);
               long upTime = (perfTime100nSec - (Long)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.ELAPSEDTIME)) / 10000L;
               if (upTime < 1L) {
                  upTime = 1L;
               }

               long user = (Long)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.PERCENTUSERTIME) / 10000L;
               long kernel = (Long)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.PERCENTPRIVILEGEDTIME) / 10000L;
               int priority = (Integer)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.PRIORITYCURRENT);
               int threadState = (Integer)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.THREADSTATE);
               long startAddr = (Long)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.STARTADDRESS);
               int contextSwitches = (Integer)threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.CONTEXTSWITCHESPERSEC);
               threadMap.put(tid, new PerfCounterBlock(name, tid, pid, now - upTime, user, kernel, priority, threadState, startAddr, contextSwitches));
            }
         }
      }
   }

   public static Map<Integer, PerfCounterBlock> buildThreadMapFromPerfCounters(Collection<Integer> pids) {
      Map<Integer, PerfCounterBlock> threadMap = new HashMap();
      Pair<List<String>, Map<ThreadInformation.ThreadPerformanceProperty, List<Long>>> instanceValues = ThreadInformation.queryThreadCounters();
      long now = System.currentTimeMillis();
      List<String> instances = (List)instanceValues.getA();
      Map<ThreadInformation.ThreadPerformanceProperty, List<Long>> valueMap = (Map)instanceValues.getB();
      List<Long> tidList = (List)valueMap.get(ThreadInformation.ThreadPerformanceProperty.IDTHREAD);
      List<Long> pidList = (List)valueMap.get(ThreadInformation.ThreadPerformanceProperty.IDPROCESS);
      List<Long> userList = (List)valueMap.get(ThreadInformation.ThreadPerformanceProperty.PERCENTUSERTIME);
      List<Long> kernelList = (List)valueMap.get(ThreadInformation.ThreadPerformanceProperty.PERCENTPRIVILEGEDTIME);
      List<Long> startTimeList = (List)valueMap.get(ThreadInformation.ThreadPerformanceProperty.ELAPSEDTIME);
      List<Long> priorityList = (List)valueMap.get(ThreadInformation.ThreadPerformanceProperty.PRIORITYCURRENT);
      List<Long> stateList = (List)valueMap.get(ThreadInformation.ThreadPerformanceProperty.THREADSTATE);
      List<Long> startAddrList = (List)valueMap.get(ThreadInformation.ThreadPerformanceProperty.STARTADDRESS);
      List<Long> contextSwitchesList = (List)valueMap.get(ThreadInformation.ThreadPerformanceProperty.CONTEXTSWITCHESPERSEC);
      int nameIndex = 0;

      for(int inst = 0; inst < instances.size(); ++inst) {
         int pid = ((Long)pidList.get(inst)).intValue();
         if (pids == null || pids.contains(pid)) {
            int tid = ((Long)tidList.get(inst)).intValue();
            String name = Integer.toString(nameIndex++);
            long startTime = (Long)startTimeList.get(inst);
            startTime = WinBase.FILETIME.filetimeToDate((int)(startTime >> 32), (int)(startTime & 4294967295L)).getTime();
            if (startTime > now) {
               startTime = now - 1L;
            }

            long user = (Long)userList.get(inst) / 10000L;
            long kernel = (Long)kernelList.get(inst) / 10000L;
            int priority = ((Long)priorityList.get(inst)).intValue();
            int threadState = ((Long)stateList.get(inst)).intValue();
            long startAddr = (Long)startAddrList.get(inst);
            int contextSwitches = ((Long)contextSwitchesList.get(inst)).intValue();
            threadMap.put(tid, new PerfCounterBlock(name, tid, pid, startTime, user, kernel, priority, threadState, startAddr, contextSwitches));
         }
      }

      return threadMap;
   }

   @Immutable
   public static class PerfCounterBlock {
      private final String name;
      private final int threadID;
      private final int owningProcessID;
      private final long startTime;
      private final long userTime;
      private final long kernelTime;
      private final int priority;
      private final int threadState;
      private final long startAddress;
      private final int contextSwitches;

      public PerfCounterBlock(String name, int threadID, int owningProcessID, long startTime, long userTime, long kernelTime, int priority, int threadState, long startAddress, int contextSwitches) {
         this.name = name;
         this.threadID = threadID;
         this.owningProcessID = owningProcessID;
         this.startTime = startTime;
         this.userTime = userTime;
         this.kernelTime = kernelTime;
         this.priority = priority;
         this.threadState = threadState;
         this.startAddress = startAddress;
         this.contextSwitches = contextSwitches;
      }

      public String getName() {
         return this.name;
      }

      public int getThreadID() {
         return this.threadID;
      }

      public int getOwningProcessID() {
         return this.owningProcessID;
      }

      public long getStartTime() {
         return this.startTime;
      }

      public long getUserTime() {
         return this.userTime;
      }

      public long getKernelTime() {
         return this.kernelTime;
      }

      public int getPriority() {
         return this.priority;
      }

      public int getThreadState() {
         return this.threadState;
      }

      public long getStartAddress() {
         return this.startAddress;
      }

      public int getContextSwitches() {
         return this.contextSwitches;
      }
   }
}

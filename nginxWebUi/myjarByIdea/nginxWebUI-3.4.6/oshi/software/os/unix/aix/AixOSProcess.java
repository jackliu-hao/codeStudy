package oshi.software.os.unix.aix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.aix.perfstat.PerfstatCpu;
import oshi.jna.platform.unix.aix.Perfstat;
import oshi.software.common.AbstractOSProcess;
import oshi.software.os.OSProcess;
import oshi.software.os.OSThread;
import oshi.util.ExecutingCommand;
import oshi.util.LsofUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public class AixOSProcess extends AbstractOSProcess {
   private Supplier<Integer> bitness = Memoizer.memoize(this::queryBitness);
   private final Supplier<Long> affinityMask = Memoizer.memoize(PerfstatCpu::queryCpuAffinityMask, Memoizer.defaultExpiration());
   private String name;
   private String path = "";
   private String commandLine;
   private String user;
   private String userID;
   private String group;
   private String groupID;
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
   private long majorFaults;
   private Supplier<Perfstat.perfstat_process_t[]> procCpu;

   public AixOSProcess(int pid, String[] split, Map<Integer, Pair<Long, Long>> cpuMap, Supplier<Perfstat.perfstat_process_t[]> procCpu) {
      super(pid);
      this.state = OSProcess.State.INVALID;
      this.procCpu = procCpu;
      this.updateAttributes(split, cpuMap);
   }

   public String getName() {
      return this.name;
   }

   public String getPath() {
      return this.path;
   }

   public String getCommandLine() {
      return this.commandLine;
   }

   public String getCurrentWorkingDirectory() {
      return LsofUtil.getCwd(this.getProcessID());
   }

   public String getUser() {
      return this.user;
   }

   public String getUserID() {
      return this.userID;
   }

   public String getGroup() {
      return this.group;
   }

   public String getGroupID() {
      return this.groupID;
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
      return LsofUtil.getOpenFiles(this.getProcessID());
   }

   public int getBitness() {
      return (Integer)this.bitness.get();
   }

   private int queryBitness() {
      List<String> pflags = ExecutingCommand.runNative("pflags " + this.getProcessID());
      Iterator var2 = pflags.iterator();

      while(var2.hasNext()) {
         String line = (String)var2.next();
         if (line.contains("data model")) {
            if (line.contains("LP32")) {
               return 32;
            }

            if (line.contains("LP64")) {
               return 64;
            }
         }
      }

      return 0;
   }

   public long getAffinityMask() {
      long mask = 0L;
      List<String> processAffinityInfoList = ExecutingCommand.runNative("ps -m -o THREAD -p " + this.getProcessID());
      if (processAffinityInfoList.size() > 2) {
         processAffinityInfoList.remove(0);
         processAffinityInfoList.remove(0);
         Iterator var4 = processAffinityInfoList.iterator();

         while(var4.hasNext()) {
            String processAffinityInfo = (String)var4.next();
            String[] threadInfoSplit = ParseUtil.whitespaces.split(processAffinityInfo.trim());
            if (threadInfoSplit.length > 13 && threadInfoSplit[4].charAt(0) != 'Z') {
               if (threadInfoSplit[11].charAt(0) == '-') {
                  return (Long)this.affinityMask.get();
               }

               int affinity = ParseUtil.parseIntOrDefault(threadInfoSplit[11], 0);
               mask |= 1L << affinity;
            }
         }
      }

      return mask;
   }

   public List<OSThread> getThreadDetails() {
      List<String> threadListInfoPs = ExecutingCommand.runNative("ps -m -o THREAD -p " + this.getProcessID());
      if (threadListInfoPs.size() > 2) {
         List<OSThread> threads = new ArrayList();
         threadListInfoPs.remove(0);
         threadListInfoPs.remove(0);
         Iterator var3 = threadListInfoPs.iterator();

         while(var3.hasNext()) {
            String threadInfo = (String)var3.next();
            String[] threadInfoSplit = ParseUtil.whitespaces.split(threadInfo.trim());
            if (threadInfoSplit.length == 13) {
               String[] split = new String[]{threadInfoSplit[3], threadInfoSplit[4], threadInfoSplit[6]};
               threads.add(new AixOSThread(this.getProcessID(), split));
            }
         }

         return threads;
      } else {
         return Collections.emptyList();
      }
   }

   public long getMajorFaults() {
      return this.majorFaults;
   }

   public boolean updateAttributes() {
      Perfstat.perfstat_process_t[] perfstat = (Perfstat.perfstat_process_t[])this.procCpu.get();
      List<String> procList = ExecutingCommand.runNative("ps -o s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args -p " + this.getProcessID());
      Map<Integer, Pair<Long, Long>> cpuMap = new HashMap();
      Perfstat.perfstat_process_t[] var4 = perfstat;
      int var5 = perfstat.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Perfstat.perfstat_process_t stat = var4[var6];
         cpuMap.put((int)stat.pid, new Pair((long)stat.ucpu_time, (long)stat.scpu_time));
      }

      if (procList.size() > 1) {
         String[] split = ParseUtil.whitespaces.split(((String)procList.get(1)).trim(), 15);
         if (split.length == 15) {
            return this.updateAttributes(split, cpuMap);
         }
      }

      this.state = OSProcess.State.INVALID;
      return false;
   }

   private boolean updateAttributes(String[] split, Map<Integer, Pair<Long, Long>> cpuMap) {
      long now = System.currentTimeMillis();
      this.state = getStateFromOutput(split[0].charAt(0));
      this.parentProcessID = ParseUtil.parseIntOrDefault(split[2], 0);
      this.user = split[3];
      this.userID = split[4];
      this.group = split[5];
      this.groupID = split[6];
      this.threadCount = ParseUtil.parseIntOrDefault(split[7], 0);
      this.priority = ParseUtil.parseIntOrDefault(split[8], 0);
      this.virtualSize = ParseUtil.parseLongOrDefault(split[9], 0L) << 10;
      this.residentSetSize = ParseUtil.parseLongOrDefault(split[10], 0L) << 10;
      long elapsedTime = ParseUtil.parseDHMSOrDefault(split[11], 0L);
      if (cpuMap.containsKey(this.getProcessID())) {
         Pair<Long, Long> userSystem = (Pair)cpuMap.get(this.getProcessID());
         this.userTime = (Long)userSystem.getA();
         this.kernelTime = (Long)userSystem.getB();
      } else {
         this.userTime = ParseUtil.parseDHMSOrDefault(split[12], 0L);
         this.kernelTime = 0L;
      }

      for(this.upTime = elapsedTime < 1L ? 1L : elapsedTime; this.upTime < this.userTime + this.kernelTime; this.upTime += 500L) {
      }

      this.startTime = now - this.upTime;
      this.name = split[13];
      this.majorFaults = ParseUtil.parseLongOrDefault(split[14], 0L);
      this.path = ParseUtil.whitespaces.split(split[15])[0];
      this.commandLine = split[15];
      return true;
   }

   static OSProcess.State getStateFromOutput(char stateValue) {
      OSProcess.State state;
      switch (stateValue) {
         case 'A':
         case 'R':
            state = OSProcess.State.RUNNING;
            break;
         case 'B':
         case 'C':
         case 'D':
         case 'E':
         case 'F':
         case 'G':
         case 'H':
         case 'J':
         case 'K':
         case 'L':
         case 'M':
         case 'N':
         case 'P':
         case 'Q':
         case 'U':
         case 'V':
         case 'X':
         case 'Y':
         default:
            state = OSProcess.State.OTHER;
            break;
         case 'I':
            state = OSProcess.State.WAITING;
            break;
         case 'O':
            state = OSProcess.State.INVALID;
            break;
         case 'S':
         case 'W':
            state = OSProcess.State.SLEEPING;
            break;
         case 'T':
            state = OSProcess.State.STOPPED;
            break;
         case 'Z':
            state = OSProcess.State.ZOMBIE;
      }

      return state;
   }
}

package oshi.software.os.unix.solaris;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractOSProcess;
import oshi.software.os.OSProcess;
import oshi.software.os.OSThread;
import oshi.util.ExecutingCommand;
import oshi.util.LsofUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;

@ThreadSafe
public class SolarisOSProcess extends AbstractOSProcess {
   private Supplier<Integer> bitness = Memoizer.memoize(this::queryBitness);
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

   public SolarisOSProcess(int pid, String[] split) {
      super(pid);
      this.state = OSProcess.State.INVALID;
      this.updateAttributes(split);
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
      long bitMask = 0L;
      String cpuset = ExecutingCommand.getFirstAnswer("pbind -q " + this.getProcessID());
      if (cpuset.isEmpty()) {
         List<String> allProcs = ExecutingCommand.runNative("psrinfo");
         Iterator var10 = allProcs.iterator();

         while(var10.hasNext()) {
            String proc = (String)var10.next();
            String[] split = ParseUtil.whitespaces.split(proc);
            int bitToSet = ParseUtil.parseIntOrDefault(split[0], -1);
            if (bitToSet >= 0) {
               bitMask |= 1L << bitToSet;
            }
         }

         return bitMask;
      } else {
         if (cpuset.endsWith(".") && cpuset.contains("strongly bound to processor(s)")) {
            String parse = cpuset.substring(0, cpuset.length() - 1);
            String[] split = ParseUtil.whitespaces.split(parse);

            for(int i = split.length - 1; i >= 0; --i) {
               int bitToSet = ParseUtil.parseIntOrDefault(split[i], -1);
               if (bitToSet < 0) {
                  break;
               }

               bitMask |= 1L << bitToSet;
            }
         }

         return bitMask;
      }
   }

   public List<OSThread> getThreadDetails() {
      List<String> threadListInfo1 = ExecutingCommand.runNative("ps -o lwp,s,etime,stime,time,addr,pri -p " + this.getProcessID());
      List<String> threadListInfo2 = ExecutingCommand.runNative("prstat -L -v -p " + this.getProcessID());
      Map<Integer, String[]> threadMap = parseAndMergeThreadInfo(threadListInfo1, threadListInfo2);
      return threadMap.keySet().size() > 1 ? (List)threadMap.entrySet().stream().map((entry) -> {
         return new SolarisOSThread(this.getProcessID(), (String[])entry.getValue());
      }).collect(Collectors.toList()) : Collections.emptyList();
   }

   public boolean updateAttributes() {
      List<String> procList = ExecutingCommand.runNative("ps -o s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args -p " + this.getProcessID());
      if (procList.size() > 1) {
         String[] split = ParseUtil.whitespaces.split(((String)procList.get(1)).trim(), 15);
         if (split.length == 15) {
            return this.updateAttributes(split);
         }
      }

      this.state = OSProcess.State.INVALID;
      return false;
   }

   private boolean updateAttributes(String[] split) {
      long now = System.currentTimeMillis();
      this.state = getStateFromOutput(split[0].charAt(0));
      this.parentProcessID = ParseUtil.parseIntOrDefault(split[2], 0);
      this.user = split[3];
      this.userID = split[4];
      this.group = split[5];
      this.groupID = split[6];
      this.threadCount = ParseUtil.parseIntOrDefault(split[7], 0);
      this.priority = ParseUtil.parseIntOrDefault(split[8], 0);
      this.virtualSize = ParseUtil.parseLongOrDefault(split[9], 0L) * 1024L;
      this.residentSetSize = ParseUtil.parseLongOrDefault(split[10], 0L) * 1024L;
      long elapsedTime = ParseUtil.parseDHMSOrDefault(split[11], 0L);
      this.upTime = elapsedTime < 1L ? 1L : elapsedTime;
      this.startTime = now - this.upTime;
      this.kernelTime = 0L;
      this.userTime = ParseUtil.parseDHMSOrDefault(split[12], 0L);
      this.path = split[13];
      this.name = this.path.substring(this.path.lastIndexOf(47) + 1);
      this.commandLine = split[14];
      return true;
   }

   static Map<Integer, String[]> parseAndMergeThreadInfo(List<String> psThreadInfo, List<String> prstatThreadInfo) {
      Map<Integer, String[]> map = new HashMap();
      String[] mergedSplit = new String[9];
      if (psThreadInfo.size() > 1) {
         psThreadInfo.stream().skip(1L).forEach((threadInfo) -> {
            String[] psSplit = ParseUtil.whitespaces.split(threadInfo.trim());
            if (psSplit.length == 7) {
               for(int idx = 0; idx < psSplit.length; ++idx) {
                  if (idx == 0) {
                     map.put(ParseUtil.parseIntOrDefault(psSplit[idx], 0), mergedSplit);
                  }

                  mergedSplit[idx] = psSplit[idx];
               }
            }

         });
         if (prstatThreadInfo.size() > 1) {
            prstatThreadInfo.stream().skip(1L).forEach((threadInfo) -> {
               String[] splitPrstat = ParseUtil.whitespaces.split(threadInfo.trim());
               if (splitPrstat.length == 15) {
                  int idxAfterForwardSlash = splitPrstat[14].lastIndexOf(47) + 1;
                  if (idxAfterForwardSlash > 0 && idxAfterForwardSlash < splitPrstat[14].length()) {
                     String threadId = splitPrstat[14].substring(idxAfterForwardSlash);
                     String[] existingSplit = (String[])map.get(Integer.parseInt(threadId));
                     if (existingSplit != null) {
                        existingSplit[7] = splitPrstat[10];
                        existingSplit[8] = splitPrstat[11];
                     }
                  }
               }

            });
         }
      }

      return map;
   }

   static OSProcess.State getStateFromOutput(char stateValue) {
      OSProcess.State state;
      switch (stateValue) {
         case 'O':
            state = OSProcess.State.RUNNING;
            break;
         case 'P':
         case 'Q':
         case 'U':
         case 'V':
         case 'X':
         case 'Y':
         default:
            state = OSProcess.State.OTHER;
            break;
         case 'R':
         case 'W':
            state = OSProcess.State.WAITING;
            break;
         case 'S':
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

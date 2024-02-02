package oshi.software.os.unix.solaris;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import oshi.software.common.AbstractOSThread;
import oshi.software.os.OSProcess;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

public class SolarisOSThread extends AbstractOSThread {
   private int threadId;
   private OSProcess.State state;
   private long startMemoryAddress;
   private long contextSwitches;
   private long kernelTime;
   private long userTime;
   private long startTime;
   private long upTime;
   private int priority;

   public SolarisOSThread(int pid, String[] split) {
      super(pid);
      this.state = OSProcess.State.INVALID;
      this.updateAttributes(split);
   }

   public int getThreadId() {
      return this.threadId;
   }

   public OSProcess.State getState() {
      return this.state;
   }

   public long getStartMemoryAddress() {
      return this.startMemoryAddress;
   }

   public long getContextSwitches() {
      return this.contextSwitches;
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

   public int getPriority() {
      return this.priority;
   }

   public boolean updateAttributes() {
      List<String> threadListInfo1 = ExecutingCommand.runNative("ps -o lwp,s,etime,stime,time,addr,pri -p " + this.getOwningProcessId());
      List<String> threadListInfo2 = ExecutingCommand.runNative("prstat -L -v -p " + this.getOwningProcessId());
      Map<Integer, String[]> threadMap = SolarisOSProcess.parseAndMergeThreadInfo(threadListInfo1, threadListInfo2);
      if (threadMap.keySet().size() > 1) {
         Optional<String[]> split = threadMap.entrySet().stream().filter((entry) -> {
            return (Integer)entry.getKey() == this.getThreadId();
         }).map(Map.Entry::getValue).findFirst();
         if (split.isPresent()) {
            return this.updateAttributes((String[])split.get());
         }
      }

      this.state = OSProcess.State.INVALID;
      return false;
   }

   private boolean updateAttributes(String[] split) {
      this.threadId = ParseUtil.parseIntOrDefault(split[0], 0);
      this.state = SolarisOSProcess.getStateFromOutput(split[1].charAt(0));
      long elapsedTime = ParseUtil.parseDHMSOrDefault(split[2], 0L);
      this.upTime = elapsedTime < 1L ? 1L : elapsedTime;
      long now = System.currentTimeMillis();
      this.startTime = now - this.upTime;
      this.kernelTime = ParseUtil.parseDHMSOrDefault(split[3], 0L);
      this.userTime = ParseUtil.parseDHMSOrDefault(split[4], 0L) - this.kernelTime;
      this.startMemoryAddress = ParseUtil.hexStringToLong(split[5], 0L);
      this.priority = ParseUtil.parseIntOrDefault(split[6], 0);
      long nonVoluntaryContextSwitches = ParseUtil.parseLongOrDefault(split[7], 0L);
      long voluntaryContextSwitches = ParseUtil.parseLongOrDefault(split[8], 0L);
      this.contextSwitches = voluntaryContextSwitches + nonVoluntaryContextSwitches;
      return true;
   }
}

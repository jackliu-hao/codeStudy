package oshi.software.os.unix.freebsd;

import java.util.Iterator;
import java.util.List;
import oshi.software.common.AbstractOSThread;
import oshi.software.os.OSProcess;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

public class FreeBsdOSThread extends AbstractOSThread {
   private int threadId;
   private String name = "";
   private OSProcess.State state;
   private long minorFaults;
   private long majorFaults;
   private long startMemoryAddress;
   private long contextSwitches;
   private long kernelTime;
   private long userTime;
   private long startTime;
   private long upTime;
   private int priority;

   public FreeBsdOSThread(int processId, String[] split) {
      super(processId);
      this.state = OSProcess.State.INVALID;
      this.updateAttributes(split);
   }

   public int getThreadId() {
      return this.threadId;
   }

   public String getName() {
      return this.name;
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

   public long getMinorFaults() {
      return this.minorFaults;
   }

   public long getMajorFaults() {
      return this.majorFaults;
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
      String psCommand = "ps -awwxo tdname,lwp,state,etimes,systime,time,tdaddr,nivcsw,nvcsw,majflt,minflt,pri -H -p " + this.getOwningProcessId();
      List<String> threadList = ExecutingCommand.runNative(psCommand);
      Iterator var3 = threadList.iterator();

      String[] split;
      do {
         if (!var3.hasNext()) {
            this.state = OSProcess.State.INVALID;
            return false;
         }

         String psOutput = (String)var3.next();
         split = ParseUtil.whitespaces.split(psOutput.trim());
      } while(split.length <= 1 || this.getThreadId() != ParseUtil.parseIntOrDefault(split[1], 0));

      return this.updateAttributes(split);
   }

   private boolean updateAttributes(String[] split) {
      if (split.length != 12) {
         this.state = OSProcess.State.INVALID;
         return false;
      } else {
         this.name = split[0];
         this.threadId = ParseUtil.parseIntOrDefault(split[1], 0);
         switch (split[2].charAt(0)) {
            case 'D':
            case 'L':
            case 'U':
               this.state = OSProcess.State.WAITING;
               break;
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            default:
               this.state = OSProcess.State.OTHER;
               break;
            case 'I':
            case 'S':
               this.state = OSProcess.State.SLEEPING;
               break;
            case 'R':
               this.state = OSProcess.State.RUNNING;
               break;
            case 'T':
               this.state = OSProcess.State.STOPPED;
               break;
            case 'Z':
               this.state = OSProcess.State.ZOMBIE;
         }

         long elapsedTime = ParseUtil.parseDHMSOrDefault(split[3], 0L);
         this.upTime = elapsedTime < 1L ? 1L : elapsedTime;
         long now = System.currentTimeMillis();
         this.startTime = now - this.upTime;
         this.kernelTime = ParseUtil.parseDHMSOrDefault(split[4], 0L);
         this.userTime = ParseUtil.parseDHMSOrDefault(split[5], 0L) - this.kernelTime;
         this.startMemoryAddress = ParseUtil.hexStringToLong(split[6], 0L);
         long nonVoluntaryContextSwitches = ParseUtil.parseLongOrDefault(split[7], 0L);
         long voluntaryContextSwitches = ParseUtil.parseLongOrDefault(split[8], 0L);
         this.contextSwitches = voluntaryContextSwitches + nonVoluntaryContextSwitches;
         this.majorFaults = ParseUtil.parseLongOrDefault(split[9], 0L);
         this.minorFaults = ParseUtil.parseLongOrDefault(split[10], 0L);
         this.priority = ParseUtil.parseIntOrDefault(split[11], 0);
         return true;
      }
   }
}

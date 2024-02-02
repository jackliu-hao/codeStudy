package oshi.software.os.unix.freebsd;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.freebsd.FreeBsdLibc;
import oshi.software.common.AbstractOSProcess;
import oshi.software.os.OSProcess;
import oshi.software.os.OSThread;
import oshi.util.ExecutingCommand;
import oshi.util.LsofUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;

@ThreadSafe
public class FreeBsdOSProcess extends AbstractOSProcess {
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
   private long minorFaults;
   private long majorFaults;

   public FreeBsdOSProcess(int pid, String[] split) {
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

   public long getAffinityMask() {
      long bitMask = 0L;
      String cpuset = ExecutingCommand.getFirstAnswer("cpuset -gp " + this.getProcessID());
      String[] split = cpuset.split(":");
      if (split.length > 1) {
         String[] bits = split[1].split(",");
         String[] var6 = bits;
         int var7 = bits.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String bit = var6[var8];
            int bitToSet = ParseUtil.parseIntOrDefault(bit.trim(), -1);
            if (bitToSet >= 0) {
               bitMask |= 1L << bitToSet;
            }
         }
      }

      return bitMask;
   }

   private int queryBitness() {
      int[] mib = new int[]{1, 14, 9, this.getProcessID()};
      Pointer abi = new Memory(32L);
      IntByReference size = new IntByReference(32);
      if (0 == FreeBsdLibc.INSTANCE.sysctl(mib, mib.length, abi, size, (Pointer)null, 0)) {
         String elf = abi.getString(0L);
         if (elf.contains("ELF32")) {
            return 32;
         }

         if (elf.contains("ELF64")) {
            return 64;
         }
      }

      return 0;
   }

   public List<OSThread> getThreadDetails() {
      List<OSThread> threads = new ArrayList();
      String psCommand = "ps -awwxo tdname,lwp,state,etimes,systime,time,tdaddr,nivcsw,nvcsw,majflt,minflt,pri -H";
      if (this.getProcessID() >= 0) {
         psCommand = psCommand + " -p " + this.getProcessID();
      }

      List<String> threadList = ExecutingCommand.runNative(psCommand);
      if (!threadList.isEmpty() && threadList.size() >= 2) {
         threadList.remove(0);
         Iterator var4 = threadList.iterator();

         while(var4.hasNext()) {
            String thread = (String)var4.next();
            String[] split = ParseUtil.whitespaces.split(thread.trim(), 12);
            if (split.length == 10) {
               threads.add(new FreeBsdOSThread(this.getProcessID(), split));
            }
         }

         return threads;
      } else {
         return threads;
      }
   }

   public long getMinorFaults() {
      return this.minorFaults;
   }

   public long getMajorFaults() {
      return this.majorFaults;
   }

   public boolean updateAttributes() {
      String psCommand = "ps -awwxo state,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etimes,systime,time,comm,args,majflt,minflt -p " + this.getProcessID();
      List<String> procList = ExecutingCommand.runNative(psCommand);
      if (procList.size() > 1) {
         String[] split = ParseUtil.whitespaces.split(((String)procList.get(1)).trim(), 18);
         if (split.length == 18) {
            return this.updateAttributes(split);
         }
      }

      this.state = OSProcess.State.INVALID;
      return false;
   }

   private boolean updateAttributes(String[] split) {
      long now = System.currentTimeMillis();
      switch (split[0].charAt(0)) {
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
      this.kernelTime = ParseUtil.parseDHMSOrDefault(split[12], 0L);
      this.userTime = ParseUtil.parseDHMSOrDefault(split[13], 0L) - this.kernelTime;
      this.path = split[14];
      this.name = this.path.substring(this.path.lastIndexOf(47) + 1);
      this.commandLine = split[15];
      this.minorFaults = ParseUtil.parseLongOrDefault(split[16], 0L);
      this.majorFaults = ParseUtil.parseLongOrDefault(split[17], 0L);
      return true;
   }
}

package oshi.software.os.linux;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.proc.ProcessStat;
import oshi.driver.linux.proc.UserGroupInfo;
import oshi.hardware.platform.linux.LinuxGlobalMemory;
import oshi.software.common.AbstractOSProcess;
import oshi.software.os.OSProcess;
import oshi.software.os.OSThread;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public class LinuxOSProcess extends AbstractOSProcess {
   private static final Logger LOG = LoggerFactory.getLogger(LinuxOSProcess.class);
   private static final String LS_F_PROC_PID_FD;
   private static final int[] PROC_PID_STAT_ORDERS;
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

   public LinuxOSProcess(int pid) {
      super(pid);
      this.state = OSProcess.State.INVALID;
      this.commandLine = FileUtil.getStringFromFile(String.format(ProcPath.PID_CMDLINE, pid));
      this.updateAttributes();
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
      try {
         String cwdLink = String.format(ProcPath.PID_CWD, this.getProcessID());
         String cwd = (new File(cwdLink)).getCanonicalPath();
         if (!cwd.equals(cwdLink)) {
            return cwd;
         }
      } catch (IOException var3) {
         LOG.trace((String)"Couldn't find cwd for pid {}: {}", (Object)this.getProcessID(), (Object)var3.getMessage());
      }

      return "";
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

   public List<OSThread> getThreadDetails() {
      List<OSThread> threadDetails = (List)ProcessStat.getThreadIds(this.getProcessID()).stream().map((id) -> {
         return new LinuxOSThread(this.getProcessID(), id);
      }).collect(Collectors.toList());
      return Collections.unmodifiableList(threadDetails);
   }

   public long getMinorFaults() {
      return this.minorFaults;
   }

   public long getMajorFaults() {
      return this.majorFaults;
   }

   public long getOpenFiles() {
      return (long)ExecutingCommand.runNative(String.format(LS_F_PROC_PID_FD, this.getProcessID())).size() - 1L;
   }

   public int getBitness() {
      return (Integer)this.bitness.get();
   }

   private int queryBitness() {
      byte[] buffer = new byte[5];
      if (!this.path.isEmpty()) {
         try {
            InputStream is = new FileInputStream(this.path);

            int var3;
            label56: {
               try {
                  if (is.read(buffer) == buffer.length) {
                     var3 = buffer[4] == 1 ? 32 : 64;
                     break label56;
                  }
               } catch (Throwable var6) {
                  try {
                     is.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }

                  throw var6;
               }

               is.close();
               return 0;
            }

            is.close();
            return var3;
         } catch (IOException var7) {
            LOG.warn((String)"Failed to read process file: {}", (Object)this.path);
         }
      }

      return 0;
   }

   public long getAffinityMask() {
      String mask = ExecutingCommand.getFirstAnswer("taskset -p " + this.getProcessID());
      String[] split = ParseUtil.whitespaces.split(mask);

      try {
         return (new BigInteger(split[split.length - 1], 16)).longValue();
      } catch (NumberFormatException var4) {
         return 0L;
      }
   }

   public boolean updateAttributes() {
      String procPidExe = String.format(ProcPath.PID_EXE, this.getProcessID());

      try {
         Path link = Paths.get(procPidExe);
         this.path = Files.readSymbolicLink(link).toString();
         int index = this.path.indexOf(" (deleted)");
         if (index != -1) {
            this.path = this.path.substring(0, index);
         }
      } catch (IOException | UnsupportedOperationException | SecurityException | InvalidPathException var8) {
         LOG.debug((String)"Unable to open symbolic link {}", (Object)procPidExe);
      }

      Map<String, String> io = FileUtil.getKeyValueMapFromFile(String.format(ProcPath.PID_IO, this.getProcessID()), ":");
      Map<String, String> status = FileUtil.getKeyValueMapFromFile(String.format(ProcPath.PID_STATUS, this.getProcessID()), ":");
      String stat = FileUtil.getStringFromFile(String.format(ProcPath.PID_STAT, this.getProcessID()));
      if (stat.isEmpty()) {
         this.state = OSProcess.State.INVALID;
         return false;
      } else {
         long now = System.currentTimeMillis();
         long[] statArray = ParseUtil.parseStringToLongArray(stat, PROC_PID_STAT_ORDERS, ProcessStat.PROC_PID_STAT_LENGTH, ' ');
         this.startTime = (LinuxOperatingSystem.BOOTTIME * LinuxOperatingSystem.getHz() + statArray[LinuxOSProcess.ProcPidStat.START_TIME.ordinal()]) * 1000L / LinuxOperatingSystem.getHz();
         if (this.startTime >= now) {
            this.startTime = now - 1L;
         }

         this.parentProcessID = (int)statArray[LinuxOSProcess.ProcPidStat.PPID.ordinal()];
         this.threadCount = (int)statArray[LinuxOSProcess.ProcPidStat.THREAD_COUNT.ordinal()];
         this.priority = (int)statArray[LinuxOSProcess.ProcPidStat.PRIORITY.ordinal()];
         this.virtualSize = statArray[LinuxOSProcess.ProcPidStat.VSZ.ordinal()];
         this.residentSetSize = statArray[LinuxOSProcess.ProcPidStat.RSS.ordinal()] * LinuxGlobalMemory.PAGE_SIZE;
         this.kernelTime = statArray[LinuxOSProcess.ProcPidStat.KERNEL_TIME.ordinal()] * 1000L / LinuxOperatingSystem.getHz();
         this.userTime = statArray[LinuxOSProcess.ProcPidStat.USER_TIME.ordinal()] * 1000L / LinuxOperatingSystem.getHz();
         this.minorFaults = statArray[LinuxOSProcess.ProcPidStat.MINOR_FAULTS.ordinal()];
         this.majorFaults = statArray[LinuxOSProcess.ProcPidStat.MAJOR_FAULTS.ordinal()];
         this.upTime = now - this.startTime;
         this.bytesRead = ParseUtil.parseLongOrDefault((String)io.getOrDefault("read_bytes", ""), 0L);
         this.bytesWritten = ParseUtil.parseLongOrDefault((String)io.getOrDefault("write_bytes", ""), 0L);
         this.userID = ParseUtil.whitespaces.split((CharSequence)status.getOrDefault("Uid", ""))[0];
         this.user = UserGroupInfo.getUser(this.userID);
         this.groupID = ParseUtil.whitespaces.split((CharSequence)status.getOrDefault("Gid", ""))[0];
         this.group = UserGroupInfo.getGroupName(this.groupID);
         this.name = (String)status.getOrDefault("Name", "");
         this.state = ProcessStat.getState(((String)status.getOrDefault("State", "U")).charAt(0));
         return true;
      }
   }

   static {
      LS_F_PROC_PID_FD = "ls -f " + ProcPath.PID_FD;
      PROC_PID_STAT_ORDERS = new int[LinuxOSProcess.ProcPidStat.values().length];
      ProcPidStat[] var0 = LinuxOSProcess.ProcPidStat.values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         ProcPidStat stat = var0[var2];
         PROC_PID_STAT_ORDERS[stat.ordinal()] = stat.getOrder() - 1;
      }

   }

   private static enum ProcPidStat {
      PPID(4),
      MINOR_FAULTS(10),
      MAJOR_FAULTS(12),
      USER_TIME(14),
      KERNEL_TIME(15),
      PRIORITY(18),
      THREAD_COUNT(20),
      START_TIME(22),
      VSZ(23),
      RSS(24);

      private int order;

      public int getOrder() {
         return this.order;
      }

      private ProcPidStat(int order) {
         this.order = order;
      }
   }
}

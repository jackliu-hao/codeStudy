package oshi.software.os.mac;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.ptr.IntByReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.mac.ThreadInfo;
import oshi.software.common.AbstractOSProcess;
import oshi.software.os.OSProcess;
import oshi.software.os.OSThread;
import oshi.util.Memoizer;
import oshi.util.platform.mac.SysctlUtil;

@ThreadSafe
public class MacOSProcess extends AbstractOSProcess {
   private static final Logger LOG = LoggerFactory.getLogger(MacOSProcess.class);
   private static final int P_LP64 = 4;
   private static final int SSLEEP = 1;
   private static final int SWAIT = 2;
   private static final int SRUN = 3;
   private static final int SIDL = 4;
   private static final int SZOMB = 5;
   private static final int SSTOP = 6;
   private int minorVersion;
   private Supplier<String> commandLine = Memoizer.memoize(this::queryCommandLine);
   private String name = "";
   private String path = "";
   private String currentWorkingDirectory;
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
   private long openFiles;
   private int bitness;
   private long minorFaults;
   private long majorFaults;

   public MacOSProcess(int pid, int minor) {
      super(pid);
      this.state = OSProcess.State.INVALID;
      this.minorVersion = minor;
      this.updateAttributes();
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

   private String queryCommandLine() {
      int[] mib = new int[]{1, 49, this.getProcessID()};
      int argmax = SysctlUtil.sysctl("kern.argmax", 0);
      Pointer procargs = new Memory((long)argmax);
      IntByReference size = new IntByReference(argmax);
      if (0 != SystemB.INSTANCE.sysctl(mib, mib.length, procargs, size, (Pointer)null, 0)) {
         LOG.warn((String)"Failed syctl call for process arguments (kern.procargs2), process {} may not exist. Error code: {}", (Object)this.getProcessID(), (Object)Native.getLastError());
         return "";
      } else {
         int nargs = procargs.getInt(0L);
         if (nargs >= 0 && nargs <= 1024) {
            List<String> args = new ArrayList(nargs);
            long offset = (long)SystemB.INT_SIZE;

            String arg;
            for(offset += (long)procargs.getString(offset).length(); nargs-- > 0 && offset < (long)size.getValue(); offset += (long)arg.length()) {
               while(procargs.getByte(offset) == 0 && ++offset < (long)size.getValue()) {
               }

               arg = procargs.getString(offset);
               args.add(arg);
            }

            return String.join("\u0000", args);
         } else {
            LOG.error((String)"Nonsensical number of process arguments for pid {}: {}", (Object)this.getProcessID(), (Object)nargs);
            return "";
         }
      }
   }

   public String getCurrentWorkingDirectory() {
      return this.currentWorkingDirectory;
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

   public List<OSThread> getThreadDetails() {
      long now = System.currentTimeMillis();
      List<MacOSThread> details = new ArrayList();
      List<ThreadInfo.ThreadStats> stats = ThreadInfo.queryTaskThreads(this.getProcessID());

      ThreadInfo.ThreadStats stat;
      long start;
      for(Iterator var5 = stats.iterator(); var5.hasNext(); details.add(new MacOSThread(this.getProcessID(), stat.getThreadId(), stat.getState(), stat.getSystemTime(), stat.getUserTime(), start, now - start, stat.getPriority()))) {
         stat = (ThreadInfo.ThreadStats)var5.next();
         start = now - stat.getUpTime();
         if (start < this.getStartTime()) {
            start = this.getStartTime();
         }
      }

      return Collections.unmodifiableList(details);
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
      int logicalProcessorCount = SysctlUtil.sysctl("hw.logicalcpu", 1);
      return logicalProcessorCount < 64 ? (1L << logicalProcessorCount) - 1L : -1L;
   }

   public long getMinorFaults() {
      return this.minorFaults;
   }

   public long getMajorFaults() {
      return this.majorFaults;
   }

   public boolean updateAttributes() {
      long now = System.currentTimeMillis();
      SystemB.ProcTaskAllInfo taskAllInfo = new SystemB.ProcTaskAllInfo();
      if (0 <= SystemB.INSTANCE.proc_pidinfo(this.getProcessID(), 2, 0L, taskAllInfo, taskAllInfo.size()) && taskAllInfo.ptinfo.pti_threadnum >= 1) {
         Pointer buf = new Memory(4096L);
         if (0 < SystemB.INSTANCE.proc_pidpath(this.getProcessID(), buf, 4096)) {
            this.path = buf.getString(0L).trim();
            String[] pathSplit = this.path.split("/");
            if (pathSplit.length > 0) {
               this.name = pathSplit[pathSplit.length - 1];
            }
         }

         if (this.name.isEmpty()) {
            for(int t = 0; t < taskAllInfo.pbsd.pbi_comm.length; ++t) {
               if (taskAllInfo.pbsd.pbi_comm[t] == 0) {
                  this.name = new String(taskAllInfo.pbsd.pbi_comm, 0, t, StandardCharsets.UTF_8);
                  break;
               }
            }
         }

         switch (taskAllInfo.pbsd.pbi_status) {
            case 1:
               this.state = OSProcess.State.SLEEPING;
               break;
            case 2:
               this.state = OSProcess.State.WAITING;
               break;
            case 3:
               this.state = OSProcess.State.RUNNING;
               break;
            case 4:
               this.state = OSProcess.State.NEW;
               break;
            case 5:
               this.state = OSProcess.State.ZOMBIE;
               break;
            case 6:
               this.state = OSProcess.State.STOPPED;
               break;
            default:
               this.state = OSProcess.State.OTHER;
         }

         this.parentProcessID = taskAllInfo.pbsd.pbi_ppid;
         this.userID = Integer.toString(taskAllInfo.pbsd.pbi_uid);
         SystemB.Passwd pwuid = SystemB.INSTANCE.getpwuid(taskAllInfo.pbsd.pbi_uid);
         if (pwuid != null) {
            this.user = pwuid.pw_name;
         }

         this.groupID = Integer.toString(taskAllInfo.pbsd.pbi_gid);
         SystemB.Group grgid = SystemB.INSTANCE.getgrgid(taskAllInfo.pbsd.pbi_gid);
         if (grgid != null) {
            this.group = grgid.gr_name;
         }

         this.threadCount = taskAllInfo.ptinfo.pti_threadnum;
         this.priority = taskAllInfo.ptinfo.pti_priority;
         this.virtualSize = taskAllInfo.ptinfo.pti_virtual_size;
         this.residentSetSize = taskAllInfo.ptinfo.pti_resident_size;
         this.kernelTime = taskAllInfo.ptinfo.pti_total_system / 1000000L;
         this.userTime = taskAllInfo.ptinfo.pti_total_user / 1000000L;
         this.startTime = taskAllInfo.pbsd.pbi_start_tvsec * 1000L + taskAllInfo.pbsd.pbi_start_tvusec / 1000L;
         this.upTime = now - this.startTime;
         this.openFiles = (long)taskAllInfo.pbsd.pbi_nfiles;
         this.bitness = (taskAllInfo.pbsd.pbi_flags & 4) == 0 ? 32 : 64;
         this.majorFaults = (long)taskAllInfo.ptinfo.pti_pageins;
         this.minorFaults = (long)(taskAllInfo.ptinfo.pti_faults - taskAllInfo.ptinfo.pti_pageins);
         if (this.minorVersion >= 9) {
            SystemB.RUsageInfoV2 rUsageInfoV2 = new SystemB.RUsageInfoV2();
            if (0 == SystemB.INSTANCE.proc_pid_rusage(this.getProcessID(), 2, rUsageInfoV2)) {
               this.bytesRead = rUsageInfoV2.ri_diskio_bytesread;
               this.bytesWritten = rUsageInfoV2.ri_diskio_byteswritten;
            }
         }

         SystemB.VnodePathInfo vpi = new SystemB.VnodePathInfo();
         if (0 < SystemB.INSTANCE.proc_pidinfo(this.getProcessID(), 9, 0L, vpi, vpi.size())) {
            int len = 0;
            byte[] var9 = vpi.pvi_cdir.vip_path;
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               byte b = var9[var11];
               if (b == 0) {
                  break;
               }

               ++len;
            }

            this.currentWorkingDirectory = new String(vpi.pvi_cdir.vip_path, 0, len, StandardCharsets.US_ASCII);
         }

         return true;
      } else {
         this.state = OSProcess.State.INVALID;
         return false;
      }
   }
}

package oshi.util.platform.linux;

import java.io.File;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.GlobalConfig;

@ThreadSafe
public final class ProcPath {
   public static final String PROC = queryProcConfig();
   public static final String ASOUND;
   public static final String CPUINFO;
   public static final String DISKSTATS;
   public static final String MEMINFO;
   public static final String MOUNTS;
   public static final String PID_CMDLINE;
   public static final String PID_CWD;
   public static final String PID_EXE;
   public static final String PID_FD;
   public static final String PID_IO;
   public static final String PID_STAT;
   public static final String PID_STATM;
   public static final String PID_STATUS;
   public static final String SELF_STAT;
   public static final String STAT;
   public static final String SYS_FS_FILE_NR;
   public static final String TASK_PATH;
   public static final String TASK_STATUS;
   public static final String TASK_STAT;
   public static final String UPTIME;
   public static final String VERSION;
   public static final String VMSTAT;

   private ProcPath() {
   }

   private static String queryProcConfig() {
      String procPath = GlobalConfig.get("oshi.util.proc.path", "/proc");
      procPath = '/' + procPath.replaceAll("/$|^/", "");
      if (!(new File(procPath)).exists()) {
         throw new GlobalConfig.PropertyException("oshi.util.proc.path", "The path does not exist");
      } else {
         return procPath;
      }
   }

   static {
      ASOUND = PROC + "/asound/";
      CPUINFO = PROC + "/cpuinfo";
      DISKSTATS = PROC + "/diskstats";
      MEMINFO = PROC + "/meminfo";
      MOUNTS = PROC + "/mounts";
      PID_CMDLINE = PROC + "/%d/cmdline";
      PID_CWD = PROC + "/%d/cwd";
      PID_EXE = PROC + "/%d/exe";
      PID_FD = PROC + "/%d/fd";
      PID_IO = PROC + "/%d/io";
      PID_STAT = PROC + "/%d/stat";
      PID_STATM = PROC + "/%d/statm";
      PID_STATUS = PROC + "/%d/status";
      SELF_STAT = PROC + "/self/stat";
      STAT = PROC + "/stat";
      SYS_FS_FILE_NR = PROC + "/sys/fs/file-nr";
      TASK_PATH = PROC + "/%d/task";
      TASK_STATUS = TASK_PATH + "/%d/status";
      TASK_STAT = TASK_PATH + "/%d/stat";
      UPTIME = PROC + "/uptime";
      VERSION = PROC + "/version";
      VMSTAT = PROC + "/vmstat";
   }
}

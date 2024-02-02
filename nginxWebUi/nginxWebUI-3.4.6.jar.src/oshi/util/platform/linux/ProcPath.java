/*    */ package oshi.util.platform.linux;
/*    */ 
/*    */ import java.io.File;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.util.GlobalConfig;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @ThreadSafe
/*    */ public final class ProcPath
/*    */ {
/* 44 */   public static final String PROC = queryProcConfig();
/*    */   
/* 46 */   public static final String ASOUND = PROC + "/asound/";
/* 47 */   public static final String CPUINFO = PROC + "/cpuinfo";
/* 48 */   public static final String DISKSTATS = PROC + "/diskstats";
/* 49 */   public static final String MEMINFO = PROC + "/meminfo";
/* 50 */   public static final String MOUNTS = PROC + "/mounts";
/* 51 */   public static final String PID_CMDLINE = PROC + "/%d/cmdline";
/* 52 */   public static final String PID_CWD = PROC + "/%d/cwd";
/* 53 */   public static final String PID_EXE = PROC + "/%d/exe";
/* 54 */   public static final String PID_FD = PROC + "/%d/fd";
/* 55 */   public static final String PID_IO = PROC + "/%d/io";
/* 56 */   public static final String PID_STAT = PROC + "/%d/stat";
/* 57 */   public static final String PID_STATM = PROC + "/%d/statm";
/* 58 */   public static final String PID_STATUS = PROC + "/%d/status";
/* 59 */   public static final String SELF_STAT = PROC + "/self/stat";
/* 60 */   public static final String STAT = PROC + "/stat";
/* 61 */   public static final String SYS_FS_FILE_NR = PROC + "/sys/fs/file-nr";
/* 62 */   public static final String TASK_PATH = PROC + "/%d/task";
/* 63 */   public static final String TASK_STATUS = TASK_PATH + "/%d/status";
/* 64 */   public static final String TASK_STAT = TASK_PATH + "/%d/stat";
/* 65 */   public static final String UPTIME = PROC + "/uptime";
/* 66 */   public static final String VERSION = PROC + "/version";
/* 67 */   public static final String VMSTAT = PROC + "/vmstat";
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static String queryProcConfig() {
/* 73 */     String procPath = GlobalConfig.get("oshi.util.proc.path", "/proc");
/*    */     
/* 75 */     procPath = '/' + procPath.replaceAll("/$|^/", "");
/* 76 */     if (!(new File(procPath)).exists()) {
/* 77 */       throw new GlobalConfig.PropertyException("oshi.util.proc.path", "The path does not exist");
/*    */     }
/* 79 */     return procPath;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\platform\linux\ProcPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
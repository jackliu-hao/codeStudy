/*     */ package oshi.software.os.mac;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.platform.mac.SystemB;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.function.Supplier;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.mac.ThreadInfo;
/*     */ import oshi.software.common.AbstractOSProcess;
/*     */ import oshi.software.os.OSProcess;
/*     */ import oshi.software.os.OSThread;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.platform.mac.SysctlUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public class MacOSProcess
/*     */   extends AbstractOSProcess
/*     */ {
/*  66 */   private static final Logger LOG = LoggerFactory.getLogger(MacOSProcess.class);
/*     */   
/*     */   private static final int P_LP64 = 4;
/*     */   
/*     */   private static final int SSLEEP = 1;
/*     */   
/*     */   private static final int SWAIT = 2;
/*     */   
/*     */   private static final int SRUN = 3;
/*     */   
/*     */   private static final int SIDL = 4;
/*     */   
/*     */   private static final int SZOMB = 5;
/*     */   
/*     */   private static final int SSTOP = 6;
/*     */   private int minorVersion;
/*  82 */   private Supplier<String> commandLine = Memoizer.memoize(this::queryCommandLine);
/*     */   
/*  84 */   private String name = "";
/*  85 */   private String path = "";
/*     */   private String currentWorkingDirectory;
/*     */   private String user;
/*     */   private String userID;
/*     */   private String group;
/*     */   private String groupID;
/*  91 */   private OSProcess.State state = OSProcess.State.INVALID;
/*     */   private int parentProcessID;
/*     */   private int threadCount;
/*     */   private int priority;
/*     */   private long virtualSize;
/*     */   private long residentSetSize;
/*     */   private long kernelTime;
/*     */   private long userTime;
/*     */   private long startTime;
/*     */   private long upTime;
/*     */   private long bytesRead;
/*     */   private long bytesWritten;
/*     */   private long openFiles;
/*     */   private int bitness;
/*     */   private long minorFaults;
/*     */   private long majorFaults;
/*     */   
/*     */   public MacOSProcess(int pid, int minor) {
/* 109 */     super(pid);
/* 110 */     this.minorVersion = minor;
/* 111 */     updateAttributes();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 116 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPath() {
/* 121 */     return this.path;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCommandLine() {
/* 126 */     return this.commandLine.get();
/*     */   }
/*     */ 
/*     */   
/*     */   private String queryCommandLine() {
/* 131 */     int[] mib = new int[3];
/* 132 */     mib[0] = 1;
/* 133 */     mib[1] = 49;
/* 134 */     mib[2] = getProcessID();
/*     */     
/* 136 */     int argmax = SysctlUtil.sysctl("kern.argmax", 0);
/* 137 */     Memory memory = new Memory(argmax);
/* 138 */     IntByReference size = new IntByReference(argmax);
/*     */     
/* 140 */     if (0 != SystemB.INSTANCE.sysctl(mib, mib.length, (Pointer)memory, size, null, 0)) {
/* 141 */       LOG.warn("Failed syctl call for process arguments (kern.procargs2), process {} may not exist. Error code: {}", 
/*     */           
/* 143 */           Integer.valueOf(getProcessID()), Integer.valueOf(Native.getLastError()));
/* 144 */       return "";
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 150 */     int nargs = memory.getInt(0L);
/*     */     
/* 152 */     if (nargs < 0 || nargs > 1024) {
/* 153 */       LOG.error("Nonsensical number of process arguments for pid {}: {}", Integer.valueOf(getProcessID()), Integer.valueOf(nargs));
/* 154 */       return "";
/*     */     } 
/* 156 */     List<String> args = new ArrayList<>(nargs);
/*     */     
/* 158 */     long offset = SystemB.INT_SIZE;
/*     */     
/* 160 */     offset += memory.getString(offset).length();
/*     */ 
/*     */     
/* 163 */     while (nargs-- > 0 && offset < size.getValue()) { do {
/*     */       
/* 165 */       } while (memory.getByte(offset) == 0 && 
/* 166 */         ++offset < size.getValue());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 171 */       String arg = memory.getString(offset);
/* 172 */       args.add(arg);
/*     */       
/* 174 */       offset += arg.length(); }
/*     */ 
/*     */     
/* 177 */     return String.join("\000", (Iterable)args);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCurrentWorkingDirectory() {
/* 182 */     return this.currentWorkingDirectory;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUser() {
/* 187 */     return this.user;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUserID() {
/* 192 */     return this.userID;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getGroup() {
/* 197 */     return this.group;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getGroupID() {
/* 202 */     return this.groupID;
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess.State getState() {
/* 207 */     return this.state;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getParentProcessID() {
/* 212 */     return this.parentProcessID;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getThreadCount() {
/* 217 */     return this.threadCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSThread> getThreadDetails() {
/* 222 */     long now = System.currentTimeMillis();
/* 223 */     List<MacOSThread> details = new ArrayList<>();
/* 224 */     List<ThreadInfo.ThreadStats> stats = ThreadInfo.queryTaskThreads(getProcessID());
/* 225 */     for (ThreadInfo.ThreadStats stat : stats) {
/*     */       
/* 227 */       long start = now - stat.getUpTime();
/* 228 */       if (start < getStartTime()) {
/* 229 */         start = getStartTime();
/*     */       }
/* 231 */       details.add(new MacOSThread(getProcessID(), stat.getThreadId(), stat.getState(), stat.getSystemTime(), stat
/* 232 */             .getUserTime(), start, now - start, stat.getPriority()));
/*     */     } 
/* 234 */     return (List)Collections.unmodifiableList(details);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPriority() {
/* 239 */     return this.priority;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getVirtualSize() {
/* 244 */     return this.virtualSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getResidentSetSize() {
/* 249 */     return this.residentSetSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getKernelTime() {
/* 254 */     return this.kernelTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUserTime() {
/* 259 */     return this.userTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUpTime() {
/* 264 */     return this.upTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartTime() {
/* 269 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesRead() {
/* 274 */     return this.bytesRead;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesWritten() {
/* 279 */     return this.bytesWritten;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOpenFiles() {
/* 284 */     return this.openFiles;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBitness() {
/* 289 */     return this.bitness;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getAffinityMask() {
/* 295 */     int logicalProcessorCount = SysctlUtil.sysctl("hw.logicalcpu", 1);
/* 296 */     return (logicalProcessorCount < 64) ? ((1L << logicalProcessorCount) - 1L) : -1L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMinorFaults() {
/* 301 */     return this.minorFaults;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMajorFaults() {
/* 306 */     return this.majorFaults;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 311 */     long now = System.currentTimeMillis();
/* 312 */     SystemB.ProcTaskAllInfo taskAllInfo = new SystemB.ProcTaskAllInfo();
/* 313 */     if (0 > SystemB.INSTANCE.proc_pidinfo(getProcessID(), 2, 0L, (Structure)taskAllInfo, taskAllInfo
/* 314 */         .size()) || taskAllInfo.ptinfo.pti_threadnum < 1) {
/* 315 */       this.state = OSProcess.State.INVALID;
/* 316 */       return false;
/*     */     } 
/* 318 */     Memory memory = new Memory(4096L);
/* 319 */     if (0 < SystemB.INSTANCE.proc_pidpath(getProcessID(), (Pointer)memory, 4096)) {
/* 320 */       this.path = memory.getString(0L).trim();
/*     */       
/* 322 */       String[] pathSplit = this.path.split("/");
/* 323 */       if (pathSplit.length > 0) {
/* 324 */         this.name = pathSplit[pathSplit.length - 1];
/*     */       }
/*     */     } 
/* 327 */     if (this.name.isEmpty())
/*     */     {
/*     */       
/* 330 */       for (int t = 0; t < taskAllInfo.pbsd.pbi_comm.length; t++) {
/* 331 */         if (taskAllInfo.pbsd.pbi_comm[t] == 0) {
/* 332 */           this.name = new String(taskAllInfo.pbsd.pbi_comm, 0, t, StandardCharsets.UTF_8);
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     }
/* 338 */     switch (taskAllInfo.pbsd.pbi_status) {
/*     */       case 1:
/* 340 */         this.state = OSProcess.State.SLEEPING;
/*     */         break;
/*     */       case 2:
/* 343 */         this.state = OSProcess.State.WAITING;
/*     */         break;
/*     */       case 3:
/* 346 */         this.state = OSProcess.State.RUNNING;
/*     */         break;
/*     */       case 4:
/* 349 */         this.state = OSProcess.State.NEW;
/*     */         break;
/*     */       case 5:
/* 352 */         this.state = OSProcess.State.ZOMBIE;
/*     */         break;
/*     */       case 6:
/* 355 */         this.state = OSProcess.State.STOPPED;
/*     */         break;
/*     */       default:
/* 358 */         this.state = OSProcess.State.OTHER;
/*     */         break;
/*     */     } 
/* 361 */     this.parentProcessID = taskAllInfo.pbsd.pbi_ppid;
/* 362 */     this.userID = Integer.toString(taskAllInfo.pbsd.pbi_uid);
/* 363 */     SystemB.Passwd pwuid = SystemB.INSTANCE.getpwuid(taskAllInfo.pbsd.pbi_uid);
/* 364 */     if (pwuid != null) {
/* 365 */       this.user = pwuid.pw_name;
/*     */     }
/* 367 */     this.groupID = Integer.toString(taskAllInfo.pbsd.pbi_gid);
/* 368 */     SystemB.Group grgid = SystemB.INSTANCE.getgrgid(taskAllInfo.pbsd.pbi_gid);
/* 369 */     if (grgid != null) {
/* 370 */       this.group = grgid.gr_name;
/*     */     }
/* 372 */     this.threadCount = taskAllInfo.ptinfo.pti_threadnum;
/* 373 */     this.priority = taskAllInfo.ptinfo.pti_priority;
/* 374 */     this.virtualSize = taskAllInfo.ptinfo.pti_virtual_size;
/* 375 */     this.residentSetSize = taskAllInfo.ptinfo.pti_resident_size;
/* 376 */     this.kernelTime = taskAllInfo.ptinfo.pti_total_system / 1000000L;
/* 377 */     this.userTime = taskAllInfo.ptinfo.pti_total_user / 1000000L;
/* 378 */     this.startTime = taskAllInfo.pbsd.pbi_start_tvsec * 1000L + taskAllInfo.pbsd.pbi_start_tvusec / 1000L;
/* 379 */     this.upTime = now - this.startTime;
/* 380 */     this.openFiles = taskAllInfo.pbsd.pbi_nfiles;
/* 381 */     this.bitness = ((taskAllInfo.pbsd.pbi_flags & 0x4) == 0) ? 32 : 64;
/* 382 */     this.majorFaults = taskAllInfo.ptinfo.pti_pageins;
/*     */     
/* 384 */     this.minorFaults = (taskAllInfo.ptinfo.pti_faults - taskAllInfo.ptinfo.pti_pageins);
/* 385 */     if (this.minorVersion >= 9) {
/* 386 */       SystemB.RUsageInfoV2 rUsageInfoV2 = new SystemB.RUsageInfoV2();
/* 387 */       if (0 == SystemB.INSTANCE.proc_pid_rusage(getProcessID(), 2, rUsageInfoV2)) {
/* 388 */         this.bytesRead = rUsageInfoV2.ri_diskio_bytesread;
/* 389 */         this.bytesWritten = rUsageInfoV2.ri_diskio_byteswritten;
/*     */       } 
/*     */     } 
/* 392 */     SystemB.VnodePathInfo vpi = new SystemB.VnodePathInfo();
/* 393 */     if (0 < SystemB.INSTANCE.proc_pidinfo(getProcessID(), 9, 0L, (Structure)vpi, vpi.size())) {
/* 394 */       int len = 0;
/* 395 */       for (byte b : vpi.pvi_cdir.vip_path) {
/* 396 */         if (b == 0) {
/*     */           break;
/*     */         }
/* 399 */         len++;
/*     */       } 
/* 401 */       this.currentWorkingDirectory = new String(vpi.pvi_cdir.vip_path, 0, len, StandardCharsets.US_ASCII);
/*     */     } 
/* 403 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\mac\MacOSProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package oshi.software.os.linux;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.InvalidPathException;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.linux.proc.ProcessStat;
/*     */ import oshi.driver.linux.proc.UserGroupInfo;
/*     */ import oshi.hardware.platform.linux.LinuxGlobalMemory;
/*     */ import oshi.software.common.AbstractOSProcess;
/*     */ import oshi.software.os.OSProcess;
/*     */ import oshi.software.os.OSThread;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.linux.ProcPath;
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
/*     */ public class LinuxOSProcess
/*     */   extends AbstractOSProcess
/*     */ {
/*  61 */   private static final Logger LOG = LoggerFactory.getLogger(LinuxOSProcess.class);
/*  62 */   private static final String LS_F_PROC_PID_FD = "ls -f " + ProcPath.PID_FD;
/*     */ 
/*     */   
/*  65 */   private static final int[] PROC_PID_STAT_ORDERS = new int[(ProcPidStat.values()).length];
/*     */   static {
/*  67 */     for (ProcPidStat stat : ProcPidStat.values())
/*     */     {
/*     */       
/*  70 */       PROC_PID_STAT_ORDERS[stat.ordinal()] = stat.getOrder() - 1;
/*     */     }
/*     */   }
/*     */   
/*  74 */   private Supplier<Integer> bitness = Memoizer.memoize(this::queryBitness);
/*     */   
/*     */   private String name;
/*  77 */   private String path = "";
/*     */   private String commandLine;
/*     */   private String user;
/*     */   private String userID;
/*     */   private String group;
/*     */   private String groupID;
/*  83 */   private OSProcess.State state = OSProcess.State.INVALID;
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
/*     */   private long minorFaults;
/*     */   private long majorFaults;
/*     */   
/*     */   public LinuxOSProcess(int pid) {
/*  99 */     super(pid);
/* 100 */     this.commandLine = FileUtil.getStringFromFile(String.format(ProcPath.PID_CMDLINE, new Object[] { Integer.valueOf(pid) }));
/* 101 */     updateAttributes();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 106 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPath() {
/* 111 */     return this.path;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCommandLine() {
/* 116 */     return this.commandLine;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCurrentWorkingDirectory() {
/*     */     try {
/* 122 */       String cwdLink = String.format(ProcPath.PID_CWD, new Object[] { Integer.valueOf(getProcessID()) });
/* 123 */       String cwd = (new File(cwdLink)).getCanonicalPath();
/* 124 */       if (!cwd.equals(cwdLink)) {
/* 125 */         return cwd;
/*     */       }
/* 127 */     } catch (IOException e) {
/* 128 */       LOG.trace("Couldn't find cwd for pid {}: {}", Integer.valueOf(getProcessID()), e.getMessage());
/*     */     } 
/* 130 */     return "";
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUser() {
/* 135 */     return this.user;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUserID() {
/* 140 */     return this.userID;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getGroup() {
/* 145 */     return this.group;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getGroupID() {
/* 150 */     return this.groupID;
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess.State getState() {
/* 155 */     return this.state;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getParentProcessID() {
/* 160 */     return this.parentProcessID;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getThreadCount() {
/* 165 */     return this.threadCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPriority() {
/* 170 */     return this.priority;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getVirtualSize() {
/* 175 */     return this.virtualSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getResidentSetSize() {
/* 180 */     return this.residentSetSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getKernelTime() {
/* 185 */     return this.kernelTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUserTime() {
/* 190 */     return this.userTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUpTime() {
/* 195 */     return this.upTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartTime() {
/* 200 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesRead() {
/* 205 */     return this.bytesRead;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesWritten() {
/* 210 */     return this.bytesWritten;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<OSThread> getThreadDetails() {
/* 216 */     List<OSThread> threadDetails = (List<OSThread>)ProcessStat.getThreadIds(getProcessID()).stream().map(id -> new LinuxOSThread(getProcessID(), id.intValue())).collect(Collectors.toList());
/* 217 */     return Collections.unmodifiableList(threadDetails);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMinorFaults() {
/* 222 */     return this.minorFaults;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMajorFaults() {
/* 227 */     return this.majorFaults;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getOpenFiles() {
/* 233 */     return ExecutingCommand.runNative(String.format(LS_F_PROC_PID_FD, new Object[] { Integer.valueOf(getProcessID()) })).size() - 1L;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBitness() {
/* 238 */     return ((Integer)this.bitness.get()).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int queryBitness() {
/* 244 */     byte[] buffer = new byte[5];
/* 245 */     if (!this.path.isEmpty()) {
/* 246 */       try { InputStream is = new FileInputStream(this.path); 
/* 247 */         try { if (is.read(buffer) == buffer.length)
/* 248 */           { byte b = (buffer[4] == 1) ? 32 : 64;
/*     */             
/* 250 */             is.close(); return b; }  is.close(); } catch (Throwable throwable) { try { is.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (IOException e)
/* 251 */       { LOG.warn("Failed to read process file: {}", this.path); }
/*     */     
/*     */     }
/* 254 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getAffinityMask() {
/* 261 */     String mask = ExecutingCommand.getFirstAnswer("taskset -p " + getProcessID());
/*     */ 
/*     */ 
/*     */     
/* 265 */     String[] split = ParseUtil.whitespaces.split(mask);
/*     */     try {
/* 267 */       return (new BigInteger(split[split.length - 1], 16)).longValue();
/* 268 */     } catch (NumberFormatException e) {
/* 269 */       return 0L;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 275 */     String procPidExe = String.format(ProcPath.PID_EXE, new Object[] { Integer.valueOf(getProcessID()) });
/*     */     try {
/* 277 */       Path link = Paths.get(procPidExe, new String[0]);
/* 278 */       this.path = Files.readSymbolicLink(link).toString();
/*     */       
/* 280 */       int index = this.path.indexOf(" (deleted)");
/* 281 */       if (index != -1) {
/* 282 */         this.path = this.path.substring(0, index);
/*     */       }
/* 284 */     } catch (InvalidPathException|IOException|UnsupportedOperationException|SecurityException e) {
/* 285 */       LOG.debug("Unable to open symbolic link {}", procPidExe);
/*     */     } 
/*     */ 
/*     */     
/* 289 */     Map<String, String> io = FileUtil.getKeyValueMapFromFile(String.format(ProcPath.PID_IO, new Object[] { Integer.valueOf(getProcessID()) }), ":");
/* 290 */     Map<String, String> status = FileUtil.getKeyValueMapFromFile(String.format(ProcPath.PID_STATUS, new Object[] { Integer.valueOf(getProcessID()) }), ":");
/*     */     
/* 292 */     String stat = FileUtil.getStringFromFile(String.format(ProcPath.PID_STAT, new Object[] { Integer.valueOf(getProcessID()) }));
/* 293 */     if (stat.isEmpty()) {
/* 294 */       this.state = OSProcess.State.INVALID;
/* 295 */       return false;
/*     */     } 
/* 297 */     long now = System.currentTimeMillis();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 302 */     long[] statArray = ParseUtil.parseStringToLongArray(stat, PROC_PID_STAT_ORDERS, ProcessStat.PROC_PID_STAT_LENGTH, ' ');
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 308 */     this
/* 309 */       .startTime = (LinuxOperatingSystem.BOOTTIME * LinuxOperatingSystem.getHz() + statArray[ProcPidStat.START_TIME.ordinal()]) * 1000L / LinuxOperatingSystem.getHz();
/*     */ 
/*     */ 
/*     */     
/* 313 */     if (this.startTime >= now) {
/* 314 */       this.startTime = now - 1L;
/*     */     }
/* 316 */     this.parentProcessID = (int)statArray[ProcPidStat.PPID.ordinal()];
/* 317 */     this.threadCount = (int)statArray[ProcPidStat.THREAD_COUNT.ordinal()];
/* 318 */     this.priority = (int)statArray[ProcPidStat.PRIORITY.ordinal()];
/* 319 */     this.virtualSize = statArray[ProcPidStat.VSZ.ordinal()];
/* 320 */     this.residentSetSize = statArray[ProcPidStat.RSS.ordinal()] * LinuxGlobalMemory.PAGE_SIZE;
/* 321 */     this.kernelTime = statArray[ProcPidStat.KERNEL_TIME.ordinal()] * 1000L / LinuxOperatingSystem.getHz();
/* 322 */     this.userTime = statArray[ProcPidStat.USER_TIME.ordinal()] * 1000L / LinuxOperatingSystem.getHz();
/* 323 */     this.minorFaults = statArray[ProcPidStat.MINOR_FAULTS.ordinal()];
/* 324 */     this.majorFaults = statArray[ProcPidStat.MAJOR_FAULTS.ordinal()];
/* 325 */     this.upTime = now - this.startTime;
/*     */ 
/*     */     
/* 328 */     this.bytesRead = ParseUtil.parseLongOrDefault(io.getOrDefault("read_bytes", ""), 0L);
/* 329 */     this.bytesWritten = ParseUtil.parseLongOrDefault(io.getOrDefault("write_bytes", ""), 0L);
/*     */ 
/*     */ 
/*     */     
/* 333 */     this.userID = ParseUtil.whitespaces.split((CharSequence)status.getOrDefault("Uid", (V)""))[0];
/* 334 */     this.user = UserGroupInfo.getUser(this.userID);
/* 335 */     this.groupID = ParseUtil.whitespaces.split((CharSequence)status.getOrDefault("Gid", (V)""))[0];
/* 336 */     this.group = UserGroupInfo.getGroupName(this.groupID);
/* 337 */     this.name = status.getOrDefault("Name", "");
/* 338 */     this.state = ProcessStat.getState(((String)status.getOrDefault("State", "U")).charAt(0));
/* 339 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private enum ProcPidStat
/*     */   {
/* 349 */     PPID(4), MINOR_FAULTS(10), MAJOR_FAULTS(12), USER_TIME(14), KERNEL_TIME(15), PRIORITY(18), THREAD_COUNT(20),
/* 350 */     START_TIME(22), VSZ(23), RSS(24);
/*     */     
/*     */     private int order;
/*     */     
/*     */     public int getOrder() {
/* 355 */       return this.order;
/*     */     }
/*     */     
/*     */     ProcPidStat(int order) {
/* 359 */       this.order = order;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\linux\LinuxOSProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
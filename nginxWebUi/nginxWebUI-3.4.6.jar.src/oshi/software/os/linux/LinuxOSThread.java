/*     */ package oshi.software.os.linux;
/*     */ 
/*     */ import java.util.Map;
/*     */ import oshi.driver.linux.proc.ProcessStat;
/*     */ import oshi.software.common.AbstractOSThread;
/*     */ import oshi.software.os.OSProcess;
/*     */ import oshi.util.FileUtil;
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
/*     */ public class LinuxOSThread
/*     */   extends AbstractOSThread
/*     */ {
/*  37 */   private static final int[] PROC_TASK_STAT_ORDERS = new int[(ThreadPidStat.values()).length];
/*     */   static {
/*  39 */     for (ThreadPidStat stat : ThreadPidStat.values())
/*     */     {
/*     */       
/*  42 */       PROC_TASK_STAT_ORDERS[stat.ordinal()] = stat.getOrder() - 1;
/*     */     }
/*     */   }
/*     */   
/*     */   private final int threadId;
/*  47 */   private OSProcess.State state = OSProcess.State.INVALID;
/*     */   private long minorFaults;
/*     */   private long majorFaults;
/*     */   private long startMemoryAddress;
/*     */   private long contextSwitches;
/*     */   private long kernelTime;
/*     */   private long userTime;
/*     */   private long startTime;
/*     */   private long upTime;
/*     */   private int priority;
/*     */   
/*     */   public LinuxOSThread(int processId, int tid) {
/*  59 */     super(processId);
/*  60 */     this.threadId = tid;
/*  61 */     updateAttributes();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getThreadId() {
/*  66 */     return this.threadId;
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess.State getState() {
/*  71 */     return this.state;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartTime() {
/*  76 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartMemoryAddress() {
/*  81 */     return this.startMemoryAddress;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getContextSwitches() {
/*  86 */     return this.contextSwitches;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMinorFaults() {
/*  91 */     return this.minorFaults;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMajorFaults() {
/*  96 */     return this.majorFaults;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getKernelTime() {
/* 101 */     return this.kernelTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUserTime() {
/* 106 */     return this.userTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUpTime() {
/* 111 */     return this.upTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPriority() {
/* 116 */     return this.priority;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 121 */     Map<String, String> status = FileUtil.getKeyValueMapFromFile(
/* 122 */         String.format(ProcPath.TASK_STATUS, new Object[] { Integer.valueOf(getOwningProcessId()), Integer.valueOf(this.threadId) }), ":");
/*     */     
/* 124 */     String stat = FileUtil.getStringFromFile(String.format(ProcPath.TASK_STAT, new Object[] { Integer.valueOf(getOwningProcessId()), Integer.valueOf(this.threadId) }));
/* 125 */     if (stat.isEmpty()) {
/* 126 */       this.state = OSProcess.State.INVALID;
/* 127 */       return false;
/*     */     } 
/* 129 */     long now = System.currentTimeMillis();
/* 130 */     long[] statArray = ParseUtil.parseStringToLongArray(stat, PROC_TASK_STAT_ORDERS, ProcessStat.PROC_PID_STAT_LENGTH, ' ');
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 136 */     this
/* 137 */       .startTime = (LinuxOperatingSystem.BOOTTIME * LinuxOperatingSystem.getHz() + statArray[ThreadPidStat.START_TIME.ordinal()]) * 1000L / LinuxOperatingSystem.getHz();
/*     */ 
/*     */ 
/*     */     
/* 141 */     if (this.startTime >= now) {
/* 142 */       this.startTime = now - 1L;
/*     */     }
/* 144 */     this.minorFaults = statArray[ThreadPidStat.MINOR_FAULTS.ordinal()];
/* 145 */     this.majorFaults = statArray[ThreadPidStat.MAJOR_FAULT.ordinal()];
/* 146 */     this.startMemoryAddress = statArray[ThreadPidStat.START_CODE.ordinal()];
/* 147 */     long voluntaryContextSwitches = ParseUtil.parseLongOrDefault(status.get("voluntary_ctxt_switches"), 0L);
/* 148 */     long nonVoluntaryContextSwitches = ParseUtil.parseLongOrDefault(status.get("nonvoluntary_ctxt_switches"), 0L);
/* 149 */     this.contextSwitches = voluntaryContextSwitches + nonVoluntaryContextSwitches;
/* 150 */     this.state = ProcessStat.getState(((String)status.getOrDefault("State", "U")).charAt(0));
/* 151 */     this.kernelTime = statArray[ThreadPidStat.KERNEL_TIME.ordinal()] * 1000L / LinuxOperatingSystem.getHz();
/* 152 */     this.userTime = statArray[ThreadPidStat.USER_TIME.ordinal()] * 1000L / LinuxOperatingSystem.getHz();
/* 153 */     this.upTime = now - this.startTime;
/* 154 */     this.priority = (int)statArray[ThreadPidStat.PRIORITY.ordinal()];
/* 155 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private enum ThreadPidStat
/*     */   {
/* 165 */     PPID(4), MINOR_FAULTS(10), MAJOR_FAULT(12), USER_TIME(14), KERNEL_TIME(15), PRIORITY(18), THREAD_COUNT(20),
/* 166 */     START_TIME(22), VSZ(23), RSS(24), START_CODE(26);
/*     */     
/*     */     private final int order;
/*     */     
/*     */     ThreadPidStat(int order) {
/* 171 */       this.order = order;
/*     */     }
/*     */     
/*     */     public int getOrder() {
/* 175 */       return this.order;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\linux\LinuxOSThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
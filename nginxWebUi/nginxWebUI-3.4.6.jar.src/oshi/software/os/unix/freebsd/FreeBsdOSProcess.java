/*     */ package oshi.software.os.unix.freebsd;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.jna.platform.unix.freebsd.FreeBsdLibc;
/*     */ import oshi.software.common.AbstractOSProcess;
/*     */ import oshi.software.os.OSProcess;
/*     */ import oshi.software.os.OSThread;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.LsofUtil;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
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
/*     */ public class FreeBsdOSProcess
/*     */   extends AbstractOSProcess
/*     */ {
/*  54 */   private Supplier<Integer> bitness = Memoizer.memoize(this::queryBitness);
/*     */   
/*     */   private String name;
/*  57 */   private String path = "";
/*     */   private String commandLine;
/*     */   private String user;
/*     */   private String userID;
/*     */   private String group;
/*     */   private String groupID;
/*  63 */   private OSProcess.State state = OSProcess.State.INVALID;
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
/*     */   public FreeBsdOSProcess(int pid, String[] split) {
/*  79 */     super(pid);
/*  80 */     updateAttributes(split);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  85 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPath() {
/*  90 */     return this.path;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCommandLine() {
/*  95 */     return this.commandLine;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCurrentWorkingDirectory() {
/* 100 */     return LsofUtil.getCwd(getProcessID());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUser() {
/* 105 */     return this.user;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUserID() {
/* 110 */     return this.userID;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getGroup() {
/* 115 */     return this.group;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getGroupID() {
/* 120 */     return this.groupID;
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess.State getState() {
/* 125 */     return this.state;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getParentProcessID() {
/* 130 */     return this.parentProcessID;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getThreadCount() {
/* 135 */     return this.threadCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPriority() {
/* 140 */     return this.priority;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getVirtualSize() {
/* 145 */     return this.virtualSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getResidentSetSize() {
/* 150 */     return this.residentSetSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getKernelTime() {
/* 155 */     return this.kernelTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUserTime() {
/* 160 */     return this.userTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUpTime() {
/* 165 */     return this.upTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartTime() {
/* 170 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesRead() {
/* 175 */     return this.bytesRead;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesWritten() {
/* 180 */     return this.bytesWritten;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOpenFiles() {
/* 185 */     return LsofUtil.getOpenFiles(getProcessID());
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBitness() {
/* 190 */     return ((Integer)this.bitness.get()).intValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getAffinityMask() {
/* 195 */     long bitMask = 0L;
/*     */ 
/*     */     
/* 198 */     String cpuset = ExecutingCommand.getFirstAnswer("cpuset -gp " + getProcessID());
/*     */ 
/*     */ 
/*     */     
/* 202 */     String[] split = cpuset.split(":");
/* 203 */     if (split.length > 1) {
/* 204 */       String[] bits = split[1].split(",");
/* 205 */       for (String bit : bits) {
/* 206 */         int bitToSet = ParseUtil.parseIntOrDefault(bit.trim(), -1);
/* 207 */         if (bitToSet >= 0) {
/* 208 */           bitMask |= 1L << bitToSet;
/*     */         }
/*     */       } 
/*     */     } 
/* 212 */     return bitMask;
/*     */   }
/*     */ 
/*     */   
/*     */   private int queryBitness() {
/* 217 */     int[] mib = new int[4];
/* 218 */     mib[0] = 1;
/* 219 */     mib[1] = 14;
/* 220 */     mib[2] = 9;
/* 221 */     mib[3] = getProcessID();
/*     */     
/* 223 */     Memory memory = new Memory(32L);
/* 224 */     IntByReference size = new IntByReference(32);
/*     */     
/* 226 */     if (0 == FreeBsdLibc.INSTANCE.sysctl(mib, mib.length, (Pointer)memory, size, null, 0)) {
/* 227 */       String elf = memory.getString(0L);
/* 228 */       if (elf.contains("ELF32"))
/* 229 */         return 32; 
/* 230 */       if (elf.contains("ELF64")) {
/* 231 */         return 64;
/*     */       }
/*     */     } 
/* 234 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSThread> getThreadDetails() {
/* 239 */     List<OSThread> threads = new ArrayList<>();
/* 240 */     String psCommand = "ps -awwxo tdname,lwp,state,etimes,systime,time,tdaddr,nivcsw,nvcsw,majflt,minflt,pri -H";
/* 241 */     if (getProcessID() >= 0) {
/* 242 */       psCommand = psCommand + " -p " + getProcessID();
/*     */     }
/* 244 */     List<String> threadList = ExecutingCommand.runNative(psCommand);
/* 245 */     if (threadList.isEmpty() || threadList.size() < 2) {
/* 246 */       return threads;
/*     */     }
/*     */     
/* 249 */     threadList.remove(0);
/*     */     
/* 251 */     for (String thread : threadList) {
/* 252 */       String[] split = ParseUtil.whitespaces.split(thread.trim(), 12);
/*     */       
/* 254 */       if (split.length == 10) {
/* 255 */         threads.add(new FreeBsdOSThread(getProcessID(), split));
/*     */       }
/*     */     } 
/* 258 */     return threads;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMinorFaults() {
/* 263 */     return this.minorFaults;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMajorFaults() {
/* 268 */     return this.majorFaults;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 274 */     String psCommand = "ps -awwxo state,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etimes,systime,time,comm,args,majflt,minflt -p " + getProcessID();
/* 275 */     List<String> procList = ExecutingCommand.runNative(psCommand);
/* 276 */     if (procList.size() > 1) {
/*     */       
/* 278 */       String[] split = ParseUtil.whitespaces.split(((String)procList.get(1)).trim(), 18);
/* 279 */       if (split.length == 18) {
/* 280 */         return updateAttributes(split);
/*     */       }
/*     */     } 
/* 283 */     this.state = OSProcess.State.INVALID;
/* 284 */     return false;
/*     */   }
/*     */   
/*     */   private boolean updateAttributes(String[] split) {
/* 288 */     long now = System.currentTimeMillis();
/* 289 */     switch (split[0].charAt(0)) {
/*     */       case 'R':
/* 291 */         this.state = OSProcess.State.RUNNING;
/*     */         break;
/*     */       case 'I':
/*     */       case 'S':
/* 295 */         this.state = OSProcess.State.SLEEPING;
/*     */         break;
/*     */       case 'D':
/*     */       case 'L':
/*     */       case 'U':
/* 300 */         this.state = OSProcess.State.WAITING;
/*     */         break;
/*     */       case 'Z':
/* 303 */         this.state = OSProcess.State.ZOMBIE;
/*     */         break;
/*     */       case 'T':
/* 306 */         this.state = OSProcess.State.STOPPED;
/*     */         break;
/*     */       default:
/* 309 */         this.state = OSProcess.State.OTHER;
/*     */         break;
/*     */     } 
/* 312 */     this.parentProcessID = ParseUtil.parseIntOrDefault(split[2], 0);
/* 313 */     this.user = split[3];
/* 314 */     this.userID = split[4];
/* 315 */     this.group = split[5];
/* 316 */     this.groupID = split[6];
/* 317 */     this.threadCount = ParseUtil.parseIntOrDefault(split[7], 0);
/* 318 */     this.priority = ParseUtil.parseIntOrDefault(split[8], 0);
/*     */     
/* 320 */     this.virtualSize = ParseUtil.parseLongOrDefault(split[9], 0L) * 1024L;
/* 321 */     this.residentSetSize = ParseUtil.parseLongOrDefault(split[10], 0L) * 1024L;
/*     */     
/* 323 */     long elapsedTime = ParseUtil.parseDHMSOrDefault(split[11], 0L);
/* 324 */     this.upTime = (elapsedTime < 1L) ? 1L : elapsedTime;
/* 325 */     this.startTime = now - this.upTime;
/* 326 */     this.kernelTime = ParseUtil.parseDHMSOrDefault(split[12], 0L);
/* 327 */     this.userTime = ParseUtil.parseDHMSOrDefault(split[13], 0L) - this.kernelTime;
/* 328 */     this.path = split[14];
/* 329 */     this.name = this.path.substring(this.path.lastIndexOf('/') + 1);
/* 330 */     this.commandLine = split[15];
/* 331 */     this.minorFaults = ParseUtil.parseLongOrDefault(split[16], 0L);
/* 332 */     this.majorFaults = ParseUtil.parseLongOrDefault(split[17], 0L);
/* 333 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\freebsd\FreeBsdOSProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
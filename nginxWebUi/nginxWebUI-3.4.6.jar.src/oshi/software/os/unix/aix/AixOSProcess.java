/*     */ package oshi.software.os.unix.aix;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.unix.aix.perfstat.PerfstatCpu;
/*     */ import oshi.jna.platform.unix.aix.Perfstat;
/*     */ import oshi.software.common.AbstractOSProcess;
/*     */ import oshi.software.os.OSProcess;
/*     */ import oshi.software.os.OSThread;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.LsofUtil;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.tuples.Pair;
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
/*     */ public class AixOSProcess
/*     */   extends AbstractOSProcess
/*     */ {
/*  56 */   private Supplier<Integer> bitness = Memoizer.memoize(this::queryBitness);
/*  57 */   private final Supplier<Long> affinityMask = Memoizer.memoize(PerfstatCpu::queryCpuAffinityMask, Memoizer.defaultExpiration());
/*     */   
/*     */   private String name;
/*  60 */   private String path = "";
/*     */   private String commandLine;
/*     */   private String user;
/*     */   private String userID;
/*     */   private String group;
/*     */   private String groupID;
/*  66 */   private OSProcess.State state = OSProcess.State.INVALID;
/*     */   
/*     */   private int parentProcessID;
/*     */   
/*     */   private int threadCount;
/*     */   
/*     */   private int priority;
/*     */   private long virtualSize;
/*     */   private long residentSetSize;
/*     */   private long kernelTime;
/*     */   private long userTime;
/*     */   private long startTime;
/*     */   private long upTime;
/*     */   private long bytesRead;
/*     */   private long bytesWritten;
/*     */   private long majorFaults;
/*     */   private Supplier<Perfstat.perfstat_process_t[]> procCpu;
/*     */   
/*     */   public AixOSProcess(int pid, String[] split, Map<Integer, Pair<Long, Long>> cpuMap, Supplier<Perfstat.perfstat_process_t[]> procCpu) {
/*  85 */     super(pid);
/*  86 */     this.procCpu = procCpu;
/*  87 */     updateAttributes(split, cpuMap);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  92 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPath() {
/*  97 */     return this.path;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCommandLine() {
/* 102 */     return this.commandLine;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCurrentWorkingDirectory() {
/* 107 */     return LsofUtil.getCwd(getProcessID());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUser() {
/* 112 */     return this.user;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUserID() {
/* 117 */     return this.userID;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getGroup() {
/* 122 */     return this.group;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getGroupID() {
/* 127 */     return this.groupID;
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess.State getState() {
/* 132 */     return this.state;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getParentProcessID() {
/* 137 */     return this.parentProcessID;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getThreadCount() {
/* 142 */     return this.threadCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPriority() {
/* 147 */     return this.priority;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getVirtualSize() {
/* 152 */     return this.virtualSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getResidentSetSize() {
/* 157 */     return this.residentSetSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getKernelTime() {
/* 162 */     return this.kernelTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUserTime() {
/* 167 */     return this.userTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUpTime() {
/* 172 */     return this.upTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartTime() {
/* 177 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesRead() {
/* 182 */     return this.bytesRead;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesWritten() {
/* 187 */     return this.bytesWritten;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOpenFiles() {
/* 192 */     return LsofUtil.getOpenFiles(getProcessID());
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBitness() {
/* 197 */     return ((Integer)this.bitness.get()).intValue();
/*     */   }
/*     */   
/*     */   private int queryBitness() {
/* 201 */     List<String> pflags = ExecutingCommand.runNative("pflags " + getProcessID());
/* 202 */     for (String line : pflags) {
/* 203 */       if (line.contains("data model")) {
/* 204 */         if (line.contains("LP32"))
/* 205 */           return 32; 
/* 206 */         if (line.contains("LP64")) {
/* 207 */           return 64;
/*     */         }
/*     */       } 
/*     */     } 
/* 211 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getAffinityMask() {
/* 220 */     long mask = 0L;
/* 221 */     List<String> processAffinityInfoList = ExecutingCommand.runNative("ps -m -o THREAD -p " + getProcessID());
/* 222 */     if (processAffinityInfoList.size() > 2) {
/* 223 */       processAffinityInfoList.remove(0);
/* 224 */       processAffinityInfoList.remove(0);
/* 225 */       for (String processAffinityInfo : processAffinityInfoList) {
/* 226 */         String[] threadInfoSplit = ParseUtil.whitespaces.split(processAffinityInfo.trim());
/* 227 */         if (threadInfoSplit.length > 13 && threadInfoSplit[4].charAt(0) != 'Z') {
/* 228 */           if (threadInfoSplit[11].charAt(0) == '-') {
/* 229 */             return ((Long)this.affinityMask.get()).longValue();
/*     */           }
/* 231 */           int affinity = ParseUtil.parseIntOrDefault(threadInfoSplit[11], 0);
/* 232 */           mask |= 1L << affinity;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 237 */     return mask;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSThread> getThreadDetails() {
/* 242 */     List<String> threadListInfoPs = ExecutingCommand.runNative("ps -m -o THREAD -p " + getProcessID());
/*     */     
/* 244 */     if (threadListInfoPs.size() > 2) {
/* 245 */       List<OSThread> threads = new ArrayList<>();
/* 246 */       threadListInfoPs.remove(0);
/* 247 */       threadListInfoPs.remove(0);
/* 248 */       for (String threadInfo : threadListInfoPs) {
/*     */         
/* 250 */         String[] threadInfoSplit = ParseUtil.whitespaces.split(threadInfo.trim());
/* 251 */         if (threadInfoSplit.length == 13) {
/* 252 */           String[] split = new String[3];
/* 253 */           split[0] = threadInfoSplit[3];
/* 254 */           split[1] = threadInfoSplit[4];
/* 255 */           split[2] = threadInfoSplit[6];
/* 256 */           threads.add(new AixOSThread(getProcessID(), split));
/*     */         } 
/*     */       } 
/* 259 */       return threads;
/*     */     } 
/* 261 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMajorFaults() {
/* 266 */     return this.majorFaults;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 271 */     Perfstat.perfstat_process_t[] perfstat = this.procCpu.get();
/* 272 */     List<String> procList = ExecutingCommand.runNative("ps -o s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args -p " + 
/* 273 */         getProcessID());
/*     */     
/* 275 */     Map<Integer, Pair<Long, Long>> cpuMap = new HashMap<>();
/* 276 */     for (Perfstat.perfstat_process_t stat : perfstat) {
/* 277 */       cpuMap.put(Integer.valueOf((int)stat.pid), new Pair(Long.valueOf((long)stat.ucpu_time), Long.valueOf((long)stat.scpu_time)));
/*     */     }
/* 279 */     if (procList.size() > 1) {
/* 280 */       String[] split = ParseUtil.whitespaces.split(((String)procList.get(1)).trim(), 15);
/*     */       
/* 282 */       if (split.length == 15) {
/* 283 */         return updateAttributes(split, cpuMap);
/*     */       }
/*     */     } 
/* 286 */     this.state = OSProcess.State.INVALID;
/* 287 */     return false;
/*     */   }
/*     */   
/*     */   private boolean updateAttributes(String[] split, Map<Integer, Pair<Long, Long>> cpuMap) {
/* 291 */     long now = System.currentTimeMillis();
/* 292 */     this.state = getStateFromOutput(split[0].charAt(0));
/* 293 */     this.parentProcessID = ParseUtil.parseIntOrDefault(split[2], 0);
/* 294 */     this.user = split[3];
/* 295 */     this.userID = split[4];
/* 296 */     this.group = split[5];
/* 297 */     this.groupID = split[6];
/* 298 */     this.threadCount = ParseUtil.parseIntOrDefault(split[7], 0);
/* 299 */     this.priority = ParseUtil.parseIntOrDefault(split[8], 0);
/*     */     
/* 301 */     this.virtualSize = ParseUtil.parseLongOrDefault(split[9], 0L) << 10L;
/* 302 */     this.residentSetSize = ParseUtil.parseLongOrDefault(split[10], 0L) << 10L;
/* 303 */     long elapsedTime = ParseUtil.parseDHMSOrDefault(split[11], 0L);
/* 304 */     if (cpuMap.containsKey(Integer.valueOf(getProcessID()))) {
/* 305 */       Pair<Long, Long> userSystem = cpuMap.get(Integer.valueOf(getProcessID()));
/* 306 */       this.userTime = ((Long)userSystem.getA()).longValue();
/* 307 */       this.kernelTime = ((Long)userSystem.getB()).longValue();
/*     */     } else {
/* 309 */       this.userTime = ParseUtil.parseDHMSOrDefault(split[12], 0L);
/* 310 */       this.kernelTime = 0L;
/*     */     } 
/*     */     
/* 313 */     this.upTime = (elapsedTime < 1L) ? 1L : elapsedTime;
/* 314 */     while (this.upTime < this.userTime + this.kernelTime) {
/* 315 */       this.upTime += 500L;
/*     */     }
/* 317 */     this.startTime = now - this.upTime;
/* 318 */     this.name = split[13];
/* 319 */     this.majorFaults = ParseUtil.parseLongOrDefault(split[14], 0L);
/* 320 */     this.path = ParseUtil.whitespaces.split(split[15])[0];
/* 321 */     this.commandLine = split[15];
/* 322 */     return true;
/*     */   }
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
/*     */   static OSProcess.State getStateFromOutput(char stateValue) {
/* 335 */     switch (stateValue)
/*     */     { case 'O':
/* 337 */         state = OSProcess.State.INVALID;
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
/* 360 */         return state;case 'A': case 'R': state = OSProcess.State.RUNNING; return state;case 'I': state = OSProcess.State.WAITING; return state;case 'S': case 'W': state = OSProcess.State.SLEEPING; return state;case 'Z': state = OSProcess.State.ZOMBIE; return state;case 'T': state = OSProcess.State.STOPPED; return state; }  OSProcess.State state = OSProcess.State.OTHER; return state;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\aix\AixOSProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
/*     */ package oshi.software.os.unix.solaris;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
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
/*     */ @ThreadSafe
/*     */ public class SolarisOSProcess
/*     */   extends AbstractOSProcess
/*     */ {
/*  51 */   private Supplier<Integer> bitness = Memoizer.memoize(this::queryBitness);
/*     */   
/*     */   private String name;
/*  54 */   private String path = "";
/*     */   private String commandLine;
/*     */   private String user;
/*     */   private String userID;
/*     */   private String group;
/*     */   private String groupID;
/*  60 */   private OSProcess.State state = OSProcess.State.INVALID;
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
/*     */   
/*     */   public SolarisOSProcess(int pid, String[] split) {
/*  74 */     super(pid);
/*  75 */     updateAttributes(split);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  80 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPath() {
/*  85 */     return this.path;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCommandLine() {
/*  90 */     return this.commandLine;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCurrentWorkingDirectory() {
/*  95 */     return LsofUtil.getCwd(getProcessID());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUser() {
/* 100 */     return this.user;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUserID() {
/* 105 */     return this.userID;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getGroup() {
/* 110 */     return this.group;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getGroupID() {
/* 115 */     return this.groupID;
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess.State getState() {
/* 120 */     return this.state;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getParentProcessID() {
/* 125 */     return this.parentProcessID;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getThreadCount() {
/* 130 */     return this.threadCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPriority() {
/* 135 */     return this.priority;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getVirtualSize() {
/* 140 */     return this.virtualSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getResidentSetSize() {
/* 145 */     return this.residentSetSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getKernelTime() {
/* 150 */     return this.kernelTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUserTime() {
/* 155 */     return this.userTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUpTime() {
/* 160 */     return this.upTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartTime() {
/* 165 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesRead() {
/* 170 */     return this.bytesRead;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesWritten() {
/* 175 */     return this.bytesWritten;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOpenFiles() {
/* 180 */     return LsofUtil.getOpenFiles(getProcessID());
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBitness() {
/* 185 */     return ((Integer)this.bitness.get()).intValue();
/*     */   }
/*     */   
/*     */   private int queryBitness() {
/* 189 */     List<String> pflags = ExecutingCommand.runNative("pflags " + getProcessID());
/* 190 */     for (String line : pflags) {
/* 191 */       if (line.contains("data model")) {
/* 192 */         if (line.contains("LP32"))
/* 193 */           return 32; 
/* 194 */         if (line.contains("LP64")) {
/* 195 */           return 64;
/*     */         }
/*     */       } 
/*     */     } 
/* 199 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getAffinityMask() {
/* 204 */     long bitMask = 0L;
/* 205 */     String cpuset = ExecutingCommand.getFirstAnswer("pbind -q " + getProcessID());
/*     */ 
/*     */ 
/*     */     
/* 209 */     if (cpuset.isEmpty()) {
/* 210 */       List<String> allProcs = ExecutingCommand.runNative("psrinfo");
/* 211 */       for (String proc : allProcs) {
/* 212 */         String[] split = ParseUtil.whitespaces.split(proc);
/* 213 */         int bitToSet = ParseUtil.parseIntOrDefault(split[0], -1);
/* 214 */         if (bitToSet >= 0) {
/* 215 */           bitMask |= 1L << bitToSet;
/*     */         }
/*     */       } 
/* 218 */       return bitMask;
/* 219 */     }  if (cpuset.endsWith(".") && cpuset.contains("strongly bound to processor(s)")) {
/* 220 */       String parse = cpuset.substring(0, cpuset.length() - 1);
/* 221 */       String[] split = ParseUtil.whitespaces.split(parse);
/* 222 */       for (int i = split.length - 1; i >= 0; ) {
/* 223 */         int bitToSet = ParseUtil.parseIntOrDefault(split[i], -1);
/* 224 */         if (bitToSet >= 0) {
/* 225 */           bitMask |= 1L << bitToSet;
/*     */           
/*     */           i--;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 232 */     return bitMask;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<OSThread> getThreadDetails() {
/* 238 */     List<String> threadListInfo1 = ExecutingCommand.runNative("ps -o lwp,s,etime,stime,time,addr,pri -p " + getProcessID());
/* 239 */     List<String> threadListInfo2 = ExecutingCommand.runNative("prstat -L -v -p " + getProcessID());
/* 240 */     Map<Integer, String[]> threadMap = parseAndMergeThreadInfo(threadListInfo1, threadListInfo2);
/* 241 */     if (threadMap.keySet().size() > 1) {
/* 242 */       return (List<OSThread>)threadMap.entrySet().stream().map(entry -> new SolarisOSThread(getProcessID(), (String[])entry.getValue()))
/* 243 */         .collect(Collectors.toList());
/*     */     }
/* 245 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 250 */     List<String> procList = ExecutingCommand.runNative("ps -o s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args -p " + 
/* 251 */         getProcessID());
/* 252 */     if (procList.size() > 1) {
/* 253 */       String[] split = ParseUtil.whitespaces.split(((String)procList.get(1)).trim(), 15);
/*     */       
/* 255 */       if (split.length == 15) {
/* 256 */         return updateAttributes(split);
/*     */       }
/*     */     } 
/* 259 */     this.state = OSProcess.State.INVALID;
/* 260 */     return false;
/*     */   }
/*     */   
/*     */   private boolean updateAttributes(String[] split) {
/* 264 */     long now = System.currentTimeMillis();
/* 265 */     this.state = getStateFromOutput(split[0].charAt(0));
/* 266 */     this.parentProcessID = ParseUtil.parseIntOrDefault(split[2], 0);
/* 267 */     this.user = split[3];
/* 268 */     this.userID = split[4];
/* 269 */     this.group = split[5];
/* 270 */     this.groupID = split[6];
/* 271 */     this.threadCount = ParseUtil.parseIntOrDefault(split[7], 0);
/* 272 */     this.priority = ParseUtil.parseIntOrDefault(split[8], 0);
/*     */     
/* 274 */     this.virtualSize = ParseUtil.parseLongOrDefault(split[9], 0L) * 1024L;
/* 275 */     this.residentSetSize = ParseUtil.parseLongOrDefault(split[10], 0L) * 1024L;
/*     */     
/* 277 */     long elapsedTime = ParseUtil.parseDHMSOrDefault(split[11], 0L);
/* 278 */     this.upTime = (elapsedTime < 1L) ? 1L : elapsedTime;
/* 279 */     this.startTime = now - this.upTime;
/* 280 */     this.kernelTime = 0L;
/* 281 */     this.userTime = ParseUtil.parseDHMSOrDefault(split[12], 0L);
/* 282 */     this.path = split[13];
/* 283 */     this.name = this.path.substring(this.path.lastIndexOf('/') + 1);
/* 284 */     this.commandLine = split[14];
/*     */     
/* 286 */     return true;
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
/*     */ 
/*     */   
/*     */   static Map<Integer, String[]> parseAndMergeThreadInfo(List<String> psThreadInfo, List<String> prstatThreadInfo) {
/* 301 */     Map<Integer, String[]> map = (Map)new HashMap<>();
/* 302 */     String[] mergedSplit = new String[9];
/*     */ 
/*     */     
/* 305 */     if (psThreadInfo.size() > 1) {
/* 306 */       psThreadInfo.stream().skip(1L).forEach(threadInfo -> {
/*     */             String[] psSplit = ParseUtil.whitespaces.split(threadInfo.trim());
/*     */             
/*     */             if (psSplit.length == 7) {
/*     */               for (int idx = 0; idx < psSplit.length; idx++) {
/*     */                 if (idx == 0) {
/*     */                   map.put(Integer.valueOf(ParseUtil.parseIntOrDefault(psSplit[idx], 0)), mergedSplit);
/*     */                 }
/*     */                 
/*     */                 mergedSplit[idx] = psSplit[idx];
/*     */               } 
/*     */             }
/*     */           });
/*     */       
/* 320 */       if (prstatThreadInfo.size() > 1) {
/* 321 */         prstatThreadInfo.stream().skip(1L).forEach(threadInfo -> {
/*     */               String[] splitPrstat = ParseUtil.whitespaces.split(threadInfo.trim());
/*     */               if (splitPrstat.length == 15) {
/*     */                 int idxAfterForwardSlash = splitPrstat[14].lastIndexOf('/') + 1;
/*     */                 if (idxAfterForwardSlash > 0 && idxAfterForwardSlash < splitPrstat[14].length()) {
/*     */                   String threadId = splitPrstat[14].substring(idxAfterForwardSlash);
/*     */                   String[] existingSplit = (String[])map.get(Integer.valueOf(Integer.parseInt(threadId)));
/*     */                   if (existingSplit != null) {
/*     */                     existingSplit[7] = splitPrstat[10];
/*     */                     existingSplit[8] = splitPrstat[11];
/*     */                   } 
/*     */                 } 
/*     */               } 
/*     */             });
/*     */       }
/*     */     } 
/* 337 */     return map;
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
/* 350 */     switch (stateValue)
/*     */     { case 'O':
/* 352 */         state = OSProcess.State.RUNNING;
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
/* 371 */         return state;case 'S': state = OSProcess.State.SLEEPING; return state;case 'R': case 'W': state = OSProcess.State.WAITING; return state;case 'Z': state = OSProcess.State.ZOMBIE; return state;case 'T': state = OSProcess.State.STOPPED; return state; }  OSProcess.State state = OSProcess.State.OTHER; return state;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\solaris\SolarisOSProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
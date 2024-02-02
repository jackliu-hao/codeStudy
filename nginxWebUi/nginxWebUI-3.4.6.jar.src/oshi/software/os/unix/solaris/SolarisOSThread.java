/*     */ package oshi.software.os.unix.solaris;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import oshi.software.common.AbstractOSThread;
/*     */ import oshi.software.os.OSProcess;
/*     */ import oshi.util.ExecutingCommand;
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
/*     */ public class SolarisOSThread
/*     */   extends AbstractOSThread
/*     */ {
/*     */   private int threadId;
/*  38 */   private OSProcess.State state = OSProcess.State.INVALID;
/*     */   private long startMemoryAddress;
/*     */   private long contextSwitches;
/*     */   private long kernelTime;
/*     */   private long userTime;
/*     */   private long startTime;
/*     */   private long upTime;
/*     */   private int priority;
/*     */   
/*     */   public SolarisOSThread(int pid, String[] split) {
/*  48 */     super(pid);
/*  49 */     updateAttributes(split);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getThreadId() {
/*  54 */     return this.threadId;
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess.State getState() {
/*  59 */     return this.state;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartMemoryAddress() {
/*  64 */     return this.startMemoryAddress;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getContextSwitches() {
/*  69 */     return this.contextSwitches;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getKernelTime() {
/*  74 */     return this.kernelTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUserTime() {
/*  79 */     return this.userTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUpTime() {
/*  84 */     return this.upTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartTime() {
/*  89 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPriority() {
/*  94 */     return this.priority;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 100 */     List<String> threadListInfo1 = ExecutingCommand.runNative("ps -o lwp,s,etime,stime,time,addr,pri -p " + getOwningProcessId());
/* 101 */     List<String> threadListInfo2 = ExecutingCommand.runNative("prstat -L -v -p " + getOwningProcessId());
/* 102 */     Map<Integer, String[]> threadMap = SolarisOSProcess.parseAndMergeThreadInfo(threadListInfo1, threadListInfo2);
/* 103 */     if (threadMap.keySet().size() > 1) {
/*     */       
/* 105 */       Optional<String[]> split = (Optional)threadMap.entrySet().stream().filter(entry -> (((Integer)entry.getKey()).intValue() == getThreadId())).map(Map.Entry::getValue).findFirst();
/* 106 */       if (split.isPresent()) {
/* 107 */         return updateAttributes(split.get());
/*     */       }
/*     */     } 
/* 110 */     this.state = OSProcess.State.INVALID;
/* 111 */     return false;
/*     */   }
/*     */   
/*     */   private boolean updateAttributes(String[] split) {
/* 115 */     this.threadId = ParseUtil.parseIntOrDefault(split[0], 0);
/* 116 */     this.state = SolarisOSProcess.getStateFromOutput(split[1].charAt(0));
/*     */     
/* 118 */     long elapsedTime = ParseUtil.parseDHMSOrDefault(split[2], 0L);
/* 119 */     this.upTime = (elapsedTime < 1L) ? 1L : elapsedTime;
/* 120 */     long now = System.currentTimeMillis();
/* 121 */     this.startTime = now - this.upTime;
/* 122 */     this.kernelTime = ParseUtil.parseDHMSOrDefault(split[3], 0L);
/* 123 */     this.userTime = ParseUtil.parseDHMSOrDefault(split[4], 0L) - this.kernelTime;
/* 124 */     this.startMemoryAddress = ParseUtil.hexStringToLong(split[5], 0L);
/* 125 */     this.priority = ParseUtil.parseIntOrDefault(split[6], 0);
/* 126 */     long nonVoluntaryContextSwitches = ParseUtil.parseLongOrDefault(split[7], 0L);
/* 127 */     long voluntaryContextSwitches = ParseUtil.parseLongOrDefault(split[8], 0L);
/* 128 */     this.contextSwitches = voluntaryContextSwitches + nonVoluntaryContextSwitches;
/* 129 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\solaris\SolarisOSThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
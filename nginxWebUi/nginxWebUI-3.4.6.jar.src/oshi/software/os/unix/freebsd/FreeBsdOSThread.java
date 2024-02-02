/*     */ package oshi.software.os.unix.freebsd;
/*     */ 
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FreeBsdOSThread
/*     */   extends AbstractOSThread
/*     */ {
/*     */   private int threadId;
/*  44 */   private String name = "";
/*  45 */   private OSProcess.State state = OSProcess.State.INVALID;
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
/*     */   public FreeBsdOSThread(int processId, String[] split) {
/*  57 */     super(processId);
/*  58 */     updateAttributes(split);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getThreadId() {
/*  63 */     return this.threadId;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  68 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess.State getState() {
/*  73 */     return this.state;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartMemoryAddress() {
/*  78 */     return this.startMemoryAddress;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getContextSwitches() {
/*  83 */     return this.contextSwitches;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMinorFaults() {
/*  88 */     return this.minorFaults;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMajorFaults() {
/*  93 */     return this.majorFaults;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getKernelTime() {
/*  98 */     return this.kernelTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUserTime() {
/* 103 */     return this.userTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUpTime() {
/* 108 */     return this.upTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartTime() {
/* 113 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPriority() {
/* 118 */     return this.priority;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 124 */     String psCommand = "ps -awwxo tdname,lwp,state,etimes,systime,time,tdaddr,nivcsw,nvcsw,majflt,minflt,pri -H -p " + getOwningProcessId();
/*     */     
/* 126 */     List<String> threadList = ExecutingCommand.runNative(psCommand);
/* 127 */     for (String psOutput : threadList) {
/* 128 */       String[] split = ParseUtil.whitespaces.split(psOutput.trim());
/* 129 */       if (split.length > 1 && getThreadId() == ParseUtil.parseIntOrDefault(split[1], 0)) {
/* 130 */         return updateAttributes(split);
/*     */       }
/*     */     } 
/* 133 */     this.state = OSProcess.State.INVALID;
/* 134 */     return false;
/*     */   }
/*     */   
/*     */   private boolean updateAttributes(String[] split) {
/* 138 */     if (split.length != 12) {
/* 139 */       this.state = OSProcess.State.INVALID;
/* 140 */       return false;
/*     */     } 
/* 142 */     this.name = split[0];
/* 143 */     this.threadId = ParseUtil.parseIntOrDefault(split[1], 0);
/* 144 */     switch (split[2].charAt(0)) {
/*     */       case 'R':
/* 146 */         this.state = OSProcess.State.RUNNING;
/*     */         break;
/*     */       case 'I':
/*     */       case 'S':
/* 150 */         this.state = OSProcess.State.SLEEPING;
/*     */         break;
/*     */       case 'D':
/*     */       case 'L':
/*     */       case 'U':
/* 155 */         this.state = OSProcess.State.WAITING;
/*     */         break;
/*     */       case 'Z':
/* 158 */         this.state = OSProcess.State.ZOMBIE;
/*     */         break;
/*     */       case 'T':
/* 161 */         this.state = OSProcess.State.STOPPED;
/*     */         break;
/*     */       default:
/* 164 */         this.state = OSProcess.State.OTHER;
/*     */         break;
/*     */     } 
/*     */     
/* 168 */     long elapsedTime = ParseUtil.parseDHMSOrDefault(split[3], 0L);
/* 169 */     this.upTime = (elapsedTime < 1L) ? 1L : elapsedTime;
/* 170 */     long now = System.currentTimeMillis();
/* 171 */     this.startTime = now - this.upTime;
/* 172 */     this.kernelTime = ParseUtil.parseDHMSOrDefault(split[4], 0L);
/* 173 */     this.userTime = ParseUtil.parseDHMSOrDefault(split[5], 0L) - this.kernelTime;
/* 174 */     this.startMemoryAddress = ParseUtil.hexStringToLong(split[6], 0L);
/* 175 */     long nonVoluntaryContextSwitches = ParseUtil.parseLongOrDefault(split[7], 0L);
/* 176 */     long voluntaryContextSwitches = ParseUtil.parseLongOrDefault(split[8], 0L);
/* 177 */     this.contextSwitches = voluntaryContextSwitches + nonVoluntaryContextSwitches;
/* 178 */     this.majorFaults = ParseUtil.parseLongOrDefault(split[9], 0L);
/* 179 */     this.minorFaults = ParseUtil.parseLongOrDefault(split[10], 0L);
/* 180 */     this.priority = ParseUtil.parseIntOrDefault(split[11], 0);
/* 181 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\freebsd\FreeBsdOSThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
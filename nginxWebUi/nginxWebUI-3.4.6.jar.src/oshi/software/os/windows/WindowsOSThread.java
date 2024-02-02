/*     */ package oshi.software.os.windows;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import oshi.driver.windows.registry.ThreadPerformanceData;
/*     */ import oshi.software.common.AbstractOSThread;
/*     */ import oshi.software.os.OSProcess;
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
/*     */ public class WindowsOSThread
/*     */   extends AbstractOSThread
/*     */ {
/*     */   private final int threadId;
/*     */   private String name;
/*     */   private OSProcess.State state;
/*     */   private long startMemoryAddress;
/*     */   private long contextSwitches;
/*     */   private long kernelTime;
/*     */   private long userTime;
/*     */   private long startTime;
/*     */   private long upTime;
/*     */   private int priority;
/*     */   
/*     */   public WindowsOSThread(int pid, int tid, String procName, ThreadPerformanceData.PerfCounterBlock pcb) {
/*  57 */     super(pid);
/*  58 */     this.threadId = tid;
/*  59 */     updateAttributes(procName, pcb);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getThreadId() {
/*  64 */     return this.threadId;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  69 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess.State getState() {
/*  74 */     return this.state;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartMemoryAddress() {
/*  79 */     return this.startMemoryAddress;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getContextSwitches() {
/*  84 */     return this.contextSwitches;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getKernelTime() {
/*  89 */     return this.kernelTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUserTime() {
/*  94 */     return this.userTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartTime() {
/*  99 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUpTime() {
/* 104 */     return this.upTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPriority() {
/* 109 */     return this.priority;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 114 */     Set<Integer> pids = Collections.singleton(Integer.valueOf(getOwningProcessId()));
/*     */ 
/*     */     
/* 117 */     Map<Integer, ThreadPerformanceData.PerfCounterBlock> threads = ThreadPerformanceData.buildThreadMapFromRegistry(pids);
/*     */     
/* 119 */     if (threads == null) {
/* 120 */       threads = ThreadPerformanceData.buildThreadMapFromPerfCounters(pids);
/*     */     }
/* 122 */     return updateAttributes(this.name.split("/")[0], threads.get(Integer.valueOf(getThreadId())));
/*     */   }
/*     */   
/*     */   private boolean updateAttributes(String procName, ThreadPerformanceData.PerfCounterBlock pcb) {
/* 126 */     if (pcb == null) {
/* 127 */       this.state = OSProcess.State.INVALID;
/* 128 */       return false;
/* 129 */     }  if (pcb.getName().contains("/") || procName.isEmpty()) {
/* 130 */       this.name = pcb.getName();
/*     */     } else {
/* 132 */       this.name = procName + "/" + pcb.getName();
/*     */     } 
/* 134 */     switch (pcb.getThreadState())
/*     */     { case 0:
/* 136 */         this.state = OSProcess.State.NEW;
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
/* 156 */         this.startMemoryAddress = pcb.getStartAddress();
/* 157 */         this.contextSwitches = pcb.getContextSwitches();
/* 158 */         this.kernelTime = pcb.getKernelTime();
/* 159 */         this.userTime = pcb.getUserTime();
/* 160 */         this.startTime = pcb.getStartTime();
/* 161 */         this.upTime = System.currentTimeMillis() - pcb.getStartTime();
/* 162 */         this.priority = pcb.getPriority();
/* 163 */         return true;case 2: case 3: this.state = OSProcess.State.RUNNING; this.startMemoryAddress = pcb.getStartAddress(); this.contextSwitches = pcb.getContextSwitches(); this.kernelTime = pcb.getKernelTime(); this.userTime = pcb.getUserTime(); this.startTime = pcb.getStartTime(); this.upTime = System.currentTimeMillis() - pcb.getStartTime(); this.priority = pcb.getPriority(); return true;case 4: this.state = OSProcess.State.STOPPED; this.startMemoryAddress = pcb.getStartAddress(); this.contextSwitches = pcb.getContextSwitches(); this.kernelTime = pcb.getKernelTime(); this.userTime = pcb.getUserTime(); this.startTime = pcb.getStartTime(); this.upTime = System.currentTimeMillis() - pcb.getStartTime(); this.priority = pcb.getPriority(); return true;case 5: this.state = OSProcess.State.SLEEPING; this.startMemoryAddress = pcb.getStartAddress(); this.contextSwitches = pcb.getContextSwitches(); this.kernelTime = pcb.getKernelTime(); this.userTime = pcb.getUserTime(); this.startTime = pcb.getStartTime(); this.upTime = System.currentTimeMillis() - pcb.getStartTime(); this.priority = pcb.getPriority(); return true;case 1: case 6: this.state = OSProcess.State.WAITING; this.startMemoryAddress = pcb.getStartAddress(); this.contextSwitches = pcb.getContextSwitches(); this.kernelTime = pcb.getKernelTime(); this.userTime = pcb.getUserTime(); this.startTime = pcb.getStartTime(); this.upTime = System.currentTimeMillis() - pcb.getStartTime(); this.priority = pcb.getPriority(); return true; }  this.state = OSProcess.State.OTHER; this.startMemoryAddress = pcb.getStartAddress(); this.contextSwitches = pcb.getContextSwitches(); this.kernelTime = pcb.getKernelTime(); this.userTime = pcb.getUserTime(); this.startTime = pcb.getStartTime(); this.upTime = System.currentTimeMillis() - pcb.getStartTime(); this.priority = pcb.getPriority(); return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\windows\WindowsOSThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */